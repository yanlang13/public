package com.example.multiplemaps;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class LayoutSetting extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("mdb", "layoutSetting");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_null, menu);
		return true;
	} // end of onCreateOptionsMenu
}
