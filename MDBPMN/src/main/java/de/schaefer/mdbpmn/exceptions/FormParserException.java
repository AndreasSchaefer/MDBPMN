package de.schaefer.mdbpmn.exceptions;

public class FormParserException extends Exception {
	
	final private static String MESSAGE = "Forms in BPMN can not parse. Please check your model!";
    
	public FormParserException() {
        super(MESSAGE);
    }

    public FormParserException(final Exception exception) {
      super(MESSAGE, exception);
    }
}

