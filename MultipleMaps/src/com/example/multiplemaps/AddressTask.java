package com.example.multiplemaps;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * 輸入地址回傳CamerPosition
 */
public class AddressTask extends AsyncTask<Object, Void, CameraPosition> {
	@Override
	protected CameraPosition doInBackground(Object... params) {
		// 需要return null or CameraPosition
		// 用params[i]來抓取輸入的值
		Context context = (Context) params[0];
		String addressInput = (String) params[1];
		Geocoder geocoder = new Geocoder(context);
		try {
			List<Address> result = null;
			result = geocoder.getFromLocationName(addressInput, 1);

			if (result != null && result.size() > 0) {
				Address address = result.get(0);
				double latitude = address.getLatitude();
				double longitude = address.getLongitude();
				
				LatLng target = new LatLng(latitude, longitude);
				float tilt = 0;
				float bearing = 0;
				float zoom = 0;
				CameraPosition cp = new CameraPosition(target, zoom, tilt, bearing);
				return cp;
			}
		} catch (IOException e) {
			Log.d("mdb", e.toString());
		}
		return null;
	}// end of doInBackground
}// end of GetAdressTask