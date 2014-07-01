package controllers;

import java.util.List;

import models.Apartamento;
import models.Bloco;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import flexjson.JSONSerializer;
import utils.Constante;

@CRUD.For(Apartamento.class)
@With(Secure.class)
public class Apartamentos extends CRUD{
	
	@Before
    static void listBlocos() {
		List<Bloco> blocos = Bloco.list();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        templateBinding.data.put(Constante.BLOCOS, blocos);
    }
	
	public static void list() {
		redirect("Moradores.index");
	}
	
	public static void listByBloco(long bloco){
		
		List<Apartamento> apartamentos = Apartamento.listByBlocoId(bloco);
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("numero","id","bloco.id","bloco.bloco").exclude("*").serialize(apartamentos));
		
	}
	public static void show(long id) {
		
		Apartamento object = Apartamento.findById(id);
		render(object);
		
	}
	
	public static void get(long id){
		
		Apartamento apartamento = Apartamento.findById(id);
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("id","numero","bloco.id","bloco.bloco","area").exclude("*").serialize(apartamento));
		
	}
	
	
}
