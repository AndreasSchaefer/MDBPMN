package de.schaefer.mdbpmn.exceptions;

public class MDBPMN_DAOException extends Exception {
	
	final private static String MESSAGE = "The persistence in DAO failed";
    
	public MDBPMN_DAOException() {
        super(MESSAGE);
    }

	//Constructor that accepts a message
    public MDBPMN_DAOException(final Exception exception) {
      super(MESSAGE, exception);
    }
}
