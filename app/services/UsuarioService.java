package services;

import java.io.Serializable;

import models.Usuario;

import org.apache.log4j.Logger;

import exceptions.UserBlockedException;
import exceptions.UserNotFoundException;

public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(UsuarioService.class);
	
	public Usuario autenticar(String email, String password) throws UserNotFoundException, UserBlockedException {
		
		LOGGER.debug(String.format("Iniciando a autenticação do usuário pelo email %s",email));
		
		
			Usuario usuario = Usuario.getByEmail(email);
			
			if( LOGGER.isTraceEnabled() ) {
				LOGGER.trace(String.format("Usuario localizado %s",usuario));
			}
			
			LOGGER.debug(String.format("Usuario %s não encontrado",email));
			if( usuario == null ) {
				throw new UserNotFoundException(String.format("Usuario %s não encontrado ou senha inválida",email));
			}
			
			if( usuario.isBloqueado() ) {
				throw new UserBlockedException(String.format("Usuario %s bloueado. Entre em contato com o Administrador do sistema",email));
			}
			
			if( password.equalsIgnoreCase(usuario.getPassword())  ){
				usuario.liberarTentativas();
				usuario.save();
				return usuario;			
			} else {
				usuario.aumentarNumeroTentativasFalhas();
				usuario.save();
				if( usuario.isBloqueado() ) {					
					throw new UserBlockedException(String.format("Usuario %s bloqueado. Entre em contato com o Administrador do sistema",email));
				}
				
				throw new UserNotFoundException(String.format("Usuario %s não encontrado ou senha inválida",email));
			}
		
	}
	

}
