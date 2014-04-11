package com.example.examplelist;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class FeatureView extends FrameLayout {

	public FeatureView(Context context) {
		super(context);
		// android啟動後，會有一些service再背後執行。
		// 抓這個service(inflating layout resources in this context)
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.feature, this);
	}// end of featureView();

	public synchronized void setTitleId(int titleId) {
		((TextView) (findViewById(R.id.title))).setText(titleId);
	}// end of setTitleId();

	public synchronized void setDescriptionId(int descriptionId) {
		((TextView) findViewById(R.id.description)).setText(descriptionId);
	} // end of setDescriptionId();

}// end of FeatureVire{};
