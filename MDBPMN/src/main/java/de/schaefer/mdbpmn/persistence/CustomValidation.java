package de.schaefer.mdbpmn.persistence;

import java.util.List;
import java.util.Map;

public interface CustomValidation {

	public List<String> validate(Object object, String language);
	
	public List<String> validateVariables(Map<String, Object> variables, String language);
}
