package com.moupress.app.dailycycle;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

	public class CustomCycle extends AbstractCycle {
	/** Called when the activity is first created. */
    @Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      Bundle extras = getIntent().getExtras(); 
	      if(extras !=null) {
	    	  chartTitle = extras.getString("Title");
	    	  yAxisTitle = extras.getString("yAxis");
	      }
	      
	      initVar();
	      initUI();
    }
    
    protected void initUI() {
    	super.initUI();
    	EditText etv_chk = (EditText) findViewById(R.id.etv_mon2chk);
    	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myChart.cal_chk.get(Calendar.YEAR), myChart.cal_chk.get(Calendar.MONTH), 0);
		//hide birthday for custom cycle
    	((EditText)findViewById(R.id.etv_bday)).setVisibility(View.INVISIBLE);
    	((TextView)findViewById(R.id.txv_bday)).setVisibility(View.INVISIBLE);
    	String[] valueTitle = {"User Data"};
    	FrameLayout flayout = (FrameLayout)findViewById(R.id.frlayout_chart);
    	myChart.setFrameLayout(flayout);
    	myChart.updateChart(this, valueTitle);
		
	}

	private void initVar() {
		//Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
		
    	myChart = new CustomChart(chartTitle, yAxisTitle, this);
    	
	}
	
	public void drawPopupInput(int datePressed) {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.input_popup, (ViewGroup)findViewById(R.id.popup_input_root));
		
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
	    popup.setFocusable(true);
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    
	    updateDateDisplay((TextView)layout.findViewById(R.id.txv_popup_input_title2), myChart.cal_chk.get(Calendar.YEAR), myChart.cal_chk.get(Calendar.MONTH), datePressed);
	    
	    Button btn_popup_input_create = (Button) layout.findViewById(R.id.btn_popup_input_create);
	    btn_popup_input_create.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
				startActivity(intent);
				popup.dismiss();
			}
		});
	    Button btn_popup_input_cancel = (Button) layout.findViewById(R.id.btn_popup_input_cancel);
	    btn_popup_input_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				popup.dismiss();
			}
		});
	}
	

	private String chartTitle = null;
    private String yAxisTitle = null;
    private CustomChart myChart = null;
	
}
