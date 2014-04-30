package com.example.multiplemaps;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DrawerItemClickListener implements ListView.OnItemClickListener {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("mdb", "onItemClicking");
		Toast.makeText(view.getContext(), ""+position, Toast.LENGTH_SHORT).show();
		Log.d("mdb", "onItemClicked");
	}// end of onItemClick

}// end of DrawerItemClickListener
