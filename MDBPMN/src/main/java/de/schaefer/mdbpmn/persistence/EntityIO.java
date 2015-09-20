package de.schaefer.mdbpmn.persistence;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class EntityIO {

	
	/**
	 * Recursive Method to get all Fields from a Java Class.
	 * 
	 * @param fields Empty List of Type Field
	 * @param entity
	 * @return
	 */
	public static List<Field> getAllFields(List<Field> fields, Class<?> entity) {
		fields.addAll(Arrays.asList(entity.getDeclaredFields()));
		if (entity.getSuperclass() != null)
			fields = getAllFields(fields, entity.getSuperclass());
		return fields;
	}
}
