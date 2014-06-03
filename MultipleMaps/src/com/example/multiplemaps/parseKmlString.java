package com.example.multiplemaps;


public class parseKmlString {
	private String kmlString; //constructor
	private JSONObject jsonKML; 
	private JSONObject kml; //使用Kml開始取得檔案

	public parseKmlString(String kmlString) {
		this.kmlString = kmlString;
		// github下載的JSONObject
		jsonKML = XML.toJSONObject(kmlString);
		// 取得kml中所需的資料
	}

	public Boolean isKML() {
		if(jsonKML.has("kml")){
			kml = jsonKML.getJSONObject("kml");
			return true;
		}
		return false;
	}// isKML()
	
	
}
