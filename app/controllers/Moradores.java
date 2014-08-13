package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import enumarations.MoradorType;
import models.Apartamento;
import models.Bloco;
import models.Documentacao;
import models.Escritura;
import models.Morador;
import models.Usuario;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import play.data.binding.As;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import services.MoradorService;
import dto.ResultList;
import exceptions.DuplicateRegisterException;
import factory.DocumentacaoFactory;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import utils.CommandFactory;
import utils.Constante;

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
		MoradorService service = new MoradorService();
		ResultList<Apartamento> result = service.search();
		List<Apartamento> moradores = result.list();
		Long count = result.getCount();
        Long page = 1L;
		render(moradores, count, page);
	}
	
	public static void getJSON(Long bloco, Long apartamento, String morador,
			Long page) {

		String QUERY = "SELECT e from Escritura e INNER JOIN FETCH e.apartamento a INNER JOIN FETCH a.bloco b INNER JOIN FETCH e.proprietario m WHERE e.dataEntrada <= CURRENT_DATE() AND COALESCE(e.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ";
		List<Object> parameters = new ArrayList<Object>();

		if (page == 0L)
			page = 1L;

		if (apartamento != null && apartamento > 0L) {
			QUERY += " AND a.id = ?";
			parameters.add(apartamento);
		}

		if (bloco != null && bloco > 0L) {
			QUERY += " AND b.id = ?";
			parameters.add(bloco);
		}

		if (morador != null && !"".equalsIgnoreCase(morador)) {
			QUERY += " AND m.nomeCompleto LIKE ?";
			parameters.add(morador + "%");
		}

		final ResultList<Escritura> resultlist = Escritura.findBy(bloco,apartamento,morador);
        resultlist.setPage(page);
        List<Escritura> escrituras = resultlist.list();
                JSONSerializer json = new JSONSerializer();
		renderJSON(json
				.include("proprietario.id", "proprietario.nomeCompleto",
						"apartamento.numero", "apartamento.bloco.bloco")
				.exclude("*").serialize(escrituras));

	}
	
	public static void pesquisar(Bloco bloco , Apartamento apartamento , int page ) {
		
		String query = " 1=1";
		if(page == 0 ){
			page = 1;
		}
		int length = 50;
		
		List<Object> parameters = new ArrayList<Object>();
		
		if( bloco.id != null && bloco.id >0 ){
			query += " AND bloco.id = ?";
			parameters.add(bloco.id);
		}
		
		if( apartamento.id != null && apartamento.id > 0 ){
			query += " AND id = ?";
			parameters.add(apartamento.id);
		}
		
		int count = (int) Apartamento.count(query , parameters.toArray() );
		count /= length;
		
		if( count % length > 0 ) {
			count++;
		}
		

		List<Apartamento> moradores = Apartamento.find(query + " order by bloco.bloco ASC, numero ASC", parameters.toArray())
				.fetch(page, length);
		
		render("Moradores/index.html",moradores,count,page);
		
	}
	
	@Before(only={"submit"})
	static void validate_new_morador() throws Exception {
		
		
		Morador object = null;
		
		if( !StringUtils.isBlank(params.get("id"))) {
			object = Morador.findById(Long.parseLong(params.get("id")));
		}
		
		if(StringUtils.isBlank(params.get("cpf"))) {
			params.remove("cpf");
		} else {
			if( Morador.count("cpf = ?", params.get("cpf")) > 0 ) {			
				
				
				if( object == null || !object.cpf.equals(params.get("cpf")) ){
					validation.addError("cpf", "CPF já em uso");
					renderArgs
							.put("error",
									"Não foi possível salvar o Morador pois o CPF informado já esta cadastrado no sistema");
					render("Moradores/novo.html",object);
				}
				
				
			}
		}
		
		if( !StringUtils.isBlank(params.get("email")) && Usuario.count("email = ?" , params.get("email")) > 0 ) {
			
			if( object == null || !object.email.equals(params.get("email")) ){
				validation.addError("email", "E-mail já em uso");
				renderArgs
						.put("error",
								"Não foi possível salvar o Morador pois o E-mail informado já esta cadastrado no sistema");
				render("Moradores/novo.html",object);
			}
			
		}
		
	}
	
	@Before(only={"save"})
	static void validate() throws Exception {
		
		
		Morador object = null;
		
		if( !StringUtils.isBlank(params.get("id"))) {
			object = Morador.findById(Long.parseLong(params.get("id")));
		}
		
		if(StringUtils.isBlank(params.get("object.cpf"))) {
			params.remove("object.cpf");
		} else {
			if( Morador.count("cpf = ?", params.get("object.cpf")) > 0 ) {
				
				if(object == null || !object.cpf.equals(params.get("object.cpf")) ){
					validation.addError("object.cpf", "CPF já em uso");
					renderArgs
							.put("error",
									"Não foi possível salvar o Morador pois o CPF informado já esta cadastrado no sistema");
					render("Moradores/show.html",object);
				}
				
				
			}
		}
		
		if( !StringUtils.isBlank(params.get("object.email")) && Usuario.count("email = ?" , params.get("object.email")) > 0 ) {
			
			if(object == null || !object.email.equals(params.get("object.email"))) {
				validation.addError("object.email", "E-mail já em uso");
				renderArgs
						.put("error",
								"Não foi possível salvar o Morador pois o E-mail informado já esta cadastrado no sistema");
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
	
	public static void buscar( String cpf ) {
		
		if( !StringUtils.isBlank(cpf) ) {
			
			Morador morador = Morador.find("cpf = ?", cpf).first();
			JSONSerializer json = new JSONSerializer();
			
			renderJSON(json.include("id","cpf",
			"nomeCompleto",
			"dataNascimento","identidade","orgaoEmissor", "dataEmissao",
			"email", "telefoneResidencial","telefoneComercial")
				.exclude("*")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataNascimento")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataEmissao")
				.serialize(morador));
		
			
		}
		
	}

	public static void submit(final Morador morador, final Apartamento apartamento,
			MoradorType tipo ,
			@Required @As(format = "dd-MM-yyyy") Date dataEntradaImovel,
			@As(format = "dd-MM-yyyy") Date dataSaidaImovel, File escritura) throws FileNotFoundException {
						
		Documentacao documentacao = DocumentacaoFactory.getDocumento(tipo, morador, apartamento, dataEntradaImovel);
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
