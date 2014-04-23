package com.example.multiplemaps;

import java.util.HashMap;
import com.example.multiplemaps.MapTools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionCallbacks,
		LocationListener, OnMyLocationButtonClickListener,
		OnConnectionFailedListener {
	private MapTools mapTools = new MapTools();
	private ProgressDialog progressDialog;
	private GoogleMap upperMap, lowerMap;
	private boolean upperMapStopper = false;
	private boolean lowerMapStopper = false;
	// user的點擊位置，放到HashMap中，目標是一次只顯示一個。
	private HashMap<String, Circle> userCircle = new HashMap<String, Circle>();
	private LocationClient mLocationClient;
	// 處理LocationClient的品質
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000).setFastestInterval(16)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private EditText etSearch; // 接收輸入的地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressDialog = new ProgressDialog(this);
	}// end of onCreate

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();

	}// end of onResume()

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}// end of onPause()

	@Override
	protected void onStop() {
		super.onStop();
		mapTools.saveTheLastCameraPosition(getApplicationContext(), upperMap,
				"theLastCameraPosition");
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
				mapTools.callTheLastCameraPosition(getApplicationContext(),
						upperMap, "theLastCameraPosition");
				syncTwoMapCameraPosition();
				whereUserClicked();
				userUiSetting();
			}// end of if
		}// end of setUpMapIfNeeded()
	}

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
	private void whereUserClicked() { // call from setUpMapIfNeeded
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
		float viewDistance = mapTools.getViewRegionHorizontalDistance(gMap);
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

	// userUiSetting
	private void userUiSetting() { // call from call from setUpMapIfNeeded
		upperMap.setMyLocationEnabled(true);
		upperMap.setOnMyLocationButtonClickListener(this);
	}// end of userUiSetting

	//
	private void setUpLocationClientIfNeeded() { // call from onResume
		if (mLocationClient == null) {
			// ConnectionCallback and OnConnectionFailedListener
			mLocationClient = new LocationClient(getApplicationContext(), this,
					this);
		}
	}// end of setUpLocationClientIfNeeded()

	// ====================================================================onResumed

	// ====================================================================onStoping

	// ====================================================================onStopinged

	// ====================================================================MenuING
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
		} else if (id == R.id.action_search) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
					MainActivity.this);
			LayoutInflater inflater = this.getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.action_search, null);
			// 取得輸入的地址
			etSearch = (EditText) dialogView.findViewById(R.id.address_search);
			alertBuilder.setView(dialogView);
			alertBuilder.setPositiveButton("Search",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Ensure that a Geocoder services is available
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
									&& Geocoder.isPresent()) {
								new GetAddressTask().execute(etSearch.getText().toString());
							}
						}
					});

			alertBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			AlertDialog alertDialog = alertBuilder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected

	private class GetAddressTask extends AddressTask {
		@Override
		protected void onPreExecute() {
			// progressDialog.show();
		}

		@Override
		protected void onPostExecute(LatLngBounds bounds) {
			Log.d("mdb", "onPostExecute");
			if (bounds != null) {
				//bounds, pidding
				upperMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
				// progressDialog.dismiss();
			} else {
				Toast.makeText(MainActivity.this, "wrong address format",
						Toast.LENGTH_SHORT).show();
				// progressDialog.dismiss();
			}
		}// end of onPostExecute
	}// end of GetAddressTask
		// ====================================================================MenuED

	// ====================================================================Overriding
	@Override
	// ConnectionCallbacks
	public void onConnected(Bundle arg0) {
		// this 是指LocationListener
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}// end of onConnected

	@Override
	// ConnectionCallbacks
	public void onDisconnected() {
		Toast.makeText(getApplication(), "LocationClient is disconnected",
				Toast.LENGTH_SHORT).show();
	}// end of onDisconnected

	@Override
	// LocationListener
	public void onLocationChanged(Location locaion) {
		Log.d("mdb", "in onLocationChanged");
	}// end of on onLocationChanged

	@Override
	// OnMyLocationButtonClickListener
	public boolean onMyLocationButtonClick() {
		// The default behavior is for the camera move such that it is centered
		// on the user location.
		// 先確認GPS和network定位的服務有無開啟，locationManager也是另一種開啟GPS定位的方法
		LocationManager status = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 連接服務，等待 onConnected時再將locationRequest的設定值交出
			mLocationClient.connect();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("Waring");
			builder.setMessage("GPS services are turned off on your device. Do you want to go to yout Location settings now?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 這裡的settings是android.provider.Settings
							startActivity(new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			AlertDialog alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
		}
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}// end of if

	@Override
	// OnConnectionFailedListener
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("mdb", "in onConnectionFailed");
	}// end of onConnectionFailed

	// ====================================================================OverrideD
}// end of MainActivity

