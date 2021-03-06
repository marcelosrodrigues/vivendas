package controllers;

import java.util.List;

import models.Apartamento;
import models.Bloco;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import utils.Constante;
import flexjson.JSONSerializer;

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

	public static void show(long apartamento) {
		
		Apartamento object = Apartamento.findById(apartamento);
		render(object);
		
	}
	
	public static void get(long id){
		
		Apartamento apartamento = Apartamento.findById(id);
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("id","numero","bloco.id","bloco.bloco","area").exclude("*").serialize(apartamento));
		
	}
	
	
}
