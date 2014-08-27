package utils.validators.dto;

import java.math.BigDecimal;

public class GreaterThanValid extends AbstractValidatorByValidateFieldType {

	private final BigDecimal compareTo;

	public GreaterThanValid(String field, BigDecimal value , BigDecimal compareTo) {
		super(field, value);
		this.compareTo = compareTo;
	}

	@Override
	public boolean isValid() {
		if( this.compareTo != null && this.getValue() != null ){
			return ((BigDecimal)this.getValue()).compareTo(this.compareTo) == 1;
		} else {			
			return false;
		}
			
	}

}



