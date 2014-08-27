package controllers;

import models.Apartamento;
import models.Bloco;
import models.Morador;
import play.Logger;
import play.data.binding.Binder;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope;
import services.MailService;
import utils.CommandFactory;
import utils.Constante;
import utils.validators.ValidatorFactory;
import utils.validators.dto.EmailIsValid;


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
	    templateBinding.data.put("object", new Morador());
    	render("Moradores/email.html");
    }
    
    @Before(only={"salvar"},priority=0)
    static void valide() {
    	
    	Logger.debug("validando a liberação de acesso ao sistema");
    	
    	Morador object = new Morador();
    	Apartamento apartamento = new Apartamento();
    	Bloco bloco = new Bloco();
    	Binder.bindBean(params.getRootParamNode(),"object", object);
    	Binder.bindBean(params.getRootParamNode(),"apartamento", apartamento);
    	Binder.bindBean(params.getRootParamNode(),"bloco", bloco);    	
    	    	
    	ValidatorFactory validations = ValidatorFactory.getInstance();
    	
    	validations.validate(bloco)
    					.and(apartamento)
    					.and(new EmailIsValid("object.email", object.email));
        	
    	if( validations.hasErrors() ){
    		for( play.data.validation.Error error : validations ){
    			validation.addError(error.getKey(), error.message());
    		}
    		renderArgs.put("error",
					"Não foi possível gerar uma senha. Não foi informado nenhum e-mail válido");
    		render("Moradores/email.html",object);
    	}
    	
    }
    
    public static void salvar(Long apartamento , final Morador object) {
    
    		Morador atual = null;
    		if( object.id > 0) {
    			atual = Morador.findById(object.id);
    		} else {
    			final Apartamento apto = Apartamento.findById(apartamento);
    			atual = apto.getMorador();
    		}
    		
    		atual.email = object.email;
    		atual.nomeCompleto = object.nomeCompleto;
    		atual.cpf = object.cpf;    		
    		atual.bloqueado = false;
    		
    		atual.save();
    		
    		MailService mail = new MailService();
    		mail.from("cvparque@ig.com.br")
    			.to(atual.email)
    			.subject("Bem-vindo ao Portal Condominio Vivendas do Parque")
    			.message("Olá, %s! Seja bem-vindo ao Portal Condominio Vivendas do Parque\r\n. Segue abaixo a sua senha de acesso \r\n\r\n %s",object.nomeCompleto,object.password)
    			.send();
    		
    		mail.from("cvparque@ig.com.br")
    			.to("cvparque@ig.com.br")
    			.subject("O morador %s solicitou liberação de acesso" , object.nomeCompleto)
    			.message("O morador %s solicitou liberação de acesso" , object.nomeCompleto)
    			.send();
    		
    		flash.success("Olá, %s! Seja bem-vindo ao Portal Condominio Vivendas do Parque\r\n. Enviamos para o e-mail que informaste os dados necessários para acessar o Portal. Em caso de duvidas, entre em contato com a Administração.",atual.nomeCompleto);
    		render("Application/index.html");
    	
    }
}