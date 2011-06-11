package com.mycycle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class EnergyCycle extends Activity {
	View v_chart_p;
    private int iEditTextIndex;
    //listeners
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    
    
	private int[] ibday = new int[3];
    private int[] ichk = new int[3];
    private int duration = 0;
    private String chartTitle = "";
    private Calendar cal_bday, cal_chk;
	
	/** Called when the activity is first created. */
    @Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.physical_energy);
	      
	      int position = 0;
	      Bundle extras = getIntent().getExtras(); 
	      if(extras !=null)
	      {
	    	  position = extras.getInt("position", 0);
	      }

	      initVar(position);
	      initUI();
	      //draw default chart
	      updateChart(Const.CAL.getActualMaximum(Calendar.DAY_OF_MONTH), duration);
	      
    }
    
    private void initVar(int position) {
    	//set default month to check
    	ichk[Const.DAY] = 0;
    	ibday[Const.DAY] = Const.CAL.get(Calendar.DAY_OF_MONTH);
    	ichk[Const.MONTH] = ibday[Const.MONTH] = Const.CAL.get(Calendar.MONTH);
    	ichk[Const.YEAR] = Const.CAL.get(Calendar.YEAR);
    	ibday[Const.YEAR] = Const.CAL.get(Calendar.YEAR) - 20;
    	
    	duration = Const.DURATION[position];
    	chartTitle = Const.CHARTTITLE[position];
    	
    	cal_bday =  Calendar.getInstance();
    	cal_chk =  Calendar.getInstance();
	}

	private void initUI() {
		//Get view_stub from chart to display
	    if (v_chart_p == null) {
	    	  v_chart_p = ((ViewStub) findViewById(R.id.vstub_p)).inflate();
	    }
	    v_chart_p.setVisibility(View.VISIBLE);
	      
    	//set default days to be display when init
    	EditText etv_chk = (EditText) findViewById(R.id.etv_mon2chk);
    	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), ichk[Const.YEAR], ichk[Const.MONTH], ichk[Const.DAY]);
    	etv_chk.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_TO_CHECK);
				return false;
			}
    		
    	});
    	EditText etv_bday = (EditText)findViewById(R.id.etv_bday);
    	updateDateDisplay(etv_bday, ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
    	
    	etv_bday.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_BDAY);
				return false;
			}
    		
    	});
    	
    	mDateSetListener = new DatePickerDialog.OnDateSetListener(){

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				switch (iEditTextIndex) {
			        case Const.DATE_TO_CHECK:
			        	ichk[Const.YEAR] 		= year;
			        	ichk[Const.MONTH] 	= monthOfYear;
			        	ichk[Const.DAY] 		= 0;
			        	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), ichk[Const.YEAR], ichk[Const.MONTH], ichk[Const.DAY]);
			        	break;
					case Const.DATE_BDAY:
						ibday[Const.YEAR] 	= year;
						ibday[Const.MONTH] 	= monthOfYear;
						ibday[Const.DAY] 		= dayOfMonth;
						updateDateDisplay((EditText)findViewById(R.id.etv_bday), ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
			        	break;
				}
				cal_chk.set(ichk[Const.YEAR], ichk[Const.MONTH], 1);
				updateChart(cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH), duration);
				
			}
    	};
    	
    	Button btn_pre =(Button) findViewById(R.id.btn_pre);
    	Button btn_next =(Button) findViewById(R.id.btn_next);
    	OnClickListener btn_listener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int id = v.getId();
				if (id == R.id.btn_next) {
					Toast.makeText(getBaseContext(), "Check Next Month", Toast.LENGTH_SHORT).show();
					if (++ichk[Const.MONTH] > Calendar.DECEMBER) {
						ichk[Const.YEAR]++;
						ichk[Const.MONTH] = Calendar.JANUARY;
					}
				} else if (id == R.id.btn_pre) {
					Toast.makeText(getBaseContext(), "Check Previous Month", Toast.LENGTH_SHORT).show();
					if (--ichk[Const.MONTH] < Calendar.JANUARY) {
						ichk[Const.YEAR]--;
						ichk[Const.MONTH] = Calendar.DECEMBER;
					}
				}
				cal_chk.set(ichk[Const.YEAR], ichk[Const.MONTH], 1);
				updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), ichk[Const.YEAR], ichk[Const.MONTH], 0);
				updateChart(cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH), duration);
			}
		};
    	btn_pre.setOnClickListener(btn_listener);
    	btn_next.setOnClickListener(btn_listener);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
    	DatePickerDialog dpd = null;
        switch (id) {
	        case Const.DATE_TO_CHECK:
        		iEditTextIndex = Const.DATE_TO_CHECK;
	        	dpd = new DatePickerDialog(this,
		                mDateSetListener,
		                ichk[Const.YEAR], ichk[Const.MONTH], ichk[Const.DAY]+1);
	        	break;
			case Const.DATE_BDAY:
				iEditTextIndex = Const.DATE_BDAY;
	        	dpd = new DatePickerDialog(this,
		                mDateSetListener,
		                ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
	        	break;
        }
        return dpd;
    }
    
    private void updateDateDisplay(EditText etx, int y, int m, int d) {
    	if (d == 0) {
    		etx.setText(new StringBuilder().append(m + 1).append("-").append(y).append(" "));
    	}
    	else {
    		etx.setText(new StringBuilder().append(m + 1).append("-").append(d).append("-").append(y).append(" "));
    	}
    }
    
    private void updateChart(int idays_in_month, int cycle) {
    	//Toast.makeText(this, "Days in this month: " + idays_in_month, Toast.LENGTH_SHORT).show();
    	
    	// clear previous chart
    	List<double[]> x = new ArrayList<double[]>();
        List<double[]> values = new ArrayList<double[]>();
        double[] dDate = new double[idays_in_month];
        double[] dEnergy = new double[idays_in_month];
        
    	// Calendar Vars
        cal_bday.set(ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
    	String[] titles = new String[] { "Body Strength"};
        
        for (int i = 0; i < titles.length; i++) {
        	for (int j = 1; j <= idays_in_month; j++) {
        		dDate[j - 1] = j;
        		cal_chk.set(ichk[Const.YEAR], ichk[Const.MONTH], (int)dDate[j - 1]);
        		
        		long idiff_days 	= (cal_chk.getTimeInMillis() - cal_bday.getTimeInMillis()) / Const.DAYS_IN_MILLSECOND;
        		int irest		= (int) (idiff_days % cycle);
        		dEnergy[j - 1] = Math.sin((double)irest/cycle * 2 * Math.PI) * 15 + 15.0;
        	}
        	x.add(dDate);
        	values.add(dEnergy);
        }
        reDrawEnergyChart(titles, x, values);
    }
   
	private void reDrawEnergyChart(String[] titles, List<double[]> x, List<double[]> values) {
		//init renderer
        XYMultipleSeriesRenderer renderer = buildRenderer(Const.COLORS, Const.STYLES);
        
        //init chart
        setChartSettings(renderer, chartTitle, Const.BOUNDARY);
        
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values); 
        GraphicalView mView = new GraphicalView(this, (XYChart)new LineChart(dataset, renderer));
        
        FrameLayout frlayout = (FrameLayout)findViewById(R.id.frlayout_chart);
        frlayout.removeAllViews();
        frlayout.addView(mView);
    }
	
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, double[] dBoundary) {
	    renderer.setChartTitle(title);
	    
	    renderer.setXTitle(Const.XTITLE);
	    renderer.setYTitle(Const.YTITLE);
	    
	    int iXMin = 0;
	    int iXMax = 1;
	    int iYMin = 2;
	    int iYMax = 3;
	    renderer.setXAxisMin(dBoundary[iXMin]);
	    renderer.setXAxisMax(dBoundary[iXMax]);
	    renderer.setYAxisMin(dBoundary[iYMin]);
	    renderer.setYAxisMax(dBoundary[iYMax]);
	    
	    
	    renderer.setAxesColor(Color.LTGRAY);
	    renderer.setLabelsColor(Color.LTGRAY);
	    
	    renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setPanLimits(Const.PANLIMIT);
        //renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
        //disable zoom
        renderer.setZoomLimits(null);
        renderer.setZoomEnabled(false, false);
    }
    
    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
		  XYSeriesRenderer r = new XYSeriesRenderer();
		  r.setColor(colors[i]);
		  r.setPointStyle(styles[i]);
		  r.setFillPoints(true);
		  renderer.addSeriesRenderer(r);
		}
		return renderer;
    }
    
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
    	      List<double[]> yValues) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      XYSeries series = new XYSeries(titles[i]);
	      double[] xV = xValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(xV[k], yV[k]);
	      }
	      dataset.addSeries(series);
	    }
	    return dataset;
    }

}
