package controllers;

import java.util.List;

import models.Bloco;
import play.mvc.Controller;
import flexjson.JSONSerializer;

public class Blocos extends Controller {

	public static void list() {

		List<Bloco> blocos = Bloco.find("ORDER BY bloco").fetch();

		JSONSerializer json = new JSONSerializer();
		renderJSON(json.include("id", "bloco").exclude("*").serialize(blocos));

	}
}
