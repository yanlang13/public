package com.example.examplelist;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CameraModify extends Activity {
	private EditText et_name, et_desc;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_modify);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		et_name = (EditText) findViewById(R.id.camera_modify_name);
		et_desc = (EditText) findViewById(R.id.camera_modify_desc);

		getDataFromListActivity();

		progressDialog = new ProgressDialog(this);
	}// end of onCreate

	private void getDataFromListActivity() { // call from onCreate
		Bundle bundle = getIntent().getExtras();
		String name = bundle.getString("Name");
		String desc = bundle.getString("Desc");
		et_name.setText(name);
		et_desc.setText(desc);
	}// end of getDataFromListActivity

	public void onCancel(View view) {
		Intent intent = new Intent(this, CameraListActivity.class);
		startActivity(intent);
	}// end of onCancel
	
	//upDate
	public void onUpdate(View view) {
		ParseQuery<CameraSaveParse> query = ParseQuery
				.getQuery(CameraSaveParse.class);
		Bundle bundle = getIntent().getExtras();
		final String name = bundle.getString("Name");
		progressDialog.show();
		query.findInBackground(new FindCallback<CameraSaveParse>() {
			@Override
			public void done(List<CameraSaveParse> results, ParseException e) {
				if (e == null) {
					for (CameraSaveParse csp : results) {
						if (csp.getName().equals(name)){
							csp.setName(et_name.getText().toString());
							csp.setDesc(et_desc.getText().toString());
							csp.saveEventually(new SaveCallback() {
								@Override
								public void done(ParseException e) {
									progressDialog.dismiss();
									Intent intent = new Intent(getApplication(), CameraListActivity.class);
									startActivity(intent);
								}
							});
						}
					}// end of for
				} else {
					Toast.makeText(getApplication(), e.toString(),
							Toast.LENGTH_LONG).show();
				}
			}// end of done
		});// end of query.findInBackground
	}// end of onCancel

	// navigation up
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();

			} else {
				NavUtils.navigateUpFromSameTask(this);
			}
		}// end of if item.getItemId
		return super.onOptionsItemSelected(item);
	}// end of on onOptionsItemSelected

}// end of onOptionsItemSelected
