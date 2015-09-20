package de.schaefer.mdbpmn.parser;

import java.util.HashMap;
import java.util.Map;

public class FormDescription extends FormItem {

	public FormDescription() {
		super(FormItemType.DESCRIPTION);
	}

	//		   <lang  , value>
	private Map<String, String> labels = new HashMap<String, String>();

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
