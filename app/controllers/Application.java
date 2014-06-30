package controllers;

import java.util.List;

import models.Apartamento;
import models.Bloco;
import models.Morador;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import play.mvc.Scope;
import services.MailService;


public class Application extends Controller {
	
    public static void index() {
        render();
    }

    
    public static void liberar() {
    	
    	List<Bloco> blocos = Bloco.list();
		Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
        templateBinding.data.put("blocos", blocos);
        
        String id = params.get("apartamento");
        String bloco_id = params.get("bloco");
        if( !StringUtils.isBlank(id) ) {
        	Apartamento apartamento = Apartamento.findById(Long.parseLong(id));
        	params.put("bloco", apartamento.bloco.id.toString());
			templateBinding.data.put("apartamentos", Apartamento.listByBloco(apartamento.bloco));
        } else if( !StringUtils.isBlank(bloco_id) ) {
			templateBinding.data.put("apartamentos", Apartamento.listByBloco((Bloco)Bloco.findById(Long.parseLong(bloco_id))));
        }
    	
        templateBinding.data.put("morador", new Morador());
    	render("Moradores/email.html");
    }
    
    public static void salvar(Long apartamento , final Morador morador) {
    
    		Morador atual = null;
    		if( morador.id > 0) {
    			atual = Morador.findById(morador.id);
    		} else {
    			final Apartamento apto = Apartamento.find("where id = ? ", apartamento).first();
    			atual = apto.getMorador();
    		}
    		
    		atual.email = morador.email;
    		atual.nomeCompleto = morador.nomeCompleto;
    		atual.cpf = morador.cpf;    		
    		atual.bloqueado = false;
    		
    		atual.save();
    		
    		MailService mail = new MailService();
    		mail.from("cvparque@ig.com.br")
    			.to(atual.email)
    			.subject("Bem-vindo ao Portal Condominio Vivendas do Parque")
    			.message("Olá, %s! Seja bem-vindo ao Portal Condominio Vivendas do Parque\r\n. Segue abaixo a sua senha de acesso \r\n\r\n %s",morador.nomeCompleto,morador.password)
    			.send();
    		
    		mail.from("cvparque@ig.com.br")
    			.to("cvparque@ig.com.br")
    			.subject("O morador %s solicitou liberação de acesso" , morador.nomeCompleto)
    			.message("O morador %s solicitou liberação de acesso" , morador.nomeCompleto)
    			.send();
    		
    		flash.success("Olá, %s! Seja bem-vindo ao Portal Condominio Vivendas do Parque\r\n. Enviamos para o e-mail que informaste os dados necessários para acessar o Portal. Em caso de duvidas, entre em contato com a Administração.",atual.nomeCompleto);
    		render("Application/index.html");
    	
    }
}