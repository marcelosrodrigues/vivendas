package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Apartamento;
import models.Bloco;
import models.Boleto;
import models.Dependente;
import models.Documentacao;
import models.Morador;
import models.Usuario;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import exceptions.DuplicateRegisterException;
import factory.DocumentacaoFactory;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@CRUD.For(Morador.class)
@With(Secure.class)
public class Moradores extends CRUD {
	
	@Before(only={"index","pesquisar"})
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

	public static void index() {
		
		List<Apartamento> moradores = Apartamento.find(
				"order by bloco.bloco ASC, numero ASC").fetch(0,30);
		
		int count = (int) Apartamento.count();
		count /= 30;
		int page = 1;
		if( count % 30 > 0 ) {
			count++;
		}
		render(moradores,count,page);
	}
	
	public static void pesquisar(Long bloco , Long apartamento , int page ) {
		
		String query = " 1=1";
		if(page == 0 ){
			page = 1;
		}
		int length = 50;
		
		List<Object> parameters = new ArrayList<Object>();
		
		if( bloco!= null && bloco >0 ){
			query += " AND bloco.id = ?";
			parameters.add(bloco);
		}
		
		if( apartamento != null && apartamento >0 ){
			query += " AND id = ?";
			parameters.add(apartamento);
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
					renderArgs.put("error", "Não foi possível salvar o Morador pois o CPF informado já esta cadastrado no sistema");					
					render("Moradores/novo.html",object);
				}
				
				
			}
		}
		
		if( !StringUtils.isBlank(params.get("email")) && Usuario.count("email = ?" , params.get("email")) > 0 ) {
			
			if( object == null || !object.email.equals(params.get("email")) ){
				validation.addError("email", "E-mail já em uso");
				renderArgs.put("error", "Não foi possível salvar o Morador pois o E-mail informado já esta cadastrado no sistema");
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
					renderArgs.put("error", "Não foi possível salvar o Morador pois o CPF informado já esta cadastrado no sistema");
					render("Moradores/show.html",object);
				}
				
				
			}
		}
		
		if( !StringUtils.isBlank(params.get("object.email")) && Usuario.count("email = ?" , params.get("object.email")) > 0 ) {
			
			if(object == null || !object.email.equals(params.get("object.email"))) {
				validation.addError("object.email", "E-mail já em uso");
				renderArgs.put("error", "Não foi possível salvar o Morador pois o E-mail informado já esta cadastrado no sistema");				
				render("Moradores/show.html",object);
			}
			
		}
		
	}
	
	public static void abrir(Long id) {
		
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();	
		
		Apartamento apartamento = Apartamento.findById(id);
		
		List<Apartamento> apartamentos = Apartamento.find("bloco = ?", apartamento.bloco).fetch();
		params.put("id",apartamento.getMorador().getId().toString());
		params.put("bloco",apartamento.bloco.id.toString());
		params.put("apartamento",apartamento.id.toString());
		params.put("cpf",apartamento.getMorador().cpf);
		params.put("nomeCompleto",apartamento.getMorador().nomeCompleto);
		
		if( apartamento.getMorador().dataNascimento != null ) {
			params.put("dataNascimento",DateFormatUtils.format(apartamento.getMorador().dataNascimento, "dd-MM-yyyy", new Locale("pt-BR")));
		}
		params.put("identidade",apartamento.getMorador().identidade);
		params.put("orgaoemissor",apartamento.getMorador().orgaoEmissor);
		params.put("dataemissao",DateFormatUtils.format(apartamento.getMorador().dataEmissao, "dd-MM-yyyy", new Locale("pt-BR")));
		params.put("email",apartamento.getMorador().email);
		params.put("telefoneResidencial",apartamento.getMorador().telefoneResidencial);
		params.put("telefoneComercial",apartamento.getMorador().telefoneComercial);
		
		if( apartamento.getMorador().equals(apartamento.getProprietario())) {
			params.put("morador_type","P");
			params.put("dataEntradaImovel",DateFormatUtils.format(apartamento.getEscritura().dataEntrada, "dd-MM-yyyy", new Locale("pt-BR")));
			if( apartamento.getEscritura().dataSaida != null ) {
				params.put("dataSaidaImovel",DateFormatUtils.format(apartamento.getEscritura().dataSaida, "dd-MM-yyyy", new Locale("pt-BR")));
			}
		} else {
			params.put("morador_type","M");
			params.put("dataEntradaImovel",DateFormatUtils.format(apartamento.getContratoLocacao().dataInicioContrato, "dd-MM-yyyy", new Locale("pt-BR")));
			
			if( apartamento.getContratoLocacao().dataTerminoContrato != null ) {
				params.put("dataSaidaImovel",DateFormatUtils.format(apartamento.getContratoLocacao().dataTerminoContrato, "dd-MM-yyyy", new Locale("pt-BR")));
			}
		}
		
		render("Moradores/novo.html", blocos, apartamentos);
	}
		
	public static void novo() {

		List<Bloco> blocos = Bloco.find("order by bloco").fetch();		
		render("Moradores/novo.html", blocos);
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

	public static void submit(@Required Long apartamento, @Required String cpf,
			@Required String nomeCompleto,
			@As(format = "dd-MM-yyyy") @Required Date dataNascimento, @Required String identidade,
			@Required String orgaoemissor, @As(format = "dd-MM-yyyy") @Required Date dataemissao,
			@Required @Email String email, @Required String telefoneResidencial, String telefoneComercial,
			String morador_type ,
			@Required @As(format = "dd-MM-yyyy") Date dataEntradaImovel,
			@As(format = "dd-MM-yyyy") Date dataSaidaImovel, File escritura) throws FileNotFoundException {
		
		Apartamento apto = null;
		if(apartamento != null ) {
			apto = Apartamento.findById(apartamento);
		} 
		
		if(validation.hasErrors() && apartamento == null ){
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			prepare();			
		}else if(validation.hasErrors()){
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			prepare(apto);
		}
		
		Morador morador = Morador.create(cpf, nomeCompleto, dataNascimento,
				identidade, orgaoemissor, dataemissao, email,
				telefoneResidencial, telefoneComercial);
				
		Documentacao documentacao = DocumentacaoFactory.getDocumento(morador_type, morador, apto, dataEntradaImovel);
		documentacao.setDataSaidaImovel(dataSaidaImovel);
		documentacao.add(escritura);
		
		try {
			documentacao.salvar();
			if( "M".equalsIgnoreCase(morador_type) ) {
				flash.success("Contrato de Locação salvo com sucesso");
			} else {
				flash.success("Escritura salva com sucesso");
			}
			
		} catch (DuplicateRegisterException e) {
			validation.addError(null, e.getMessage());
			renderArgs.put("error",  e.getMessage());
			prepare(apto, documentacao);
		}
		
		index();
	}

	private static void prepare(Apartamento apartamento) {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		List<Apartamento> apartamentos = Apartamento.find("bloco = ?", apartamento.bloco).fetch();
		render("Moradores/novo.html",apartamento.bloco,apartamento,blocos,apartamentos);
		
	}

	
	private static void prepare() {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		render("Moradores/novo.html",blocos);
		
	}

	private static void prepare(Apartamento apto, Documentacao documento) {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		if( apto != null ) {
			List<Apartamento> apartamentos = Apartamento.find("bloco = ?", apto.bloco).fetch();
			render("Moradores/novo.html",documento,apto.bloco,apto,blocos,apartamentos);
		} else {
			render("Moradores/novo.html",documento,blocos);
		}
	}
	
}
