package de.schaefer.mdbpmn.exceptions;

public class InitializeException extends Exception {
    
	public InitializeException(String message) {
        super(message);
    }

	//Constructor that accepts a message
    public InitializeException(String message, final Exception exception) {
      super(message, exception);
    }
}
