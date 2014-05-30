package com.example.multiplemaps;

import com.google.android.gms.maps.model.LatLng;

import android.widget.Toast;

public class parseKmlString {
	private String kmlString;
	private JSONObject jsonKML;
	private JSONObject kml;

	public parseKmlString(String kmlString) {
		this.kmlString = kmlString;
		// github下載的JSONObject
		jsonKML = XML.toJSONObject(kmlString);
		String s = jsonKML.toString();

		// 取得kml中所需的資料
	}

	public Boolean isKML() {
		try {
			kml = jsonKML.getJSONObject("kml");
			return true;
		} catch (JSONException e) {
			// kml 不存在
			return false;
		}
	}// isKML()

	public String getKmlType() {
		JSONObject placeMark = kml.getJSONObject("Document").getJSONObject(
				"Placemark");
		try {
			placeMark.getJSONObject("Polygon");
			return "polygon";
		} catch (JSONException e) {
			try {
				placeMark.getJSONObject("LineString");
				return "lineString";
			} catch (JSONException e1) {
				placeMark.getJSONObject("GroundOverlay");
				return "groundOverlay";
			}
		}
	}// end of getKmlType
}
