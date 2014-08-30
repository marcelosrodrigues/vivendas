package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import models.Apartamento;
import models.Boleto;
import models.ConfiguracaoBoleto;
import models.Lancamento;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import play.db.jpa.Transactional;
import utils.Constante;
import br.com.caelum.stella.boleto.Banco;
import br.com.caelum.stella.boleto.Datas;
import br.com.caelum.stella.boleto.Emissor;
import br.com.caelum.stella.boleto.Sacado;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;


public class BoletoService {

	private static final Logger LOGGER = Logger
			.getLogger(BoletoService.class);

	@Transactional
	public Boleto gerarBoletos(Apartamento apartamento, Date dataVencimento) {

		try {

			LOGGER.info(String
					.format("Inicio da geração do boleto com vencimento para %tD para o apartamento ",
							dataVencimento, apartamento));

			DateTime vencimento = new DateTime(dataVencimento.getTime());
			Collection<Lancamento> lancamentos = Lancamento.findLancamentoPorData(vencimento.minusMonths(1), apartamento); 

			Boleto boleto = new Boleto(apartamento, dataVencimento);
			for (Lancamento lancamento : lancamentos) {
				boleto.adicionarLancamento(lancamento);
			}

			apartamento.adicionarBoleto(boleto);
			apartamento.save();
			LOGGER.info(String
					.format("Gerado boleto de %s para o apartamento %s com vencimento para %tD",
							boleto.valor, apartamento, dataVencimento));
			
			return boleto;

		} finally {

			LOGGER.info(String
					.format("Termino da geração do boleto com vencimento para %tD do apartamento %s",
							dataVencimento, apartamento));

		}

	}

	@Transactional
	public Collection<Boleto> gerarBoletos(Date dataVencimento) {

		DateTime vencimento = new DateTime(dataVencimento.getTime());

		Collection<Boleto> boletos = new ArrayList<Boleto>();
		try {

			LOGGER.info(String.format(
					"Inicio da geração dos boletos com vencimento para %tD",
					dataVencimento));

			Collection<Lancamento> lancamentos = Lancamento.findLancamentoPorData(vencimento.minusMonths(1));

			Apartamento apartamento = null;
			Boleto boleto = null;
			
			for (Lancamento lancamento : lancamentos) {

				if (apartamento != null
						&& !apartamento.equals(lancamento.apartamento)) {

					boletos.add(boleto);
					apartamento.adicionarBoleto(boleto);
					boleto.save();
					apartamento.save();					
					boleto = null;
					
					LOGGER.info(String
							.format("Gerado boleto para o apartamento %s com vencimento para %tD",
									apartamento, dataVencimento));					
					
				}
				
				apartamento = lancamento.apartamento;
				LOGGER.debug(String.format(
						"Lancamento %s [%s] para o apartamento %s",
						lancamento.historico, lancamento.getValor(),
						lancamento.apartamento));

				if( boleto == null){
					boleto = new Boleto(lancamento.apartamento, dataVencimento);
				}
				
				boleto.adicionarLancamento(lancamento);

			}
			if( boleto != null ) {
				boletos.add(boleto);
				apartamento.adicionarBoleto(boleto);
				boleto.save();
				apartamento.save();
			}
			return boletos;

		} finally {

			LOGGER.info(String.format(
					"Termino da geração dos boletos com vencimento para %tD",
					dataVencimento));

		}

	}

	public void calcularCondominio(Date vencimento, BigDecimal areaTotal,
			BigDecimal valor, BigDecimal agua, BigDecimal cotaextra, BigDecimal taxa) {
		BigDecimal condominio = valor.divide(areaTotal, RoundingMode.HALF_UP);
		BigDecimal fundoReserva = condominio.multiply(new BigDecimal(0.10));
		BigDecimal contaagua = agua.divide(areaTotal , RoundingMode.HALF_UP);
		
		List<Apartamento> apartamentos = Apartamento.list();

		for (Apartamento apartamento : apartamentos) {

			apartamento.extorna(Constante.LANCAMENTO_CONDOMINIO,vencimento);
			apartamento.extorna(Constante.LANCAMENTO_FUNDO_REVERSA,vencimento);
			
			apartamento.extorna(Constante.COTA_EXTRA,vencimento);
			apartamento.extorna(Constante.AGUA,vencimento);
			apartamento.extorna(Constante.TAXA_BANCARIA,vencimento);
			
			apartamento.fazerLancamento(vencimento, taxa, Constante.TAXA_BANCARIA);
			apartamento.fazerLancamento(vencimento, contaagua.multiply(apartamento.area), Constante.AGUA);
			
			if( cotaextra != null && !cotaextra.equals(BigDecimal.ZERO) ){
				apartamento.fazerLancamento(vencimento, cotaextra, Constante.COTA_EXTRA);
			}
			
			apartamento.fazerLancamento(vencimento, fundoReserva.multiply(apartamento.area),
					Constante.LANCAMENTO_FUNDO_REVERSA);
			
			
			if (!apartamento.getMorador().isSindico()) {
				if (!apartamento.getMorador().isSubSindico()) {
										
					apartamento
							.fazerLancamento(vencimento,
									condominio.multiply(apartamento.area),
									Constante.LANCAMENTO_CONDOMINIO);
				} else {
					apartamento.fazerLancamento(vencimento,
							condominio.divide(new BigDecimal(2)).multiply(
									apartamento.area), Constante.LANCAMENTO_CONDOMINIO);
				}
			}			
			apartamento.save();
		}
	}

	public byte[] imprimirBoleto(Long id) throws Exception {
		
		Boleto boleto = Boleto.findById(id);
		ConfiguracaoBoleto configuracao = ConfiguracaoBoleto.all().first();
		DateTime dataGeracao = new DateTime(boleto.dataGeracao.getTime());
		DateTime dataVencimento = new DateTime(boleto.dataVencimento.getTime());
		
		Datas datas = Datas.novasDatas().comProcessamento(dataGeracao.getDayOfMonth() , dataGeracao.getMonthOfYear(),dataGeracao.getYear())
										.comVencimento(dataVencimento.getDayOfMonth() , dataVencimento.getMonthOfYear(),dataVencimento.getYear());
		
		Emissor emissor = Emissor.novoEmissor()
				.comAgencia(configuracao.agencia)
				.comCedente(configuracao.cedente)
				.comCarteira(Integer.parseInt(configuracao.carteira))
				.comNossoNumero(Long.parseLong(configuracao.nossoNumero))
				.comDigitoNossoNumero(configuracao.digitoNossoNumero)
				.comContaCorrente(Integer.parseInt(configuracao.contacorrente))
				.comDigitoContaCorrente(
						configuracao.digitoContaCorrente.charAt(0));
		
		Sacado sacado = Sacado.novoSacado()
							  .comNome(boleto.apartamento.getMorador().nomeCompleto)
							  .comCpf(boleto.apartamento.getMorador().cpf);
		
							  
		Banco banco = (Banco) Class.forName(configuracao.banco).newInstance();
		
		br.com.caelum.stella.boleto.Boleto pagamento = br.com.caelum.stella.boleto.Boleto.novoBoleto()
					.comBanco(banco)
					.comDatas(datas)
					.comEmissor(emissor)
					.comSacado(sacado)
					.comNumeroDoDocumento(boleto.id.toString())
					.comValorBoleto(boleto.valor);
		
		GeradorDeBoleto gerador = new GeradorDeBoleto(pagamento);
		return gerador.geraPDF();
					
		
	}
}
