package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Apartamento;
import models.Bloco;
import models.Documentacao;
import models.Escritura;
import models.Morador;

import org.apache.commons.lang.time.DateFormatUtils;

import play.data.binding.As;
import play.data.binding.Binder;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import utils.CommandFactory;
import utils.Constante;
import utils.validators.ValidatorFactory;
import utils.validators.dto.EmailIsValid;
import utils.validators.dto.Error;
import utils.validators.dto.ExistsByCPFValid;
import utils.validators.dto.ExistsByEmailValid;
import utils.validators.dto.NotBlankValid;
import dto.ResultList;
import enumarations.MoradorType;
import exceptions.DuplicateRegisterException;
import factory.DocumentacaoFactory;
import flexjson.JSONSerializer;

@CRUD.For(Morador.class)
@With(Secure.class)
public class Moradores extends CRUD {
	
	@Before(only={"index","pesquisar","novo"})
    public static void listBlocos() {

        final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        CommandFactory.getInstance()
                      .get(Constante.BLOCOS,params,templateBinding)
                      .execute();

    }

	public static void index() {
		ResultList<Apartamento> result = Apartamento.findBy(null,null);
		List<Apartamento> moradores = result.list();
		Long count = result.getCount();
        Long page = 1L;
		render(moradores, count, page);
	}
	
	public static void list() {
		index();
	}
	
	public static void getJSON(Long bloco, Long apartamento, String morador,
			Long page) {

		final ResultList<Escritura> resultlist = Escritura.findBy(bloco,apartamento,morador);
        resultlist.setPage(page);
        List<Escritura> escrituras = resultlist.list();
                JSONSerializer json = new JSONSerializer();
		renderJSON(json
				.include("proprietario.id", "proprietario.nomeCompleto",
						"apartamento.numero", "apartamento.bloco.bloco")
				.exclude("*").serialize(escrituras));

	}
	
	public static void pesquisar(Bloco bloco , Apartamento apartamento , Long page ) {

		ResultList<Apartamento> resultlist = Apartamento.findBy(bloco,apartamento);
        resultlist.setPage(page);
        Long count = resultlist.getCount();
        List<Apartamento> moradores = resultlist.list();
		
		render("Moradores/index.html",moradores,count,page);
		
	}
	
	@Before(only={"submit","save"})
	static void validate() throws Exception {		
		
		Morador object = new Morador();
		Binder.bindBean(params.getRootParamNode(),"object", object);

		ValidatorFactory validations = ValidatorFactory.getInstance();
    	
    	validations.validate(new EmailIsValid("object.email", object.email))
				   .and(new ExistsByEmailValid("object.email", object))
				   .and(new NotBlankValid("object.cpf", object.cpf))
				   .and(new ExistsByCPFValid("object.cpf", object));		
		
    	if( validations.hasErrors()){
    		
    		for( Error error : validations ){
    			validation.addError(error.getKey(), error.getMessage());
    		}
    		
    		renderArgs.put("error",	"Não foi possível salvar o Morador. Verifique os erros abaixo");
    		
    		if( request.actionMethod.equals("submit")) {
    			render("Moradores/novo.html",object);
    		} else {
    			render("Moradores/show.html",object);
    		}
    		
    	}
    	
	}
	
	
	public static void abrir(Long id) {
		
		List<Bloco> blocos = Bloco.list();
		
		Apartamento apartamento = Apartamento.findById(id);
		Bloco bloco = apartamento.bloco;
		List<Apartamento> apartamentos = Apartamento.listByBloco(apartamento.bloco);
		Morador object = apartamento.getMorador();
		
		if( apartamento.getMorador().equals(apartamento.getProprietario())) {
			params.put("tipo",MoradorType.PROPRIETARIO.toString());
			params.put("dataEntradaImovel",DateFormatUtils.format(apartamento.getEscritura().dataEntrada, "dd-MM-yyyy", new Locale("pt-BR")));
			if( apartamento.getEscritura().dataSaida != null ) {
				params.put("dataSaidaImovel",DateFormatUtils.format(apartamento.getEscritura().dataSaida, "dd-MM-yyyy", new Locale("pt-BR")));
			}
		} else {
            params.put("tipo",MoradorType.INQUILINO.toString());
			params.put("dataEntradaImovel",DateFormatUtils.format(apartamento.getContratoLocacao().dataInicioContrato, "dd-MM-yyyy", new Locale("pt-BR")));
			
			if( apartamento.getContratoLocacao().dataTerminoContrato != null ) {
				params.put("dataSaidaImovel",DateFormatUtils.format(apartamento.getContratoLocacao().dataTerminoContrato, "dd-MM-yyyy", new Locale("pt-BR")));
			}
		}
		
		render("Moradores/novo.html", object , apartamento , bloco , blocos, apartamentos);
	}
		
	public static void novo() {
			
		Morador object = new Morador();
		Apartamento apartamento = new Apartamento();
		apartamento.bloco = new Bloco();
		render("Moradores/novo.html", object , apartamento);
	}

	public static void submit(final Morador object, final Apartamento apartamento,
			MoradorType tipo ,
			@Required @As(format = "dd-MM-yyyy") Date dataEntradaImovel,
			@As(format = "dd-MM-yyyy") Date dataSaidaImovel, File escritura) throws FileNotFoundException {
						
		Documentacao documentacao = DocumentacaoFactory.getDocumento(tipo, object, apartamento, dataEntradaImovel);
		documentacao.setDataSaidaImovel(dataSaidaImovel);
		documentacao.add(escritura);
		
		try {
			documentacao.salvar();
			if( tipo == MoradorType.INQUILINO ) {
				flash.success("Contrato de Locação salvo com sucesso");
			} else {
				flash.success("Escritura salva com sucesso");
			}
			
		} catch (DuplicateRegisterException e) {
			validation.addError(null, e.getMessage());
			renderArgs.put("error",  e.getMessage());
			prepare(apartamento, documentacao);
		}
		
		index();
	}

	private static void prepare(Apartamento apto, Documentacao documento) {
		List<Bloco> blocos = Bloco.list();
		if( apto != null ) {
			List<Apartamento> apartamentos = Apartamento.listByBloco(apto.bloco);
			render("Moradores/novo.html",documento,apto.bloco,apto,blocos,apartamentos);
		} else {
			render("Moradores/novo.html",documento,blocos);
		}
	}
	
}
