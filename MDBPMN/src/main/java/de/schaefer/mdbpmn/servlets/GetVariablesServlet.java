package de.schaefer.mdbpmn.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.TaskInformation;

@WebServlet("/GetVariables")
public class GetVariablesServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	try {	
		String taskId = java.net.URLDecoder.decode(request.getParameter("processKey"), "UTF-8");
		TaskInformation taskInfo = MDBPMN_Framework.getFramework().getUserTaskInformation(taskId);
		Map<String, Object> variables = MDBPMN_Framework.getFramework().getProcessVariables(taskInfo.getProcessInstanceId());
		String result = "";
		for (Entry<String, Object> entry: variables.entrySet()) {
			result += entry.getKey() + ":";
		}
		response.getWriter().write(result);
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
