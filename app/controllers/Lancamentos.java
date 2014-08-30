package controllers;

import java.math.BigDecimal;
import java.util.List;

import models.Apartamento;
import models.Boleto;
import models.Lancamento;
import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import utils.CommandFactory;
import utils.Constante;

@CRUD.For(Lancamento.class)
@With(Secure.class)
public class Lancamentos extends CRUD {

	@Before(unless="novo")
    static void listBlocos() {
		 final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
	        CommandFactory.getInstance()
	                      .get(Constante.BLOCOS,params,templateBinding)
	                      .execute();
    }
	
	@Check("FINANCEIRO")
	public static void novo(Long id) {
		Lancamento object = new Lancamento();
		object.apartamento = Apartamento.findById(id);
		render("Lancamentos/blank.html",object);
	}
	
	@Check("FINANCEIRO")
	public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Lancamento object = new Lancamento();
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
        	list(object.apartamento.id);
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }
	
	@Check("FINANCEIRO")
	public static void list(Long id) {
		
		Apartamento apartamento = Apartamento.findById(id);
		List<Lancamento> lancamentos = Lancamento.find("apartamento.id = ? and boleto is null order by dataLancamento desc", id).fetch();
		BigDecimal total = new BigDecimal(0);
		
		for( Lancamento lancamento : lancamentos ){
			total = total.add(lancamento.valor);
		}
		
		render(lancamentos,total,apartamento);
		
	}
	
	public static void detalhar(Long id) {
		Boleto boleto = Boleto.findById(id);
		Apartamento apartamento = boleto.apartamento;
		List<Lancamento> lancamentos = Lancamento.find("boleto.id = ? order by dataLancamento desc", id).fetch();
		BigDecimal total = new BigDecimal(0);
		
		for( Lancamento lancamento : lancamentos ){
			total = total.add(lancamento.valor);
		}
		
		render("Lancamentos/list.html",lancamentos,total,apartamento,boleto);
	}
	
}
