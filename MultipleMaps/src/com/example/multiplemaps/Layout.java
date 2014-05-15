package com.example.multiplemaps;

public class Layout {
	private String id;
	private String title;
	private String desc;
	private String mapURL;

	public Layout() {
	}

	public Layout(String title, String desc, String mapURL) {
		super();
		this.title = title;
		this.desc = desc;
		this.mapURL = mapURL;
	}

	@Override
	public String toString() {
		return String.format(
				"[Title: %s, Description: %s, MapURL: %s]", title,
				desc, mapURL);
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

	public String getMapURL() {
		return mapURL;
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

	public void setMapURL(String mapURL) {
		this.mapURL = mapURL;
	}
}
