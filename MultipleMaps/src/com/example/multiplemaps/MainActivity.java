package com.example.multiplemaps;

import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private GoogleMap upperMap, lowerMap;
	private boolean upperMapStopper = false;
	private boolean lowerMapStopper = false;
	// user的點擊位置，放到HashMap中，目標是一次只顯示一個。
	private HashMap<String, Circle> userCircle = new HashMap<String, Circle>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}// end of onCreate

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}// end of onResume()

	@Override
	protected void onStop() {
		super.onStop();
		saveTheLastCameraPosition();
	}// end of onStop

	// ====================================================================onResuming
	// ===== 確認地圖有無正確讀取
	private void setUpMapIfNeeded() { // call from onResume()
		if (upperMap == null || lowerMap == null) {
			upperMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.UpperMap)).getMap();
			lowerMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.lowerMap)).getMap();
			if (upperMap != null && lowerMap != null) {
				// 存取後執行
				callTheLastCameraPosition();
				syncTwoMapCameraPosition();
				whereUserClicked();
			}// end of if
		}// end of setUpMapIfNeeded()
	}

	// ===== 抓關閉前的點cameraPosition
	private void callTheLastCameraPosition() { // call from setUpMapIfNeeded()
		SharedPreferences sp = getSharedPreferences("theLastCameraPosition",
				Context.MODE_PRIVATE);
		double latitude = Double.valueOf(sp.getString("latitude", "0.0"));
		double longitude = Double.valueOf(sp.getString("longitude", "0.0"));
		float tilt = sp.getFloat("tilt", 0);
		float bearing = sp.getFloat("bearing", 0);
		float zoom = sp.getFloat("zoom", 0);
		LatLng target = new LatLng(latitude, longitude);
		CameraPosition cp = new CameraPosition(target, zoom, tilt, bearing);
		// 一次修改兩個地圖的位置，這樣可以讓cameraChange的功能不影響到calltheLastCP的結果
		upperMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
		lowerMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
	}// end of callTheLastCameraPostion()

	// ===== 同步移動cameraPosition
	private void syncTwoMapCameraPosition() { // call from setUpMapIfNeeded()
		upperMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (!upperMapStopper) {
					// 停止lowerMap移動(不停指的話，系統會以為使用者一直操作)
					lowerMapStopper = true;
					lowerMap.moveCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
				// 開啟upperMap的移動功能，讓下次移動能改變lower的位置
				upperMapStopper = false;
			}
		}); // end of upperMap.setOnCameraChangeListener

		lowerMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				if (!lowerMapStopper) {
					upperMapStopper = true;
					upperMap.moveCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
				lowerMapStopper = false;
			}
		});// end of lowerMap.setOnCameraChangeListener
	}// end of syncTwoMap

	// =====輕點顯示點擊位置
	private void whereUserClicked() {
		upperMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng geoPoint) {
				// 如果HashMap為空
				if (userCircle.isEmpty()) {
					displayUserClicked(upperMap, geoPoint);
				} else {
					// 如果HashMap有circle，就取出來刪除(裡面只會有一個，所以不用collection?)。
					Circle uCircle = userCircle.get("uClick");
					uCircle.remove();
					Circle lCircle = userCircle.get("lClick");
					lCircle.remove();
					displayUserClicked(upperMap, geoPoint);
				}
			}
		}); // end of upperMap.setOnMapClickListener
		lowerMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng geoPoint) {
				if (userCircle.isEmpty()) {
					displayUserClicked(lowerMap, geoPoint);
				} else {
					Circle uCircle = userCircle.get("uClick");
					uCircle.remove();
					Circle lCircle = userCircle.get("lClick");
					lCircle.remove();
					displayUserClicked(lowerMap, geoPoint);
				}
			}
		}); // end of lowerMap.setOnMapClickListener
	}// end of showUserClicked()

	// 在user點擊位置，顯示圓圈。透過location class讓這個circle不至於失控。
	private void displayUserClicked(GoogleMap gMap, LatLng geoPoint) { // call
		// from
		// "left"是為lfetLocation命名
		Location leftLocation = new Location("left");
		// getProjection用來轉換螢幕座標(pixels)與地圖座標(LatLng)
		// getVisibleRegion(): four-sided polygon that is visible in a map's
		// camera.
		leftLocation
				.setLatitude(gMap.getProjection().getVisibleRegion().farLeft.latitude);
		leftLocation
				.setLongitude(gMap.getProjection().getVisibleRegion().farLeft.longitude);

		Location rightLocation = new Location("rifht");
		rightLocation
				.setLatitude(gMap.getProjection().getVisibleRegion().farRight.latitude);
		rightLocation
				.setLongitude(gMap.getProjection().getVisibleRegion().farRight.longitude);

		float viewDistance = leftLocation.distanceTo(rightLocation);
		double radius = viewDistance / 1000;

		CircleOptions co = new CircleOptions();
		co.center(geoPoint);
		co.radius(radius);
		// 要用getResources().getColor(R.color...)，才能正確獨到顏色。
		// 只用R.color不會顯示錯誤，但不會有顏色。
		co.fillColor(getResources().getColor(R.color.lava_red));
		co.strokeColor(getResources().getColor(R.color.lava_red));
		Circle uCircle = upperMap.addCircle(co);
		userCircle.put("uClick", uCircle);
		Circle lCircle = lowerMap.addCircle(co);
		userCircle.put("lClick", lCircle);
	} // end of displayUserClicked

	// ====================================================================onResumed

	// ====================================================================onStoping
	private void saveTheLastCameraPosition() { // call from onStop
		CameraPosition cpUpperMap = upperMap.getCameraPosition();
		SharedPreferences sp = getSharedPreferences("theLastCameraPosition",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.putString("latitude", String.valueOf(cpUpperMap.target.latitude));
		spe.putString("longitude", String.valueOf(cpUpperMap.target.longitude));
		spe.putFloat("tilt", cpUpperMap.tilt);
		spe.putFloat("bearing", cpUpperMap.bearing);
		spe.putFloat("zoom", cpUpperMap.zoom);
		spe.commit();
	}// end of saveTheLastCameraPosition()

	// ====================================================================onStopinged

	// ====================================================================MenuS
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end of onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected

	// ====================================================================MenuE
}// end of MainActivity

