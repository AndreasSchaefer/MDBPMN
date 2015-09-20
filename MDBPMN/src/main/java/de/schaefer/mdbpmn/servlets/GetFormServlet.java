package de.schaefer.mdbpmn.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.generator.TargetPlatform;

@WebServlet("/GetForm")
public class GetFormServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String processDefIdOrUserTaskId = java.net.URLDecoder.decode(request.getParameter("processKey"), "UTF-8");
		String language = request.getParameter("language");
		// TODO Camunda Tasklist nicht hart codiert hier !!!
		try {
		if (processDefIdOrUserTaskId.contains(":"))
			response.getWriter().write(MDBPMN_Framework.getFramework().getStartForm(processDefIdOrUserTaskId, null, language, TargetPlatform.CAMUNDA_TASKLIST));
		else
			response.getWriter().write(MDBPMN_Framework.getFramework().getTaskForm(processDefIdOrUserTaskId, language, TargetPlatform.CAMUNDA_TASKLIST));
		} catch (Exception e) { response.getWriter().write(e.toString()); e.printStackTrace(); }
	}
	
}
