package de.schaefer.mdbpmn.exceptions;

public class ProcessDefinitionNotFoundException extends Exception {
	
	final private static String MESSAGE = "Process Definition could not be found!";
    
	public ProcessDefinitionNotFoundException() {
        super(MESSAGE);
    }

	//Constructor that accepts a message
    public ProcessDefinitionNotFoundException(final Exception exception) {
      super(MESSAGE, exception);
    }
}
