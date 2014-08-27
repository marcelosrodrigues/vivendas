package utils.validators;

import models.Bloco;

public class BlocoValidator extends AbstractValidator<Bloco> {

	public BlocoValidator(final ValidatorFactory factory) {
		super(factory);
	}

	@Override
	public Validator validate(Bloco bloco) {
		
		if( bloco.id == null || bloco.id == 0L ) {
    		factory.addError("bloco", "Bloco é obrigatório");
    	}
		
		return this;
	}

}
