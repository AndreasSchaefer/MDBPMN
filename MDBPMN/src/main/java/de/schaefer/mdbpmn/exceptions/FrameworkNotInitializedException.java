package de.schaefer.mdbpmn.exceptions;

public class FrameworkNotInitializedException extends Exception {
	private final static String MESSAGE = "Framework not initialized!";
    
	public FrameworkNotInitializedException() {
        super(MESSAGE);
    }
	
	public FrameworkNotInitializedException(final Exception e) {
        super(MESSAGE, e);
    }

}
