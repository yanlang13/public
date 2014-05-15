package com.example.multiplemaps;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * 透過sharedPrederences存取各種defaultSettings，主要為地圖顯示方式與內容。
 */
public class DefaultSettings {
	
	private static final String DISPLAY_MODE = "Dispaly Mode"; // key value
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

}// end of  DefaultSettings
