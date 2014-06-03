package com.example.multiplemaps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import com.google.android.gms.maps.model.PolygonOptions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class TaskAddInput extends AsyncTask<Object, Void, PolygonOptions> {
	private DBHelper dbHelper; // 寫kmlStringToDataBase
	private parseKmlString kmlString; // 讀取關鍵
	private PolygonOptions po;// 輸出

	@Override
	protected PolygonOptions doInBackground(Object... params) {
		Context context = (Context) params[0];
		File kml = (File) params[1];
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(kml));
			String line;
			StringBuilder sb = new StringBuilder();
			

			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			br.close();

			// 將kml存入db，並轉為polygon
			kmlString = new parseKmlString(sb.toString());

			if (kmlString.isKML()) {
				Log.d("mdb", "Input is a KML file.");
				
				//寫到database
				dbHelper = new DBHelper(context);
				Layout layout = new Layout();
				layout.setTitle("PolygonC");
				layout.setDesc("test kml");
				layout.setInputType("Kml");
				layout.setSource(sb.toString());
				dbHelper.addLayout(layout);
				//
				Layout l = dbHelper.getLayout("PolygonC");
				Log.d("mdb", l.getSource());
				po = new PolygonOptions();
				return po;
			} else {
				// 直接跳出doInBackgroud
				return null;
			}
		} catch (FileNotFoundException e) {
			Log.d("mdb", "TaskAddInput, " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("mdb", "TaskAddInput, " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
}
