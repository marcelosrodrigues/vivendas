package controllers;

import models.Apartamento;
import models.Morador;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;

import play.Logger;
import play.data.binding.Binder;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import services.MailService;
import utils.CommandFactory;
import utils.Constante;


public class Application extends Controller {
	
    public static void index() {
        render();
    }

    @Before
    public static void prepare() {
    	
    	Logger.debug("Preparar tela de liberação");
    	
    	final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
	    CommandFactory.getInstance()
	                   .get(Constante.BLOCOS,params,templateBinding)
	                   .execute();
	    
    }
    
    public static void liberar() {
    	
		final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
	    templateBinding.data.put(Constante.MORADOR, new Morador());
    	render("Moradores/email.html");
    }
    
    @Before(only={"salvar"},priority=0)
    static void valide() {
    	
    	Logger.debug("validando a liberação de acesso ao sistema");
    	
    	
    	final Scope.RenderArgs templateBinding = Scope.RenderArgs.current();
    	Morador morador = new Morador();
    	Binder.bindBean(params.getRootParamNode(), Constante.MORADOR, morador);
    	
    	final String email = params.get("morador.email");
    	if( StringUtils.isEmpty(email) ) {
    		validation.addError("morador.email", "E-mail é obrigatório");
			renderArgs
					.put("error",
							"Não foi possível gerar uma senha. Não foi informado nenhum e-mail válido");
			render("Moradores/email.html",morador);
    	} else if( !GenericValidator.isEmail(email)){
    		validation.addError("morador.email", "E-mail inválido");
			renderArgs
					.put("error",
							"Não foi possível gerar uma senha. Não foi informado nenhum e-mail válido");
			render("Moradores/email.html",morador);
    	}
    	
    }
    
    public static void salvar(Long apartamento , final Morador morador) {
    
    		Morador atual = null;
    		if( morador.id > 0) {
    			atual = Morador.findById(morador.id);
    		} else {
    			final Apartamento apto = Apartamento.findById(apartamento);
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