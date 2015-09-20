package de.schaefer.mdbpmn.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.schaefer.mdbpmn.annotations.Constraint;
import de.schaefer.mdbpmn.annotations.Label;
import de.schaefer.mdbpmn.annotations.Labels;
import de.schaefer.mdbpmn.annotations.Properties;
import de.schaefer.mdbpmn.annotations.Property;
import de.schaefer.mdbpmn.annotations.Validation;
import de.schaefer.mdbpmn.persistence.EntityIO;

public class AnnotationsParser extends EntityIO {

	final private Class<?> clazz;
	final private List<Field> fields;
	final private Map<String, Field> fieldsMap = new HashMap<String, Field>();
	
	public AnnotationsParser(Class<?> clazz) {
		this.clazz = clazz;
		fields = getAllFields(new ArrayList<Field>(), clazz);
		for (Field field: fields) {
			fieldsMap.put(field.getName(), field);
		}
	}
	
	/**
	 * Get the the 
	 * @return Class
	 */
	public Class<?> getParsingClass() {
		return clazz;
	}
	
	public boolean cotainsField(String name) {
		return fieldsMap.containsKey(name);
	}
	
	public FormField getFormField(String name) {
		return parseFormField(fieldsMap.get(name));
	}
	
	public Map<String, FormField> getAllFormFields() {
		Map<String, FormField> retVal = new HashMap<String, FormField>();
		for (Entry<String, Field> entry: fieldsMap.entrySet()) {
			FormField formField = parseFormField(entry.getValue());
			retVal.put(entry.getKey(), formField);
		}
		return retVal;
	}
	
	private FormField parseFormField(Field field) {
		FormField formField = new FormField();
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation: annotations) {

			if (annotation.annotationType() == Labels.class) {
				Labels labels = (Labels)annotation;
				for (Label label: labels.value()) {
					formField.getLabels().put(label.language().toUpperCase(), label.value());
				}
			}
			else if (annotation.annotationType() == Validation.class) {
				Validation validation = (Validation)annotation;
				for (Constraint constraint: validation.value()) {
					formField.getValidation().put(constraint.name().toUpperCase(), constraint.config());
				}
			}
			else if (annotation.annotationType() == Properties.class) {
				Properties properties = (Properties)annotation;
				for (Property property: properties.value()) {
					formField.getProperties().put(property.id(), property.value());
				}
			}
		}
		return formField;
	}
}

