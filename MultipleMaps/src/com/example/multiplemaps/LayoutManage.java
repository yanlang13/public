package com.example.multiplemaps;

import java.util.ArrayList;
import java.util.List;

import com.example.multiplemaps.R.layout;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class LayoutManage extends Activity {
	private Spinner spUMap, spLMap;
	private TextView tvUMap, tvLMap;
	private ArrayList<String> listId;
	private ArrayList<String> listTitle;
	private ArrayList<String> listDesc;
	private ArrayList<String> listURL;
	private List<Layout> layouts;
	private DBHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		listId = new ArrayList<String>();
		listTitle = new ArrayList<String>();
		listDesc = new ArrayList<String>();
		listURL = new ArrayList<String>();
		layouts = new ArrayList<Layout>();
		setLayoutList();
		
		spUMap = (Spinner) findViewById(R.id.sp_manage_upperMap);
		spLMap = (Spinner) findViewById(R.id.sp_manage_lowerMap);
		tvUMap = (TextView) findViewById(R.id.tv_manage_descUpper);
		tvLMap = (TextView) findViewById(R.id.tv_manage_descLower);
		
		//下拉前的呈現方式
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, listTitle);
		
		//下拉後的呈現方式
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUMap.setAdapter(adapter);
		spLMap.setAdapter(adapter);
		
		setSpinnerDescription(spUMap, tvUMap);
		setSpinnerDescription(spLMap, tvLMap);
		
		
	}// end of onCreate
	private void setSpinnerDescription(Spinner sp,TextView tv){
		final TextView tv1 = tv;
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tv1.setText(listDesc.get(position));
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private void setLayoutList() {
		dbHelper = new DBHelper(LayoutManage.this);
		layouts = dbHelper.getAllLayout();
		for (Layout l : layouts) {
			listId.add(l.getId());
			listTitle.add(l.getTitle());
			listDesc.add(l.getDesc());
			listURL.add(l.getMapURL());
		}
		dbHelper.close();
	}// end of setLayoutList

	public void exportDatabase(View view) {
		OtherTools.copyDBtoSDcard();
	}// end of exportDatabase
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_null, menu);
		return true;
	} // end of onCreateOptionsMenu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.create(this)
						.addNextIntentWithParentStack(upIntent)
						.startActivities();
			} else {
				NavUtils.navigateUpFromSameTask(LayoutManage.this);
			}
		}
		return super.onOptionsItemSelected(item);
	}// end of onOptionsItemSelected
}
