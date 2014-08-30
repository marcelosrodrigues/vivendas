package utils.validators;

public class EmailExistsValidator extends AbstractValidatorByField {

	public EmailExistsValidator(ValidatorFactory factory) {
		super(factory);		
	}

	@Override
	protected String getErrorMessage() {
		return "E-mail informado já existe";
	}

}
