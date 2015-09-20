package de.schaefer.mdbpmn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.camunda.bpm.model.bpmn.Bpmn;

import de.schaefer.mdbpmn.exceptions.InitializeException;
import de.schaefer.mdbpmn.parser.AnnotationsParser;
import de.schaefer.mdbpmn.parser.FormData;
import de.schaefer.mdbpmn.parser.FormField;
import de.schaefer.mdbpmn.parser.MDBPMNParser;
import de.schaefer.mdbpmn.parser.Type;

public class ModelValidator {
	
	private final String completeReport;
	private Set<String> uniqueClasses = new HashSet<String>();
	private boolean isValid = true;
	private boolean isUsable = true;

	public ModelValidator(ClassLoader classLoader) throws InitializeException {
		MDBPMN_Framework framework = MDBPMN_Framework.initializeFramework(null, classLoader, null, TargetBPMNPlatform.TEST);
		String path = this.getClass().getClassLoader().getResource("MDBPMN").getPath();
		path = path.substring(0, path.length() - 7);
		List<String> bpmnFilesList = listFilesForFolder(new File(path));
		
		StringBuilder report = new StringBuilder();
		
		report.append("--- START BPMN VALIDATION REPORT --- \n");
		
		for (String bpmnFilePath: bpmnFilesList) {
			try {
			report.append("FILENAME: " + bpmnFilePath + "\n");
			MDBPMNParser parser = new MDBPMNParser(Bpmn.readModelFromFile(new File(bpmnFilePath)), classLoader);
			
			for (Entry<String, FormData> entry: parser.getStartEventsForms().entrySet()) {
				report.append("START EVENT: " + entry.getValue().getId() + "\n");
				report.append(validateFormData(entry.getValue()));
			}
			
			for (Entry<String, FormData> entry: parser.getUserTasksForms().entrySet()) {
				report.append("USER TASK: " + entry.getValue().getId() + "\n");
				report.append(validateFormData(entry.getValue()));
			}
			} catch (Exception e) {
				report.append("EXCEPTION: :" + e.getMessage());
				isValid = isUsable = false;
			}
			report.append("##########################\n");
		}
		
		for (String name: uniqueClasses) {
			report.append("ANNOTATIONS: " + name + "\n");
			AnnotationsParser parser;
			try {
				parser = framework.getAnnotationsParser(Class.forName(name));
				Map<String, FormField> formFields = parser.getAllFormFields();
				for (Entry<String, FormField> entry: formFields.entrySet()) {
					report.append(validateFormField(entry.getValue()));
				}
			} catch (ClassNotFoundException e) {
				report.append("ClassNotFoundException:" + name + " - " + e.getMessage());
				isValid = isUsable = false;
			}
			report.append("##########################\n");
		}
		
		completeReport = report.toString();
	}
	
	public String getReport() {
		return completeReport;
	}
	
	private String validateFormData(FormData formData) {
		String retVal = "";
		for (FormField formField: formData.formFields)
			retVal += validateFormField(formField);
		retVal += "----------------------------\n";
		return retVal;
	}
	
	private String validateFormField(FormField formField) {
		String retVal = "";
		try {
			String classpath = formField.getClazz() + "." + formField.getId();
			//uniqueClasses.add(formField.getClazz());
			if (formField.getType().getStandardHtmlType().equals("number")) {
				if (formField.getValidation().containsKey("MINLENGTH") || formField.getValidation().containsKey("REGEX") || formField.getValidation().containsKey("MAXLENGTH")) {
					retVal += getString(classpath,"The Constraints minlength, maxlength and regex are not supported for numeric FormFields", false);
					isValid = false;
				}
				retVal += checkMinMax(formField, classpath, false);
			}
			
			if (formField.getType() == Type.STRING) {
				if (formField.getValidation().containsKey("MIN") || formField.getValidation().containsKey("MAX")) {
					retVal += getString(classpath,"The Constraints min an max are not supported for String FormFields", false);
					isValid = false;
				}
				retVal += checkMinMax(formField, classpath, true);
			}
			} catch (Exception e) {
				
			}
		return retVal;
	}
	
	private String checkMinMax(FormField formField, String classpath, boolean length) {
		String retVal = "";
		String strLength = "";
		if (length)
			strLength = "LENGTH";
		Long min = null, max = null;
		try {
			if (formField.getValidation().containsKey("MIN" + strLength))
				min = Long.parseLong(formField.getValidation().get("MIN" + strLength));
		} catch (Exception e) {
			retVal += getString(classpath,"Not a valid number for Contstraint min", false);
			isValid = isUsable = false;
		}
		
		try {
			if (formField.getValidation().containsKey("MAX" + strLength))
				max = Long.parseLong(formField.getValidation().get("MAX" + strLength));
		} catch (Exception e) {
			retVal += getString(classpath,"Not a valid number for Contstraint max", false);
			isValid = isUsable = false;
		}
		
		if (min != null && max != null) {
			if (min > max) {
				retVal += getString(classpath,"The min Value is bigger than the max value", false);
				isValid = isUsable = false;
			}
		}
		return retVal;
	}
	
	private String getString (String classpath, String message, boolean annotation) {
		String begin = "FormField@";
		if (annotation) {
			begin = "Annotation@";
		}
		return begin + classpath + ": " + message + "\n";
	}
	
	private List<String> listFilesForFolder(final File folder) {
	    List<String> list = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            list.addAll(listFilesForFolder(fileEntry));
	        } else {
	            String filepath = fileEntry.getPath();
	            if ( filepath.substring(filepath.length() - 5, filepath.length()).equals(".bpmn"))
	        		list.add(fileEntry.getPath());
	        }
	    }
		return list;
	}

	public boolean isValid() {
		return isValid;
	}

	public boolean isUsable() {
		return isUsable;
	}
	
}
