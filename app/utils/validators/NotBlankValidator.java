package utils.validators;


public class NotBlankValidator extends AbstractValidatorByField {

	public NotBlankValidator(ValidatorFactory factory) {
		super(factory);
	}

	@Override
	protected String getErrorMessage() {
		return "Campo é obrigatório";
	}

}
