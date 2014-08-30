package utils.validators.dto;

import org.apache.commons.validator.GenericValidator;

public class NotBlankValid extends AbstractValidatorByValidateFieldType{
	
	public NotBlankValid(String field, String value) {
		super(field, value);		
	}

	@Override
	public boolean isValid() {
		return !GenericValidator.isBlankOrNull((String)this.getValue());
	}


}
