package de.schaefer.mdbpmn.servlets;


import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.TaskInformation;
import de.schaefer.mdbpmn.exceptions.FrameworkNotInitializedException;
import de.schaefer.mdbpmn.parser.FormField;
import de.schaefer.mdbpmn.parser.MDBPMNParser;
import de.schaefer.mdbpmn.persistence.EntityWriter;

@WebServlet("/StartProcessServlet")
public class StartProcessServlet extends HttpServlet {
	private final MDBPMN_Framework framework;
       
    public StartProcessServlet() throws FrameworkNotInitializedException {
        super();
        framework = MDBPMN_Framework.getFramework();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> httpValues = request.getParameterMap();
		String result;
		try {
			String formId = httpValues.get("formId")[0];
			String processDefinitionIdOrInstanceId = java.net.URLDecoder.decode(httpValues.get("processId")[0], "UTF-8");
			String processDefinitionId, processInstanceId;
			if (!processDefinitionIdOrInstanceId.contains(":")) { // Für UserTask
				processDefinitionId = framework.getProcessInstanceDefinitionId(processDefinitionIdOrInstanceId);
				processInstanceId = processDefinitionIdOrInstanceId;
			} else {
				processDefinitionId = processDefinitionIdOrInstanceId;
				processInstanceId = null;
			}
			
			MDBPMNParser parser = framework.getParserByProcessDefinitionID(processDefinitionId);	
			HashSet<String> classNames = new HashSet<String>();
			Map<String, FormField> formFieldsMap = new HashMap<String, FormField>();
			boolean isStart = false;
			if (processInstanceId == null) { // für StartEvent
				isStart = true;
				for (FormField formField: parser.getStartEventsForms().get(formId).formFields) {
					classNames.add(formField.getClazz());
					formFieldsMap.put(formField.getClazz() + "." + formField.getId(), formField);
				}
			} else {
				for (FormField formField: parser.getUserTasksForms().get(formId).formFields) {
					classNames.add(formField.getClazz());
					formFieldsMap.put(formField.getClazz() + "." + formField.getId(), formField);
				}
			}
			// Zum Prüfen ob Task noch vorhanden ist
			TaskInformation taskInfo = null;
			if (!isStart)
				taskInfo = framework.getUserTaskInformation(httpValues.get("taskId")[0]);

			Map<String, Object> variables = new HashMap<String, Object>();
			if (processInstanceId != null) {
				System.out.println("taskId: " + processInstanceId);
				variables = framework.getProcessVariables(processInstanceId);
			}
			
			result = EntityWriter.writeEntities(httpValues, classNames, variables, formFieldsMap);
			
			boolean saveOnly = httpValues.get("saveOnly")[0].equals("true");
			boolean controlProcessEngine = httpValues.get("controlProcessEngine")[0].equals("true");
			
			if (saveOnly)
				framework.saveProcessVariables(taskInfo.getProcessInstanceId(), variables);
			
			if (controlProcessEngine && !result.startsWith("VALIDATION_ERROR") && !saveOnly) {
				if (isStart)
					framework.startProcess(processDefinitionId, variables);
				else
					framework.completeTask(httpValues.get("taskId")[0], variables);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result = "EXCEPTION;" + e.getMessage();
		}
		response.getWriter().write(result);
	}
	
	/*private static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     String str = sw.getBuffer().toString();
	     if (str.length() > 300)
	    	 return str.substring(0, 300);
	     return str;
	}*/

}
