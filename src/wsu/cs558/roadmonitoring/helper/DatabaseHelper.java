package wsu.cs558.roadmonitoring.helper;

import java.util.ArrayList;
import java.util.List;

import wsu.cs558.roadmonitoring.bean.AccelLocData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "roadmonitor.db";
	static String TABLE_NAME = "AccelLocation";
	
	private static final String KEY_ID = "id";
	private static final String LATITUDE_VAL = "latitude";
	private static final String LONGITUDE_VAL = "longitude";
	private static final String ACCEL_X = "accelX";
	private static final String ACCEL_Y = "accelY";
	private static final String ACCEL_Z = "accelZ";
	private static final String CUR_TIME = "dataTime";

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("cmg in database helper");
		String CREATE_TABLE_SQL = "create table if not exists " + TABLE_NAME
				+ " (" + KEY_ID + " integer primary key autoincrement,"
				+ LATITUDE_VAL + " TEXT," + LONGITUDE_VAL + " TEXT," + ACCEL_X
				+ " TEXT," + ACCEL_Y + " TEXT," + ACCEL_Z + " TEXT," + CUR_TIME
				+ " TEXT" + ")";
		Log.d("createquery", CREATE_TABLE_SQL);
		System.out.println("Create query :" + CREATE_TABLE_SQL);
		// db.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);

		db.execSQL(CREATE_TABLE_SQL);
	//	db.close();
	}

	public void insertLocData(AccelLocData accelLocData) {
		
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(LATITUDE_VAL, accelLocData.getLatitude());
		values.put(LONGITUDE_VAL, accelLocData.getLongitude());
		values.put(ACCEL_X, accelLocData.getX());
		values.put(ACCEL_Y, accelLocData.getY());
		values.put(ACCEL_Z, accelLocData.getZ());
		values.put(CUR_TIME, accelLocData.getTimeStamp());

		// Inserting Row
		db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection

	}

	public List<AccelLocData> getAllData() {

		Cursor cursor = null;

		List<AccelLocData> accelLocDataList = new ArrayList<AccelLocData>();
		SQLiteDatabase db = null;
		// Select All Query
		try {
			String selectQuery = "SELECT  * FROM " + TABLE_NAME;

			db = this.getWritableDatabase();
			cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					AccelLocData accelLocData = new AccelLocData(
							Integer.parseInt(cursor.getString(0)),
							Long.parseLong(cursor.getString(6)),
							Double.parseDouble(cursor.getString(3)),
							Double.parseDouble(cursor.getString(4)),
							Double.parseDouble(cursor.getString(5)),
							Double.parseDouble(cursor.getString(1)),
							Double.parseDouble(cursor.getString(2)));
					accelLocDataList.add(accelLocData);
				} while (cursor.moveToNext());
			}

		} catch (Exception e) {
			Log.e("DatabasegetallData", e.toString());
		} finally {
			cursor.close();
			db.close();
			

		}

		// return accelLocData list
		return accelLocDataList;

	}

	// Deleting single accelLocData
	public void deleteLocData(AccelLocData accelLocData) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?",
				new String[] { String.valueOf(accelLocData.getId()) });
		db.close();
	}

	// Getting contacts Count
	public int getLocDataCount() {
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int noOfRows = cursor.getCount();
		cursor.close();
		db.close();
		return noOfRows;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void removeAll() {

		
		SQLiteDatabase db = this.getWritableDatabase(); // helper is object
														// extends
														// SQLiteOpenHelper
		db.delete(DatabaseHelper.TABLE_NAME, null, null);
		db.close();

	}

}
