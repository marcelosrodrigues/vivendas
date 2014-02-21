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
import org.joda.time.DateTime;

import play.data.binding.As;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.With;
import services.BoletoService;

@With(Secure.class)
public class Boletos extends Controller {
	
	@Before(only="pesquisar")
    static void listBlocos() {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        templateBinding.data.put("blocos", blocos);
        
        String id = params.get("apartamento");
        String bloco_id = params.get("bloco");
        if( !StringUtils.isBlank(id) ) {
        	Apartamento apartamento = Apartamento.findById(Long.parseLong(id));
        	params.put("bloco", apartamento.bloco.id.toString());
        	templateBinding.data.put("apartamentos", Apartamento.find(" bloco = ? order by numero ", apartamento.bloco).fetch());
        } else if( !StringUtils.isBlank(bloco_id) ) {
        	templateBinding.data.put("apartamentos", Apartamento.find(" bloco.id = ? order by numero ", Long.parseLong(bloco_id) ).fetch());
        }
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
	public static void editar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento , @As(format="dd-MM-yyyy") @Required Date dataPagamento , Long bloco , Long apartamento , int page) {
		
		Boleto boleto = Boleto.findById(id);
		boleto.dataPagamento = dataPagamento;
		boleto.save();
		
		flash.success("Boleto salvo com sucesso");
		
		pesquisar(dataVencimento,bloco,apartamento,page);
	}
	
	@Check("FINANCEIRO")
	public static void cancelar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento ,  Long bloco , Long apartamento , int page) {
		Boleto boleto = Boleto.findById(id);
		boleto.cancela();
		boleto.save();
		
		flash.success("Boleto cancelado com sucesso");
		
		pesquisar(dataVencimento,bloco,apartamento,page);
	}
	
	public static void imprimir(Long id) {
		
		try {
			BoletoService service = new BoletoService();
			byte[] boleto = service.imprimirBoleto(id);
			renderBinary(new ByteArrayInputStream(boleto),"boleto.pdf");
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
	}
	@Check("FINANCEIRO")
	public static void pesquisar(@As(format="dd-MM-yyyy") Date dataVencimento , Long bloco , Long apartamento , int page) {
				
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
		
		if( bloco!= null && bloco >0 ){
			query += " AND apartamento.bloco.id = ?";
			parameters.add(bloco);
		}
		
		if( apartamento != null && apartamento >0 ){
			query += " AND apartamento.id = ?";
			parameters.add(apartamento);
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
