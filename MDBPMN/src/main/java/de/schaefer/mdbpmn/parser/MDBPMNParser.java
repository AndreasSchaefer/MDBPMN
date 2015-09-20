package de.schaefer.mdbpmn.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.schaefer.mdbpmn.MDBPMN_Framework;
import de.schaefer.mdbpmn.exceptions.FormParserException;

public class MDBPMNParser {
	private BpmnModelInstance model;
	public static final String URI = "MDBPMN";
	private ClassLoader classLoader;
	
	private final Map<String, FormData> userTasksForms = new HashMap<String, FormData>();
	private final Map<String, FormData> startEventsForms = new HashMap<String, FormData>();
	
	private final Map<String, Map<String, FormField>> xmlFormFieldsMap = new HashMap<String, Map<String, FormField>>();
	
	public MDBPMNParser(BpmnModelInstance modelInstance, ClassLoader classLoader) throws FormParserException {
		try {
			model = modelInstance;
			this.classLoader = classLoader;
			parse();
		} catch (Exception e) {
			e.printStackTrace();
			throw new FormParserException(e);
		}
	}
	
	private FormField getXMLFormField(String filename, String reference, FormField formField) throws Exception {
		if (!xmlFormFieldsMap.containsKey(filename)) {
			readXML(filename);
		}
		
		FormField xmlFormField = xmlFormFieldsMap.get(filename).get(reference);
		formField.getLabels().putAll(xmlFormField.getLabels());
		formField.getProperties().putAll(xmlFormField.getProperties());
		formField.getValidation().putAll(xmlFormField.getValidation());
		return formField;
	}
	
	private void readXML(String filename) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		//System.out.println(filename);
		File file = new File(classLoader.getResource(filename).getFile());
		Document doc = db.parse(file);
		Map <String, FormField> formFields = new HashMap<String, FormField>();
	
		NodeList domFormFields = doc.getElementsByTagName("formField");
		for (int i=0; i < domFormFields.getLength(); i++) {
			FormField formField = new FormField();
			Element domFormField = (Element)domFormFields.item(i);
			String reference = domFormField.getAttribute("reference");
			
			try {
				Element domLabelElement = (Element)domFormField.getElementsByTagName("labels").item(0); 
				NodeList domLabels = domLabelElement.getElementsByTagName("label");
				for (int j=0; j < domLabels.getLength(); j++) {
					Element domLabel = (Element)domLabels.item(j);
					formField.getLabels().put(domLabel.getAttribute("language").toUpperCase(), domLabel.getAttribute("value"));
				}
			} catch (Exception e) {};
			
			try {
				Element domValidationElement = (Element)domFormField.getElementsByTagName("validation").item(0); 
				NodeList domConstraints = domValidationElement.getElementsByTagName("constraint");
				for (int j=0; j < domConstraints.getLength(); j++) {
					Element domConstraint = (Element)domConstraints.item(j);
					formField.getValidation().put(domConstraint.getAttribute("name").toUpperCase(), domConstraint.getAttribute("config"));
				}
			} catch (Exception e) {};
			
			try {
				Element domPropertiesElement = (Element)domFormField.getElementsByTagName("properties").item(0); 
				NodeList domProperties = domPropertiesElement.getElementsByTagName("property");
				for (int j=0; j < domProperties.getLength(); j++) {
					Element domProperty = (Element)domProperties.item(j);
					formField.getProperties().put(domProperty.getAttribute("id"), domProperty.getAttribute("value"));
				}
			} catch (Exception e) {};
			formFields.put(reference, formField);
		}
		xmlFormFieldsMap.put(filename, formFields);
	}
	
	private void parse() throws Exception {
		Collection<UserTask> userTasksColl = model.getModelElementsByType(UserTask.class);
		for (UserTask userTask: userTasksColl) {
			//userTasks.put(userTask.getId(), userTask);
			FormData formData = new FormData();
			formData.setStart(false);
			formData.setId(userTask.getId());
			
			boolean dataObjectFormData = false;
			Collection<DataInputAssociation> dataInputAssociations = userTask.getDataInputAssociations();
			for (DataInputAssociation dataInputAss: dataInputAssociations) {
				boolean i = parseFormData(dataInputAss.getSources().iterator().next().getExtensionElements(), formData);
				if (i)
					dataObjectFormData = true;
			}
			
			boolean userTaskFormData = false;
			if (userTask.getExtensionElements() != null) {
				userTaskFormData = parseFormData(userTask.getExtensionElements(), formData);
				parseOverrideFormItems(userTask.getExtensionElements(), formData);
			}
			
			if (userTaskFormData || dataObjectFormData)
				userTasksForms.put(userTask.getId(), formData);
		}
		
		Collection<StartEvent> StartEventsColl = model.getModelElementsByType(StartEvent.class);
		for (StartEvent startEvent: StartEventsColl) {
			//startEvents.put(startEvent.getId(), startEvent);
			FormData formData = new FormData();
			formData.setStart(true);
			formData.setId(startEvent.getId());
			
			if (startEvent.getExtensionElements() != null) {
				boolean startFormData = parseFormData(startEvent.getExtensionElements(), formData);
				parseOverrideFormItems(startEvent.getExtensionElements(), formData);
				if (startFormData)
					startEventsForms.put(startEvent.getId(), formData);
			}
		}
		//System.out.println(userTasksForms);
	}
	
	private void parseOverrideFormItems(ExtensionElements extensionElements, FormData formData) throws Exception {
		if (extensionElements.getDomElement().getChildElementsByNameNs(URI, "overrideFormItems").size() != 0) {
			DomElement domOverrideFormItems = extensionElements.getDomElement().getChildElementsByNameNs(URI, "overrideFormItems").get(0);
			List<DomElement> domOverrideFormItemsElements = domOverrideFormItems.getChildElements();
			
			for (DomElement domFormItem: domOverrideFormItemsElements) {
				String id = domFormItem.getAttribute("id");
				FormItem formItem = null;
				for (FormItem iFormItem: formData.formItems) {
					if ( id.equals(iFormItem.getId())) {
						formItem = iFormItem;
						break;
					}
				}
				if (formItem instanceof FormField && domFormItem.getLocalName().equals("formField")) {
					FormField formField = (FormField)formItem;
					parseFormField(domFormItem, formField);
				} else if (formItem instanceof FormDescription && domFormItem.getLocalName().equals("description")) {
					FormDescription formDescription = (FormDescription)formItem;
					parseLabels(domFormItem, formDescription.getLabels());
					parseProperties(domFormItem, formDescription.getProperties());
				} else if (domFormItem.getLocalName().equals("line")) {
					parseProperties(domFormItem, formItem.getProperties());
				}
			}
		}
	}
	
	private boolean parseFormData(ExtensionElements extensionElements, FormData formData) throws Exception {
	if (extensionElements.getDomElement().getChildElementsByNameNs(URI, "formData").size() != 0) {
		DomElement domFormData = extensionElements.getDomElement().getChildElementsByNameNs(URI, "formData").get(0);
		
		try { DomElement domTitles = domFormData.getChildElementsByNameNs(URI, "titles").get(0);
		List<DomElement> domListTitles = domTitles.getChildElementsByNameNs(URI, "label");
		for (DomElement domTitle: domListTitles) {
			formData.getTitles().put(domTitle.getAttribute("language").toUpperCase(), domTitle.getAttribute("value"));
		}} catch (Exception e) {;};
		
		DomElement test = domFormData.getChildElementsByNameNs(URI, "formItems").get(0);
		List<DomElement> domRows = test.getChildElementsByNameNs(URI, "row");
		for (DomElement domRow: domRows) {
			FormItem formRowItem = new FormItem(FormItemType.NEW_ROW);
			formData.formItems.add(formRowItem);
			parseProperties(domRow, formRowItem.getProperties());
			
			List<DomElement> domColumns = domRow.getChildElementsByNameNs(URI, "column");
			for (DomElement domColumn: domColumns) {
				FormItem formColItem = new FormItem(FormItemType.COLUMN_BEGIN);
				formData.formItems.add(formColItem);
				parseProperties(domColumn, formColItem.getProperties());
				List<DomElement> domFormItemElements = domColumn.getChildElements();
				
				for (DomElement domFormItem: domFormItemElements) {
					if (domFormItem.getLocalName().equals("formField")) {
						FormField formField = parseFormField(domFormItem, null);
						formData.formItems.add(formField);
						formData.formFields.add(formField);
					} else if (domFormItem.getLocalName().equals("description")) {
						FormDescription formDescription = new FormDescription();
						formDescription.setId(domFormItem.getAttribute("id"));
						parseLabels(domFormItem, formDescription.getLabels());
						parseProperties(domFormItem, formDescription.getProperties());
						formData.formItems.add(formDescription);
					} else if (domFormItem.getLocalName().equals("line")) {
						FormItem formLine = new FormItem(FormItemType.LINE);
						formLine.setId(domFormItem.getAttribute("id"));
						parseProperties(domFormItem, formLine.getProperties());
						formData.formItems.add(formLine);
					}
				}
				formData.formItems.add(new FormItem(FormItemType.COLUMN_END));
			}
		}
	return true;
	} else {
		return false;
	}
	}
	
	private void parseLabels(DomElement domFormField, Map<String, String> map) {
		try{ // TODO Schöner machen
			List<DomElement> domLabels = domFormField.getChildElementsByNameNs(URI, "labels").get(0).getChildElements();
			for (DomElement domLabel: domLabels) {
				map.put(domLabel.getAttribute("language").toUpperCase(), domLabel.getAttribute("value"));
		}} catch(Exception e) {};
	}
	
	private void parseProperties(DomElement domFormField, Map<String, String> map) {
		try { // TODO Schöner Machen
			List<DomElement> domProperties = domFormField.getChildElementsByNameNs(URI, "properties").get(0).getChildElements();
			for (DomElement domProperty: domProperties) {
				map.put(domProperty.getAttribute("id"), domProperty.getAttribute("value"));
		}} catch(Exception e) {};
	}
	
	private FormField parseFormField(DomElement domFormField, FormField formField) throws Exception {
			if (formField == null) {
				formField = new FormField();
				if (!domFormField.getAttribute("class").toUpperCase().equals("PROCESS")) {
					Class<?> clazz = Class.forName(domFormField.getAttribute("class"));
					if (MDBPMN_Framework.getFramework().getAnnotationsParser(clazz).cotainsField(domFormField.getAttribute("id"))) {
						formField = MDBPMN_Framework.getFramework().getAnnotationsParser(clazz).getFormField(domFormField.getAttribute("id"));
					}
				}
				if (domFormField.getAttribute("referenceFile") != null && domFormField.getAttribute("referenceName") != null) {
					formField = getXMLFormField(domFormField.getAttribute("referenceFile"), domFormField.getAttribute("referenceName"), formField);
				}
			}

			formField.setId(domFormField.getAttribute("id"));
			if (formField.getClazz() == null)
				formField.setClazz(domFormField.getAttribute("class"));
			if (formField.getType() == null)
				formField.setType(Type.valueOf(domFormField.getAttribute("type").toUpperCase()));
			if (formField.getDefinition() == null)
				formField.setDefinition(domFormField.getAttribute("definition"));
			try {
				formField.setValueType(Type.valueOf(domFormField.getAttribute("valueType").toUpperCase()));
				formField.setKeyType(Type.valueOf(domFormField.getAttribute("keyType").toUpperCase()));
			} catch(Exception e) {};
			
			parseLabels(domFormField, formField.getLabels());
			parseProperties(domFormField, formField.getProperties());

			try{ // TODO Schöner machen
				List<DomElement> domValidation = domFormField.getChildElementsByNameNs(URI, "validation").get(0).getChildElements();
				for (DomElement domConstraint: domValidation) {
					formField.getValidation().put(domConstraint.getAttribute("name").toUpperCase(), domConstraint.getAttribute("config"));
			}} catch(Exception e) {};
			
			return formField;
	}

	public Map<String, FormData> getUserTasksForms() {
		return userTasksForms;
	}

	public Map<String, FormData> getStartEventsForms() {
		return startEventsForms;
	}
	
}
