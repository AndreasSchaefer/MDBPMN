<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="de.schaefer.mdbpmn.generator.*" %>
    <%@ page import="de.schaefer.mdbpmn.parser.*" %>
    <%@ page import="de.schaefer.mdbpmn.*" %>
    <%@ page import="org.camunda.bpm.model.bpmn.Bpmn" %>
    <%@ page import="java.io.File" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src=mdbpmn.js></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
<link rel="stylesheet" href="bootstrap.min.css" type="text/css">
<link rel="stylesheet" href="mdbpmn.css" type="text/css">
</head>
<body style="margin: 20px;">
<h1 style="margin-bottom: 20px;">Meine Prozessrahmenapplikation</h1>
<div style="width: 100px; height: 453px; float:left; border-width: 1px; border-color: black; border-style: solid;"> Hier könnte beispielsweise eine Aufgabenliste angezeigt werden...</div>
<div style="width: 460px; float: left; margin-left: 20px;">
<%=MDBPMN_Framework.getFramework().getStartFormByKey("TestPrimitiv","StartEvent_1","DE", TargetPlatform.WEB)%>
</div><!--<br><br><br><br>
<input type=button onClick="test();" name=mybutton value="Mein Button"><input type=date id=gebdat> <p id="demo"></p><script>function myFunction() {document.getElementById("demo").innerHTML = "Hello JavaScript!";}</script>" +
<form name="form1" method="GET" action="Ajaxexample" id="form1"><table><tr><td>Number 1</td><td><input type="text" name="n1"/></td></tr><tr><td>Number 2</td><td><input type="text" name="n2"/></td></tr><tr><td></td><td><input type="button" onClick="submitCalc();" value="Calculate"/></td></tr><tr><td>Result</td><td><input type="text" value="" id="result"/></td></tr></table></form>
slalaslsl--></html>