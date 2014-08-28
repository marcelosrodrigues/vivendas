package controllers;

import java.util.List;

import models.Apartamento;
import models.Morador;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

public class Utilities extends Controller {

	public static void listByBloco(Long bloco){
		
		List<Apartamento> apartamentos = Apartamento.listByBlocoId(bloco);
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("numero","id","bloco.id","bloco.bloco").exclude("*").serialize(apartamentos));
		
	}
	
	public static void buscarMoradorByCPF( String cpf ) {
		
		if( !StringUtils.isBlank(cpf) ) {
			
			Morador morador = Morador.getByCPF(cpf);
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
	
	public static void buscarMoradorByApartamento( Long apartamento ) {
		
		if( apartamento > 0 ) {
			final Apartamento apto = Apartamento.findById(apartamento);
			
			JSONSerializer json = new JSONSerializer();
			
			renderJSON(json.include("id","cpf",
			"nomeCompleto",
			"dataNascimento","identidade","orgaoEmissor", "dataEmissao",
			"email", "telefoneResidencial","telefoneComercial")
				.exclude("*")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataNascimento")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataEmissao")
				.serialize(apto.getMorador()));
		}
		
	}

}
