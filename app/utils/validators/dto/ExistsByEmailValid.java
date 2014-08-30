package utils.validators.dto;

import models.Usuario;

public class ExistsByEmailValid extends AbstractValidatorByValidateFieldType {

	public ExistsByEmailValid(String field, Usuario value) {
		super(field, value);		
	}

	@Override
	public boolean isValid() {
		
		Usuario usuario = (Usuario) this.getValue();
		
		if( Usuario.exists(usuario.email) ){
			
			Usuario existed = Usuario.getByEmail(usuario.email);
			return (existed.equals(usuario));
			
		} else {
			return true;
		}
		
	}

}
