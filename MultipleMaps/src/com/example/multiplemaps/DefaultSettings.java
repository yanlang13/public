package com.example.multiplemaps;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;

/*
 * 透過sharedPrederences存取各種defaultSettings，主要為地圖顯示方式與內容。
 */
public class DefaultSettings {

	private static final String DISPLAY_MODE = "Dispaly Mode"; // key value
	private static final String UPPER_MAP_LAYOUT = "Upper Map Layout";
	private static final String LOWER_MAP_LAYOUT = "Lower Map Layout";
	private static final String UPPER_MAP_SPINNER_POSITION = "Upper Spinner Position";
	private static final String LOWER_MAP_SPINNER_POSITION = "Lower Spinner Position";
	private static final int U_MAP = 1; // single map: upperMap
	private static final int L_Map = 2;// single map: lowerMap
	private static final int TWO_Map = 3;// show two map
	private SharedPreferences sharedSettings; // 各種UI設定存檔
	private SharedPreferences.Editor defaultSettings;// 各種UI設定存檔

	public DefaultSettings(Context context) {
		this.sharedSettings = context.getSharedPreferences("DefaultSettings",
				Context.MODE_PRIVATE);
		this.defaultSettings = sharedSettings.edit();
	}

	public int getDisMode() {
		return sharedSettings.getInt(DISPLAY_MODE, TWO_Map);
	}// end of getDisMode

	public void setDisMode(int disMode) {
		defaultSettings.putInt(DISPLAY_MODE, disMode);
		defaultSettings.commit();
	}// end of setDisMode

	public void setUpperMapLayout(String type) {
		defaultSettings.putString(UPPER_MAP_LAYOUT, type);
		defaultSettings.commit();
	}// end of setUpperMapLayout

	public String getUpperMapLayout() {
		return sharedSettings.getString(UPPER_MAP_LAYOUT, "GoogleMap NORMAL");
	}// end of getUpperMapLayout

	public void setLowerMapLayout(String type) {
		defaultSettings.putString(LOWER_MAP_LAYOUT, type);
		defaultSettings.commit();
	}// end of setLowerMapLayout

	public String getLowerMapLayout() {
		return sharedSettings.getString(LOWER_MAP_LAYOUT, "GoogleMap NORMAL");
	}// end of getLowerMapLayout

	public void setUpperSpinnerPosition(int position) {
		defaultSettings.putInt(UPPER_MAP_SPINNER_POSITION, position);
	}// end of setUpperSpinnerPosition

	public void setLowerSpinnerPosition(int position) {
		defaultSettings.putInt(LOWER_MAP_SPINNER_POSITION, position);
	}// end of setLowerSpinnerPosition

	public int getUpperSpinnerPosition() {
		return sharedSettings.getInt(UPPER_MAP_SPINNER_POSITION, 1);
	}// end of getUpperSpinnerPosition

	public int getLowerSpinnerPosition() {
		return sharedSettings.getInt(LOWER_MAP_SPINNER_POSITION, 1);
	}// end of getLowerSpinnerPosition

}// end of DefaultSettings
