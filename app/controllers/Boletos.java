package controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import models.Apartamento;
import models.Bloco;
import models.Boleto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.joda.time.DateTime;

import play.data.binding.As;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.With;
import services.BoletoService;
import utils.CommandFactory;
import utils.Constante;

@With(Secure.class)
public class Boletos extends Controller {
	
	@Before(only="pesquisar")
    static void listBlocos() {
        final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        CommandFactory.getInstance()
                .get(Constante.BLOCOS,params,templateBinding)
                .execute();
    }
	
	@Check("FINANCEIRO")
	public static void index() {
		render("Boletos/index.html");
	}
	
	@Check("FINANCEIRO")
	public static void gerarBoleto(
			@As(format = "dd-MM-yyyy") @Required Date dataVencimento) {
		
		BoletoService service = new BoletoService();
		
		Collection<Boleto> boletos = service.gerarBoletos(dataVencimento);
		render(boletos);
		
	}
	
	@Check("FINANCEIRO")
	public static void voltar(Long id) {
		
		Boleto boleto = Boleto.findById(id);
		Collection<Boleto> boletos = Boleto.find("dataVencimento = ? and dataCancelamento is null order by apartamento.bloco.bloco , apartamento.numero" , boleto.dataVencimento).fetch();
		render("Boletos/gerarBoleto.html",boletos);
	}
	
	public static void condominio() {
		BigDecimal areaTotal = (BigDecimal) Apartamento.em()
				.createQuery("SELECT sum(area) from Apartamento")
				.getSingleResult();
		render("Boletos/condominio.html", areaTotal);
	}

	public static void calcularCondominio(
			@Required @As(format = "#,## 0.00") BigDecimal despesa) {

		BigDecimal areaTotal = (BigDecimal) Apartamento.em()
				.createQuery("SELECT sum(area) from Apartamento")
				.getSingleResult();

		if (despesa == null || despesa.doubleValue() <= 0D) {
			validation.addError("despesa",
					"Valor total da despesa é obrigatória");
			renderArgs
					.put("error",
							"Não foi possível calcular o condominio, verifique os erros abaixo");
		} else {

			BoletoService service = new BoletoService();
			service.calcularCondominio(DateTime.now().minusMonths(1).toDate(),
					areaTotal, despesa);
		}

		render("Boletos/condominio.html", areaTotal);

	}

	@Check("FINANCEIRO")
	public static void show(Long id,@As(format="dd-MM-yyyy") Date dataVencimento , Long bloco , Long apartamento , int page) {
		Boleto boleto = Boleto.findById(id);
		render(boleto,dataVencimento,bloco,apartamento,page);
	}
	
	@Check("FINANCEIRO")
	public static void editar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento , @As(format="dd-MM-yyyy") @Required Date dataPagamento , Bloco bloco , Apartamento apartamento , int page) {
		
		Boleto boleto = Boleto.findById(id);
		boleto.dataPagamento = dataPagamento;
		boleto.save();
		
		flash.success("Boleto salvo com sucesso");
		
		pesquisar(dataVencimento,bloco,apartamento,page);
	}
	
	@Check("FINANCEIRO")
	public static void cancelar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento ,  Bloco bloco , Apartamento apartamento , int page) {
		Boleto boleto = Boleto.findById(id);
		boleto.cancela();
		boleto.save();
		
		flash.success("Boleto cancelado com sucesso");
		
		pesquisar(dataVencimento,bloco,apartamento,page);
	}
	
	public static void imprimir(Long id) {
		
		try {
			BoletoService service = new BoletoService();
			Boleto documento = Boleto.findById(id);
			byte[] boleto = service.imprimirBoleto(id);
			renderBinary(new ByteArrayInputStream(boleto),String.format("boleto_%s.pdf", DateFormatUtils.format(documento.dataVencimento,"MM-yyyy")));
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
	@Check("FINANCEIRO")
	public static void pesquisar(@As(format="dd-MM-yyyy") Date dataVencimento , Bloco bloco , Apartamento apartamento , int page) {
				
		String query = " 1=1";
		if(page == 0 ){
			page = 1;
		}
		int length = 50;
		
		List<Object> parameters = new ArrayList<Object>();
		if( dataVencimento != null  ) {
			query += " AND dataVencimento = ?";
			parameters.add(dataVencimento);
		}
		
		if( bloco!= null && bloco.id != null ){
			query += " AND apartamento.bloco.id = ?";
			parameters.add(bloco.id);
		}
		
		if( apartamento != null && apartamento.id  != null  ){
			query += " AND apartamento.id = ?";
			parameters.add(apartamento.id);
		}
		
		int count = (int) Boleto.count(query , parameters.toArray() );
		count /= length;
		
		if( count % length > 0 ) {
			count++;
		}
		
		List<Boleto> boletos = Boleto.find(query + " ORDER by apartamento.bloco.bloco , apartamento.numero , dataVencimento , dataPagamento , dataCancelamento", parameters.toArray())
				.fetch(page, length);
		render("Boletos/pesquisar.html",boletos,count,page);
	}
	
}
