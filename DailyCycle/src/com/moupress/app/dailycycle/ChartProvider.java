package com.moupress.app.dailycycle;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class ChartProvider extends ContentProvider {
	
	static final String DATABASE_NAME = "custom_chart.db";
    static final String CHARTS_TABLE_NAME = "charts";
    static final int DATABASE_VERSION = 2;
    static final String CREATE_DATABASE = "CREATE TABLE " + CHARTS_TABLE_NAME + " ("
										    + Charts._ID + " INTEGER PRIMARY KEY,"
										    + Charts.TITLE + " TEXT,"
										    + Charts.XCOOR + " TEXT,"
										    + Charts.YCOOR + " TEXT,"
										    + Charts.CREATED_DATE + " INTEGER,"
										    + Charts.MODIFIED_DATE + " INTEGER"
										    + ");";
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
	
		DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DATABASE);
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
	static UriMatcher sUriMatcher; // = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	}

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != 1) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
		ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        Long now = Long.valueOf(System.currentTimeMillis());
        
     // Make sure that the fields are all set
        if (values.containsKey(Charts.CREATED_DATE) == false) {
            values.put(Charts.CREATED_DATE, now);
        }
        
        if (values.containsKey(Charts.MODIFIED_DATE) == false) {
            values.put(Charts.MODIFIED_DATE, now);
        }
        
        if (values.containsKey(Charts.TITLE) == false) {
            Resources r = Resources.getSystem();
            values.put(Charts.TITLE, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(Charts.XCOOR) == false) {
            values.put(Charts.XCOOR, "");
        }
        
        if (values.containsKey(Charts.YCOOR) == false) {
            values.put(Charts.YCOOR, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(CHARTS_TABLE_NAME, Charts.XCOOR, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Charts.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
		
	}

}
