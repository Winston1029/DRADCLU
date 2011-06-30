package com.moupress.app.dailycycle;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChartHelper {

	static final String DATABASE_NAME = "custom_chart.db";
    static final String CHARTS_TABLE_NAME = "charts";
    static final int DATABASE_VERSION = 2;
    static final String CREATE_TABLE = "CREATE TABLE " + CHARTS_TABLE_NAME + " ("
										    + Charts._ID + " INTEGER PRIMARY KEY,"
										    + Charts.TITLE + " TEXT,"
										    + Charts.XCOOR + " FLOAT,"
										    + Charts.YCOOR + " FLOAT,"
										    + Charts.CREATED_DATE + " INTEGER,"
										    + Charts.MODIFIED_DATE + " INTEGER"
										    + ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(Const.TAG, "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + CHARTS_TABLE_NAME);
			onCreate(db);
		}
	
	}
	
	private DatabaseHelper mOpenHelper;
	private SQLiteDatabase db;
	
	public ChartHelper(Context context) {
		mOpenHelper = new DatabaseHelper(context);
		if (db == null) {
			db = mOpenHelper.getWritableDatabase();
		}
	}
	
	public void cleanup() {
		if (db != null) {
			db.close();
			db = null;
		}
	}
	
	public void insert(String chartTitle, double xCoor, double yCoor) {
		Cursor c = null;
		String[] db_cols = {Charts.TITLE, Charts.XCOOR, Charts.YCOOR};
		int newRecordID = 0;
		try {
			c = db.query(CHARTS_TABLE_NAME, db_cols, null, null, null, null, Charts.DEFAULT_SORT_ORDER);
			newRecordID = c.getCount() + 1;
		} catch (SQLException e) {
			Log.v(Const.TAG, "Insert: Fail to get research from db");
		} finally {
			if (c != null && !c.isClosed()) c.close();
		}
		
		ContentValues values = new ContentValues();
		values.put(Charts._ID, newRecordID);
		values.put(Charts.TITLE, chartTitle);
		values.put(Charts.XCOOR, xCoor);
		values.put(Charts.YCOOR, yCoor);
		
		Long now = Long.valueOf(System.currentTimeMillis());
		values.put(Charts.CREATED_DATE, now);
		values.put(Charts.MODIFIED_DATE, now);
		
		db.insert(CHARTS_TABLE_NAME, null, values);
	}
	
	public void delete() {
		
	}
	public ArrayList<Charts> getAll(){
		ArrayList<Charts> val = new ArrayList<Charts>();
		return val;
	}
	public void get() {
		
	}
	public void update() {
		
	}
}
