package controllers;

import java.util.List;

import models.Grupo;
import models.Usuario;
import play.data.validation.Required;
import services.UsuarioService;
import exceptions.UserBlockedException;
import exceptions.UserNotFoundException;


public class LoginController extends Secure.Security{

	static boolean authentify(String username, String password)  {
		return authenticate(username, password);
	}

	static boolean authenticate(@Required String username, @Required String password)  {
        try {
              	
			UsuarioService service = new UsuarioService();
			Usuario usuario = service.autenticar(username, password);
			
			return ( usuario != null );
			
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

		List<Grupo> grupos = Grupo.find("SELECT g from Grupo as g inner join g.usuarios as m WHERE m.email = ? AND nome = ?", LoginController.connected(),profile).fetch();
		return grupos!=null && !grupos.isEmpty(); 
		
    }
	
	static boolean isConnected() {		
		return session.contains("username");
	}
	
}
