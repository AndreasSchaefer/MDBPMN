package de.schaefer.mdbpmn.exceptions;

public class ProcessInstanceNotFoundException extends Exception {
	
	final private static String MESSAGE = "ProcessInstance could not be found!";
    
	public ProcessInstanceNotFoundException() {
        super(MESSAGE);
    }

	//Constructor that accepts a message
    public ProcessInstanceNotFoundException(final Exception exception) {
      super(MESSAGE, exception);
    }
}
