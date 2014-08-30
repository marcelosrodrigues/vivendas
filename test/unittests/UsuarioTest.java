package unittests;

import models.Usuario;

import org.junit.Test;

import play.test.UnitTest;

public class UsuarioTest extends UnitTest {

	@Test
	public void pesquisarUsuarioPorEmail() {
	
		Usuario usuario = Usuario.getByEmail("marcelosrodrigues@globo.com");
		assertNotNull(usuario);
		
	}

}
