package de.schaefer.beispiel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.schaefer.mdbpmn.persistence.CustomValidation;

public class ExampleValidation implements CustomValidation {

	@Override
	public List<String> validate(Object object, String language) {
		// return empty list
		return new ArrayList<String>();
	}

	@Override
	public List<String> validateVariables(Map<String, Object> variables, String language) {
		List<String> retVal = new ArrayList<String>();
		if (variables.containsKey("startDate") && variables.containsKey("endDate")) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date startDate = formatter.parse(variables.get("startDate").toString());
				Date endDate = formatter.parse(variables.get("endDate").toString());
				if (endDate.before(startDate))
					if (language.toUpperCase().equals("DE"))
						retVal.add("PROCESS.endDate: Das Enddatum liegt vor dem Startdatum");
					else
						retVal.add("PROCESS.endDate: The Endate is earlier than the Startdate");
			} catch (ParseException e) {
				if (language.toUpperCase().equals("DE"))
					retVal.add("PROCESS.startDate: Start oder Enddatum können nicht geparst werden");
				else
					retVal.add("PROCESS.startDate: Can not parse Startdate oder Enddate");
			}
		}
		return retVal;
	}

}
