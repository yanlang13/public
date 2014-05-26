package com.example.multiplemaps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.example.multiplemaps.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class LayoutManage extends Activity {
	private Spinner spUMap, spLMap;
	private TextView tvUMap, tvLMap, tvKML;
	private ArrayList<String> listId;
	private ArrayList<String> listTitle;
	private ArrayList<String> listDesc;
	private ArrayList<String> listURL;
	private List<Layout> layouts;
	private DBHelper dbHelper;
	private DefaultSettings ds;

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

		listId = new ArrayList<String>();
		listTitle = new ArrayList<String>();
		listDesc = new ArrayList<String>();
		listURL = new ArrayList<String>();
		layouts = new ArrayList<Layout>();

		ds = new DefaultSettings(LayoutManage.this);

		setLayoutList();
		// 下拉前的呈現方式

		setSpinner();
		Log.d("mdb", "ready fro getKML");
		// getKML();
		testKML();
		// textview scrolling, 搭配.xml的 android:scrollbars = "vertical"
		tvKML.setMovementMethod(new ScrollingMovementMethod());

	}// end of onCreate

	private void testKML() {
		//
		int PRETTY_PRINT_INDENT_FACTOR = 4;

		try {
			File sd = Environment.getExternalStorageDirectory();
			File kmlFrom = new File(sd, "polygonC.kml");

			// filename is filepath string
			BufferedReader br = new BufferedReader(new FileReader(kmlFrom));
			String line;
			StringBuilder sb = new StringBuilder();

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();

			tvKML.setText(sb.toString());

			JSONObject xmlJSONObj = XML.toJSONObject(sb.toString());
			String jsonPrettyPrintString = xmlJSONObj
					.toString(PRETTY_PRINT_INDENT_FACTOR);
			tvKML.setText(jsonPrettyPrintString);
		} catch (IOException e) {
			Log.d("mdb", e.toString());
		} catch (JSONException e) {
			Log.d("mdb", e.toString());
		}
	}

	private void getKML() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File kmlFrom = new File(sd, "polygonC.kml");
			Log.d("mdb", "file setted");
			int i = 0;

			XmlPullParser pullParser = Xml.newPullParser();
			// 因為將test.xml放在/assets之下，所以必須以讀取檔案的方式來處理
			try {
				FileInputStream fis = new FileInputStream(kmlFrom);
				pullParser.setInput(fis, "utf-8"); // 設定語系
				// 利用eventType來判斷目前分析到XML是哪一個部份
				int eventType = pullParser.getEventType();
				// XmlPullParser.END_DOCUMENT表示已經完成分析XML
				while (eventType != XmlPullParser.END_DOCUMENT) {
					i++;
					// XmlPullParser.START_TAG表示目前分析到的是XML的Tag，如<title>
					if (eventType == XmlPullParser.START_TAG) {
						String name = pullParser.getName();
						tvKML.setText(tvKML.getText() + ", " + name);
					}
					// XmlPullParser.TEXT表示目前分析到的是XML Tag的值，如：台南美食吃不完
					if (eventType == XmlPullParser.TEXT) {
						String value = pullParser.getText();
						tvKML.setText(tvKML.getText() + ", " + value);
					}
					// 分析下一個XML Tag
					eventType = pullParser.next();
				}
			} catch (FileNotFoundException e) {
				Log.d("mdb", "file is't exit");
			} catch (IOException e) {
			} catch (XmlPullParserException e) {
			}

			Log.d("mdb", "tryed");

			// Kml kml = Kml.unmarshal(kmlFrom);
			// Document document = (Document) kml.getFeature();
			// tvKML.setText(document.getName());
		} catch (NoClassDefFoundError e) {
			Log.d("mdb", e.toString());
		}
	}

	private void getKMLFailed() {
		File sd = Environment.getExternalStorageDirectory();
		FileChannel source = null;
		String kmlInfo = null;
		String kmlPath = "polygon.kml";
		File kmlFrom = new File(sd, kmlPath);
		try {
			try {
				// 這邊fis獨立出來，是因為寫fis(kmlFrom).getChannel 會沒關閉fit，而導致memory leak
				FileInputStream fis = new FileInputStream(kmlFrom);
				source = fis.getChannel();
				// 取得檔案大小 in bytes
				int size = (int) source.size();
				// 配置buffer的大小
				ByteBuffer byteBuffer = ByteBuffer.allocate(size);
				// 將檔案內容放到緩衝區中
				source.read(byteBuffer);
				// 將緩衝區回轉至零，以便讀取其內容
				byteBuffer.rewind();
				for (int i = 0; i < size; i++) {
					kmlInfo = "" + i;
				}

				tvKML.setText(kmlInfo);
				fis.close();
				source.close();
				Log.d("mdb", "trans kml to json");
			} finally {
				source.close();
			}
		} catch (FileNotFoundException e) {
			Log.d("mdb", e.toString());
		} catch (IOException e) {
			Log.d("mdb", e.toString());
		}
	}

	private void setSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, listTitle);
		// 下拉後的呈現方式
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUMap.setAdapter(adapter);
		// 設定預設的選擇項目
		spUMap.setSelection(ds.getUpperSpinnerPosition());
		spLMap.setAdapter(adapter);
		spLMap.setSelection(ds.getLowerSpinnerPosition());
		spinnerSelected(spUMap, tvUMap, true);
		spinnerSelected(spLMap, tvLMap, false);
	}

	private void spinnerSelected(Spinner sp, TextView tv, boolean upOrNot) {
		final TextView tv1 = tv;
		final boolean upOrNot1 = upOrNot; // true = upper, false = lower;
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tv1.setText(listDesc.get(position));
				if (upOrNot1) {
					ds.setUpperMapLayout(listTitle.get(position));
					ds.setUpperSpinnerPosition(position);
				}
				if (!upOrNot1) {
					ds.setLowerMapLayout(listTitle.get(position));
					ds.setLowerSpinnerPosition(position);
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
