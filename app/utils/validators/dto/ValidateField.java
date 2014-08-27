package utils.validators.dto;

public interface ValidateField {

	public abstract boolean isValid();

	public abstract Object getValue();

	public abstract String getField();

}