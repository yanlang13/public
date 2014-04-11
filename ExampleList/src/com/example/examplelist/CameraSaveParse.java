package com.example.examplelist;


import java.text.SimpleDateFormat;
import java.util.Date;


import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CameraSave")
public class CameraSaveParse extends ParseObject {
	
	public void setName(String name) {
		put("Name", name);
	}// end of setName

	public void setDesc(String desc) {
		put("Description", desc);
	} // end of setDesc

	public void setLatitude(String lat) {
		put("Latitude", lat);
	}// end of setLatitude

	public void setLongtitude(String lng) {
		put("Longtitude", lng);
	}// end of setLongtitude

	public void setBearing(float bearing) {
		put("Bearing", bearing);
	}// end of setBearing

	public void setTilt(float tilt) {
		put("Tilt", tilt);
	}// end of setTilt

	public void setZoom(float zoom) {
		put("Zoom", zoom);
	}// end of setZoom

	// ===================================================
	public String getDate(){
		//PARSE 是GMT 0:00
		Date date = getUpdatedAt();
		//這個轉換，似乎會參考各地的時間
		SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss, yyyy/MM/dd");
		return sDateFormat.format(date).toString();
	}//end of getDate
	
	public String getName() {
		return getString("Name");
	}// end of getName

	public String getDesc() {
		return getString("Description");
	} // end of getDesc

	public String getLatitude() {
		return getString("Latitude");
	}// end of getLatitude

	public String getLongtitude() {
		return getString("Longtitude");
	}// end of getLongtitude

	public float getBearing() {
		String s = getString("Bearing");
		return Float.parseFloat(s);
	}// end of getBearing

	public float getTilt() {
		String s = getString("Tilt");
		return Float.parseFloat(s);
	}// end of getTilt

	public float getZoom() {
		String s = getString("Zoom");
		return Float.parseFloat(s);
	}// end of getZoom
	
}// end of CameraPositionParse

