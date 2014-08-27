package utils.validators;

import utils.validators.dto.GreaterThanValid;

public class GreaterThanValidator extends AbstractValidator<GreaterThanValid> {

	public GreaterThanValidator(ValidatorFactory factory) {
		super(factory);	
	}

	@Override
	public Validator validate(GreaterThanValid e) {
		if( !e.isValid() ) {
			factory.addError(e.getField(),"Valor inválido");
		}
		return this;
	}

}
