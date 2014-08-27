package utils.validators;

import models.Apartamento;

public class ApartamentoValidator extends AbstractValidator<Apartamento> {

	public ApartamentoValidator(final ValidatorFactory factory) {
		super(factory);
	}

	@Override
	public Validator validate(final Apartamento apartamento) {
		
		if( apartamento.id == null || apartamento.id == 0L ) {
    		factory.addError("apartamento", "Apartamento é obrigatório");
    	}
		return this;
		
	}

}
