package utils.validators;

import utils.validators.dto.EmailIsValid;

public class EmailValidator extends AbstractValidator<EmailIsValid> {

	public EmailValidator(ValidatorFactory factory) {
		super(factory);
	}

	@Override
	public Validator validate(EmailIsValid e) {
		if( !e.isValid() ) {
			factory.addError(e.getField(),"E-mail inválido");
		}
		return this;
	}

}
