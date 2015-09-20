package de.schaefer.mdbpmn.parser;

public enum Type {
	
	STRING("text"),
	BYTE("number"),
	SHORT("number"),
	INTEGER("number"),
	LONG("number"),
	FLOAT("number"),
	DOUBLE("number"),
	DATE("date"),
	BOOLEAN("checkbox"),
	ENUM("select"),
	LIST(""),
	MAP(""),
	PROCESS("");
	
	private String standardHtmlType;
	
	Type(String standardHtmlType) {
		this.standardHtmlType = standardHtmlType;
	}

	public String getStandardHtmlType() {
		return standardHtmlType;
	}

}
