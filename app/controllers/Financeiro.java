package controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import models.Acordo;
import models.Apartamento;
import models.Bloco;

import org.joda.time.DateTime;

import play.data.binding.Binder;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import play.mvc.With;
import utils.CommandFactory;
import utils.Constante;

@With(Secure.class)
public class Financeiro extends Controller {

	@Before
    static void listBlocos() {
        final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        CommandFactory.getInstance()
                .get(Constante.BLOCOS,params,templateBinding)
                .execute();
        
   
    }
	
	public static void index() {
		
	    Acordo object = new Acordo();
		object.apartamento = new Apartamento();
		object.apartamento.bloco = new Bloco();
		render(object);
	}
	
	public static void salvar() {
		
		Acordo object = new Acordo();
		Binder.bindBean(params.getRootParamNode(),"object", object);
		DateTime dataLancamento = DateTime.now().plusMonths(1);
		object.dataInicio = dataLancamento.toDate();
		Apartamento apartamento = Apartamento.findById(object.apartamento.id);
		for( long i = 0 ; i < object.quantidade ; i++ ) {
			
			apartamento.fazerLancamento(dataLancamento.toDate(), object.valor.divide(new BigDecimal(object.quantidade),RoundingMode.HALF_UP), String.format("ACORDO %s de %s",i + 1,object.quantidade));
			dataLancamento = dataLancamento.plusMonths(1);
			
		}
		apartamento.save();
		
		object.dataTermino = DateTime.now().plusMonths(object.quantidade).toDate();
		object.save();
		
		flash.success("Lançamento referente ao acordo gerado com sucesso");
		redirect("Financeiro.pesquisar");		
		
	}
	
	public static void pesquisar() {
		
	    Acordo object = new Acordo();
	    Binder.bindBean(params.getRootParamNode(),"object", object);

	    if( object.apartamento == null )
	    	object.apartamento = new Apartamento();
	    
	    if( object.apartamento.bloco == null )
	    	object.apartamento.bloco = new Bloco();
	    
		List<Acordo> acordos = null;
	    if( object.apartamento != null && object.apartamento.id != null && object.apartamento.id > 0L) {
			acordos = Acordo.find("SELECT a FROM Acordo a inner join fetch a.apartamento ap inner join  fetch ap.bloco b WHERE a.apartamento = ? ORDER BY a.dataCriacao ASC", object.apartamento).fetch();
		} else if (object.apartamento != null && object.apartamento.bloco != null && ( object.apartamento.id == null || object.apartamento.id == 0L) && object.apartamento.bloco.id != null && object.apartamento.bloco.id > 0L ){
			acordos = Acordo.find("SELECT a FROM Acordo a inner join fetch a.apartamento ap inner join  fetch ap.bloco b WHERE ap.bloco = ? ORDER BY a.dataCriacao ASC", object.apartamento.bloco).fetch();
		} else {
			acordos = Acordo.find("ORDER by dataCriacao ASC").fetch();
		}
	 
		render(acordos,object);
	}

	
	public static void abrirAcordo(BigDecimal valor , Long apartamento) {
		
		
		Acordo object = new Acordo();
		object.apartamento = Apartamento.findById(apartamento);
		object.valor = valor;
		List<Apartamento> apartamentos = Apartamento.listByBloco(object.apartamento.bloco);
		render("Financeiro/index.html",object,apartamentos);	
	}

}
