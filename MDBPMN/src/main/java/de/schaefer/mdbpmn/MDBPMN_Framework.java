package de.schaefer.mdbpmn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.schaefer.mdbpmn.exceptions.FrameworkNotInitializedException;
import de.schaefer.mdbpmn.exceptions.InitializeException;
import de.schaefer.mdbpmn.exceptions.ProcessInstanceNotFoundException;
import de.schaefer.mdbpmn.exceptions.TaskNotFoundException;
import de.schaefer.mdbpmn.generator.TargetPlatform;
import de.schaefer.mdbpmn.parser.AnnotationsParser;
import de.schaefer.mdbpmn.parser.FormData;
import de.schaefer.mdbpmn.parser.FormField;
import de.schaefer.mdbpmn.parser.MDBPMNParser;
import de.schaefer.mdbpmn.persistence.CustomValidation;
import de.schaefer.mdbpmn.persistence.EntityIO;
import de.schaefer.mdbpmn.persistence.EntityReader;
import de.schaefer.mdbpmn.persistence.MDBPMN_DAO;

abstract public class MDBPMN_Framework {
	
	private static MDBPMN_Framework singleton;
	protected MDBPMN_DAO dao;
	protected ClassLoader classLoader;
	protected CustomValidation customValidation;
	protected Map<Class<?>, AnnotationsParser> annotationsParserMap = new HashMap<Class<?>, AnnotationsParser>();
	protected Map<String, MDBPMNParser> parserMap = new HashMap<String, MDBPMNParser>();
	
	
	/**
	 * 
	 * @return the singleton instance of MDBPMN_Framework
	 * @throws FrameworkNotInitializedException
	 */
	public static synchronized MDBPMN_Framework getFramework() throws FrameworkNotInitializedException {
		return singleton;
	}
	
	/**
	 * Method to initialize the MDBPMN Framework
	 * 
	 * @param dao to load and save Objects in Database
	 * @param classLoader of your Process Driven Application
	 * @param validator optional for customized validations
	 * @param platform chosen Process Engine
	 * @return the singleton instance of MDBPMN_Framework
	 * @throws InitializeException
	 */
	public static synchronized MDBPMN_Framework initializeFramework(MDBPMN_DAO dao, ClassLoader classLoader, CustomValidation validator, TargetBPMNPlatform platform) throws InitializeException {
		if (singleton == null) {
			if (classLoader == null)
				throw new InitializeException("MDBPMN_Framework needs ClassLoader of your Project to load Files in Resources");
			if (platform != TargetBPMNPlatform.TEST && dao == null)
				throw new InitializeException("MDBPMN_Framework needs a MDBPMN_DAO to persist Objects");

			if (platform == TargetBPMNPlatform.CAMUNDA)
				singleton = new MDBPMN_FrameworkCamunda();
			else 
				singleton = new MDBPMN_FrameworkForValidation();
			
			singleton.classLoader = classLoader;
			singleton.customValidation = validator;
			singleton.dao = dao;
		}
		return singleton;
	}
	
	/**
	 * To get a HTML5 Start Form by ProcessDefinitionKey
	 * 
	 * @param processKey
	 * @param formID
	 * @param language
	 * @param target
	 * @param horizontal
	 * @return
	 * @throws Exception
	 */
	abstract public String getStartFormByKey(String processKey, String formID, String language, TargetPlatform target) throws Exception;
	
	/**
	 * To get a HTML5 Start Form by Process Definition ID
	 * 
	 * @param processDef
	 * @param formID
	 * @param language
	 * @param target
	 * @return
	 * @throws Exception
	 */
	abstract public String getStartForm(String processDef, String formID, String language, TargetPlatform target) throws Exception;
	
	
	/**
	 * To get a HTML5 User Task Form by User Task ID
	 * 
	 * @param userTaskId
	 * @param language
	 * @param target
	 * @return
	 * @throws Exception
	 */
	abstract public String getTaskForm(String userTaskId, String language, TargetPlatform target) throws Exception;
	
	/**
	 * To get a MDBPMNParser by Instance ID of Process. Parsers are cached by MDBPMN-Framework.
	 * 
	 * @param processId
	 * @return
	 * @throws Exception
	 */
	abstract public MDBPMNParser getParserByProcessInstanceID(String processId) throws Exception;
	
	
	/**
	 * To get a MDBPMNParser by Instance ID of Process. Parsers are cached by MDBPMN-Framework
	 * 
	 * @param processDefinitionID
	 * @return
	 * @throws Exception
	 */
	abstract public MDBPMNParser getParserByProcessDefinitionID(String processDefinitionID) throws Exception;
	
	
	/**
	 * Start a new Process Instance over Process Engine
	 * 
	 * @param processDefinitionId
	 * @param variables
	 * @return
	 * @throws Exception
	 */
	abstract public String startProcess(String processDefinitionId, Map<String, Object> variables) throws Exception;
	
	/**
	 * To get Informations to a User Task Instance.
	 * 
	 * @param taskId
	 * @return
	 * @throws TaskNotFoundException
	 */
	abstract public TaskInformation getUserTaskInformation(String taskId) throws TaskNotFoundException;
	
	/**
	 * To get the Definition ID of a Process Instance
	 * 
	 * @param instanceId
	 * @return
	 * @throws ProcessInstanceNotFoundException
	 */
	abstract public String getProcessInstanceDefinitionId(String instanceId) throws ProcessInstanceNotFoundException;
	
	/**
	 * Complete a User Task over the Process Engine
	 * 
	 * @param taskId
	 * @param variables
	 */
	abstract public void completeTask(String taskId, Map<String, Object> variables);
	
	/**
	 * Get Variables of a Process Instance by ID
	 * 
	 * @param processInstanceId
	 * @return
	 */
	abstract public Map<String, Object> getProcessVariables(String processInstanceId);
	
	/**
	 * Get the used DAO by MDBPMN Framework
	 * @return
	 */
	public MDBPMN_DAO getDAO() {
		return dao;
	}
	
	/**
	 * Get a AnnotationsParser for a Java Class. AnnotationsParser Objects are cached by MDBPMN Framework.
	 * 
	 * @param clazz
	 * @return
	 */
	public AnnotationsParser getAnnotationsParser(Class<?> clazz) {
		if (annotationsParserMap.containsKey(clazz)) {
			return annotationsParserMap.get(clazz);
		} else {
			AnnotationsParser parser = new AnnotationsParser(clazz);
			annotationsParserMap.put(clazz, parser);
			return parser;
		}
	}
	
	
	/**
	 * Returns the CustomValidation Object
	 * 
	 * @return CustomValidation Object
	 */
	public CustomValidation getCustomValidaton() {
		return customValidation;
	}
	
	/**
	 * Save Process Variables if User doesn't want to complete the task
	 * @param processInstanceId
	 * @param variables
	 */
 	abstract public void saveProcessVariables(String processInstanceId, Map<String, Object> variables);
	
	/**
	 * Create a EntityReader to read Informations from Database
	 * 
	 * @param variables Process Variables Map
	 * @param formData
	 * @return
	 */
	protected EntityReader createEntityReader(Map<String, Object> variables, FormData formData) {
		Set<String> uniqueClassnames = new HashSet<String>();
		List<Object> loadedEntities = new ArrayList<Object>();
		//Set<String> loadedClassnames = new HashSet<String>();
		//Map<String, Object> processVariables = new HashMap<String, Object>();
		
		try {
		if (variables != null) {
			for (FormField formField: formData.formFields) {
				if (!formField.getClazz().equals("PROCESS"))
					uniqueClassnames.add(formField.getClazz());				
			}
			
			for (String name: uniqueClassnames) {
				// TODO Wie mit Strings umgehen?
				Object objValue = variables.get(name);
				if (objValue != null) {
					int id = Integer.parseInt(objValue.toString());
					Class<?> clazz = Class.forName(name);
					Object instance = dao.read(clazz, id);
					loadedEntities.add(instance);
					
					List<Field> fields = EntityIO.getAllFields(new ArrayList<Field>(), clazz);
					for (Field field: fields) 
						if (uniqueClassnames.contains(field.getType().getName())) {
							field.setAccessible(true);
							if (field.get(instance) != null )
								loadedEntities.add(field.get(instance));
						}
					//loadedClassnames.add(name);
				}
			}
			
			return new EntityReader(loadedEntities, variables);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
