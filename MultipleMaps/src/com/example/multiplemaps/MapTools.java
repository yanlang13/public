package com.example.multiplemaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

	/**
	 * 將SharedPreferences中的CameraPosition叫回。
	 */
	public void callTheLastCameraPosition(Context context, GoogleMap map,
			String SPName) { // call from setUpMapIfNeeded()
		SharedPreferences sp = context.getSharedPreferences(SPName,
				Context.MODE_PRIVATE);
		double latitude = Double.valueOf(sp.getString("latitude", "0.0"));
		double longitude = Double.valueOf(sp.getString("longitude", "0.0"));
		float tilt = sp.getFloat("tilt", 0);
		float bearing = sp.getFloat("bearing", 0);
		float zoom = sp.getFloat("zoom", 0);
		LatLng target = new LatLng(latitude, longitude);
		CameraPosition cp = new CameraPosition(target, zoom, tilt, bearing);
		// 一次修改兩個地圖的位置，這樣可以讓cameraChange的功能不影響到calltheLastCP的結果
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
	}// end of callTheLastCameraPostion()

	/**
	 * 取得當下ViewRegin的兩邊距離(公尺，float)
	 */
	public float getViewRegionHorizontalDistance(GoogleMap map) {
		// "left"是為lfetLocation命名
		Location leftLocation = new Location("left");
		// getProjection用來轉換螢幕座標(pixels)與地圖座標(LatLng)
		// getVisibleRegion(): four-sided polygon that is visible in a map's
		// camera.
		leftLocation
				.setLatitude(map.getProjection().getVisibleRegion().farLeft.latitude);
		leftLocation
				.setLongitude(map.getProjection().getVisibleRegion().farLeft.longitude);

		Location rightLocation = new Location("rifht");
		rightLocation
				.setLatitude(map.getProjection().getVisibleRegion().farRight.latitude);
		rightLocation
				.setLongitude(map.getProjection().getVisibleRegion().farRight.longitude);
		return leftLocation.distanceTo(rightLocation);
	}// end of getViewRegionHorizontalDistance
}
