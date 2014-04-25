package controllers;

import java.util.List;

import models.Apartamento;
import models.Morador;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

public class Utilities extends Controller {

	public static void listByBloco(long bloco){
		
		List<Apartamento> apartamentos = Apartamento.find("select a from Apartamento a INNER JOIN a.bloco b where b.id = ? order by a.numero", bloco).fetch();
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("numero","id","bloco.id","bloco.bloco").exclude("*").serialize(apartamentos));
		
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
	
	public static void buscar( Long apartamento ) {
		
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
