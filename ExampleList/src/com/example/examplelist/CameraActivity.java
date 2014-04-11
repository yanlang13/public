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

	// setUpMapIfNeeded()�A�B�z�a�ϨS��J�����D(googlemap����)
	private void setUpMapIfNeeded() { // call from onResume()
		if (gMap == null) {
			gMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.gMap)).getMap();
			if (gMap != null) {
				// ����ZoomControls(�|�Qbutton�ר�)
				gMap.getUiSettings().setZoomControlsEnabled(false);

				// �B�z�U��listener
				setUpMapListener();
			}
		}
	}// end of setUpMapIfNeeded()

	// setUpMapListener��ť��googlemap���p
	private void setUpMapListener() { // call from setUpMapIfNeeded
		// �@�جOimplement listener�A�N�i�H�����I�smethod�C�@�شN�O�ϥΰΦW���O
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

	// onCameraList�}��CameraListActivity
	public void onCameraList(View view) { // call from XML
		Intent intent = new Intent(this, CameraListActivity.class);
		startActivity(intent);
	}// end of onCameraList

	// onSaveCamera�s�ɥ\��
	public void onSaveCamera(View view) { // call from XML
		// this�O�u��U��context
		AlertDialog.Builder aDialogBuilder = new AlertDialog.Builder(this);
		// layoutInflater�O�Nxml�ରView����
		LayoutInflater inflater = this.getLayoutInflater();

		// �o���view���X�ӡA�O���F���oview�U����textview
		View dialogview = inflater.inflate(R.layout.camera_save_dialog, null);
		aDialogBuilder.setView(dialogview);

		tvDialogName = (TextView) dialogview.findViewById(R.id.dialog_name);
		tvDialogDesc = (TextView) dialogview
				.findViewById(R.id.dialog_description);

		// �гy���s
		aDialogBuilder.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						progressDialog.setTitle("Saving");
						progressDialog.setMessage("Please Wait.");
						progressDialog.show();

						CameraPosition cp = gMap.getCameraPosition();

						// �ثeparseGeoPoint�u���o��A���CameraSaveParse�ɷ|�o�Ϳ��~
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
		// bulider.show()�O�b�P�@��process
		AlertDialog alertDialog = aDialogBuilder.create();
		// alerDialog�@�w�nshow
		alertDialog.show();
	} // end of onSaveCamera

	// {{saveThePreferences�BcallTheLastCameraPostion�A�s�h�y�Ш�sharedPreferences
	// callTheLastCameraPostion():���}�ɡA��^�W�����y�СC
	public void callTheLastCameraPostion() { // call from onCreate
		// ���^sharePrefrences����CameraPosition
		SharedPreferences sp = getSharedPreferences("Preferences",
				Context.MODE_PRIVATE);
		String latString = sp.getString("latitude", "0.0");
		String lngString = sp.getString("longitude", "0.0");
		float bearing = sp.getFloat("bearing", 0);
		float tilt = sp.getFloat("tilt", 0);
		float zoom = sp.getFloat("zoom", 0);
		// �Nstring �ରlatlng
		LatLng lastPosition = new LatLng(Double.valueOf(latString),
				Double.valueOf(lngString));

		CameraPosition cp = new CameraPosition(lastPosition, zoom, tilt,
				bearing);

		// moreCamera�]�wcamera
		// CameraUpdateFactory�Oreturn CameraUpdate class
		gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));

	} // end of callTheLastCameraPostion

	// saveThePreferences()�A���̫᪺��m
	public void saveThePreferences() {// call from onPause
		// ���ocp
		CameraPosition cp = gMap.getCameraPosition();

		// �гy�ɦW�A�H��Ū���Ҧ��C SharedPreferences�O�x�s²�檺KEY-VALUE
		// MODE_PRIVATE�u���o��APP��Ū���ɮ�(�e���O�L����A�ҥH�ɮצ��Q�諸���I)
		SharedPreferences sp = getSharedPreferences("Preferences",
				Context.MODE_PRIVATE);

		// editor�t�d�ާ@�ɮ�
		SharedPreferences.Editor spe = sp.edit();
		// put key-value
		spe.putString("latitude", String.valueOf(cp.target.latitude));
		spe.putString("longitude", String.valueOf(cp.target.longitude));
		spe.putFloat("bearing", cp.bearing);
		spe.putFloat("tilt", cp.tilt);
		spe.putFloat("zoom", cp.zoom);
		// ��������commit()�A�~�|�ק�
		spe.commit();
	}// end of saveThePreferences

	// }}

	// {{�a�϶ɱסAonTiltMore�B onTiltMore�BchangeCamera*2

	// onTiltMore�ɱצa��
	public void onTiltMore(View v) { // call from XML
		// getCameraPosition()���ocamera����m(�OCameraPostion Class)
		// return the center of the padded region.
		// CameraPostion �O��camera����m�B�ɨ��B��ҤءB���_�w��ʨ���
		// LatLng�O�¸g�n�סACameraPosition�|�A�hzoom, tilt, bearing���T����T
		CameraPosition currentCameraPosition = gMap.getCameraPosition();
		// ���o�ɨ�
		float currentTilt = currentCameraPosition.tilt;
		float newTilt = currentTilt + 10;
		newTilt = (newTilt > 90) ? 90 : newTilt;
		// CameraPosition.Builder�MCameraPosition�O��Ӥ��P��CLASS
		CameraPosition updateCameraPosition = new CameraPosition.Builder(
				currentCameraPosition).tilt(newTilt).build();

		// �ݭn�ե�CameraUpdateFactory�ӧ���cameraPosition������
		changeCamera(CameraUpdateFactory
				.newCameraPosition(updateCameraPosition));
	}// end of onTiltMore

	// onTiltLess�ɱצa��
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

	// changeCamera�ɱצa�Ϫ��ʧ@
	private void changeCamera(CameraUpdate update) {// call from onTiltMore, on
													// TiltLess
		// �T�ز���cameraPosition���覡(��CameraUpdate Class)
		// 1.moveCamera(CameraUpdate), ��������
		// gMap.moveCamera(update);

		// 2.animateCamera(CameraUpdate), �w�C���ʡA��animate
		// gMap.animateCamera(update);

		// 3.animateCamera(CameraUpdate, GoogleMap.CancelableCallback)
		// callback�o�ͦbanimate����ɭ�(�|�Ұ�onCancel)�A���㵲���h�OonFinish()
		changeCamera(update, null);
	}// end of changeCamera(CameraUpdate update)

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {// call
		gMap.animateCamera(update, callback);
	}// end of changeCamera
		// }}
}// end of CameraActivity

