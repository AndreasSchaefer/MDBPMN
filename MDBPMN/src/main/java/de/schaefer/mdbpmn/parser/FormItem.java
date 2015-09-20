package de.schaefer.mdbpmn.parser;

import java.util.HashMap;
import java.util.Map;

public class FormItem {

	protected String id;
	
	protected final FormItemType formItemType;
	
	//			<prop , value>
	protected Map<String, String> properties = new HashMap<String, String>();
	
	public FormItem(FormItemType formItemType) {
		this.formItemType = formItemType;
	}
	
	public FormItemType getFormItemType() {
		return formItemType;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
