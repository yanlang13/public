package com.example.examplelist;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import java.lang.annotation.Target;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.R.anim;
import android.R.array;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Example1Activity extends Activity {
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example1);
		// add up action
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setUpMapIfNeeded();

		Spinner spinner = (Spinner) findViewById(R.id.spMapType);
		// http://developer.android.com/guide/topics/ui/controls/spinner.html
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.map_type, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// An item was selected. You can retrieve the selected item
				// using parent.getItemAtPosition(pos)
				setLayer((String) parent.getItemAtPosition(position));
			}

			// OnItemSelectedListener必做的第二個METHOD
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});// end of setOnItemSelectedListener
	}// end of onCcreate{}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Respond to the action bar's Up/Home button
		if(item.getItemId() == android.R.id.home){
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is NOT part of this app's task, so create a new
				// task when navigating up, with a synthesized back stack.
				TaskStackBuilder.create(this)
				// Add all of this activity's parents to the back stack
				.addNextIntentWithParentStack(upIntent)
				// Navigate up to the closest parent
				.startActivities();
				
			} else {
				// 簡易版一行即可
				NavUtils.navigateUpFromSameTask(Example1Activity.this);
			}
		}//end of if item.getItemId
		return super.onOptionsItemSelected(item);
	}// end of on onOptionsItemSelected

	private void setLayer(String layerName) { // call from
												// setOnItemSelectedListener
		if (!checkReady()) {
			return;
		}
		if (layerName.equals(getString(R.string.normal))) {
			mMap.setMapType(MAP_TYPE_NORMAL);
		} else if (layerName.equals(getString(R.string.hybrid))) {
			mMap.setMapType(MAP_TYPE_HYBRID);
		} else if (layerName.equals(getString(R.string.satellite))) {
			mMap.setMapType(MAP_TYPE_SATELLITE);
		} else if (layerName.equals(getString(R.string.terrain))) {
			mMap.setMapType(MAP_TYPE_TERRAIN);
		} else if (layerName.equals(getString(R.string.none_map))) {
			mMap.setMapType(MAP_TYPE_NONE);
		} else {
			Log.i("LDA", "Error setting layer with name " + layerName);
		}
	} // end of setLayer

	private boolean checkReady() { // call from setLayer
		if (mMap == null) {
			Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	} // end of checkReady()

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}// end of onResume()

	private void setUpMapIfNeeded() { // call from onResume()
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.mMap)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}// end of setUpMapIfNeeded()

	private void setUpMap() { // call from setUpMapIfNeeded()
		mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(
				"Marker"));
	}// end of setUpMap()
}