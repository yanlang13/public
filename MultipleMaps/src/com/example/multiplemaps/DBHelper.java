package com.example.multiplemaps;

import java.util.ArrayList;
import java.util.List;
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
	private static final String FIELD_TITLE = "MapTitle";
	private static final String FIELD_DESC = "MapDescription";
	private static final String FIELD_MAP_URl = "MapURL";
	private static final String FIELD_ID = "id";

	final static String[] COLUMNS = { FIELD_ID, FIELD_TITLE, FIELD_DESC,
			FIELD_MAP_URl };

	final static String INIT_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LAYOUT + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ FIELD_TITLE + " TEXT, " + FIELD_DESC + " TEXT, " + FIELD_MAP_URl
			+ " TEXT);";

	final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_LAYOUT;

	final static String GOOGLE_MAP = "INSERT INTO " + TABLE_LAYOUT
			+ " Values ('1','GoogleMap NONE', 'NONE', '1'),"
			+ "('2','GoogleMap NORMAL', 'NORMAL', '2'),"
			+ "('3','GoogleMap HYBRID', 'HYBRID', '3'),"
			+ "('4','GoogleMap SATELLITE', 'SATELLITE', '4'), "
			+ "('5','GoogleMap TERRAIN', 'TERRAIN', '5');";

	private Context context;

	// =================================================================

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		this.context = context;
		Log.d("mdb", "DBHepler onCreate");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 只有當getRead/Writable...時才會做onCreate
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
		values.put(FIELD_MAP_URl, layout.getMapURL());

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
		try{
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
		layout.setMapURL(cursor.getString(3));
		// 5. return book
		}catch (CursorIndexOutOfBoundsException e){
			Toast.makeText(context, "ID: ["+id + "] isn't exist.", Toast.LENGTH_LONG).show();
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
		values.put(FIELD_MAP_URl, newLayout.getMapURL());
		db.update(TABLE_LAYOUT, values, FIELD_ID + "=?",
				new String[] { String.valueOf(oldLaout.getId()) });
	}// end of updateLayout

	// ==============================================================DBControled
}
