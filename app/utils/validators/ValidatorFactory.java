package utils.validators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Apartamento;
import models.Bloco;
import utils.validators.dto.EmailIsValid;
import utils.validators.dto.Error;
import utils.validators.dto.GreaterThanValid;

public final class ValidatorFactory implements Iterable<Error>{
	
	private final List<Error> ERRORS_LIST = new ArrayList<Error>();
	
	public void addError(final String fieldname , final String errorMessage) {
		
		this.ERRORS_LIST.add(new Error(fieldname,errorMessage));
		
	}
	
	public boolean hasErrors() {
		return !ERRORS_LIST.isEmpty();
	}
	
	public static final ValidatorFactory getInstance() {
		return new ValidatorFactory();
	}
	
	public Validator validate(final Object object) {
		
		if( object instanceof Bloco) {
			return (new BlocoValidator(this)).validate((Bloco) object);
		} else if ( object instanceof Apartamento) {
			return (new ApartamentoValidator(this)).validate((Apartamento) object);
		} else if ( object instanceof EmailIsValid ){
			return (new EmailValidator(this)).validate((EmailIsValid) object);
		} else if ( object instanceof GreaterThanValid ){
			return (new GreaterThanValidator(this)).validate((GreaterThanValid) object);
		} else {
			return null;
		}
		
	}
	
	private ValidatorFactory(){}

	@Override
	public Iterator<Error> iterator() {
		return this.ERRORS_LIST.iterator();
	}
	
}
