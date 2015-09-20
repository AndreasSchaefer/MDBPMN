package de.schaefer.mdbpmn.parser;

import java.util.HashMap;
import java.util.Map;

public class FormField extends FormItem {

	private Type type;
	private String definition;
	private String clazz;
	private Type keyType;
	private Type valueType;
	
	// 			<lang , value>
	private Map<String, String> labels = new HashMap<String, String>();
	
	//			<const, config>
	private Map<String, String> validation = new HashMap<String, String>();
	
	public FormField() {
		super(FormItemType.FIELD);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	
	public Map<String, String> getValidation() {
		return validation;
	}

	public void setValidation(Map<String, String> validation) {
		this.validation = validation;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public Type getKeyType() {
		return keyType;
	}

	public void setKeyType(Type keyType) {
		this.keyType = keyType;
	}

	public Type getValueType() {
		return valueType;
	}

	public void setValueType(Type valueType) {
		this.valueType = valueType;
	}
	
}
