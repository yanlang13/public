package com.example.multiplemaps;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static android.provider.BaseColumns._ID;

public class DBHelper extends SQLiteOpenHelper {
	final static int DB_VERSION = 1;
	final static String DATABASE_NAME = "MultiMaps.db";
	final static String TABLE_LAYOUT = "Layouts";
	final static String FIELD_TITLE = "MapTitle";
	final static String FIELD_DESC = "MapDescription";
	final static String FIELD_MAP_URl = "MapURL";

	final static String[] COLUMNS = { FIELD_TITLE, FIELD_DESC, FIELD_MAP_URl };

	final static String INIT_TABLE = "CREATE TABLE " + TABLE_LAYOUT + " ("
			+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_TITLE
			+ " TEXT, " + FIELD_DESC + " TEXT, " + FIELD_MAP_URl + " TEXT);";

	final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAYOUT;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只有當getRead/Writable...時才會做onCreate
		db.execSQL(INIT_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("mdb", "db onUpgrade");
		db.execSQL(DROP_TABLE);
	}

	public void addLayout(Layout layout) {
		Log.d("mdb", "addLayout");
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(FIELD_TITLE, layout.getTitle());
		values.put(FIELD_DESC, layout.getDesc());
		values.put(FIELD_MAP_URl, layout.getMapURL());
		
		// 3. insert
		db.insert(TABLE_LAYOUT, null, values);

		// 4. close
		db.close();
		Log.d("mdb", "db.close");
	}// end of addLayout

	public Layout getLayout(int id) {
		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();

		// 2. build query
		Cursor cursor = db.query(TABLE_LAYOUT, // a. table
				COLUMNS, // b. column names
				_ID + "=?", // c. selections
				new String[] { String.valueOf(id) }, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null); // h. limit

		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		Layout layout = new Layout();
		layout.setTitle(cursor.getString(0));
		layout.setDesc(cursor.getString(1));
		layout.setMapURL(cursor.getString(2));

		Log.d("mdb", "getlayout(" + id + ")" + layout.toString());

		// 5. return book
		return layout;
	}

	public List<Layout> getAllLayout() {
		List<Layout> layouts = new ArrayList<Layout>();
		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_LAYOUT;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build layout and add it to list

		Layout layout = null;
		if (cursor.moveToFirst()) {
			do {
				layout = new Layout();
				layout.setId(cursor.getString(0));
				layout.setTitle(cursor.getString(1));
				layout.setDesc(cursor.getString(2));
				layout.setMapURL(cursor.getString(3));
				// Add book to books
				layouts.add(layout);
			} while (cursor.moveToNext());
		}
		Log.d("mdb", layouts.toString());

		// return books
		return layouts;
	}

}
