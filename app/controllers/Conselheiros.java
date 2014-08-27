package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import models.Arquivo;
import models.Conselho;
import play.data.binding.Binder;
import play.db.jpa.Blob;
import play.libs.MimeTypes;
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

	public static void salvar(File ata) throws FileNotFoundException {
		Conselho conselho = new Conselho();
		Binder.bindBean(params.getRootParamNode(), "conselho", conselho);
		conselho.ata = new Arquivo(ata.getName(), new Blob());
		conselho.ata.contentFile.set(new FileInputStream(ata),
				MimeTypes.getContentType(ata.getName()));
		conselho.save();
		index();
	}

}
