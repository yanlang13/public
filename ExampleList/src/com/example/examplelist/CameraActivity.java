package com.example.examplelist;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CameraActivity extends Activity {

	private GoogleMap gMap;
	private TextView tvPress, tvCamera;
	private TextView tvDialogName, tvDialogDesc;
	private ProgressDialog progressDialog;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.camera);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setUpMapIfNeeded();
		callTheLastCameraPostion();
		tvPress = (TextView) findViewById(R.id.press_position);
		tvCamera = (TextView) findViewById(R.id.camera_position);
		progressDialog = new ProgressDialog(this);
	}// end of onCreate
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
				.addNextIntentWithParentStack(upIntent)
				.startActivities();
				
			} else {
				NavUtils.navigateUpFromSameTask(this);
			}
		}//end of if item.getItemId
		return super.onOptionsItemSelected(item);
	}// end of on onOptionsItemSelected
	
	
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

	}// end of onResume()

	// setUpMapIfNeeded()，處理地圖沒放入的問題(googlemap內建)
	private void setUpMapIfNeeded() { // call from onResume()
		if (gMap == null) {
			gMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.gMap)).getMap();
			if (gMap != null) {
				// 關閉ZoomControls(會被button擋到)
				gMap.getUiSettings().setZoomControlsEnabled(false);

				// 處理各式listener
				setUpMapListener();
			}
		}
	}// end of setUpMapIfNeeded()

	// setUpMapListener監聽的googlemap狀況
	private void setUpMapListener() { // call from setUpMapIfNeeded
		// 一種是implement listener，就可以直接呼叫method。一種就是使用匿名類別
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cp) {
				tvCamera.setText("Camera Centre: " + cp.target.toString());
			}
		});// end of gMap.setOnCameraChangeListener
		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				tvPress.setText("tap position: " + point.toString());
			}
		});// end of gMap.setOnMapClickListener
		gMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng point) {
				tvPress.setText("long pressed position: " + point.toString());
			}
		});// end of gMap.setOnMapLongClickListener
	} // end of setUpMapListener

	protected void onPause() {
		super.onPause();
		saveThePreferences();
	}// end of onPause

	// onCameraList開啟CameraListActivity
	public void onCameraList(View view) { // call from XML
		Intent intent = new Intent(this, CameraListActivity.class);
		startActivity(intent);
	}// end of onCameraList

	// onSaveCamera存檔功能
	public void onSaveCamera(View view) { // call from XML
		// this是只當下的context
		AlertDialog.Builder aDialogBuilder = new AlertDialog.Builder(this);
		// layoutInflater是將xml轉為View的基本
		LayoutInflater inflater = this.getLayoutInflater();

		// 這邊把view取出來，是為了取得view下面的textview
		View dialogview = inflater.inflate(R.layout.camera_save_dialog, null);
		aDialogBuilder.setView(dialogview);

		tvDialogName = (TextView) dialogview.findViewById(R.id.dialog_name);
		tvDialogDesc = (TextView) dialogview
				.findViewById(R.id.dialog_description);

		// 創造按鈕
		aDialogBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog.setTitle("Saving");
						progressDialog.setMessage("Please Wait.");
						progressDialog.show();

						CameraPosition cp = gMap.getCameraPosition();

						// 目前parseGeoPoint只能放這邊，放到CameraSaveParse時會發生錯誤
						ParseGeoPoint pgp = new ParseGeoPoint(
								cp.target.latitude, cp.target.longitude);
						CameraSaveParse csp = new CameraSaveParse();
						csp.put("ParseGeoPoint", pgp);
						csp.setName(tvDialogName.getText().toString());
						csp.setDesc(tvDialogDesc.getText().toString());
						csp.setBearing(cp.bearing);
						csp.setTilt(cp.tilt);
						csp.setZoom(cp.zoom);
						
						csp.saveEventually(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								progressDialog.dismiss();
							}
						});
					}
				}); // end of setPositiveButton

		aDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}// end of setNegativeButton
				});
		// Get the AlertDialog from create()=> do extra processing
		// bulider.show()是在同一個process
		AlertDialog alertDialog = aDialogBuilder.create();
		// alerDialog一定要show
		alertDialog.show();
	} // end of onSaveCamera

	// {{saveThePreferences、callTheLastCameraPostion，存去座標到sharedPreferences
	// callTheLastCameraPostion():打開時，抓回上次的座標。
	public void callTheLastCameraPostion() { // call from onCreate
		// 取回sharePrefrences中的CameraPosition
		SharedPreferences sp = getSharedPreferences("Preferences",
				Context.MODE_PRIVATE);
		String latString = sp.getString("latitude", "0.0");
		String lngString = sp.getString("longitude", "0.0");
		float bearing = sp.getFloat("bearing", 0);
		float tilt = sp.getFloat("tilt", 0);
		float zoom = sp.getFloat("zoom", 0);
		// 將string 轉為latlng
		LatLng lastPosition = new LatLng(Double.valueOf(latString),
				Double.valueOf(lngString));

		CameraPosition cp = new CameraPosition(lastPosition, zoom, tilt,
				bearing);

		// moreCamera設定camera
		// CameraUpdateFactory是return CameraUpdate class
		gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

	} // end of callTheLastCameraPostion

	// saveThePreferences()，抓住最後的位置
	public void saveThePreferences() {// call from onPause
		// 取得cp
		CameraPosition cp = gMap.getCameraPosition();

		// 創造檔名，以及讀取模式。 SharedPreferences是儲存簡單的KEY-VALUE
		// MODE_PRIVATE只有這個APP能讀取檔案(前提是無刷機，所以檔案有被改的風險)
		SharedPreferences sp = getSharedPreferences("Preferences",
				Context.MODE_PRIVATE);

		// editor負責操作檔案
		SharedPreferences.Editor spe = sp.edit();
		// put key-value
		spe.putString("latitude", String.valueOf(cp.target.latitude));
		spe.putString("longitude", String.valueOf(cp.target.longitude));
		spe.putFloat("bearing", cp.bearing);
		spe.putFloat("tilt", cp.tilt);
		spe.putFloat("zoom", cp.zoom);
		// 完成必做commit()，才會修改
		spe.commit();
	}// end of saveThePreferences

	// }}

	// {{地圖傾斜，onTiltMore、 onTiltMore、changeCamera*2

	// onTiltMore傾斜地圖
	public void onTiltMore(View v) { // call from XML
		// getCameraPosition()取得camera的位置(是CameraPostion Class)
		// return the center of the padded region.
		// CameraPostion 是談camera的位置、傾角、比例尺、指北針轉動角度
		// LatLng是純經緯度，CameraPosition會再多zoom, tilt, bearing等三項資訊
		CameraPosition currentCameraPosition = gMap.getCameraPosition();
		// 取得傾角
		float currentTilt = currentCameraPosition.tilt;
		float newTilt = currentTilt + 10;
		newTilt = (newTilt > 90) ? 90 : newTilt;
		// CameraPosition.Builder和CameraPosition是兩個不同的CLASS
		CameraPosition updateCameraPosition = new CameraPosition.Builder(
				currentCameraPosition).tilt(newTilt).build();

		// 需要調用CameraUpdateFactory來完成cameraPosition的移動
		changeCamera(CameraUpdateFactory
				.newCameraPosition(updateCameraPosition));
	}// end of onTiltMore

	// onTiltLess傾斜地圖
	public void onTiltLess(View v) { // call from XML
		CameraPosition currentCameraPosition = gMap.getCameraPosition();
		float currentTilt = currentCameraPosition.tilt;

		float newTilt = currentTilt - 10;
		newTilt = (newTilt < 0) ? 0 : newTilt;

		CameraPosition updateCameraPosition = new CameraPosition.Builder(
				currentCameraPosition).tilt(newTilt).build();

		changeCamera(CameraUpdateFactory
				.newCameraPosition(updateCameraPosition));
	}// end of onTiltLess

	// changeCamera傾斜地圖的動作
	private void changeCamera(CameraUpdate update) {// call from onTiltMore, on
													// TiltLess
		// 三種移動cameraPosition的方式(接CameraUpdate Class)
		// 1.moveCamera(CameraUpdate), 瞬間移動
		// gMap.moveCamera(update);

		// 2.animateCamera(CameraUpdate), 緩慢移動，有animate
		// gMap.animateCamera(update);

		// 3.animateCamera(CameraUpdate, GoogleMap.CancelableCallback)
		// callback發生在animate停止的時候(會啟動onCancel)，完整結束則是onFinish()
		changeCamera(update, null);
	}// end of changeCamera(CameraUpdate update)

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {// call
		gMap.animateCamera(update, callback);
	}// end of changeCamera
		// }}
}// end of CameraActivity

