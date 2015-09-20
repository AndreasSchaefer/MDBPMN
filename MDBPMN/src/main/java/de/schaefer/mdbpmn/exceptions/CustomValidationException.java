package de.schaefer.mdbpmn.exceptions;

public class CustomValidationException extends Exception {
	
	final private static String MESSAGE = "The custom validation failed";
    
	public CustomValidationException() {
        super(MESSAGE);
    }

	//Constructor that accepts a message
    public CustomValidationException(final Exception exception) {
      super(MESSAGE, exception);
    }
}
