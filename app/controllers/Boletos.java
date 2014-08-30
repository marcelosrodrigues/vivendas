package controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import models.Apartamento;
import models.Bloco;
import models.Boleto;

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
import utils.validators.ValidatorFactory;
import utils.validators.dto.Error;
import utils.validators.dto.GreaterThanValid;
import dto.ResultList;

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
		Collection<Boleto> boletos = Boleto.findByDataVencimento(boleto.dataVencimento);
		render("Boletos/gerarBoleto.html",boletos);
	}
	
	public static void condominio() {
		BigDecimal areaTotal = Apartamento.getAreaTotal();
		render("Boletos/condominio.html", areaTotal);
	}

	public static void calcularCondominio(@As(format = "dd-MM-yyyy") @Required Date dataVencimento,
			@Required @As(format = "#,##",lang={"pt"}) BigDecimal despesa, @Required  @As(format = "#,##",lang={"pt"}) BigDecimal agua,  @As(format = "#,##",lang={"pt"}) BigDecimal cotaextra, @Required  @As(format = "#,##",lang={"pt"}) BigDecimal taxa) {

		BigDecimal areaTotal = Apartamento.getAreaTotal();		
		
		ValidatorFactory validations = ValidatorFactory.getInstance();
		validations.validate(new GreaterThanValid("despesa", despesa, BigDecimal.ZERO));
		
		if( validations.hasErrors() ){
    		for( Error error : validations ){
    			validation.addError(error.getKey(), error.getMessage());
    		}
			renderArgs
					.put("error",
							"Não foi possível calcular o condominio, verifique os erros abaixo");
		} else {

			BoletoService service = new BoletoService();
			service.calcularCondominio(DateTime.now().toDate(),
					areaTotal, despesa,agua,cotaextra,taxa);
			
			Collection<Boleto> boletos = service.gerarBoletos(dataVencimento);
			render("Boletos/gerarBoleto.html",boletos);
		}

		

	}

	@Check("FINANCEIRO")
	public static void show(Long id,@As(format="dd-MM-yyyy") Date dataVencimento , Long bloco , Long apartamento , int page) {
		Boleto boleto = Boleto.findById(id);
		render(boleto,dataVencimento,bloco,apartamento,page);
	}
	
	@Check("FINANCEIRO")
	public static void editar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento , @As(format="dd-MM-yyyy") @Required Date dataPagamento , Bloco bloco , Apartamento apartamento , Long page) {
		
		Boleto boleto = Boleto.findById(id);
		boleto.dataPagamento = dataPagamento;
		boleto.save();
		
		flash.success("Boleto salvo com sucesso");
		
		pesquisar(dataVencimento,bloco,apartamento,page);
	}
	
	@Check("FINANCEIRO")
	public static void cancelar(Long id ,@As(format="dd-MM-yyyy") Date dataVencimento ,  Bloco bloco , Apartamento apartamento , Long page) {
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
	public static void pesquisar(@As(format="dd-MM-yyyy") Date dataVencimento , Bloco bloco , Apartamento apartamento , Long page) {
				
		ResultList<Boleto> resultlist = Boleto.findBy(dataVencimento, bloco, apartamento);
		Long count = resultlist.getCount();
		resultlist.setPage(page);
		List<Boleto> boletos = resultlist.list();
		
		page = page == null ? 1L : page;
		
		render("Boletos/pesquisar.html",boletos,count,page);
	}
	
}
