package com.example.multiplemaps;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.multiplemaps.JSONObject;
import com.google.android.gms.internal.bw;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LayoutManage extends Activity {
	private Spinner spUMap, spLMap;
	private TextView tvUMap, tvLMap, tvKML;
	private ArrayList<String> listId, listTitle, listDesc, listInputType,
			listSource;
	private List<Layout> layouts;
	private DBHelper dbHelper;
	private DefaultSettings ds;

	// 測試用，input的polygon file name
	private String INPUT_KML_FILE = "twopolygon.kml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_layout);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		spUMap = (Spinner) findViewById(R.id.sp_manage_upperMap);
		spLMap = (Spinner) findViewById(R.id.sp_manage_lowerMap);
		tvUMap = (TextView) findViewById(R.id.tv_manage_descUpper);
		tvLMap = (TextView) findViewById(R.id.tv_manage_descLower);
		tvKML = (TextView) findViewById(R.id.tv_manage_GeoJSON);
		// textview scrolling, 搭配.xml的 android:scrollbars = "vertical"
		tvKML.setMovementMethod(new ScrollingMovementMethod());

		// setLayoutList處理
		listId = new ArrayList<String>();
		listTitle = new ArrayList<String>();
		listDesc = new ArrayList<String>();
		listInputType = new ArrayList<String>();
		listSource = new ArrayList<String>();
		layouts = new ArrayList<Layout>();

		ds = new DefaultSettings(LayoutManage.this);

		setLayoutList();

		// 下拉前的呈現方式
		setSpinnerInfo();

		// getKML();

	}// end of onCreate

	// ============================================================ onCreating
	private void setSpinnerInfo() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, listTitle);
		// 下拉後的呈現方式
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUMap.setAdapter(adapter);
		// 設定預設的選擇項目
		spUMap.setSelection(ds.getUpperSpinnerPosition());
		spLMap.setAdapter(adapter);
		spLMap.setSelection(ds.getLowerSpinnerPosition());
		Log.d("mdb", "get" + ds.getLowerSpinnerPosition() + "");
		spinnerSelected(spUMap, tvUMap, true);
		spinnerSelected(spLMap, tvLMap, false);
	}

	private void spinnerSelected(Spinner sp, TextView tv, boolean upOrNot) {
		final TextView tv1 = tv;
		// upOrNot1: true = upper, false = lower;
		final boolean upOrNot1 = upOrNot;
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tv1.setText(listDesc.get(position));
				if (upOrNot1) {
					ds.setUpperMapLayoutFrom(listId.get(position));
					ds.setUpperSpinnerPosition(position);
				}
				if (!upOrNot1) {
					ds.setLowerMapLayoutFrom(listId.get(position));
					ds.setLowerSpinnerPosition(position);
					Log.d("mdb", "p: " + position + "");
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}// end of spinnerSelected

	private void setLayoutList() {
		dbHelper = new DBHelper(LayoutManage.this);
		layouts = dbHelper.getAllLayout();
		for (Layout l : layouts) {
			listId.add(l.getId());
			listTitle.add(l.getTitle());
			listDesc.add(l.getDesc());
			listInputType.add(l.getInputType());
			listSource.add(l.getSource());
		}
		dbHelper.close();
	}// end of setLayoutList

	public void parseKMLtoText(View view) { // onClick
		int PRETTY_PRINT_INDENT_FACTOR = 4;
		try {
			File sd = Environment.getExternalStorageDirectory();
			File kmlFrom = new File(sd, INPUT_KML_FILE);
			Toast.makeText(LayoutManage.this, "input file is " + INPUT_KML_FILE,
					Toast.LENGTH_SHORT).show();
			File txt = new File(sd, "xmlParsed.txt");

			BufferedReader br = new BufferedReader(new FileReader(kmlFrom));
			String line;
			StringBuilder sb = new StringBuilder();

			BufferedWriter bw = new BufferedWriter(new FileWriter(txt));

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();

			// github下載的JSONObject
			JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
			String jsonPrettyPrintString = xmlJSONObj
					.toString(PRETTY_PRINT_INDENT_FACTOR);
			tvKML.setText(jsonPrettyPrintString);
			bw.write(jsonPrettyPrintString);
			bw.close();

		} catch (IOException e) {
			Log.d("mdb", "LayoutManage, " + e.toString());
		} catch (JSONException e) {
			Log.d("mdb", e.toString());
		}
	}

	public void exportDatabase(View view) {
		OtherTools.copyDBtoSDcard();
	}// end of exportDatabase

	// ============================================================ onCreated

	// ============================================================ Menu
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

	// ============================================================ MenuED
}
