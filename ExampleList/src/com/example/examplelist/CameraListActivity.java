package com.example.examplelist;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import android.R.array;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CameraListActivity extends Activity {
	private ArrayList<String> arrayListName;
	private ArrayList<String> arrayListDesc;
	private ArrayList<Double> arrayListLat;
	private ArrayList<Double> arrayListLng;
	private ArrayList<String> arrayListTime;
	private ProgressDialog progressDialog;
	private ListView listView;
	private TextView textView;
	private NumberFormat nF; // ����g�n�ת��p���I

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameralist);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		textView = (TextView) findViewById(R.id.textView11);
		listView = (ListView) findViewById(R.id.listView1);
		arrayListName = new ArrayList<String>();
		arrayListDesc = new ArrayList<String>();
		arrayListLat = new ArrayList<Double>();
		arrayListLng = new ArrayList<Double>();
		arrayListTime = new ArrayList<String>();
		nF = new DecimalFormat("#.##");
		progressDialog = new ProgressDialog(this);

		progressDialog.show();
		getDataFromParse();
		setUpLongListener();
	}// end of onCreate

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

	// setUpLongListener()�A��listView�����I��
	private void setUpLongListener() { // call from onCreate
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				setAlertDialog(position).show();
				return false;
			}// end of onItemLongClick
		});// end of listView.setOnItemLongClickListener
	}// end of setUpAllListener()

	private AlertDialog setAlertDialog(int position) {// call from
														// setUpAllListener()
		final int listPosition = position;
		AlertDialog.Builder aDialogBuilder = new AlertDialog.Builder(
				CameraListActivity.this);
		// �z�Lposition���parse��U�Ӫ����
		final String parseName = arrayListName.get(listPosition);

		aDialogBuilder.setTitle(parseName);
		final String editSelect[] = new String[] { "Modify", "Delete" };

		// ��ťalertDialog
		aDialogBuilder.setItems(editSelect,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							// modify file
							Intent intent = new Intent(CameraListActivity.this,
									CameraModify.class);
							Bundle bundle = new Bundle();
							bundle.putString("Name",
									arrayListName.get(listPosition));
							bundle.putString("Desc",
									arrayListDesc.get(listPosition));
							intent.putExtras(bundle);
							startActivity(intent);
						} else if (which == 1) {
							// delete file
							delParseData(arrayListName.get(listPosition));
						} else {
						}
					}
				});
		return aDialogBuilder.create();
	}// end of setAlertDialog()

	// �z�Lname�ӧR��parse�W�����ɮ�
	private void delParseData(String parseName) { // call from setUpLongListener
		final String parseName1 = parseName;
		ParseQuery<CameraSaveParse> query = ParseQuery
				.getQuery(CameraSaveParse.class);
		progressDialog.setTitle("delete data");
		progressDialog.show();
		query.findInBackground(new FindCallback<CameraSaveParse>() {
			@Override
			public void done(List<CameraSaveParse> result, ParseException e) {
				if (e == null) {
					for (CameraSaveParse csp : result) {
						if (csp.getName().equals(parseName1)) {
							try {
								csp.delete();
								getDataFromParse();
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
					}
				} else {
					Toast.makeText(getApplication(), e.toString(),
							Toast.LENGTH_LONG).show();
				}// end of if
			}// end of done
		});// end of query.findInBackground
	}// end of delParseData

	// {{getDataFromParse()��oparse��ơAsetUpListView()���lsit
	// ��oparse�W�����
	private void getDataFromParse() { // call from onCreate, delParseData
		// ParseQuery �ϥ� subclass CameraSaveParse
		// getQuery Creates a new query for the given ParseObject subclass type.

		// �����M��array�A�T�O�C��parse�쪺��Ƴ����л\�¦���
		clearAllArrayList();

		ParseQuery<CameraSaveParse> query = ParseQuery
				.getQuery(CameraSaveParse.class);

		query.findInBackground(new FindCallback<CameraSaveParse>() {
			@Override
			public void done(List<CameraSaveParse> results, ParseException e) {
				// �T�{Parse�S�����D
				if (e == null) {
					for (CameraSaveParse csp : results) {
						arrayListName.add(csp.getName());
						arrayListDesc.add(csp.getDesc());
						ParseGeoPoint pgp = new ParseGeoPoint();
						pgp = csp.getParseGeoPoint("ParseGeoPoint");
						arrayListLat.add(pgp.getLatitude());
						arrayListLng.add(pgp.getLongitude());
						arrayListTime.add(csp.getDate());
					}// end of for each
					setUpListView();
				} else {
					Toast.makeText(getApplication(), e.toString(),
							Toast.LENGTH_LONG).show();
				}
				progressDialog.dismiss();
			}// end of done
		});// end of query.findInBackground
	}// end of getDataFromParse

	//

	// �M�żȦs��arrayList
	private void clearAllArrayList() { // call from getDataFromParse()
		arrayListName.clear();
		arrayListDesc.clear();
		arrayListLat.clear();
		arrayListLng.clear();
		arrayListTime.clear();
	}// end of clearAllArrayList

	// �Nparse��Ʃ��listView
	private void setUpListView() { // call from getDataFromParse()
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < arrayListName.size(); i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("Name", "Name: " + arrayListName.get(i));
			item.put("Desc", "Description: \n" + arrayListDesc.get(i));
			item.put("Time", arrayListTime.get(i));

			// �_�n�n�n�ഫ
			if (arrayListLat.get(i) < 0) {
				item.put("Lat", nF.format(Math.abs(arrayListLat.get(i))) + "S");
			} else {
				item.put("Lat", nF.format(arrayListLat.get(i)) + "N");
			}// end of if

			// �F�g��g�ഫ
			if (arrayListLng.get(i) < 0) {
				item.put("Lng", nF.format(Math.abs(arrayListLng.get(i))) + "W");
			} else {
				item.put("Lng", nF.format(arrayListLng.get(i)) + "E");
			}// end of if

			list.add(item);
		}// end of for
			// �s�WSimpleAdapter
		String[] from = { "Name", "Desc", "Lat", "Lng", "Time" };
		int[] to = { R.id.list_name, R.id.list_desc, R.id.list_lat,
				R.id.list_lng, R.id.list_time };

		SimpleAdapter simpleAdapter;
		simpleAdapter = new SimpleAdapter(getApplication(), list,
				R.layout.camera_list_view, from, to);

		// ListActivity�]�wadapter
		listView.setAdapter(simpleAdapter);
	}// end of setListView
		// }}
}// end of CameraListActivity

