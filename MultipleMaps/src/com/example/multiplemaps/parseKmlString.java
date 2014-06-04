package com.example.multiplemaps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

/**
 * 處理kmlString
 * 
 */
public class parseKmlString {
	private String kmlString; // constructor
	private JSONObject jsonKML;
	private JSONObject kml; // 使用Kml開始取得檔案

	public parseKmlString(String kmlString) {
		this.kmlString = kmlString;
		// github下載的JSONObject
		jsonKML = XML.toJSONObject(kmlString);
		// 取得kml中所需的資料
	}

	/**
	 * @return true = kml ; false = not;
	 */
	public Boolean isKML() {
		if (jsonKML.has("kml")) {
			kml = jsonKML.getJSONObject("kml");
			return true;
		}
		return false;
	}// isKML()

	public boolean hasDocument() {
		if (kml.has("Document")) {
			return true;
		}
		return false;
	}// end of hasDocument
	
	/**
	 * 取得kmlSting(JSON)，後抓取coordinates Tag
	 * @return ArrayList LatLng
	 */
	public ArrayList<LatLng> getCoordinates() {
		if (hasDocument()) {
			try {
				String coordinates = kml.getJSONObject("Document")
						.getJSONObject("Placemark").getJSONObject("Polygon")
						.getJSONObject("outerBoundaryIs")
						.getJSONObject("LinearRing").getString("coordinates");
				// Log.d("mdb", "getCoordinates: " + coordinates);

				// 取出的kmlString轉為list，split用 | 分隔使用的分隔符號
				List<String> listStringCoordinates = new ArrayList<String>(
						Arrays.asList(coordinates.split(",| ")));
				
				ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

				int length = listStringCoordinates.size();

				for (int i = 0; i < length - 1; i += 3) {
					// 取kmlString的coordinates 轉double
					// d1為longitude
					double longitude = Double.valueOf(listStringCoordinates
							.get(i));
					// d2為latitude
					double latitude = Double.valueOf(listStringCoordinates
							.get(i + 1));
					LatLng latLng = new LatLng(latitude, longitude);

					// 放LatLng到ArrayList
					latLngs.add(latLng);
				}
				return latLngs;
			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的coordinates，就是拿掉document的jsonarray
			return null;
		}
	}// end of getCoordiantes
	
	public String getDescription(){
		if (hasDocument()) {
			try {
				String description = kml.getJSONObject("Document")
						.getJSONObject("Placemark").getString("description");
				return description;
			} catch (JSONException e) {
				Log.d("mdb", "parserkmlString class," + e.toString());
				return null;
			}
		} else {
			// TODO 如果沒有DOCUMENT的description
			return null;
		}
	}
	
}// end of parseKmlString

