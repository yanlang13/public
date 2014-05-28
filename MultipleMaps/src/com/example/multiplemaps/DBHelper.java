package com.example.multiplemaps;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;
	private static final String DATABASE_NAME = "MultiMaps.db";
	private static final String TABLE_LAYOUT = "Layouts";

	// 欄位1，自動產生
	private static final String FIELD_ID = "id";
	// 欄位2
	private static final String FIELD_TITLE = "MapTitle";
	// 欄位3
	private static final String FIELD_DESC = "MapDescription";
	// 欄位4,GoogleMap, KML and Image
	private static final String FIELD_INPUT_TYPE = "InputType";
	// 欄位5, source, kmlString, imageSource, google type
	private static final String FIELD_SOURCE = "Source";

	final static String[] COLUMNS = { FIELD_ID, FIELD_TITLE, FIELD_DESC,
			FIELD_INPUT_TYPE, FIELD_SOURCE };

	final static String INIT_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LAYOUT + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FIELD_TITLE + " TEXT, " + FIELD_DESC + " TEXT, "
			+ FIELD_INPUT_TYPE + " TEXT, " + FIELD_SOURCE + " TEXT);";

	final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAYOUT;

	final static String GOOGLE_MAP = "INSERT INTO "
			+ TABLE_LAYOUT
			+ " Values ('1','GoogleMap NONE', 'NONE', 'GoogleMap', 'NONE'),"
			+ "('2','GoogleMap NORMAL', 'NORMAL', 'GoogleMap', 'NORMAL'),"
			+ "('3','GoogleMap HYBRID', 'HYBRID', 'GoogleMap', 'HYBRID'),"
			+ "('4','GoogleMap SATELLITE', 'SATELLITE', 'GoogleMap', 'SATELLITE'), "
			+ "('5','GoogleMap TERRAIN', 'TERRAIN', 'GoogleMap', 'TERRAIN');";

	private Context context;

	// =================================================================

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只有當getRead/Writable...時才會做onCreate
		Log.d("mdb", "DBHepler onCreate");
		db.execSQL(INIT_TABLE);
		db.execSQL(GOOGLE_MAP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("mdb", "db onUpgrade");
		db.execSQL(DROP_TABLE);
	}

	// ==============================================================DBControl
	public void addLayout(Layout layout) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(FIELD_TITLE, layout.getTitle());
		values.put(FIELD_DESC, layout.getDesc());
		values.put(FIELD_INPUT_TYPE, layout.getInputType());
		values.put(FIELD_SOURCE, layout.getSource());

		// 3. insert
		db.insert(TABLE_LAYOUT, null, values);

		// 4. close
		db.close();
	}// end of addLayout

	public Layout getLayout(int id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		Layout layout = new Layout();
		// 2. build query
		try {
			Cursor cursor = db.query(TABLE_LAYOUT, // a. table
					COLUMNS, // b. column names
					FIELD_ID + "=?", // c. selections
					new String[] { String.valueOf(id) }, // d. selections args
					null, // e. group by
					null, // f. having
					null, // g. order by
					null); // h. limit
			// 3. if we got results get the first one
			if (cursor != null)
				cursor.moveToFirst();
			// 4. build book object
			layout.setId(cursor.getString(0));
			layout.setTitle(cursor.getString(1));
			layout.setDesc(cursor.getString(2));
			layout.setInputType(cursor.getString(3));
			layout.setSource(cursor.getString(4));
			// 5. return book
		} catch (CursorIndexOutOfBoundsException e) {
			Log.d("mdb", "DBHelper, "+"Error:"+e.toString());
		}
		return layout;

	}// end of getLayout

	/*
	 * return List<Layout>
	 */
	public List<Layout> getAllLayout() {
		List<Layout> layouts = new ArrayList<Layout>();
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_LAYOUT;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build layout and add it to list

		Layout layout = null;
		try {
			if (cursor.moveToFirst()) {
				do {
					layout = new Layout();
					layout.setId(cursor.getString(0));
					layout.setTitle(cursor.getString(1));
					layout.setDesc(cursor.getString(2));
					layout.setInputType(cursor.getString(3));
					layout.setSource(cursor.getString(4));
					// Add book to books
					layouts.add(layout);
				} while (cursor.moveToNext());
			}
		} catch (IllegalStateException e) {
			Log.d("mdb", "DBHelper, "+"Error:"+e.toString());
		}

		// return books
		return layouts;
	}// end of getAllLayout()

	public void deleteLayoutRow(Layout layout) {
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_LAYOUT, FIELD_ID + " = ?",
				new String[] { String.valueOf(layout.getId()) });

		// 3. close
		db.close();
	}// end of deleteLayout

	public void updateLayoutRow(Layout oldLaout, Layout newLayout) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FIELD_TITLE, newLayout.getTitle());
		values.put(FIELD_DESC, newLayout.getDesc());
		values.put(FIELD_INPUT_TYPE, newLayout.getInputType());
		values.put(FIELD_SOURCE, newLayout.getSource());
		db.update(TABLE_LAYOUT, values, FIELD_ID + "=?",
				new String[] { String.valueOf(oldLaout.getId()) });
	}// end of updateLayout

	// ==============================================================DBControled
}
