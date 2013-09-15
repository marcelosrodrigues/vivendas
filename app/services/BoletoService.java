package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import models.Apartamento;
import models.Boleto;
import models.Lancamento;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import play.db.jpa.Transactional;


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
			Collection<Lancamento> lancamentos = Lancamento.find("apartamento = ? and MONTH(L.dataLancamento) = ?", apartamento , vencimento.minusMonths(1).getMonthOfYear() ).fetch(); 

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

		
		Collection<Boleto> boletos = new ArrayList<Boleto>();
		try {

			LOGGER.info(String.format(
					"Inicio da geração dos boletos com vencimento para %tD",
					dataVencimento));
			
			DateTime vencimento = new DateTime(dataVencimento.getTime());
			Collection<Lancamento> lancamentos = Lancamento.find("MONTH(dataLancamento) = ? ORDER BY apartamento.bloco.bloco , apartamento.numero",  vencimento.minusMonths(1).getMonthOfYear() ).fetch();

			Apartamento apartamento = null;
			Boleto boleto = null;
			
			for (Lancamento lancamento : lancamentos) {

				if (apartamento == null
						|| apartamento.equals(lancamento.apartamento)) {

					LOGGER.debug(String.format(
							"Lancamento %s [%s] para o apartamento %s",
							lancamento.historico, lancamento.getValor(),
							lancamento.apartamento));

					if( boleto == null){
						boleto = new Boleto(lancamento.apartamento, dataVencimento);
					}
					
					boleto.adicionarLancamento(lancamento);					
					
				} else {
					
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
}
