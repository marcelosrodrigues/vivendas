package utils.validators;


public interface Validator<E>
{
	Validator validate(E e);

	Validator and(Object object);
}
