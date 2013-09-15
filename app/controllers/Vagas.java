package controllers;

import java.util.List;

import controllers.CRUD.ObjectType;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;

import flexjson.JSONSerializer;

import models.Apartamento;
import models.Bloco;
import models.Morador;
import models.Vaga;

@CRUD.For(Vaga.class)
@With(Secure.class)
public class Vagas extends CRUD{
	
	
	@Before(only="show")
	public static void  prepare() {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        templateBinding.data.put("blocos", blocos);
	}
	
	public static void list(Long id) {

		List<Vaga> vagas = Vaga.find("SELECT v from Vaga v join v.apartamento a WHERE a.id = ?", id).fetch();
		Apartamento apartamento = Apartamento.findById(id);
		Morador proprietario = apartamento.getProprietario();
		render(proprietario,vagas);
	}
	
	public static void buscar(Long id){
		
		Vaga vaga = Vaga.findById(id);
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("id","local","numeroVaga","observacao","alugadoPara.id","alugadoPara.numero","alugadoPara.bloco.id","alugadoPara.bloco.id").exclude("*").serialize(vaga));
		
	}
	
	public static void save(String id) throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Vaga object = Vaga.findById(Long.parseLong(id));
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.saved.female", type.modelName));
        if (params.get("_save") != null) {
            list(object.apartamento.id);
        }
        redirect(request.controller + ".show", object._key());
    }
}
