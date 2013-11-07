package controllers;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import models.Apartamento;
import models.Bloco;
import models.Boleto;
import models.Lancamento;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import controllers.CRUD.ObjectType;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;

@CRUD.For(Lancamento.class)
@With(Secure.class)
public class Lancamentos extends CRUD {

	@Before(unless="novo")
    static void listBlocos() {
		List<Bloco> blocos = Bloco.find("order by bloco").fetch();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        templateBinding.data.put("blocos", blocos);
        
        String id = params.get("object.apartamento.id");
        String bloco_id = params.get("bloco");
        if( !StringUtils.isBlank(id) ) {
        	Apartamento apartamento = Apartamento.findById(Long.parseLong(id));
        	params.put("bloco", apartamento.bloco.id.toString());
        	templateBinding.data.put("apartamentos", Apartamento.find(" bloco = ? order by numero ", apartamento.bloco).fetch());
        } else if( !StringUtils.isBlank(bloco_id) ) {
        	templateBinding.data.put("apartamentos", Apartamento.find(" bloco.id = ? order by numero ", Long.parseLong(bloco_id) ).fetch());
        }
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
	
	@Check("FINANCEIRO")
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
