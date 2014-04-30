package com.example.multiplemaps;

import android.app.Activity;

public class DrawerListDetials {
	public final String title;
	public final Class<? extends Activity> activityClass;
	public DrawerListDetials(String title, Class<? extends Activity> activityClass){
		this.title = title;
		this.activityClass = activityClass;
	}// end of DrawerListDetials
}// end of DrawerListDetials
