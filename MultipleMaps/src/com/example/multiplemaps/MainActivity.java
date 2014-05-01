package com.example.multiplemaps;

import java.util.HashMap;

import javax.security.auth.PrivateCredentialPermission;

import com.example.multiplemaps.MapTools;
import com.example.multiplemaps.R.string;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
	private HashMap<String, Marker> centerMarker = new HashMap<String, Marker>();

	private LocationClient mLocationClient;
	// 處理LocationClient的品質
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000).setFastestInterval(16)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private EditText etSearch; // 接收輸入的地址

	// 有關sliding menu
	private String[] drawerTitles; // listView的內容
	private DrawerLayout drawerLayout;
	private ListView drawerList; // listView的view
	private ActionBarDrawerToggle actionBarDrawerToggle; // drawerLayout的listener
	private CharSequence title; // action bar title

	// ====================================================================Declared

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressDialog = new ProgressDialog(this);

		setLeftDrawer();
	}// end of onCreate

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// 寫入設定的actionBarDrawerToggle
		actionBarDrawerToggle.syncState();
	}

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

	// onConfigurationChanged
	// 是指狀態改變時(ex:跳出鍵盤、螢幕旋轉等)，會導致activity被destory後再重新設定actionBarDrawerToggle的
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	// ====================================================================onCreating
	private void setLeftDrawer() {
		title = getTitle(); // 用來讓user知道目前的所在位置
		drawerTitles = getResources().getStringArray(R.array.drawer_array);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		// START=>Push object to x-axis position at the start of its container,
		// not changing its size.
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// set up the drawer's list view with items and click listener
		drawerList.setAdapter(new DrawerArrayAdapter(this, DrawerList.LIST));
//		drawerList.setAdapter(new ArrayAdapter<String>(this,
//				R.layout.drawer_list_item, drawerTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon/*
		// ic_drawer是取代up的drawer
		// 因為actionBarDrawerToggle已經implement了
		// DrawerLayout.DrawerListener，所以可以override DrawerListener的method
		// ic_drawer的顯示位置，是交由.png檔所決定
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(title);
				// 因為drawer所以改變了menu，會再call onCreateOptionsMenu
				invalidateOptionsMenu();
			}

			public void onDrawerClosed(View drawerView) {
				getActionBar().setTitle(title);
				// 因為drawer所以改變了menu，會再call onCreateOptionsMenu
				invalidateOptionsMenu();
			}
		};

		// 讀入actionBarDrawerToggle
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		// 未使用，看起來是確保selectItem不會出錯
		// if (savedInstanceState == null) {
		// selectItem(0);
		// }
	}

	// ====================================================================onCreated

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
				// 偵測user點擊位置，再顯示小紅點。
				whereUserClicked(upperMap);
				whereUserClicked(lowerMap);
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

	/**
	 * map onMapClick時，1.顯示點擊位置
	 */
	private void whereUserClicked(GoogleMap map) { // call from setUpMapIfNeeded
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng geoPoint) {
				// 1.顯示點擊位置
				displayUserClicked(upperMap, geoPoint);
				displayUserClicked(lowerMap, geoPoint);
			}
		});
	}// end of whereUserClicked

	// 在user點擊位置，顯示圓圈。透過location class讓這個circle不至於失控。
	private void displayUserClicked(GoogleMap map, LatLng geoPoint) { // call
																		// from
																		// whereUserClicked
		float viewDistance = mapTools.getViewRegionHorizontalDistance(map);
		double radius = viewDistance / 1000;

		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(geoPoint);
		circleOptions.radius(radius);
		// 要用getResources().getColor(R.color...)，才能正確獨到顏色。
		// 只用R.color不會顯示錯誤，但不會有顏色。
		circleOptions.fillColor(getResources().getColor(R.color.lava_red));
		circleOptions.strokeColor(getResources().getColor(R.color.lava_red));

		String key = String.valueOf(map.hashCode());
		if (userCircle.containsKey(key)) {
			userCircle.get(key).remove();
			Circle tempCircle = map.addCircle(circleOptions);
			userCircle.put(key, tempCircle);
		} else {
			Circle tempCircle = map.addCircle(circleOptions);
			userCircle.put(key, tempCircle);
		}
	}

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

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		// if it returns true, then it has handled the app icon touch event

		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
								new GetAddressTask().execute(etSearch.getText()
										.toString());
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
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(LatLngBounds bounds) {
			if (bounds != null) {
				// bounds, pidding
				upperMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
						0));
				displayBoundMarker(upperMap, bounds.getCenter());
				displayBoundMarker(lowerMap, bounds.getCenter());
				progressDialog.dismiss();
			} else {
				Toast.makeText(MainActivity.this, "wrong address format",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}
		}// end of onPostExecute

		/**
		 * 做中心marker
		 */
		private void displayBoundMarker(GoogleMap map, LatLng position) { // call
																			// from
																			// onPostExecute
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.snippet(etSearch.getText().toString());
			markerOptions.title("Input:");
			markerOptions.infoWindowAnchor(0.5f, 0.5f);
			markerOptions.position(position);

			// 運用每個 obejct獨有的hashCode作為建立HashMap的Key，可以獨立存取值。
			String key = String.valueOf(map.hashCode());
			if (centerMarker.containsKey(key)) {
				centerMarker.get(key).remove();
				Marker tempMarker = map.addMarker(markerOptions);
				centerMarker.put(key, tempMarker);
			} else {
				Marker tempMarker = map.addMarker(markerOptions);
				centerMarker.put(key, tempMarker);
			}
		}// end of displayBoundMarker
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

