package controllers;

import java.util.List;

import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;

import controllers.CRUD.ObjectType;

import models.ExameMedico;
import models.Morador;

@CRUD.For(ExameMedico.class)
@With(Secure.class)
public class ExameMedicos extends CRUD {

	public static final String LIST_ALL = " select m from Morador m " +
										 " left join m.exameMedico e  " +
										 " where (  exists ( select ee from Escritura ee  " +
										 "  where ee.proprietario = m  " +
										 "   and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  " +
										 "  exists ( select cc from ContratoLocacao cc  " +
										 "  where cc.inquilino = m  " +
										 "  and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()) 	 or  " + 
										 "  exists ( select dd from Dependente dd  " +
										 "  where dd = m ) )		  " +
										 "  order by m.nomeCompleto asc ";
	
	public static void list() {
		
		List<Morador> moradores = Morador.find(LIST_ALL).fetch();
		render(moradores);
		
	}
	
	public static void novo(Long id) {
		Morador object = Morador.findById(id);
		object.exameMedico = new ExameMedico();
		render("ExameMedicos/blank.html",object);
	}
	
	public static void criar(Long id) {
		ObjectType type = ObjectType.get(getControllerClass());
		Morador object = Morador.findById(id);
		object.exameMedico = new ExameMedico();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object.exameMedico);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            render("ExameMedicos/blank.html",object);
        }
        object.exameMedico.save();
        object.save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        redirect(request.controller + ".list");
		
	}
}
