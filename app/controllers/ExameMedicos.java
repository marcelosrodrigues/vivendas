package controllers;

import java.util.List;

import models.Apartamento;
import models.Bloco;
import models.ExameMedico;
import models.Morador;

import org.apache.commons.lang.StringUtils;

import play.data.binding.Binder;
import play.mvc.Before;
import play.mvc.Scope;
import play.mvc.With;

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
		List<Bloco> blocos = Bloco.list();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
		templateBinding.data.put("blocos", blocos);

		String id = params.get("apartamento");
		String bloco_id = params.get("bloco");
		if (!StringUtils.isBlank(id)) {
			Apartamento apartamento = Apartamento.findById(Long.parseLong(id));
			params.put("bloco", apartamento.bloco.id.toString());
			templateBinding.data.put("apartamentos", Apartamento.listByBloco(apartamento.bloco));
		} else if (!StringUtils.isBlank(bloco_id)) {
			templateBinding.data.put("apartamentos", Apartamento.listByBloco((Bloco)Bloco.findById(Long.parseLong(bloco_id))));
		}
	}
	
	public static void list() {
		
		List<Morador> moradores = Morador.find(LIST_ALL).fetch();
		render(moradores);
		
	}
	
	public static void pesquisar(Long bloco, Long apartamento) {

		String QUERY = "";
		List<Morador> moradores = null;
		if (bloco != null && bloco > 0L
				&& (apartamento == null || apartamento == 0L)) {
			QUERY = " select m from Morador m "
					+ " left join m.exameMedico e  "
					+ " where (  exists ( select ee from Escritura ee  inner join ee.apartamento a inner join a.bloco b"
					+ "  					where ee.proprietario = m  "
					+ "					  and b.id = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a inner join a.bloco b "
					+ "  		     where cc.inquilino = m  "
					+ "			   and b.id = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()) or  "
					+ "  exists ( select dd from Dependente dd inner join dd.morador mm "
					+ "  where dd = m "
					+ "    and (exists ( select ee from Escritura ee  inner join ee.apartamento a inner join a.bloco b"
					+ "  					where ee.proprietario = mm  "
					+ "					  and b.id = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a inner join a.bloco b "
					+ "  		     where cc.inquilino = mm  "
					+ "			   and b.id = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()))"
					+ ") )		  "
					+ "  order by m.nomeCompleto asc ";
			moradores = Morador.find(QUERY, bloco, bloco, bloco, bloco).fetch();
		} else if (apartamento != null && apartamento > 0L) {
			QUERY = " select m from Morador m "
					+ " left join m.exameMedico e  "
					+ " where (  exists ( select ee from Escritura ee  inner join ee.apartamento a "
					+ "  					where ee.proprietario = m  "
					+ "					  and a.id = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a "
					+ "  		     where cc.inquilino = m  "
					+ "			   and a.id = ? 		 "
					+ "  		   and cc.dataInicioContrato <= CURRENT_DATE() AND COALESCE(cc.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()) or  "
					+ "  exists ( select dd from Dependente dd inner join dd.morador mm "
					+ "  where dd = m "
					+ "    and (exists ( select ee from Escritura ee  inner join ee.apartamento a"
					+ "  					where ee.proprietario = mm  "
					+ "					  and a.id = ? "
					+ "   				  and ee.dataEntrada <= CURRENT_DATE() AND COALESCE(ee.dataSaida,CURRENT_DATE()) >= CURRENT_DATE() ) or  "
					+ "  exists ( select cc from ContratoLocacao cc  inner join cc.apartamento a "
					+ "  		     where cc.inquilino = mm  "
					+ "			       and a.id = ? 		 "
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
