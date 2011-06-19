package com.mycycle;

import java.util.Calendar;

import org.achartengine.chart.PointStyle;

import android.graphics.Color;

public class Const {
	// menu
	static final String MENUNAME = "name";
	static final String MENUDESC = "desc";
	
	static final String[] MENU = new String[] {"Physical", "Emotional", "Intellectual", "All"};
	static final String[] MENU_SUMMARY = new String[] 
                                  {"how fast, how strong you are", 
								   "how cheerful, how happy you are", 
								   "how smart, how creative you are",
								   "combine all curves above"};
	
	//
	final static Calendar CAL = Calendar.getInstance();
    static final int DATE_TO_CHECK = 0;
    static final int DATE_BDAY = 1;
    static final int DATE_EVENT = 2;
    static final int TIME_EVENT_START = 3;
    static final int TIME_EVENT_END = 4;
    static final int YEAR = 0;
    static final int MONTH = 1;
    static final int DAY = 2;
    
    //
    static final long DAYS_IN_MILLSECOND = 3600*24*1000;
    
    //33¡¢28¡¢23 --> Physical, Emtional, Intelliectual
    static final int[] DURATION = new int[] {23, 28, 33};
    static final String[] CHARTTITLE = new String[] {"Physical Energy", "Emotional Energy", "Intellectual Energy"};
    static final String[][] VALUE_TITLE = new String[][] { {"Body Strength"}, {"Mental Highness"}, {"Smartness"}};
    
    //Chart display
    static final PointStyle[] STYLES = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
            PointStyle.TRIANGLE, PointStyle.SQUARE };
    static final int[] COLORS = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
    static final double[] BOUNDARY = new double[] {0.5, 12.5, 0, 32};
    static final double[] PANLIMIT = new double[] { 0, 32, 0, 40 };
    static final String XTITLE = "Month";
    static final String YTITLE = "Energy";
}
