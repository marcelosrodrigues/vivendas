package utils.validators.dto;

import org.apache.commons.validator.GenericValidator;

public class EmailIsValid extends AbstractValidatorByValidateFieldType{
	
	public EmailIsValid(String field, String value) {
		super(field, value);		
	}

	@Override
	public boolean isValid() {
		return !GenericValidator.isBlankOrNull((String)this.getValue()) && GenericValidator.isEmail((String)this.getValue());
	}


}
