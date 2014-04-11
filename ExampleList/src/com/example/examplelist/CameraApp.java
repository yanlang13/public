package com.example.examplelist;

import com.parse.Parse;
import com.parse.ParseObject;
import android.app.Application;

public class CameraApp extends Application {

	private String PAESE_APPLICATION_ID = "BML8dGuDZm1bAlrZTAvrziF0VzRD7zHC1snaE6F6";
	private String PAESE_CLIENT_KEY = "sOoI88XtmkZANeXeTry3T4XFS4E6xM3yH9CnFLZ1";

	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(CameraSaveParse.class);
		Parse.initialize(this, PAESE_APPLICATION_ID, PAESE_CLIENT_KEY);
	}// end of onCreate
	
}
