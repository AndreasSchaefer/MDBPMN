package de.schaefer.mdbpmn.persistence;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.annotations.MapToProcess;
import de.schaefer.mdbpmn.exceptions.MDBPMN_DAOException;
import de.schaefer.mdbpmn.parser.FormField;
import de.schaefer.mdbpmn.parser.Type;

public class EntityWriter extends EntityIO {

	// private Constructor to avoid Initializing
	private EntityWriter() {

	}

	/**
	 * Method to write Informations to Entity Object. Validates and Persist the filled Object.
	 * 
	 * @param httpValues
	 * @param classNames
	 * @param processVariables
	 * @param formFieldsMap
	 * @return
	 * @throws Exception
	 */
	public static String writeEntities(Map<String, String[]> httpValues, HashSet<String> classNames, Map<String, Object> processVariables,
			Map<String, FormField> formFieldsMap) throws Exception {
		MDBPMN_DAO dao = MDBPMN_Framework.getFramework().getDAO();
		StringBuffer sb = new StringBuffer();

		HashMap<String, Object> allObjects = new HashMap<String, Object>();
		HashMap<String, Object> persistObjects = new HashMap<String, Object>();
		HashMap<String, List<Field>> fieldsMap = new HashMap<String, List<Field>>();
		HashMap<String, Field> idMap = new HashMap<String, Field>();
		HashMap<String, Field> userTypes = new HashMap<String, Field>();

		List<String> errorMessages = new ArrayList<String>();
		for (String className: classNames) {
			if (className.equals("PROCESS"))
				continue;
			
			Class<?> clazz = Class.forName(className);
			fieldsMap.put(className, getAllFields(new ArrayList<Field>(), clazz));
			List<Field> fields = getAllFields(new ArrayList<Field>(), clazz);

			for (Field field: fields) {
				if (field.getAnnotation(MapToProcess.class) != null)
					idMap.put(className, field);
				if (classNames.contains(field.getType().getName()))
					userTypes.put(field.getType().getName(), field);
			}

			Object instance;
			if (processVariables.containsKey(className)) {
				instance = dao.read(clazz, Integer.parseInt(processVariables.get(className).toString()));
				allObjects.put(className, instance);
				persistObjects.put(className, instance);
			} else if (idMap.containsKey(className)) {
				Constructor<?> constructor = clazz.getConstructor();
				instance = constructor.newInstance();
				allObjects.put(className, instance);
				persistObjects.put(className, instance);
			}
		}
		
		for (Entry<String, Field> entry: userTypes.entrySet()) {
			Field field = entry.getValue();
			String className = entry.getKey();
			field.setAccessible(true);
			String holdingClass = field.getDeclaringClass().getName();
			Object instance = field.get(allObjects.get(holdingClass));
			if (instance == null) {
				Class<?> clazz = Class.forName(entry.getKey());
				Constructor<?> constructor = clazz.getConstructor();
				instance = constructor.newInstance();
				field.set(allObjects.get(holdingClass), instance);
			}
			allObjects.put(className, instance);
		}

		for (Entry<String, FormField> entry: formFieldsMap.entrySet()) {
			FormField formField = entry.getValue();
			String formFieldId = entry.getKey();

			if (httpValues.containsKey(formFieldId)) {
				if (formField.getValidation().containsKey("READONLY")) {
					continue;
				}
				
				if (httpValues.get(formFieldId)[0].isEmpty()) {
					if (formField.getValidation().containsKey("REQUIRED"))
						errorMessages.add(formField.getClazz() + "." + formField.getId() + ": The Field ist required!");
					continue;
				}

				// Validierung
				String errorMessage = null;
				try {
					if ( formField.getType() == Type.BYTE || formField.getType() == Type.SHORT
							|| formField.getType() == Type.INTEGER || formField.getType() == Type.LONG ) {

						Long testValue = Long.parseLong(httpValues.get(formFieldId)[0]);
						errorMessage = isNumberInRange(testValue, formFieldsMap.get(formFieldId));

					} else if (formField.getType() == Type.FLOAT || formField.getType() == Type.DOUBLE) {

						Double testValue = Double.parseDouble(httpValues.get(formFieldId)[0]);
						errorMessage = isDecimalInRange(testValue, formFieldsMap.get(formFieldId));
						
					} else if (formField.getType() == Type.STRING) {
						errorMessage = checkStringLength(httpValues.get(formFieldId)[0], formFieldsMap.get(formFieldId));
						if ( formFieldsMap.get(formFieldId).getValidation().containsKey("REGEX")) {
							String regex = formFieldsMap.get(formFieldId).getValidation().get("REGEX");

							if (!httpValues.get(formFieldId)[0].matches(regex)) {

								if (errorMessage != null)
									errorMessages.add(errorMessage);

								errorMessage = formFieldsMap.get(formFieldId).getClazz() + "." + formFieldsMap.get(formFieldId).getId()
										+ ":Invalid Value!";
							}
						}
					}

					if (errorMessage != null) {
						errorMessages.add(errorMessage);
						continue;
					}
				} catch(Exception e) {
					errorMessages.add(formFieldsMap.get(formFieldId).getClazz() + "." + formFieldsMap.get(formFieldId).getId()
							+ ":Invalid Value (Exception)!");
					continue;
				}

				if (formField.getClazz().equals("PROCESS")) {
					processVariables.put(formField.getId(), httpValues.get(formFieldId)[0]);
				} else {
					writeValue(fieldsMap, formField, allObjects, httpValues.get(formFieldId)[0]);
				}
			} else if (formField.getType() == Type.MAP) {
				HashMap map = new HashMap();
				int i=1;
				while (httpValues.containsKey(formFieldId + "_Key" + i)) {
					String key = httpValues.get(formFieldId + "_Key" + i)[0];
					String value = httpValues.get(formFieldId + "_Value" + i)[0];
					map.put(getValue(formFieldsMap.get(formFieldId).getKeyType(), key), 
							getValue(formFieldsMap.get(formFieldId).getValueType(), value));
					i++;
				}
				writeValue(fieldsMap, formField, allObjects, map);
			} else if (formField.getType() == Type.LIST) {
				List list = new ArrayList();
				int i=1;
				while (httpValues.containsKey(formFieldId + "_Value" + i)) {
					String value = httpValues.get(formFieldId + "_Value" + i)[0];
					list.add(getValue(formFieldsMap.get(formFieldId).getValueType(), value));
					i++;
				}
				writeValue(fieldsMap, formField, allObjects, list);
			} else if (formField.getType() == Type.BOOLEAN && formField.getClazz().equals("PROCESS")) {
				processVariables.put(formField.getId(), "off");
			}
		}

		CustomValidation validator = MDBPMN_Framework.getFramework().getCustomValidaton();
		for (Map.Entry<String, Object> entry : allObjects.entrySet()) {
			if (validator != null) {
				List<String> customValidationErrorMessages = validator.validate(entry.getValue(),  httpValues.get("language")[0]);
				if (customValidationErrorMessages != null)
					errorMessages.addAll(customValidationErrorMessages);
			}
		}
		
		if (validator != null)
			errorMessages.addAll(validator.validateVariables(processVariables, httpValues.get("language")[0]));
		
		List<Object> objectsList = new ArrayList<Object>();
		for (Map.Entry<String, Object> entry : persistObjects.entrySet())
			objectsList.add(entry.getValue());


		if (errorMessages.isEmpty()) {
			try {
				dao.save(objectsList);
			} catch (Exception e) {
				e.printStackTrace();
				throw new MDBPMN_DAOException(e);
			}
			for (Entry<String, Object> entry: processVariables.entrySet()) {
				sb.append(entry.getKey() + ":" + entry.getValue() + ";");
			}
			for (Object object: objectsList) {
				Field idField = idMap.get(object.getClass().getName());
				idField.setAccessible(true);
				sb.append(object.getClass().getName() + ":" + idField.get(object) + ";");
				processVariables.put(object.getClass().getName(), idField.get(object));
			}
		} else {
			sb.append("VALIDATION_ERROR;");
			for (String str: errorMessages) {

				sb.append(str + ";");
			}
		}
		return sb.toString();
	}

	private static void writeValue(Map<String, List<Field>> fieldsMap, FormField formField, Map<String, Object> objects, Object value) throws Exception {
		List<Field> fields = fieldsMap.get(formField.getClazz());
		for (Field field: fields) {
			if (field.getName().equals(formField.getId())) {
				field.setAccessible(true);
				String strValue = value.toString();
				if (Type.ENUM == formField.getType()) {
					Class<MDBPMN_Enum> enumm = (Class<MDBPMN_Enum>)field.getType();
					MDBPMN_Enum[] enums = enumm.getEnumConstants();
					for (MDBPMN_Enum e: enums) {
						System.out.println(strValue + "  " + e);
						if (e.toString().equals(strValue))
							field.set(objects.get(formField.getClazz()), e);
					}
				} else if (Type.LIST == formField.getType() || Type.MAP == formField.getType()) {
					field.set(objects.get(formField.getClazz()), value);
				} else {
					field.set(objects.get(formField.getClazz()), getValue(formField.getType() ,strValue));
				}
			}
		}
	}

	private static Object getValue(Type type, String value) throws ParseException {
		if        (type == Type.BYTE) {
			return Byte.parseByte(value);
		} else if (type == Type.SHORT) {
			return Short.parseShort(value);
		} else if (type == Type.INTEGER) {
			return Integer.parseInt(value);
		} else if (type == Type.LONG) {
			return Long.parseLong(value);
		} else if (type == Type.FLOAT) {
			value = convertCommaToDot(value);
			return Float.parseFloat(value);
		} else if (type == Type.DOUBLE) {
			value = convertCommaToDot(value);
			return Double.parseDouble(value);
		} else if (type == Type.BOOLEAN) {
			if (value.equals("on"))
				return Boolean.TRUE;
			else 
				return Boolean.FALSE;
		} else if (type == Type.DATE) {
			SimpleDateFormat formatter = null;
			
			if (value.length() == 10)
				formatter = new SimpleDateFormat("yyyy-MM-dd");
			else if (value.length() == 16)
				formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM");
			else if (value.length() == 7)
				formatter = new SimpleDateFormat("yyyy-MM");
			return formatter.parse(value);
		} 
		return value;
	}

	private static String isNumberInRange(long number, FormField formField) {
		if (formField.getValidation().containsKey("MIN")) {
			long min = Long.parseLong(formField.getValidation().get("MIN"));
			if (number < min)
				return formField.getClazz() + "." + formField.getId() + ": The value must be greater than or equal to " + min;
		}
		if (formField.getValidation().containsKey("MAX")) {
			long max = Long.parseLong(formField.getValidation().get("MAX"));
			if (number > max)
				return formField.getClazz() + "." + formField.getId() + ": The value must be less than or equal to " + max;
		}
		return null;
	}

	private static String isDecimalInRange(double number, FormField formField) {
		if (formField.getValidation().containsKey("MIN")) {
			double min = Double.parseDouble(formField.getValidation().get("MIN"));
			if (number < min)
				return formField.getClazz() + "." + formField.getId() + ": The value must be greater than or equal to " + min;
		}
		if (formField.getValidation().containsKey("MAX")) {
			double max = Double.parseDouble(formField.getValidation().get("MAX"));
			if (number > max)
				return formField.getClazz() + "." + formField.getId() + ": The value must be less than or equal to " + max;
		}
		return null;
	}

	private static String checkStringLength(String str, FormField formField) {
		if (formField.getValidation().containsKey("MINLENGTH")) {
			int min = Integer.parseInt(formField.getValidation().get("MINLENGTH"));
			if (str.length() < min)
				return formField.getClazz() + "." + formField.getId() + ": The length must be greater than or equal to " + min;
		}
		if (formField.getValidation().containsKey("MAXLENGTH")) {
			int max = Integer.parseInt(formField.getValidation().get("MAXLENGTH"));
			if (str.length() > max)
				return formField.getClazz() + "." + formField.getId() + ": The length must be greater than or equal to " + max;
		}

		return null;
	}

	private static String convertCommaToDot(String s) {
		char c=',';
		char d='.';
		int counterC=0, lastDPos=1000, lastCPos=1000;
		for(int i=s.length()-1;i>=0;i--){
			if(s.charAt(i)==d) {
				lastDPos=i;
			}
			if(s.charAt(i)==c) {
				counterC++;
				lastCPos=i;
			}
		}
		if (counterC == 1 && (lastDPos < lastCPos) ) {
			s = s.replace('.', ',');
			char[] chars = s.toCharArray();
			chars[lastCPos] = '.';
			return String.valueOf(chars);
		} else
			return s;
	}
}
