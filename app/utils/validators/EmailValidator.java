package utils.validators;


public class EmailValidator extends AbstractValidatorByField {

	public EmailValidator(ValidatorFactory factory) {
		super(factory);
	}

	@Override
	protected String getErrorMessage() {
		return "E-mail inválido";
	}

}
