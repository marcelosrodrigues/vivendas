package controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import java.util.Iterator;

import models.Boleto;
import play.mvc.Controller;

public class Relatorios extends Controller {

	@Check("FINANCEIRO")
	public static void inadimplentes() {
		
		List<Map> inadimplentes = Boleto.findInadimplentes();
		BigDecimal total = new BigDecimal(0);
		for(Iterator iter = inadimplentes.iterator() ; iter.hasNext();) {
			Object[] o = (Object[]) iter.next();
			total = total.add((BigDecimal)o[2]);
		}
		
		render(inadimplentes,total);
		
	}
	
	public static void detalharInadimplentes(String numero , String bloco ) {
		List<Boleto> boletos = Boleto.findBoletosAbertos(bloco, numero);
		
		BigDecimal total = new BigDecimal(0);
		for(Boleto boleto : boletos){
			total = total.add(boleto.valor);
		}
		
		render(boletos,total);
	}
	
}
