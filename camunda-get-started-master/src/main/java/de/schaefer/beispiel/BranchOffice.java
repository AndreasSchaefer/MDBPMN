package de.schaefer.beispiel;

import java.util.HashMap;

import de.schaefer.mdbpmn.persistence.MDBPMN_Enum;

public enum BranchOffice implements MDBPMN_Enum {

	COLOGNE,
	MUNICH,
	HAMBURG,
	BERLIN;
	
	public static HashMap<String, String> translations = new HashMap<String, String>();
	
	static {
		translations.put("DE:COLOGNE", "Köln");
		translations.put("DE:MUNICH", "München");
		translations.put("DE:HAMBURG", "Hamburg");
		translations.put("DE:BERLIN", "Berlin");
		
		translations.put("EN-US:COLOGNE", "Cologne");
		translations.put("EN-US:MUNICH", "Munich");
		translations.put("EN-US:HAMBURG", "Hamburg");
		translations.put("EN-US:BERLIN", "Berlin");
	}
	
	@Override
	public String getTranslation(String language) {
		language = language.toUpperCase();
		String str = language + ":" + this;
		return translations.get(str);
	}
}
