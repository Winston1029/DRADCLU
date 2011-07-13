package com.moupress.app.dailycycle;

import android.provider.BaseColumns;

public class Birthday implements BaseColumns {

	public static final String NAME = "name";
	public static final String BIRTHDAY = "birthday";
	
	public static final String CREATED_DATE = "created";
    public static final String MODIFIED_DATE = "modified";
	
	private Birthday() {}
}
