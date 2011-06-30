package com.moupress.app.dailycycle;

import java.util.Calendar;

import android.content.Context;
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

	public static ChartHelper chartHelper;

	private String chartTitle = null;
    private String yAxisTitle = null;
    private CustomChart myChart = null;
		
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
    	myChart.updateChart(this, valueTitle);
		
	}

	private void initVar() {
		//Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
		
    	myChart = new CustomChart(chartTitle, yAxisTitle, this);
    	chartHelper = new ChartHelper(this);
    	
	}
	
	private static int xCoorPressed;
	private static int yCoorPressed;
	
	@Override
	public void drawPopup(int datePressed) {

		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.input_popup, (ViewGroup)findViewById(R.id.popup_input_root));
		
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
	    popup.setFocusable(true);
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    final EditText etx_popup_input_value = (EditText)layout.findViewById(R.id.etx_popup_input_value);
	    
	    //xCoorPressed = 20110609
		xCoorPressed = myChart.cal_chk.get(Calendar.YEAR) * 10000 + (myChart.cal_chk.get(Calendar.MONTH) +1) * 100 + datePressed;
	    updateDateDisplay((TextView)layout.findViewById(R.id.txv_popup_input_title2), myChart.cal_chk.get(Calendar.YEAR), myChart.cal_chk.get(Calendar.MONTH), datePressed);
	    
	    Button btn_popup_input_create = (Button) layout.findViewById(R.id.btn_popup_input_create);
	    btn_popup_input_create.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(Intent.ACTION_INSERT, getIntent().getData());
				//startActivity(intent);
				//Toast.makeText(getBaseContext(), "Insert testing records", 2000).show();
				String yPressed = etx_popup_input_value.getText().toString();
				if (yPressed != null && !yPressed.equals(""))
					yCoorPressed = Integer.parseInt(etx_popup_input_value.getText().toString());
				else yCoorPressed  = 0;
				
				chartHelper.insert(chartTitle, xCoorPressed, yCoorPressed);
				popup.dismiss();
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
