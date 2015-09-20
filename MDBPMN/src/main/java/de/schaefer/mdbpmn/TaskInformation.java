package de.schaefer.mdbpmn;

public class TaskInformation {

	private String processDefinitionId;
	private String processInstanceId;
	private String taskDefinitionKey;
	
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
	
}
