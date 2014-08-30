package utils.validators.dto;

import models.Morador;

public class ExistsByCPFValid extends AbstractValidatorByValidateFieldType {

	public ExistsByCPFValid(String field, Morador value) {
		super(field, value);		
	}

	@Override
	public boolean isValid() {
		
		Morador morador = (Morador) this.getValue();
		
		if( Morador.exists(morador.cpf) ){
			
			Morador existed = Morador.getByCPF(morador.cpf);
			return (existed.equals(morador));
			
		} else {
			return true;
		}
		
	}

}
