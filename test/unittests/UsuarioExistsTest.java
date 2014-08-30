package unittests;

import models.Usuario;

import org.junit.Test;

import play.test.UnitTest;
import utils.validators.dto.ExistsByEmailValid;

public class UsuarioExistsTest extends UnitTest{

	@Test
	public void naoDeveExistir() {
		ExistsByEmailValid validate = new ExistsByEmailValid(null, new Usuario("aaa@bbb.ccc"));
		assertTrue(validate.isValid());
	}
	
	@Test
	public void deveExistirESerValido() {
		
		Usuario usuario = Usuario.getByEmail("marcelosrodrigues@globo.com");
		ExistsByEmailValid validate = new ExistsByEmailValid(null, usuario);
		assertTrue(validate.isValid());
	}

	@Test
	public void deveExistirESerInvalido() {
		ExistsByEmailValid validate = new ExistsByEmailValid(null, new Usuario("marcelosrodrigues@globo.com"));
		assertFalse(validate.isValid());
	}
}
