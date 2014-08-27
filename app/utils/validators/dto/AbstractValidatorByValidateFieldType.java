package utils.validators.dto;


abstract class AbstractValidatorByValidateFieldType implements ValidateField {
	
	private final Object value;
	private final String field;

	public AbstractValidatorByValidateFieldType(final String field , final Object value ) {
		this.field = field;
		this.value = value;
	}
	
	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getField() {
		return field;
	}

}
