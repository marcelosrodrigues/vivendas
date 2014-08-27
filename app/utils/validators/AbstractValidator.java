package utils.validators;


abstract class AbstractValidator<E> implements Validator<E>{

	protected final ValidatorFactory factory;

	public AbstractValidator(ValidatorFactory factory) {
		this.factory = factory;
	}

	@Override
	public Validator and(final Object object) {
		Validator validator =  factory.validate(object);
		return validator.validate(object);
	}

}