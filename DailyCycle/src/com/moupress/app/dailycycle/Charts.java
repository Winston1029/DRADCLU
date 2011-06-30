package com.moupress.app.dailycycle;

import android.net.Uri;
import android.provider.BaseColumns;

public class Charts implements BaseColumns {

	public static final String AUTHORITY = "com.moupress.app.dailycyle";
	private Charts() {}
	
	public static final String TITLE = "title";
    public static final String XCOOR = "xcoor";
    public static final String YCOOR = "ycoor";
    public static final String CREATED_DATE = "created";
    public static final String MODIFIED_DATE = "modified";
    
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/charts");
    
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";
    
    public static final String DEFAULT_SORT_ORDER = "modified DESC";
}
