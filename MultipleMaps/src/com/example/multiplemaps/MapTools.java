package com.example.multiplemaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

public class MapTools {
	/**
     * 將關閉時的CameraPosition存到SharedPreferences中。
     */
	public void saveTheLastCameraPosition(Context context, GoogleMap map,
			String SPName) {
		CameraPosition cp = map.getCameraPosition();
		SharedPreferences sp = context.getSharedPreferences(SPName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.putString("latitude", String.valueOf(cp.target.latitude));
		spe.putString("longitude", String.valueOf(cp.target.longitude));
		spe.putFloat("tilt", cp.tilt);
		spe.putFloat("bearing", cp.bearing);
		spe.putFloat("zoom", cp.zoom);
		spe.commit();
	} // end of saveTheLastCameraPosition()
}
