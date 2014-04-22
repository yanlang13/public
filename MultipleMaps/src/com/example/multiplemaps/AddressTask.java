package com.example.multiplemaps;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * 輸入地址回傳CamerPosition，四個參數分別為context, maxZoomLevel, minZoomLevel, addressInput
 */
public class AddressTask extends AsyncTask<Object, Void, CameraPosition> {
	@Override
	protected CameraPosition doInBackground(Object... params) {
		Log.d("mdb", "doInBacjground");
		// 需要return null or CameraPosition
		// 用params[i]來抓取輸入的值
		Context context = (Context) params[0];
		float maxZoom = (float) params[1];
		float minZoom = (float) params[2];
		Log.d("mdb", "max: "+maxZoom);
		Log.d("mdb", "min: "+minZoom);
		String addressInput = (String) params[3];
		Log.d("mdb", "gmapppp");
		Log.d("mdb", "gma");
		Geocoder geocoder = new Geocoder(context);
		try {
			List<Address> result = null;
			result = geocoder.getFromLocationName(addressInput, 1);
			Address address = result.get(0);
//			Log.d("mdb", "CountryName: " + address.getCountryName());
//			Log.d("mdb", "getCountryCode: " + address.getCountryCode());
//			Log.d("mdb", "AdminArea: " + address.getAdminArea());
//			Log.d("mdb", "SubAdminArea: " + address.getSubAdminArea());
//			Log.d("mdb", "FeatureName: " + address.getFeatureName());
//			Log.d("mdb", "Phone: " + address.getPhone());
//			Log.d("mdb", "PostalCode: " + address.getPostalCode());
//			Log.d("mdb", "Premises: " + address.getPremises());
//			Log.d("mdb", "Locality: " + address.getLocality());
//			Log.d("mdb", "SubLocality: " + address.getSubLocality());
//			Log.d("mdb", "Thoroughfare: " + address.getThoroughfare());
//			Log.d("mdb", "SubThoroughfare: " + address.getSubThoroughfare());
//			Log.d("mdb", "FeatureName: " + address.getFeatureName());
//			Log.d("mdb", "Extras: " + address.getExtras());
//			Log.d("mdb", "Locale: " + address.getLocale());

			LatLng target = new LatLng(address.getLatitude(), address.getLongitude());
			float zoom = 1;
			
			CameraPosition cp = CameraPosition.fromLatLngZoom(target, zoom);
			return cp;
		} catch (IOException e) {
			Log.d("mdb", e.toString());
		}
		return null;
	}// end of doInBackground
}// end of GetAdressTask