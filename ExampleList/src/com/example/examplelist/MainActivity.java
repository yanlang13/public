package com.example.examplelist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * The main activity of the API library demo gallery.
 * <p>
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends ListActivity {

	/**
	 * A custom array adapter that shows a {@link FeatureView} containing
	 * details about the demo.
	 */
	private static class CustomArrayAdapter extends
			ArrayAdapter<ExampleListDetails> {

		/**
		 * An array containing the details of the demos to be displayed.
		 */
		public CustomArrayAdapter(Context context, ExampleListDetails[] examples) {
			super(context, R.layout.feature, R.id.title, examples);
		}// end of CustomArrayAdapter();
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FeatureView featureView;
			if (convertView instanceof FeatureView) {
				featureView = (FeatureView) convertView;
			} else {
				featureView = new FeatureView(getContext());
			}

			ExampleListDetails example = getItem(position);

			featureView.setTitleId(example.titleId);
			featureView.setDescriptionId(example.descriptionId);

			return featureView;
		}// end of getView();
	}// end of CustomArrayAdapter{};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ListAdapter adapter = new CustomArrayAdapter(this,
				ExampleDetailsList.EXAMPLES);

		setListAdapter(adapter);
	}// end of onCreate{};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		if (item.getItemId() == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ExampleListDetails example = (ExampleListDetails) getListAdapter().getItem(
				position);
		startActivity(new Intent(this, example.activityClass));
	}
}
