package utils.validators;

import utils.validators.dto.AbstractValidatorByValidateFieldType;

public abstract class AbstractValidatorByField
		extends
			AbstractValidator {

	public AbstractValidatorByField(ValidatorFactory factory) {
		super(factory);
	}
	
	@Override
	public Validator validate(Object object) {
		final AbstractValidatorByValidateFieldType e = (AbstractValidatorByValidateFieldType) object;
		if( !e.isValid() ) {
			factory.addError(e.getField(),this.getErrorMessage());
		}
		return this;
	}

	protected abstract String getErrorMessage();

}