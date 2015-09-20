package de.schaefer.mdbpmn.exceptions;

public class TaskNotFoundException extends Exception {
	
	final private static String MESSAGE = "Task could not be found!";
    
	public TaskNotFoundException() {
        super(MESSAGE);
    }

	//Constructor that accepts a message
    public TaskNotFoundException(final Exception exception) {
      super(MESSAGE, exception);
    }
}
