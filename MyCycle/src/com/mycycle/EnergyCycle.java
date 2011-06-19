package com.mycycle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

public class EnergyCycle extends Activity {
	View v_chart_p;
	FrameLayout frlayout;
    private int iEditTextIndex;
    //listeners
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    
    
	private int[] ibday = new int[3];
    private int[] ichk = new int[3];
    // co-ordinates of image, x[0] is down, x[1] is up
	private int xCoorChartPress[] = new int[2];
	private int yCoorChartPress[] = new int[2];
	// timing of the press
	private long tCoorChartPress[] = new long[2];
    private int datePressed;
    private int position = 0;
    private String chartTitle = "";
    private Calendar cal_bday, cal_chk;
    private XYMultipleSeriesRenderer chartRenderer;
    private PopupWindow eventPopup;
    private EditText etx_event_date, etx_event_start, etx_event_end;
    GraphicalView mView;
    //private DismissPopup mDismissPopup;
    

    
	/** Called when the activity is first created. */
    @Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.energy_display);
	      
	      Bundle extras = getIntent().getExtras(); 
	      if(extras !=null)
	      {
	    	  position = extras.getInt("position", 0);
	      }

	      initVar();
	      initUI();
	      //draw default chart
	      updateChart(Const.CAL.getActualMaximum(Calendar.DAY_OF_MONTH));
    }
    
    private void initVar() {
    	//set default month to check
    	ichk[Const.DAY] = 0;
    	ibday[Const.DAY] = Const.CAL.get(Calendar.DAY_OF_MONTH);
    	ichk[Const.MONTH] = ibday[Const.MONTH] = Const.CAL.get(Calendar.MONTH);
    	ichk[Const.YEAR] = Const.CAL.get(Calendar.YEAR);
    	ibday[Const.YEAR] = Const.CAL.get(Calendar.YEAR) - 20;
    	
    	chartTitle = Const.CHARTTITLE[position];
    	
    	cal_bday =  Calendar.getInstance();
    	cal_chk =  Calendar.getInstance();
    	
    	datePressed = 1;
    	
    	//mDismissPopup = new DismissPopup();
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
			        	ichk[Const.MONTH] 		= monthOfYear;
			        	ichk[Const.DAY] 		= 0;
			        	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), ichk[Const.YEAR], ichk[Const.MONTH], ichk[Const.DAY]);
			        	break;
					case Const.DATE_BDAY:
						ibday[Const.YEAR] 	= year;
						ibday[Const.MONTH] 	= monthOfYear;
						ibday[Const.DAY] 		= dayOfMonth;
						updateDateDisplay((EditText)findViewById(R.id.etv_bday), ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
			        	break;
					case Const.DATE_EVENT:
						updateDateDisplay((EditText)findViewById(R.id.etx_event_date), year, monthOfYear, dayOfMonth);
						break;
				}
				cal_chk.set(ichk[Const.YEAR], ichk[Const.MONTH], 1);
				updateChart(cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH));
				
			}
    	};
    	
    	mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				switch (iEditTextIndex) {
					case Const.TIME_EVENT_END:
						updateTimeDisplay(etx_event_end, hourOfDay, minute, 0);
						break;
					case Const.TIME_EVENT_START:
						updateTimeDisplay(etx_event_start, hourOfDay, minute, 0);
						break;
				}
				
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
				updateChart(cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		};
    	btn_pre.setOnClickListener(btn_listener);
    	btn_next.setOnClickListener(btn_listener);
    	
    	//addCalendarEvent();
    	frlayout = (FrameLayout)findViewById(R.id.frlayout_chart);
    	
    	//init renderer for chart
        chartRenderer = buildRenderer(Const.COLORS, Const.STYLES);
        //init chart
        setChartSettings(chartRenderer, chartTitle, Const.BOUNDARY);
    }
	
	private void addCalendarEvent(String title, String disc, String date, String start, String end, boolean allday) {
		String[] projection = new String[] { "_id", "name" };
		Uri calendars = Uri.parse("content://calendar/calendars");

		//String[] selectionArgs = new String[] {"name=Testing"}
		Cursor managedCursor =  managedQuery(calendars, projection, "name=\"Testing\"", null, null);
		//String calName;
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dstart = null, dend = null;
		try {
			dstart = sdf.parse(date + " " + start);
			dend = sdf.parse(date + " " + end);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String calId = null;
		if (managedCursor.moveToFirst()) {
			 
			 //int nameColumn = managedCursor.getColumnIndex("name"); 
			 int idColumn = managedCursor.getColumnIndex("_id");
			 do {
				 //calName = managedCursor.getString(nameColumn);
				 calId = managedCursor.getString(idColumn);
			 } while (managedCursor.moveToNext());
			 
			 ContentValues event = new ContentValues();
			event.put("calendar_id", calId);
			event.put("title", title);
			event.put("description", disc);
			//event.put("eventLocation", "Event Location");
			
		    event.put("dtstart", dstart.getTime());
		    event.put("dtend", dend.getTime());

		    if (allday) event.put("allDay", 0); else event.put("allDay", 1); // 0 for false, 1 for true
		    
			event.put("eventStatus", 1);
			event.put("visibility", 0);
			event.put("transparency", 0);
			event.put("hasAlarm", 1); // 0 for false, 1 for true
			Uri eventsUri = Uri.parse("content://calendar/events");
			Uri url = getContentResolver().insert(eventsUri, event);
		}
		
		
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog = null;
        switch (id) {
	        case Const.DATE_TO_CHECK:
        		iEditTextIndex = Const.DATE_TO_CHECK;
        		dialog = new DatePickerDialog(this,
		                mDateSetListener,
		                ichk[Const.YEAR], ichk[Const.MONTH], ichk[Const.DAY]+1);
	        	break;
			case Const.DATE_BDAY:
				iEditTextIndex = Const.DATE_BDAY;
				dialog = new DatePickerDialog(this,
		                mDateSetListener,
		                ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
	        	break;
			case Const.DATE_EVENT:
				iEditTextIndex = Const.DATE_EVENT;
				dialog = new DatePickerDialog(this,
		                mDateSetListener,
		                ichk[Const.YEAR], ichk[Const.MONTH], datePressed);
	        	break;
			case Const.TIME_EVENT_START:
				iEditTextIndex = Const.TIME_EVENT_START;
				dialog = new TimePickerDialog(this,
						mTimeSetListener, 9, 0, true);
	        	break;
			case Const.TIME_EVENT_END:
				iEditTextIndex = Const.TIME_EVENT_END;
				dialog = new TimePickerDialog(this,
						mTimeSetListener, 13, 0, true);
	        	break;
				
        }
        return dialog;
    }
    
    private void updateDateDisplay(EditText etx, int y, int m, int d) {
    	if (d == 0) {
    		etx.setText(new StringBuilder().append(y).append("-").append(m + 1).append(" "));
    	}
    	else {
    		etx.setText(new StringBuilder().append(y).append("-").append(m + 1).append("-").append(d).append(" "));
    	}
    }
    
    private void updateTimeDisplay(EditText etx, int hour, int min, int sec) {
    	String sHour, sMin, sSec;
    	if (hour < 10) sHour = "0" + hour; else sHour = "" + hour;
    	if (min < 10) sMin = "0" + min; else sMin = "" + min;
    	if (sec < 10) sSec = "0" + sec; else sSec = "" + sec;
		etx.setText(sHour + ":" + sMin + ":" + sSec + " ");
    }
    
    private void updateChart(int idays_in_month) {
    	//Toast.makeText(this, "Days in this month: " + idays_in_month, Toast.LENGTH_SHORT).show();
    	
    	// clear previous chart
    	List<double[]> x = new ArrayList<double[]>();
        List<double[]> values = new ArrayList<double[]>();
        double[] dDate = new double[idays_in_month];
        double[] dEnergy = new double[idays_in_month];
        
        int cycle = Const.DURATION[position];
        
    	// Calendar Vars
        cal_bday.set(ibday[Const.YEAR], ibday[Const.MONTH], ibday[Const.DAY]);
    	String[] titles = Const.VALUE_TITLE[position];
        
        for (int i = 0; i < titles.length; i++) {
        	for (int j = 1; j <= idays_in_month; j++) {
        		dDate[j - 1] = j;
        		cal_chk.set(ichk[Const.YEAR], ichk[Const.MONTH], (int)dDate[j - 1]);
        		
        		long idiff_days 	= (cal_chk.getTimeInMillis() - cal_bday.getTimeInMillis()) / Const.DAYS_IN_MILLSECOND;
        		int irest		= (int) (idiff_days % cycle);
        		dEnergy[j - 1] = Math.sin((double)irest/cycle * 2 * Math.PI) * 15 + 20.0;
        	}
        	x.add(dDate);
        	values.add(dEnergy);
        }
        reDrawEnergyChart(titles, x, values);
    }
   
	private void reDrawEnergyChart(String[] titles, List<double[]> x, List<double[]> values) {
		
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values); 
        mView = new GraphicalView(this, (XYChart)new LineChart(dataset, chartRenderer));
        
        mView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					xCoorChartPress[0] = (int) event.getX();
					yCoorChartPress[0] = (int) event.getY();
					tCoorChartPress[0] = System.currentTimeMillis();
				} 
				if (action == MotionEvent.ACTION_UP) {
					xCoorChartPress[1] = (int) event.getX();
					yCoorChartPress[1] = (int) event.getY();
					tCoorChartPress[1] = System.currentTimeMillis();
				}
				
				//if movement is less than 10, and holding time is longer than 3s, trigger add calendar events
				//long dist = (x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0]) * (y[1] - y[0]); 
				long delta = 300;
				if ( (xCoorChartPress[1] - xCoorChartPress[0]) * (xCoorChartPress[1] - xCoorChartPress[0]) 
						+ (yCoorChartPress[1] - yCoorChartPress[0]) * (yCoorChartPress[1] - yCoorChartPress[0]) < delta // down & up distance 
						&& (tCoorChartPress[1] - tCoorChartPress[0]) > 3 /* down & up time */) {
					//Toast.makeText(getBaseContext(), "Create Calendar Events", Toast.LENGTH_SHORT).show();
					double xstart = chartRenderer.getXAxisMin();
					datePressed = (int) Math.round((xCoorChartPress[1] + xCoorChartPress[0]) / 
														(2.0 * v.getWidth() / chartRenderer.getXLabels())
														+ xstart);
					Toast.makeText(getBaseContext(), "Coor Up: " + xCoorChartPress[1] + " " + yCoorChartPress[1] + " Date pressed: "
							+ datePressed, Toast.LENGTH_SHORT).show();
					drawPopupEvent();
					return true;
				}
				
				// action == move when be handle by AChartEng API
				return false;
			}

			

			 
		});

        frlayout.removeAllViews();
        frlayout.addView(mView);
        
    }
	
	
	private void drawPopupEvent() {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.event_popup, (ViewGroup)findViewById(R.id.popup_event_root));
	    //eventPopup = new PopupWindow(inflater.inflate(R.layout.event_popup, null, false),  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
		eventPopup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
	    eventPopup.setFocusable(true);
	    eventPopup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    //pw.setOutsideTouchable(false);
	    //eventPopup.setTouchable(true);
	    
	    etx_event_date = (EditText) layout.findViewById(R.id.etx_event_date);
	    etx_event_date.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_EVENT);
				return false;
			}
    		
    	});
	    etx_event_start = (EditText) layout.findViewById(R.id.etx_event_start);
	    etx_event_start.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.TIME_EVENT_START);
				return false;
			}
    		
    	});
	    etx_event_end = (EditText) layout.findViewById(R.id.etx_event_end);
	    etx_event_end.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.TIME_EVENT_END);
				return false;
			}
    		
    	});
	    final EditText etx_event_title = (EditText) layout.findViewById(R.id.etx_event_title);
	    final EditText etx_event_disc = (EditText) layout.findViewById(R.id.etx_event_disc);
	    final CheckBox ckb_event_allday = (CheckBox) layout.findViewById(R.id.ckb_event_allday);
	    
	    updateDateDisplay(etx_event_date, ichk[0], ichk[1], datePressed);
	    updateTimeDisplay(etx_event_start, 9, 0, 0);
	    updateTimeDisplay(etx_event_end, 13, 0, 0);
	    
	    Button btn_event_create = (Button) layout.findViewById(R.id.btn_event_create);
	    btn_event_create.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addCalendarEvent(etx_event_title.getText().toString(), 
								etx_event_disc.getText().toString(), 
								etx_event_date.getText().toString(), 
								etx_event_start.getText().toString(), 
								etx_event_end.getText().toString(), 
								ckb_event_allday.isChecked());
				eventPopup.dismiss();
				Toast.makeText(getBaseContext(), "Create Events", Toast.LENGTH_SHORT).show();
			}
		});
	    Button btn_event_cancel = (Button) layout.findViewById(R.id.btn_event_cancel);
	    btn_event_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				eventPopup.dismiss();
				Toast.makeText(getBaseContext(), "Cancel Events", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	class DismissPopup implements Runnable {
		  public void run() {
		      // Protect against null-pointer exceptions
		      if (eventPopup != null) {
		    	  eventPopup.dismiss();
		      }
		  }
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
