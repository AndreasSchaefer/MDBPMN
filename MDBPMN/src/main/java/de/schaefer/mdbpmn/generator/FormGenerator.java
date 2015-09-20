package de.schaefer.mdbpmn.generator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.schaefer.mdbpmn.parser.FormData;
import de.schaefer.mdbpmn.parser.FormDescription;
import de.schaefer.mdbpmn.parser.FormField;
import de.schaefer.mdbpmn.parser.FormItem;
import de.schaefer.mdbpmn.parser.FormItemType;
import de.schaefer.mdbpmn.parser.Type;
import de.schaefer.mdbpmn.persistence.EntityReader;
import de.schaefer.mdbpmn.persistence.MDBPMN_Enum;

public class FormGenerator {

	//private FormData formData;
	private StringBuffer htmlCode = new StringBuffer();
	private static long idCounter;

	
	/**
	 * @param formData
	 * @param language
	 * @param processId
	 * @param target
	 * @param reader
	 * @param userTaskId
	 */
	public FormGenerator(FormData formData, String language, String processId, TargetPlatform target, EntityReader reader, String userTaskId) {
		language = language.toUpperCase();
		
		htmlCode.append("<div class=\"mdbpmnform\">");

		if (target == TargetPlatform.WEB) {
			htmlCode.append("<form class=\"form-horizontal\" id=\"" + formData.getId() + "\" name=\"" + formData.getId() + getActionString(formData.isStart()) + "\">");
			htmlCode.append("<div class=\"mdbpmntitle\">" + formData.getTitles().get(language) + "</div>");
		}

		for (FormItem formItem: formData.formItems) {
			if (formItem instanceof FormField) {
				FormField formField = (FormField)formItem;
				htmlCode.append("<div class=\"form-group\">");
				htmlCode.append("<label for=\"" + formField.getClazz() + "." + formField.getId() + "\">");
				htmlCode.append(formField.getLabels().get(language) + "</label>");

				if (!(formField.getType() == Type.LIST || formField.getType() == Type.MAP))
					htmlCode.append("<div >");

				if (formField.getType() == Type.BOOLEAN && "radio".equals(formField.getProperties().get("type"))) {
					String checkedYes = "", checkedNo = "", str = null;
					if (reader != null && reader.containsClass(formField.getClazz())) {
						str = reader.getFieldValueAsString(formField.getClazz(), formField.getId());
						if (str != null && (str.equals("true") || (str.equals("on"))))
							checkedYes = "checked";
						else if (str != null && (str.equals("false") || (str.equals("off"))))
							checkedNo = "checked";
					}
					StringBuffer sb = new StringBuffer();
					parseProperties(sb, formField);
					String properties = sb.toString();
					
					htmlCode.append("<input name=\"" + formField.getClazz() + "." + formField.getId() + "\" ");
					htmlCode.append("type=\"radio\" value=\"on\" " + checkedYes + properties + ">Yes");
					htmlCode.append("<input name=\"" + formField.getClazz() + "." + formField.getId() + "\" ");
					htmlCode.append("type=\"radio\" value=\"off\" " + checkedNo + properties + ">No</div></div>");
				} else if (formField.getType() == Type.BOOLEAN && "select".equals(formField.getProperties().get("type"))) {
					htmlCode.append("<select class=\"form-control\" name=\"" + formField.getClazz() + "." + formField.getId() + "\">");
					String selectedYes = "", selectedNo = "", str = null;
					if (reader != null && reader.containsClass(formField.getClazz())) {
						str = reader.getFieldValueAsString(formField.getClazz(), formField.getId());
						if (str != null && (str.equals("true") || (str.equals("on"))))
							selectedYes = "selected";
						else if (str != null && (str.equals("false") || (str.equals("off"))))
							selectedNo = "selected";
					}
					StringBuffer sb = new StringBuffer();
					parseProperties(sb, formField);
					String properties = sb.toString();
					
					htmlCode.append("<option value=\"on\" " + selectedYes + properties + ">Yes</option>");
					htmlCode.append("<option value=\"off\" " + selectedNo + properties + ">No</option></select></div></div>");
				} else if (formField.getType() == Type.BOOLEAN) {
					String selectedYes = "", str = null;
					if (reader != null && reader.containsClass(formField.getClazz())) {
						str = reader.getFieldValueAsString(formField.getClazz(), formField.getId());
						if (str != null && (str.equals("true") || (str.equals("on"))))
							selectedYes = "checked";
					}
					htmlCode.append("<input type=\"checkbox\" value=\"on\" name=\"" + formField.getClazz() + "." + formField.getId() + "\" " + selectedYes);
					parseProperties(htmlCode, formField);
					htmlCode.append(">Yes</div></div>");
				} else if (formField.getType() == Type.ENUM) {
					try {
						htmlCode.append("<select class=\"form-control\" name=\"" + formField.getClazz() + "." + formField.getId() + "\"");
						parseProperties(htmlCode, formField);
						htmlCode.append(">");
						Class<MDBPMN_Enum> enumm = (Class<MDBPMN_Enum>)Class.forName(formField.getDefinition());
						MDBPMN_Enum[] enums = enumm.getEnumConstants();
						String str = null;
						if (reader != null && reader.containsClass(formField.getClazz())) {
							str = reader.getFieldValueAsString(formField.getClazz(), formField.getId());
						}
						for (MDBPMN_Enum e: enums) {
							String selected = "";
							if (str != null && e.toString().equals(str)) {
								selected = "selected";
							}
							htmlCode.append("<option value=\"" + e + "\" " + selected + ">" + e.getTranslation(language) + "</option>");
						}
					} catch (Exception e) {e.printStackTrace();}
					htmlCode.append("</select></div></div>");
				} else if (formField.getType() == Type.LIST) {
					String id = formField.getId() + ++idCounter;
					String textfields = "";
					int i = 1;
					
					String readOnly = "";
					if (formField.getValidation().containsKey("READONLY"))
						readOnly = " readonly ";
					
					if ( reader != null && reader.containsClass(formField.getClazz())) {
						List<String> list = reader.getListValuesAsString(formField.getClazz(), formField.getId());
						if ( list != null) {
							for (String str: list) {
								textfields += "<input class=\"form-control\" " + readOnly + "type='text' id='" + id + "_Value" + i + "' name='" + formField.getClazz() + "." + formField.getId() + "_Value" + i + "' value='" + str + "'><br>";
								i++;
							}
						}
					}
					
					if (!formField.getValidation().containsKey("READONLY")) {
						int minsize, maxsize;
						if (formField.getValidation().containsKey("MINSIZE")) {
							minsize = Integer.parseInt(formField.getValidation().get("MINSIZE"));
						} else
							minsize = -1;
						if (formField.getValidation().containsKey("MAXSIZE")) {
							maxsize = Integer.parseInt(formField.getValidation().get("MAXSIZE"));
						} else
							maxsize = -1;

						htmlCode.append(" <input type=\"button\" value=\"-\" onClick=\"createListFields('" + formField.getClazz() + "." + formField.getId() +"',-1,'" + id +"'," + i + "," + minsize + "," + maxsize + ",'" + formField.getValueType().getStandardHtmlType() + "')\">");
						htmlCode.append("<input type=\"button\" value=\"+\" onClick=\"createListFields('" +formField.getClazz() + "." + formField.getId() +"',1,'" + id +"'," + i + "," + minsize + "," + maxsize + ",'" + formField.getValueType().getStandardHtmlType() + "')\">");
					}
					htmlCode.append("<div id=\"" + id + "DIV\">");
					htmlCode.append(textfields + "</div></div>");
				} else if (formField.getType() == Type.MAP) {
					String textfields = "";
					String id = formField.getId() + ++idCounter;
					int i = 1;
					String readOnly = "";
					if (formField.getValidation().containsKey("READONLY"))
						readOnly = " readonly ";
					if ( reader != null && reader.containsClass(formField.getClazz())) {
						Map<String, String> map = reader.getMapValuesAsString(formField.getClazz(), formField.getId());
						if ( map != null) {
							for (Entry<String, String> entry: map.entrySet()) {
								textfields += "<div class=\"mdbpmnkey\"><input class=\"form-control\"" + readOnly + "type='" + formField.getKeyType().getStandardHtmlType()  + "' id='" + id + "_Key" + i + "' name='" + formField.getClazz() + "." + formField.getId() + "_Key" + i + "' value='" + entry.getKey() + "'></div>";
								textfields += "<div class=\"mdbpmnvalue\"><input class=\"form-control\"" + readOnly + "type='" + formField.getValueType().getStandardHtmlType()  + "' id='" + id + "_Value" + i + "' name='" + formField.getClazz() + "." + formField.getId() + "_Value" + i + "' value='" + entry.getValue() + "'></div>";
								textfields += "<div style=\"clear: both;\"></div>";
								i++;
							}
						}
					}
					int minsize, maxsize;
					if (!formField.getValidation().containsKey("READONLY")) {
						if (formField.getValidation().containsKey("MINSIZE")) {
							minsize = Integer.parseInt(formField.getValidation().get("MINSIZE"));
						} else
							minsize = -1;
						if (formField.getValidation().containsKey("MAXSIZE")) {
							maxsize = Integer.parseInt(formField.getValidation().get("MAXSIZE"));
						} else
							maxsize = -1;

						htmlCode.append(" <input type=\"button\" value=\"-\" onClick=\"createMapFields('" + formField.getClazz() + "." + formField.getId() +"', -1,'" + id +"'," + i + "," + minsize + "," + maxsize + ",'" + formField.getKeyType().getStandardHtmlType() + "','" + formField.getValueType().getStandardHtmlType() + "')\">");
						htmlCode.append("<input type=\"button\" value=\"+\" onClick=\"createMapFields('" + formField.getClazz() + "." + formField.getId() +"', 1,'" + id +"'," + i + "," + minsize + "," + maxsize + ",'" + formField.getKeyType().getStandardHtmlType() + "','" + formField.getValueType().getStandardHtmlType() + "')\">");
					}
					htmlCode.append("<div id=\"" + id + "DIV\">");
					htmlCode.append(textfields + "</div></div>");
				} else {
					htmlCode.append("<input class=\"form-control\" name=\"" + formField.getClazz() + "." + formField.getId() + "\" ");
					htmlCode.append("id=\"" + formField.getClazz() + "." + formField.getId() + "\" ");

					if (!formField.getProperties().containsKey("type"))
						htmlCode.append("type=\"" + formField.getType().getStandardHtmlType() + "\" ");
					
					if (formField.getType().getStandardHtmlType().equals("number")) {
						if (formField.getValidation().containsKey("MIN"))
								htmlCode.append("min=\"" + formField.getValidation().get("MIN") + "\" ");

						if (formField.getValidation().containsKey("MAX"))
								htmlCode.append("max=\"" + formField.getValidation().get("MAX") + "\" ");

						if (formField.getType() == Type.FLOAT || formField.getType() == Type.DOUBLE)
							htmlCode.append("onkeypress=\"validateDigit(event, 0)\" ");
						else
							htmlCode.append("onkeypress=\"validateDigit(event, 1)\" ");
					} else {
						if (formField.getValidation().containsKey("MINLENGTH"))
							htmlCode.append("minlength=\"" + formField.getValidation().get("MINLENGTH") + "\" ");

						if (formField.getValidation().containsKey("MAXLENGTH"))
							htmlCode.append("maxlength=\"" + formField.getValidation().get("MAXLENGTH") + "\" ");
					}

					if (reader != null && reader.containsClass(formField.getClazz())) {
						htmlCode.append("value=\"" + reader.getFieldValueAsString(formField.getClazz(), formField.getId()) + "\" ");
					}

					parseProperties(htmlCode, formItem);
					htmlCode.append("></div></div>");
				}
			} else if (formItem instanceof FormDescription) {
				FormDescription formDescription = (FormDescription)formItem;
				htmlCode.append("<div ");
				parseProperties(htmlCode, formItem);
				htmlCode.append(">" + formDescription.getLabels().get(language));
				htmlCode.append("</div>");
			} else {
				if (formItem.getFormItemType() == FormItemType.COLUMN_BEGIN) {
					htmlCode.append("<div class=\"mdbpmncolumn\"");
					parseProperties(htmlCode, formItem);
					htmlCode.append(">");
				} else if (formItem.getFormItemType() == FormItemType.NEW_ROW) {
					htmlCode.append("<div class=\"mdbpmnrow\"");
					parseProperties(htmlCode, formItem);
					htmlCode.append("></div>");
				} else if (formItem.getFormItemType() == FormItemType.COLUMN_END) {
					htmlCode.append("</div>");
				} else if (formItem.getFormItemType() == FormItemType.LINE) {
					htmlCode.append("<hr class=\"mdbpmnline\"");
					parseProperties(htmlCode, formItem);
					htmlCode.append(">");
				}
			}
		}

		htmlCode.append("<input type=\"hidden\" name=\"processId\" value=\"" + processId + "\">" );
		htmlCode.append("<input type=\"hidden\" name=\"formId\" value=\"" + formData.getId() + "\">" );
		htmlCode.append("<input type=\"hidden\" name=\"language\" value=\"" + language + "\">" );
		htmlCode.append("<input type=\"hidden\" name=\"taskId\" value=\"" + userTaskId + "\">" );
		htmlCode.append("<input type=\"hidden\" name=\"saveOnly\" value=\"false\">" );
		
		boolean controlProcessEngine = (target == TargetPlatform.WEB);
		htmlCode.append("<input type=\"hidden\" name=\"controlProcessEngine\" value=\"" + controlProcessEngine + "\">" );
		
		htmlCode.append("<div style=\"clear: both;\"></div>");
		if (formData.isStart())
			htmlCode.append("<div id=\"errorMessagesStart\" class=\"alert alert-danger\" style=\"display: none;\" ></div>");
		else
			htmlCode.append("<div id=\"errorMessages\" class=\"alert alert-danger\" style=\"display: none;\" ></div>");
		
		if (target == TargetPlatform.WEB && !formData.isStart())
			htmlCode.append("<input class=\"btn btn-success\" type=\"button\" onClick=\"submitFormSaveOnly('" + formData.getId() + "');\" value=\"Speichern\"/>");
		
		if (target == TargetPlatform.CAMUNDA_TASKLIST && !formData.isStart())
			htmlCode.append("<input style=\"position: absolute; right: 90px; z-index: 999;\" class=\"btn btn-success\" type=\"button\" onClick=\"submitFormSaveOnly('userTask');\" value=\"Speichern\"/>");
		
		if (target == TargetPlatform.WEB)
			htmlCode.append("<input class=\"btn btn-success\" type=\"button\" onClick=\"submitForm('" + formData.getId() + "');\" value=\"Absenden\"/></form>");
		htmlCode.append("</div>");
	}
	
	private void parseProperties(StringBuffer htmlCode, FormItem formItem) {
		if (formItem instanceof FormField) {
			FormField formField = (FormField)formItem;
			if (formField.getValidation().containsKey("READONLY"))
				htmlCode.append(" readonly ");
		}
		for (Entry<String, String> entry : formItem.getProperties().entrySet()) {
			htmlCode.append(" " + entry.getKey());
			if (entry.getValue() != null)
				htmlCode.append("=\"" + entry.getValue() + "\" ");
		}
	}

	/**
	 * Get the form in HTML format
	 * @return HTML Code of Form
	 */
	public String getHTMLCode() {
		return htmlCode.toString();
	}

	private String getActionString(boolean isStart) {
		return "\" method=\"POST\" action=\"http://localhost:8080/camunda-get-started-master/StartProcessServlet";
	}
}
