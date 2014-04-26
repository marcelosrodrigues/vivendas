package controllers;

import java.math.BigDecimal;
import java.util.List;

import models.Boleto;
import models.Dependente;
import models.GrauParentesco;
import models.Morador;
import models.Usuario;

import org.apache.commons.lang.StringUtils;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import services.MailService;

@With(Secure.class)
public class Usuarios  extends Controller {

	public static void abrir() {
		
		Usuario usuario = LoginController.getUserAuthenticated();
		Morador morador = Morador.findById(usuario.id);
		
		
		render(morador);		
	}
	
	@Before(only={"salvar"})
	public static void validate() {
		
		Usuario usuario = Usuario.getByEmail(params.get("morador.email"));
		
		if( usuario !=null && usuario.id != Long.parseLong(params.get("morador.id")) ) {
			validation.addError("morador.email", "E-mail já em uso");
			renderArgs
					.put("error",
							"Não foi possível salvar os seus dados pois o E-mail informado já esta cadastrado no sistema");
		} else {
			Morador morador = Morador.getByCPF(params.get("morador.id"));
			if( morador != null && morador.id != Long.parseLong(params.get("morador.id"))){
				validation.addError("morador.cpf", "CPF já em uso");
				renderArgs
						.put("error",
								"Não foi possível salvar os seus dados pois o CPF informado já esta cadastrado no sistema");
			}
		}
		
	}
	
	public static void salvar(final Morador morador) {
		
		validation.valid(morador);
		if( validation.hasErrors() ) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			render("Usuarios/abrir.html",morador);
		} else {
			morador.save();
			session.put("username", morador.email);
			flash.success("Sua atualização foi encaminhada para Administração avaliar. Obrigado por sua atualização.");
			MailService mail = new MailService();
			mail.from("cvparque@ig.com.br")
				.to("cvparque@ig.com.br")
				.subject("O morador %s atualizou o seu registro. Por favor verifique." , morador.nomeCompleto)
				.message("O morador %s atualizou o seu registro. Por favor verifique.\r\nCPF: %s" , morador.nomeCompleto,morador.cpf)
				.send();
			
			render("Application/index.html");
		}
		
	}
	
	public static void dependentes() {
		
		Morador parente = (Morador) LoginController.getUserAuthenticated();
		
		List<Dependente> dependentes = Dependente
				.find("SELECT d from Dependente d join d.morador m where m.id = ? order by d.grauParentesco.nome asc, d.nomeCompleto",
						parente.id).fetch();
		render("Dependentes/search.html",dependentes, parente);
		
	}
	
	public static void dependente() {
		Morador parente = (Morador) LoginController.getUserAuthenticated();
		Dependente object = new Dependente();
		object.grauParentesco = new GrauParentesco();
		object.morador = parente;

		render("Dependentes/blank.html", parente, object);
	}
	
	public static void boletos() {
		Morador morador = (Morador) LoginController.getUserAuthenticated();
		List<Boleto> boletos = Boleto.findBoletosAbertos(morador);
		
		BigDecimal total = new BigDecimal(0);
		for(Boleto boleto : boletos ) {
			total = total.add(boleto.valor);
		}
		render(boletos,total);
	}

	public static void abrir_trocar_senha() {
		render("Usuarios/trocarsenha.html");
	}
	
	public static void trocarsenha(String senhaAntiga , String novaSenha , String confirmacaoSenha) {
		
		
		Usuario usuario = LoginController.getUserAuthenticated();
		
		if( StringUtils.isBlank(senhaAntiga) ) {
			validation.addError("senhaAntiga", "Senha atual é obrigatória");
		} else if( !usuario.password.equalsIgnoreCase(senhaAntiga) ) {
			validation.addError("senhaAntiga", "Senha antiga não confere");
		} 
		
		if( StringUtils.isBlank(novaSenha) ){
			validation.addError("novaSenha", "Nova senha é obrigatória");
		}
		
		if( StringUtils.isBlank(confirmacaoSenha) ){
			validation.addError("confirmacaoSenha", "Confirmação da senha é obrigatória");
		}
		
		if( !StringUtils.isBlank(novaSenha) && !StringUtils.isBlank(confirmacaoSenha) && !novaSenha.equalsIgnoreCase(confirmacaoSenha)){
			validation.addError("confirmacaoSenha", "Não confere a nova senha com a confirmação");
		}
		
		if( validation.hasErrors() ){
			renderArgs
			.put("error",
					"Não foi possível trocar a sua senha");
			render("Usuarios/trocarsenha.html");
		} else {
			usuario.password = novaSenha;
			usuario.trocasenha = false;
			usuario.save();
			
			MailService mail = new MailService();
    		mail.from("cvparque@ig.com.br")
    			.to(usuario.email)
    			.subject("Troca efetuada com sucesso")
    			.message("Sua senha foi trocada com sucesso.\r\n\r\nSegue a sua nova senha %s\r\n\r\nPortal Condominio Vivendas do Parque\r\n.",usuario.password)
    			.send();
			
			flash.success("Senha trocada com sucesso");
			render("Application/index.html");
		}
	}
	
}
