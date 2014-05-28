package com.example.multiplemaps;

public class Layout {
	private String id;
	private String title;
	private String desc;
	private String inputType;
	private String source;

	public Layout() {
	}

	public Layout(String title, String desc, String inputType,String source) {
		super();
		this.title = title;
		this.desc = desc;
		this.inputType = inputType;
		this.source = source;
	}

	@Override
	public String toString() {
		return String.format("[Id: %s,Title: %s, Description: %s, InputType: %s, Source: %s]",
				id, title, desc, inputType, source);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDesc() {
		return desc;
	}

	public String getInputType() {
		return inputType;
	}
	
	public String getSource() {
		return source;
	}
	

	public void setId(String id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	public void setSource(String source){
		this.source = source;
	}
}
