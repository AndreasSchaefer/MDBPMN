package de.schaefer.mdbpmn;

import java.util.Map;

import de.schaefer.mdbpmn.exceptions.ProcessInstanceNotFoundException;
import de.schaefer.mdbpmn.exceptions.TaskNotFoundException;
import de.schaefer.mdbpmn.generator.TargetPlatform;
import de.schaefer.mdbpmn.parser.MDBPMNParser;

public class MDBPMN_FrameworkForValidation extends MDBPMN_Framework {

	@Override
	public String getStartFormByKey(String processKey, String formID,
			String language, TargetPlatform target)
			throws Exception {
		// Empty implementation
		return null;
	}

	@Override
	public String getStartForm(String processDef, String formID,
			String language, TargetPlatform target)
			throws Exception {
		// Empty implementation
		return null;
	}

	@Override
	public String getTaskForm(String userTaskId, String language,
			TargetPlatform target) {
		// Empty implementation
		return null;
	}

	@Override
	public MDBPMNParser getParserByProcessInstanceID(String processId) throws Exception {
		// Empty implementation
		return null;
	}

	@Override
	public String startProcess(String processDefinitionId,
			Map<String, Object> variables) {
		// Empty implementation
		return null;
	}

	@Override
	public TaskInformation getUserTaskInformation(String taskId)
			throws TaskNotFoundException {
		// Empty implementation
		return null;
	}

	@Override
	public String getProcessInstanceDefinitionId(String instanceId)
			throws ProcessInstanceNotFoundException {
		// Empty implementation
		return null;
	}

	@Override
	public MDBPMNParser getParserByProcessDefinitionID(
			String processDefinitionID) throws Exception {
		// Empty implementation
		return null;
	}

	@Override
	public void completeTask(String taskId, Map<String, Object> variables) {
		// Empty implementation
		
	}

	@Override
	public Map<String, Object> getProcessVariables(String processInstanceId) {
		// Empty implementation
		return null;
	}

	@Override
	public void saveProcessVariables(String processInstanceId,
			Map<String, Object> variables) {
		// Empty implementation
	}

}
