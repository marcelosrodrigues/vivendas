package controllers;

import java.util.List;

import models.Dependente;
import models.GrauParentesco;
import models.Morador;
import models.Usuario;

import org.apache.commons.lang.StringUtils;

import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@CRUD.For(Dependente.class)
@With(Secure.class)
public class Dependentes extends CRUD {

	@Before
	static void listBlocos() {
		List<GrauParentesco> grausParentesco = GrauParentesco.find(
				"order by nome").fetch();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
		templateBinding.data.put("grausParentesco", grausParentesco);
	}

	@Before(only = { "save", "create" })
	static void validateUpdate() throws Exception {

		Dependente object = new Dependente();

		if (!StringUtils.isBlank(params.get("id"))) {
			object = Dependente.findById(Long.parseLong(params.get("id")));
		} else {
			Binder.bindBean(params.getRootParamNode(), "object", object);
		}
		Morador morador = Morador.findById(object.morador.id);
		object.morador = morador;

		if (StringUtils.isBlank(params.get("object.cpf"))) {
			params.remove("object.cpf");
		} else {
			if (Dependente.count("cpf = ?", object.cpf) > 0) {

				if (StringUtils.isBlank(params.get("id"))
						|| !object.cpf.equals(params.get("object.cpf"))) {
					validation.addError("object.cpf", "CPF já em uso");
					renderArgs
							.put("error",
									"Não foi possível salvar o Dependente pois o CPF informado já esta cadastrado no sistema");
					if (!StringUtils.isBlank(params.get("id"))) {
						render("Dependentes/show.html", object);
					} else {
						render("Dependentes/blank.html", object);
					}
				}

			}
		}

		if (!StringUtils.isBlank(params.get("object.email"))
				&& Usuario.count("email = ?", params.get("object.email")) > 0) {

			if (StringUtils.isBlank(params.get("id"))
					|| !object.email.equals(params.get("object.email"))) {
				validation.addError("object.email", "E-mail já em uso");
				renderArgs
						.put("error",
								"Não foi possível salvar o Dependente pois o E-mail informado já esta cadastrado no sistema");
				if (!StringUtils.isBlank(params.get("id"))) {
					render("Dependentes/show.html", object);
				} else {
					render("Dependentes/blank.html", object);
				}
			}

		}

	}

	public static void delete(Long id) {
		ObjectType type = ObjectType.get(getControllerClass());
		Dependente dependente = Dependente.findById(id);
		Morador morador = dependente.morador;

		try {
			dependente.delete();
		} catch (Exception e) {
			flash.error(play.i18n.Messages.get("crud.delete.error",
					type.modelName));
			redirect(request.controller + ".show", id);
		}
		flash.success(play.i18n.Messages.get("crud.deleted", type.modelName));
		search(morador.id);
	}

	public static void search(Long morador) {

		Morador parente = Morador.findById(morador);

		List<Dependente> dependentes = Dependente
				.find("SELECT d from Dependente d join d.morador m where m.id = ? order by d.grauParentesco.nome asc, d.nomeCompleto",
						morador).fetch();
		render(dependentes, parente);

	}

	public static void add(Long id) {
		Morador parente = Morador.findById(id);
		Dependente object = new Dependente();
		object.grauParentesco = new GrauParentesco();
		object.morador = parente;

		render("Dependentes/blank.html", parente, object);
	}
	
	public static void save(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Dependente object = Dependente.findById(Long.parseLong(id));
		notFoundIfNull(object);
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		object._save();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			search(object.morador.id);
		}
		redirect(request.controller + ".show", object._key());
	}

	public static void create() {

		ObjectType type = ObjectType.get(getControllerClass());
		Dependente object = new Dependente();
		Binder.bindBean(params.getRootParamNode(), "object", object);
		Morador parente = Morador.findById(object.morador.id);

		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}
		object.save();
		flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			search(object.morador.id);
		}
		if (params.get("_saveAndAddAnother") != null) {
			object = new Dependente();
			object.morador = parente;
			render("Dependentes/blank.html",object);
		}
		redirect(request.controller + ".show", object._key());

	}

	public static void buscar(Long id) {
		Dependente dependente = Dependente.findById(id);

		JSONSerializer json = new JSONSerializer();

		renderJSON(json
				.include("id", "cpf", "nomeCompleto", "dataNascimento",
						"identidade", "orgaoEmissor", "dataEmissao", "email",
						"telefoneResidencial", "telefoneComercial",
						"grauParentesco.id", "grauParentesco.nome")
				.exclude("*")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataNascimento")
				.transform(new DateTransformer("dd-MM-yyyy"), "dataEmissao")
				.serialize(dependente));
	}

}
