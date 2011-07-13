package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.widget.Toast;

public class ChartHelper {

	static final String DATABASE_NAME = "custom_chart.db";
    static final String CHARTS_TABLE_NAME = "charts";
    static final int DATABASE_VERSION = 2;
    static final String CREATE_TABLE = "CREATE TABLE " + CHARTS_TABLE_NAME + " ("
										    + Charts._ID + " INTEGER PRIMARY KEY,"
										    + Charts.TITLE + " TEXT,"
										    + Charts.YAXIS + " TEXT,"
										    + Charts.XCOOR + " INTEGER,"
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
	
	public void insert(String chartTitle, String yAxis, int xCoor, double yCoor) {
		Cursor c = null;
		String[] db_cols = {Charts.TITLE, Charts.YAXIS, Charts.XCOOR, Charts.YCOOR};
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
		values.put(Charts.YAXIS, yAxis);
		values.put(Charts.XCOOR, xCoor);
		values.put(Charts.YCOOR, yCoor);
		
		Long now = Long.valueOf(System.currentTimeMillis());
		values.put(Charts.CREATED_DATE, now);
		values.put(Charts.MODIFIED_DATE, now);
		
		db.insert(CHARTS_TABLE_NAME, null, values);
	}
	
	public void delete(String titleToDelete) {
		String whereClause = Charts.TITLE + " = '" + titleToDelete + "'";
		db.delete(CHARTS_TABLE_NAME, whereClause, null);
	}
	
	public List<ContentValues> get(String chartTitle) {
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		Cursor c = null;
		String selection = Charts.TITLE + " = '" + chartTitle + "'";
		String[] db_cols = {Charts.TITLE, Charts.YAXIS, Charts.XCOOR, Charts.YCOOR};
		try {
			c = db.query(true, CHARTS_TABLE_NAME, db_cols, selection, null, null, null, Charts.DEFAULT_SORT_ORDER, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				ContentValues values = new ContentValues();
				values.put(Charts.TITLE, c.getString(0));
				values.put(Charts.YAXIS, c.getString(1));
				values.put(Charts.XCOOR, c.getInt(2));
				values.put(Charts.YCOOR, c.getFloat(3));
				ret.add(values);
				c.moveToNext();
			}
		} catch (SQLException e) {
			Log.v(Const.TAG, "Get "+ chartTitle + " failed.", e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return ret;
	}
	
	public List<ContentValues> getAll() {
		ArrayList<ContentValues> ret = new ArrayList<ContentValues>();
		Cursor c = null;
		String[] db_cols = {Charts.TITLE, Charts.YAXIS};
		try {
			c = db.query(true, CHARTS_TABLE_NAME, db_cols, null, null, null, null, Charts.DEFAULT_SORT_ORDER, null);
			int numRows = c.getCount();
			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {
				ContentValues values = new ContentValues();
				values.put(Charts.TITLE, c.getString(0));
				values.put(Charts.YAXIS, c.getString(1));
				//values.put(Charts.XCOOR, c.getFloat(2));
				//values.put(Charts.YCOOR, c.getFloat(3));
				ret.add(values);
				c.moveToNext();
			}
		} catch (SQLException e) {
			Log.v(Const.TAG, "Getall result fail", e);
		} finally {
			if (c != null && !c.isClosed()) {
				c.close();
			}
		}
		return ret;
	}
	
	public void update() {
//		ContentValues values = new ContentValues();
//		values.put("zip", location.zip);
//		values.put("city", location.city);
//		values.put("region", location.region);
//		values.put("lastalert", location.lastalert);
//		values.put("alertenabled", location.alertenabled);
//		db.update(DBHelper.DB_TABLE, values,
//		"_id=" + location.id, null);
	}
}
