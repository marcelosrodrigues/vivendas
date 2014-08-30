package controllers;

import java.util.List;

import models.Apartamento;
import models.Bloco;
import models.ExameMedico;
import models.Morador;
import play.data.binding.Binder;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;
import utils.CommandFactory;
import utils.Constante;

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


	@Before(only = {"list", "pesquisar"})
	static void listBlocos() {
		final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
	    CommandFactory.getInstance()
	                   .get(Constante.BLOCOS,params,templateBinding)
	                   .execute();
	    
	}
	
	public static void list() {
		
		List<Morador> moradores = Morador.find(LIST_ALL).fetch();
		render(moradores);
		
	}
	
	public static void pesquisar(Bloco bloco, Apartamento apartamento) {

		String QUERY = "";
		List<Morador> moradores = null;
		if (bloco != null && bloco.id !=null && bloco.id > 0L
				&& (apartamento == null || apartamento.id == null)) {
			QUERY = " select m from Morador m "
					+ " left join m.exameMedico e  "
					+ " where (  exists ( select ee from Escritura ee  inner join ee.apartamento a inner join a.bloco b"
					+ "  					where ee.proprietario = m  "
					+ "					  and b = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a inner join a.bloco b "
					+ "  		     where cc.inquilino = m  "
					+ "			   and b = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()) or  "
					+ "  exists ( select dd from Dependente dd inner join dd.morador mm "
					+ "  where dd = m "
					+ "    and (exists ( select ee from Escritura ee  inner join ee.apartamento a inner join a.bloco b"
					+ "  					where ee.proprietario = mm  "
					+ "					  and b = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a inner join a.bloco b "
					+ "  		     where cc.inquilino = mm  "
					+ "			   and b = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()))"
					+ ") )		  "
					+ "  order by m.nomeCompleto asc ";
			moradores = Morador.find(QUERY, bloco, bloco, bloco, bloco).fetch();
		} else if (apartamento != null && apartamento.id != null && apartamento.id > 0L) {
			QUERY = " select m from Morador m "
					+ " left join m.exameMedico e  "
					+ " where (  exists ( select ee from Escritura ee  inner join ee.apartamento a "
					+ "  					where ee.proprietario = m  "
					+ "					  and a = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a "
					+ "  		     where cc.inquilino = m  "
					+ "			   and a = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()) or  "
					+ "  exists ( select dd from Dependente dd inner join dd.morador mm "
					+ "  where dd = m "
					+ "    and (exists ( select ee from Escritura ee  inner join ee.apartamento a"
					+ "  					where ee.proprietario = mm  "
					+ "					  and a = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a "
					+ "  		     where cc.inquilino = mm  "
					+ "			       and a = ? 		 "
					+ "  		       and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()))"
					+ ") )		  " + "  order by m.nomeCompleto asc ";

			moradores = Morador.find(QUERY, apartamento, apartamento,
					apartamento, apartamento).fetch();
		} else {
			moradores = Morador.find(LIST_ALL).fetch();
		}


		render("ExameMedicos/list.html", moradores);

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
