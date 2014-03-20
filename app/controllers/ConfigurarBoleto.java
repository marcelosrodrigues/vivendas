package controllers;

import models.ConfiguracaoBoleto;
import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

@CRUD.For(ConfiguracaoBoleto.class)
@With(Secure.class)
public class ConfigurarBoleto extends CRUD {

	@Check("SUPERADMIN")
	public static void index() {

		ConfiguracaoBoleto object = ConfiguracaoBoleto.all().first();
		if (object == null) {
			object = new ConfiguracaoBoleto();
		}
		render("ConfigurarBoleto/index.html", object);
	}

	public static void create() throws Exception {

		ObjectType type = ObjectType.get(getControllerClass());
		ConfiguracaoBoleto object = ConfiguracaoBoleto.all().first();
		if (object == null) {
			object = new ConfiguracaoBoleto();
		}

		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/index.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/index.html", type, object);
			}
		}

		object._save();
		flash.success(play.i18n.Messages.get("crud.created", "Configuração"));
		redirect(request.controller + ".index");

	}
}
