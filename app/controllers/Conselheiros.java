package controllers;

import models.Conselho;
import play.data.binding.Binder;
import play.db.jpa.Model;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Conselheiros extends Controller {

	public static void index() {

		Conselho conselho = Conselho.vigente();
		
		if (conselho != null) {
			render(conselho);
		} else {
			conselho = new Conselho();
			render("Conselheiros/novo.html", conselho);
		}
	}

	public static void novo() {
		Conselho conselho = new Conselho();
		render("Conselheiros/novo.html", conselho);
	}

	public static void salvar() {
		Model conselho = new Conselho();
		Binder.bindBean(params.getRootParamNode(), "conselho", conselho);

		conselho.save();
		index();
	}

}
