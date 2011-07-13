package com.moupress.app.dailycycle;

import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
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

	

	private String chartTitle = null;
    private String yAxisTitle = null;
    private CustomChart myChart = null;
    private double[] xValue;
    private double[] yValue;
		
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
    	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myChart.cal_chk.get(Calendar.YEAR), myChart.cal_chk.get(Calendar.MONTH), 0);
		//hide birthday for custom cycle
    	((EditText)findViewById(R.id.etv_bday)).setVisibility(View.INVISIBLE);
    	((TextView)findViewById(R.id.txv_bday)).setVisibility(View.INVISIBLE);
    	String[] valueTitle = {"User Data"};
    	FrameLayout flayout = (FrameLayout)findViewById(R.id.frlayout_chart);
    	myChart.setFrameLayout(flayout);
    	myChart.updateChart(this, valueTitle, xValue, yValue);
		
	}

	protected void initVar() {
		//Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
		super.initVar();
    	myChart = new CustomChart(chartTitle, yAxisTitle, this);
    	setXYValue();
	}
	
	private void setXYValue() {
		List<ContentValues> xyValues = dbHelper.getCustomChartValue(chartTitle);
    	int cntCoors = xyValues.size();
    	if (cntCoors > 0) {
    		xValue = new double[cntCoors];
    		yValue = new double[cntCoors];
			for (int i = 0; i < cntCoors; i++) {
				xValue[i] = xyValues.get(i).getAsInteger(Charts.XCOOR) % 100;
				yValue[i] = xyValues.get(i).getAsFloat(Charts.YCOOR);
			}
		} else {
			xValue = new double[1];
	    	yValue = new double[1];
		}
	}
	
	private static int xCoorPressed;
	private static float yCoorPressed;
	
	@Override
	public void drawPopup(int datePressed) {

		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.input_popup, (ViewGroup)findViewById(R.id.popup_input_root));
		
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
		popup.setBackgroundDrawable(new BitmapDrawable()); //essential to catch KeyEvents happen outside popup window
    	layout.setOnKeyListener(new View.OnKeyListener() {
			
			//@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK)) {
					popup.dismiss();
			        //Log.d(this.getClass().getName(), "back button pressed");
			    }
			    //return super.onKeyDown(keyCode, event);
				return false;
			}
		});
	    popup.setFocusable(true);
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    final EditText etx_popup_input_value = (EditText)layout.findViewById(R.id.etx_popup_input_value);
	    
	    //xCoorPressed = 20110609
		xCoorPressed = myChart.cal_chk.get(Calendar.YEAR) * 10000 + (myChart.cal_chk.get(Calendar.MONTH) +1) * 100 + datePressed;
	    updateDateDisplay((TextView)layout.findViewById(R.id.txv_popup_input_title2), myChart.cal_chk.get(Calendar.YEAR), myChart.cal_chk.get(Calendar.MONTH), datePressed);
	    
	    final List<ContentValues> xPressedOrgVal = dbHelper.getCustomerChartPoint(chartTitle, xCoorPressed);
		if (xPressedOrgVal.size() > 0 ) {
			String tmpYVal = xPressedOrgVal.get(0).get(Charts.YCOOR).toString();
			etx_popup_input_value.setText(tmpYVal);
		}
		
	    Button btn_popup_input_create = (Button) layout.findViewById(R.id.btn_popup_input_create);
	    btn_popup_input_create.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View arg0) {
				
				String yPressed = etx_popup_input_value.getText().toString();
				if (yPressed != null && !yPressed.equals(""))
					yCoorPressed = Float.parseFloat(etx_popup_input_value.getText().toString());
				else yCoorPressed  = 0;
				
				
				if (xPressedOrgVal.size() > 0 ) {
					dbHelper.updateCustomChartPoint(chartTitle, xCoorPressed, yCoorPressed);
				} else {
					dbHelper.insertCustomChart(chartTitle, yAxisTitle, xCoorPressed, yCoorPressed);
				}
				
				
				//Toast.makeText(getBaseContext(), "Insert Data", 6000);
				popup.dismiss();
				setXYValue();
				myChart.updateChart(getBaseContext(), new String[]{"User Data"}, xValue, yValue);
			}
		});
	    Button btn_popup_input_cancel = (Button) layout.findViewById(R.id.btn_popup_input_cancel);
	    btn_popup_input_cancel.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View arg0) {
				popup.dismiss();
			}
		});
	}
	
	
}
