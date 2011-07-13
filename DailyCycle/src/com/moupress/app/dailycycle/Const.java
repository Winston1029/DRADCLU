package com.moupress.app.dailycycle;

import java.util.Calendar;

import org.achartengine.chart.PointStyle;
import android.graphics.Color;
import android.view.Menu;

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
	
	static final String TAG = "DailyCycle";
	static final int MENU_CHART_DELETE = Menu.FIRST;
	static final int MENU_CHART_INSERT = Menu.FIRST + 1;
	static final int MENU_SAVED_BDAY = Menu.FIRST + 2;
	static final int MENU_CHART_ABOUT = 1;
	static final int MENU_CHART_SAVEAS = 2;
	static final int MENU_CHART_CHANGEVIEW = 3;
	
	//33¡¢28¡¢23 --> Physical, Emtional, Intelliectual
    static final int[][] DURATION = new int[][] {{23}, {28}, {33}, {23, 28, 33}};
    static final String[] CHARTTITLE = new String[] {"Physical Energy", "Emotional Energy", "Intellectual Energy", "All"};
    static final String[][] VALUETITLE = new String[][] { {"Body Strength"}, {"Mental Highness"}, {"Smartness"}, 
    													{"Body Strength", "Mental Strength", "Smartness"}};
	// array index
	final static Calendar CAL = Calendar.getInstance();
    static final int DATE_TO_CHECK = 0;
    static final int DATE_BDAY = 1;
    static final int DATE_EVENT = 2;
    static final int TIME_EVENT_START = 3;
    static final int TIME_EVENT_END = 4;
    
    // timing conversion
    static final long DAYS_IN_MILLSECOND = 3600*24*1000;
    
    //Chart display
    static final PointStyle[] STYLES = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
            PointStyle.TRIANGLE, PointStyle.SQUARE };
    static final int[] COLORS = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
    static final double[] BOUNDARY = new double[] {0.5, 12.5, 0, 32};
    static final double[] PANLIMIT = new double[] { 0, 32, 0, 40 };
    static final String XTITLE = "Month";
    static final String YTITLE = "Energy";
    
    
    static final int TOASTSHOWTIME = 2000;
	
}
