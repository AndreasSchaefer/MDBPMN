package de.schaefer.mdbpmn.persistence;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntityReader extends EntityIO {
	
	private Map<String, Object> objectsMap = new HashMap<String, Object>();
	private Map<String, List<Field>> fieldsMap = new HashMap<String, List<Field>>();
	private Map<String, Object> processVariables;

	public EntityReader(List<Object> objects, Map<String, Object> processVariables) {
		this.processVariables = processVariables;
		for (Object object: objects) {
			Class<?> clazz = object.getClass();
			System.out.println(clazz.getName());
			objectsMap.put(clazz.getName(), object);
			fieldsMap.put(clazz.getName(), getAllFields(new ArrayList<Field>(), clazz));
		}
	}
	
	/**
	 * Get a Field Value As String
	 * 
	 * @param className
	 * @param fieldName
	 * @return
	 */
	public String getFieldValueAsString(String className, String fieldName) {
		if (className.equals("PROCESS")) {
			if (processVariables.containsKey(fieldName))
				return processVariables.get(fieldName).toString();
			else 
				return "";
		} else {
			List<Field> fields = fieldsMap.get(className);
			Object instance = objectsMap.get(className);
			for (Field field: fields) {
				if (field.getName().equals(fieldName)) {
					try {
					field.setAccessible(true);
					Object value = field.get(instance);
					if (value == null)
						return "";
					if (field.getType() == Date.class) {
						Date date = (Date)field.get(instance);
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						return formatter.format(date);
					} else {
						return  field.get(instance).toString();
					}
					} catch (Exception e) {e.printStackTrace();}
				}
			}
		}
		return "ERROR";
	}
	
	/**
	 * Get Values of a List Field as a List of Strings
	 * 
	 * @param className
	 * @param fieldName
	 * @return
	 */
	public List<String> getListValuesAsString(String className, String fieldName) {
		List<Field> fields = fieldsMap.get(className);
		Object instance = objectsMap.get(className);
		for (Field field: fields) {
			if (field.getName().equals(fieldName)) {
				try {
					field.setAccessible(true);
					List<?> values = (List<?>) field.get(instance);
					if (values == null)
						return null;
					else {
						List<String> stringList = new ArrayList<String>();
						for (Object object: values) {
							String valueStr;
						if (object.getClass() == Date.class) {
							Date date = (Date)object;
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							valueStr = formatter.format(date);
						} else {
							valueStr = object.toString();
						}
							stringList.add(valueStr);
						}
						return stringList;
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		return null;
	}
	
	/**
	 * Get the Values of a Map as a Map of Types <String, String>
	 * 
	 * @param className
	 * @param fieldName
	 * @return
	 */
	public Map<String, String> getMapValuesAsString(String className, String fieldName) {
		List<Field> fields = fieldsMap.get(className);
		Object instance = objectsMap.get(className);
		for (Field field: fields) {
			if (field.getName().equals(fieldName)) {
				try {
					field.setAccessible(true);
					Map<?,?> values = (Map<?,?>) field.get(instance);
					if (values == null)
						return null;
					else {
						Map<String, String> stringMap = new HashMap<String, String>();
						for (Entry<?, ?> e : values.entrySet()) {
							String value;
							if (e.getValue() instanceof Date) {
								Date date = (Date)e.getValue();
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								value = formatter.format(date);
							} else {
								value = e.getValue().toString();
							}
							stringMap.put(e.getKey().toString(), value);
						}
						return stringMap;
					}
				} catch (Exception e) {e.printStackTrace();}
			}
		}
		return null;
	}
	
	public boolean containsClass(String classname) {
		if (classname.equals("PROCESS"))
			return true;
		return fieldsMap.containsKey(classname);
	}
	
}