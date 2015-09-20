package de.schaefer.mdbpmn;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.schaefer.mdbpmn.exceptions.FormParserException;
import de.schaefer.mdbpmn.exceptions.InitializeException;
import de.schaefer.mdbpmn.exceptions.ProcessDefinitionNotFoundException;
import de.schaefer.mdbpmn.exceptions.ProcessInstanceNotFoundException;
import de.schaefer.mdbpmn.exceptions.TaskNotFoundException;
import de.schaefer.mdbpmn.generator.FormGenerator;
import de.schaefer.mdbpmn.generator.TargetPlatform;
import de.schaefer.mdbpmn.parser.FormData;
import de.schaefer.mdbpmn.parser.MDBPMNParser;
import de.schaefer.mdbpmn.persistence.EntityReader;

public class MDBPMN_FrameworkCamunda extends MDBPMN_Framework {
	
	private ProcessEngine processEngine;
	
	protected MDBPMN_FrameworkCamunda() throws InitializeException {
		processEngine = BpmPlatform.getDefaultProcessEngine();
	}
	
	// Über ProzessKey
	public String getStartFormByKey(String processKey, String formID, String language, TargetPlatform target) throws Exception {
		String processDefinitionID = processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult().getId();
		return getStartForm(processDefinitionID, formID, language, target);
	}
	
	// Über ProcessDefinitonId
	public String getStartForm(String processDef, String formID, String language, TargetPlatform target) throws Exception {
		MDBPMNParser parser = getParserByProcessDefinitionID(processDef);
		
	 	FormData formData;
	 	if (formID != null)
	 		formData = parser.getStartEventsForms().get(formID);
	 	else {
	 		formData = parser.getStartEventsForms().entrySet().iterator().next().getValue();
	 	}
	 	FormGenerator formGenerator = new FormGenerator(formData, language, processDef, target, null, null);
	 	return formGenerator.getHTMLCode();
	}
	
	public String getTaskForm(String userTaskId, String language, TargetPlatform target) throws Exception {
		Task task = processEngine.getTaskService().createTaskQuery().taskId(userTaskId).singleResult();
		if (task == null)
			return "NO TASK FOUND!";
		
		Map<String, Object> variables = processEngine.getRuntimeService().getVariables(task.getProcessInstanceId());
		MDBPMNParser parser = getParserByProcessDefinitionID(task.getProcessDefinitionId());
	 	FormData formData = parser.getUserTasksForms().get(task.getTaskDefinitionKey());
	 	
	 	if (formData == null)
	 		return "NO MDBPMN TASK FORM";
	 	
	 	EntityReader reader = createEntityReader(variables, formData);
	 	FormGenerator formGenerator = new FormGenerator(formData, language, task.getProcessInstanceId(), target, reader, userTaskId);
	 	return formGenerator.getHTMLCode();
	}
	
	public MDBPMNParser getParserByProcessInstanceID(String processId) throws ProcessInstanceNotFoundException, ProcessDefinitionNotFoundException, FormParserException {
		String processDefinitionID = getProcessInstanceDefinitionId(processId);
		return getParserByProcessDefinitionID(processDefinitionID);
	}
	
	public MDBPMNParser getParserByProcessDefinitionID(String processDefinitionID) throws ProcessDefinitionNotFoundException, FormParserException {
		if (!parserMap.containsKey(processDefinitionID)) {
			BpmnModelInstance instance = processEngine.getRepositoryService().getBpmnModelInstance(processDefinitionID);
			if (instance == null)
				throw new ProcessDefinitionNotFoundException();
			parserMap.put(processDefinitionID, new MDBPMNParser(instance, classLoader));
		}
		return parserMap.get(processDefinitionID);
	}
	
	public String startProcess(String processDefinitionId, Map<String, Object> variables) {
		ProcessInstance instance = processEngine.getRuntimeService().startProcessInstanceById(processDefinitionId, variables);
		System.out.println(instance.getProcessDefinitionId() + "  " + instance.getProcessInstanceId() + " " + instance.isEnded() + "  " + instance.isSuspended());
		System.out.println("is Ended "+ instance.isEnded());
		return instance.getId();
	}
	
	public TaskInformation getUserTaskInformation(String taskId) throws TaskNotFoundException {
		try {
			Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
			TaskInformation taskInfo = new TaskInformation();
			taskInfo.setTaskDefinitionKey(task.getTaskDefinitionKey());
			taskInfo.setProcessInstanceId(task.getProcessInstanceId());
			taskInfo.setProcessDefinitionId(task.getProcessDefinitionId());
			return taskInfo;
		} catch (Exception e) {
			throw new TaskNotFoundException(e);
		}
	}
	
	public String getProcessInstanceDefinitionId(String instanceId) throws ProcessInstanceNotFoundException {
		try {
			ProcessInstance instance = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(instanceId).singleResult();
			return instance.getProcessDefinitionId();
		} catch (Exception e) {
			throw new ProcessInstanceNotFoundException(e);
		}
	}
	
	public void saveProcessVariables(String processInstanceId, Map<String, Object> variables) {
		processEngine.getRuntimeService().setVariables(processInstanceId, variables);
	}
	
	public void completeTask(String taskId, Map<String, Object> variables) {
		processEngine.getTaskService().complete(taskId, variables);
	}

	@Override
	public Map<String, Object> getProcessVariables(String processInstanceId) {
		return processEngine.getRuntimeService().getVariables(processInstanceId);
	}
}
