package com.moupress.app.dailycycle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
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

public class EnergyCycle extends AbstractCycle {
	
    private int iEditTextIndex;
    //listeners
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    
    private int datePressed;
    private int position = 0;
    private String chartTitle = "";
    //private Calendar cal_chk;
    private static BioChart myBioCycle;
    private EditText etx_event_date, etx_event_start, etx_event_end;
    
    
	/** Called when the activity is first created. */
    @Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      Bundle extras = getIntent().getExtras(); 
	      if(extras !=null)
	      {
	    	  position = extras.getInt("position", 0);
	      }

	      initVar();
	      initUI();
	      //draw default chart
	      myBioCycle.updateChart(this, Const.DURATION[position], Const.VALUETITLE[position]);
    }
    
   

	private void initVar() {
	
    	chartTitle = Const.CHARTTITLE[position];
    	//FrameLayout layout = (FrameLayout)findViewById(R.id.frlayout_chart);
    	if (myBioCycle == null)
    		myBioCycle = new BioChart(chartTitle, this);
    	
    	datePressed = 1;
	}

	@Override
	protected void initUI() {
		super.initUI();
	      
    	//set default days to be display when init
    	EditText etv_chk = (EditText) findViewById(R.id.etv_mon2chk);
    	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), 0);
    	etv_chk.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_TO_CHECK);
				return false;
			}
    		
    	});
    	EditText etv_bday = (EditText)findViewById(R.id.etv_bday);
    	updateDateDisplay(etv_bday, myBioCycle.cal_bday.get(Calendar.YEAR), myBioCycle.cal_bday.get(Calendar.MONTH), myBioCycle.cal_bday.get(Calendar.DAY_OF_MONTH));
    	
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
			        	myBioCycle.cal_chk.set(year, monthOfYear, 1);
			        	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), year, monthOfYear, 0);
			        	break;
					case Const.DATE_BDAY:
						myBioCycle.cal_bday.set(year, monthOfYear, dayOfMonth);
						updateDateDisplay((EditText)findViewById(R.id.etv_bday), year, monthOfYear, dayOfMonth);
			        	break;
					case Const.DATE_EVENT:
						updateDateDisplay((EditText)findViewById(R.id.etx_event_date), year, monthOfYear, dayOfMonth);
						break;
				}
				myBioCycle.updateChart(getBaseContext(), Const.DURATION[position], Const.VALUETITLE[position]);
				
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
					Toast.makeText(getBaseContext(), "Check Next Month", Const.TOASTSHOWTIME).show();
					if (myBioCycle.cal_chk.get(Calendar.MONTH) > Calendar.DECEMBER) {
						myBioCycle.cal_chk.set(myBioCycle.cal_chk.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
					}
					else {
						myBioCycle.cal_chk.set(myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH) + 1, 1);
					}
				} else if (id == R.id.btn_pre) {
					Toast.makeText(getBaseContext(), "Check Previous Month", Const.TOASTSHOWTIME).show();
					if (myBioCycle.cal_chk.get(Calendar.MONTH) == Calendar.JANUARY) {
						myBioCycle.cal_chk.set(myBioCycle.cal_chk.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 1);
					} else {
						myBioCycle.cal_chk.set(myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH) - 1, 1);
					}
				}
				updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), 0);
				myBioCycle.updateChart(getBaseContext(), Const.DURATION[position], Const.VALUETITLE[position]);
			}
		};
    	btn_pre.setOnClickListener(btn_listener);
    	btn_next.setOnClickListener(btn_listener);
    	

	    //FrameLayout framelayout = (FrameLayout)findViewById(R.id.frlayout_chart);
	    myBioCycle.setFrameLayout((FrameLayout)findViewById(R.id.frlayout_chart));
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
		                myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), 1);
	        	break;
			case Const.DATE_BDAY:
				iEditTextIndex = Const.DATE_BDAY;
				dialog = new DatePickerDialog(this,
		                mDateSetListener,
		                myBioCycle.cal_bday.get(Calendar.YEAR), myBioCycle.cal_bday.get(Calendar.MONTH), myBioCycle.cal_bday.get(Calendar.DAY_OF_MONTH));
	        	break;
			case Const.DATE_EVENT:
				iEditTextIndex = Const.DATE_EVENT;
				dialog = new DatePickerDialog(this,
		                mDateSetListener,
		                myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), datePressed);
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
    
	
	public void drawPopupEvent(int datePressed) {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.event_popup, (ViewGroup)findViewById(R.id.popup_event_root));
//		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		layoutParam.setMargins(50, 10, 50, 20);
//		layout.setLayoutParams(layoutParam);
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
	    popup.setFocusable(true);
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    
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
	    
	    updateDateDisplay(etx_event_date, myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), datePressed);
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
				popup.dismiss();
				Toast.makeText(getBaseContext(), "Create Events", Const.TOASTSHOWTIME).show();
			}
		});
	    Button btn_event_cancel = (Button) layout.findViewById(R.id.btn_event_cancel);
	    btn_event_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popup.dismiss();
				Toast.makeText(getBaseContext(), "Cancel Events", Const.TOASTSHOWTIME).show();
			}
		});

	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        int menuIndex = Menu.FIRST;
        menu.add(0, menuIndex++, 0, R.string.menu_about)
        //.setShortcut('3', 'a')
        .setIcon(android.R.drawable.ic_menu_help);
        
        menu.add(0, menuIndex++, 0, R.string.menu_save_as)
        //.setShortcut('3', 'a')
        .setIcon(android.R.drawable.ic_menu_save);
        
        menu.add(0, menuIndex++, 0, R.string.menu_changeview)
        //.setShortcut('3', 'a')
        .setIcon(android.R.drawable.ic_menu_my_calendar);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Const.MENU_CHART_ABOUT:
            //Display more info to user
            return true;
        case Const.MENU_CHART_SAVEAS:
        	//Save the chart to db
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
