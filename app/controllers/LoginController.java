package controllers;

import java.util.List;

import play.data.validation.Required;

import exceptions.UserBlockedException;
import exceptions.UserNotFoundException;
import models.Grupo;
import models.Usuario;
import services.UsuarioService;


public class LoginController extends Secure.Security{

	static boolean authentify(String username, String password)  {
		return authenticate(username, password);
	}

	static boolean authenticate(String username, String password)  {
        try {
			UsuarioService service = new UsuarioService();
			Usuario usuario = service.autenticar(username, password);
			
			return usuario != null;
			
		} catch (UserNotFoundException e) {
			renderArgs.put("error", e.getMessage());	
			return false;
		} catch (UserBlockedException e) {
			renderArgs.put("error", e.getMessage());	
			return false;
		}
    }
	
	public static Usuario getUserAuthenticated() {
		return Usuario.getByEmail(LoginController.connected());
	}
	
	static boolean check(String profile) {

		List<Grupo> grupos = Grupo.find("SELECT g from Grupo inner join g.membros m WHERE m.email = ? AND nome = ?", LoginController.connected(),profile).fetch();
		return grupos!=null && !grupos.isEmpty(); 
		
    }
	
}
