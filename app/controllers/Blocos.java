package controllers;

import java.util.List;

import models.Bloco;
import play.mvc.Controller;
import flexjson.JSONSerializer;

public class Blocos extends Controller {

	public static void list() {

		List<Bloco> blocos = Bloco.list();
		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("id", "bloco").exclude("*").serialize(blocos));

	}
}
