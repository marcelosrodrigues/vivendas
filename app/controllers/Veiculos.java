package controllers;

import java.lang.reflect.Constructor;

import controllers.CRUD.ObjectType;
import models.Vaga;
import models.Veiculo;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

@CRUD.For(Veiculo.class)
@With(Secure.class)
public class Veiculos extends CRUD {
	
	 public static void novo(Long id) throws Exception {
	
		 Vaga vaga = Vaga.findById(id);
		 Veiculo object = new Veiculo();
		 object.vaga = vaga;
		 render("Veiculos/blank.html",object);
		 
	 }
	 
	 public static void create() throws Exception {
	        ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	        constructor.setAccessible(true);
	        Veiculo object = new Veiculo();
	        Binder.bindBean(params.getRootParamNode(), "object", object);
	        validation.valid(object);
	        if (validation.hasErrors()) {
	            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
	            try {
	                render(request.controller.replace(".", "/") + "/blank.html", type, object);
	            } catch (TemplateNotFoundException e) {
	                render("CRUD/blank.html", type, object);
	            }
	        }
	        object._save();
	        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
	        if (params.get("_save") != null) {
	        	redirect("Vagas.list", object.vaga.apartamento.id);
	        }
	        if (params.get("_saveAndAddAnother") != null) {
	            redirect(request.controller + ".blank");
	        }
	        redirect(request.controller + ".show", object._key());
	    }

	  public static void save(String id) throws Exception {
	        ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        Veiculo object = Veiculo.findById(Long.parseLong(id));
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
	        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
	        if (params.get("_save") != null) {
	            redirect("Vagas.list", object.vaga.apartamento.id);
	        }
	        redirect(request.controller + ".show", object._key());
	    }
}
