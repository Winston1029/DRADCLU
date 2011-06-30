package com.moupress.app.dailycycle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AbstractCycle extends Activity {
	View v_chart_p;
	protected PopupWindow popup;
	
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.energy_display);
		
	}
	
	protected void updateDateDisplay(EditText etx, int y, int m, int d) {
    	if (d == 0) {
    		etx.setText(new StringBuilder().append(y).append("-").append(m + 1).append(" "));
    	}
    	else {
    		etx.setText(new StringBuilder().append(y).append("-").append(m + 1).append("-").append(d).append(" "));
    	}
    }
	
	protected void updateDateDisplay(TextView txv, int y, int m, int d) {
    	if (d == 0) {
    		txv.setText(new StringBuilder().append(y).append("-").append(m + 1).append(" "));
    	}
    	else {
    		txv.setText(new StringBuilder().append(y).append("-").append(m + 1).append("-").append(d).append(" "));
    	}
    }
    
	protected void updateTimeDisplay(EditText etx, int hour, int min, int sec) {
    	String sHour, sMin, sSec;
    	if (hour < 10) sHour = "0" + hour; else sHour = "" + hour;
    	if (min < 10) sMin = "0" + min; else sMin = "" + min;
    	if (sec < 10) sSec = "0" + sec; else sSec = "" + sec;
		etx.setText(sHour + ":" + sMin + ":" + sSec + " ");
    }
	
	protected void initUI() {

		//Get view_stub from chart to display
	    if (v_chart_p == null) {
	    	  v_chart_p = ((ViewStub) findViewById(R.id.vstub_p)).inflate();
	    }
	    v_chart_p.setVisibility(View.VISIBLE);
	    
	    //EditText etv_chk = (EditText) findViewById(R.id.etv_mon2chk);
    	//updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), 0);
	}
}
