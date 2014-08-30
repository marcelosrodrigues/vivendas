package utils.validators;

public class CPFExistsValidator extends AbstractValidatorByField {

	public CPFExistsValidator(ValidatorFactory factory) {
		super(factory);		
	}

	@Override
	protected String getErrorMessage() {
		return "CPF informado já existe";
	}

}
