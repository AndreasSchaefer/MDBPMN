package de.schaefer.mdbpmn.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormData {
	
	private String id;
	public Map<String, String> titles = new HashMap<String, String>();
	private boolean isStart;
	
	public List<FormField> formFields = new ArrayList<FormField>();
	public List<FormItem> formItems = new ArrayList<FormItem>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getTitles() {
		return titles;
	}

	public void setTitles(Map<String, String> titles) {
		this.titles = titles;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
}
