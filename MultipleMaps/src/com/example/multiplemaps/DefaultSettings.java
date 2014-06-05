package com.example.multiplemaps;

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
	private static final int TWO_Map = 3;// up 1, low 2, two 3
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

	public void setUpperMapLayoutFrom(String id) {
		defaultSettings.putString(UPPER_MAP_LAYOUT, id);
		defaultSettings.commit();
	}// end of setUpperMapLayout

	public String getUpperMapLayout() {
		return sharedSettings.getString(UPPER_MAP_LAYOUT, "1");
	}// end of getUpperMapLayout

	public void setLowerMapLayoutFrom(String id) {
		defaultSettings.putString(LOWER_MAP_LAYOUT, id);
		defaultSettings.commit();
	}// end of setLowerMapLayout

	public String getLowerMapLayout() {
		return sharedSettings.getString(LOWER_MAP_LAYOUT, "1");
	}// end of getLowerMapLayout
	
	//存LayoutManage UpperSpinner選擇的項目
	public void setUpperSpinnerPosition(int position) {
		defaultSettings.putInt(UPPER_MAP_SPINNER_POSITION, position);
		defaultSettings.commit();
	}// end of setUpperSpinnerPosition
	
	//存LayoutManage LowerSpinner選擇的項目
	public void setLowerSpinnerPosition(int position) {
		defaultSettings.putInt(LOWER_MAP_SPINNER_POSITION, position);
		defaultSettings.commit();
	}// end of setLowerSpinnerPosition
	
	//取LayoutManage UpperSpinner選擇的項目
	public int getUpperSpinnerPosition() {
		return sharedSettings.getInt(UPPER_MAP_SPINNER_POSITION, 1);
	}// end of getUpperSpinnerPosition
	
	//取LayoutManage LowerSpinner選擇的項目
	public int getLowerSpinnerPosition() {
		return sharedSettings.getInt(LOWER_MAP_SPINNER_POSITION, 1);
	}// end of getLowerSpinnerPosition

}// end of DefaultSettings
