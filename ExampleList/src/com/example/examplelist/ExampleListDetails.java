package com.example.examplelist;

import android.app.Activity;

public class ExampleListDetails {
	public final int titleId;
	public final int descriptionId;
	public final Class<? extends Activity> activityClass;

	public ExampleListDetails(int titleId, int descriptionId,
			Class<? extends Activity> activityClass) {
		this.titleId = titleId;
		this.descriptionId = descriptionId;
		this.activityClass = activityClass;
	}// end of ExampleDetails();
}// end of ExampleDetails{};
