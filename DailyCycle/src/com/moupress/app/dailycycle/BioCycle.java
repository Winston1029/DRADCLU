package com.moupress.app.dailycycle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BioCycle extends AbstractCycle {
	
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
    //@Override
	 public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      
	      Bundle extras = getIntent().getExtras(); 
	      if(extras !=null)
	      {
	    	  birthday = extras.getInt(Birthday.BIRTHDAY, -1);
	    	  if (birthday > -1) {
	    		  position = 3; //display all
	    	  } else {
	    		  position = extras.getInt("position", 0);
	    	  }
	      }

	      initVar();
	      initUI();
	      //draw default chart
	      myBioCycle.updateChart(this, Const.DURATION[position], Const.VALUETITLE[position]);
    }
    
   

	protected void initVar() {
		super.initVar();
    	chartTitle = Const.CHARTTITLE[position];
    	//FrameLayout layout = (FrameLayout)findViewById(R.id.frlayout_chart);
    	if (myBioCycle == null)
    		myBioCycle = new BioChart(chartTitle, this);
    	myBioCycle.chartRenderer.setChartTitle(chartTitle);
    	datePressed = 1;
    	
    	if (birthday > -1) {
    		myBioCycle.cal_bday.set(birthday / 10000, (birthday % 10000) / 100 - 1, birthday % 100);
    	}
	}

	@Override
	protected void initUI() {
		super.initUI();
	      
    	//set default days to be display when init
    	EditText etv_chk = (EditText) findViewById(R.id.etv_mon2chk);
    	updateDateDisplay((EditText)findViewById(R.id.etv_mon2chk), myBioCycle.cal_chk.get(Calendar.YEAR), myBioCycle.cal_chk.get(Calendar.MONTH), 0);
    	etv_chk.setOnTouchListener(new View.OnTouchListener() {

			//@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_TO_CHECK);
				return false;
			}
    		
    	});
    	EditText etv_bday = (EditText)findViewById(R.id.etv_bday);
    	updateDateDisplay(etv_bday, myBioCycle.cal_bday.get(Calendar.YEAR), myBioCycle.cal_bday.get(Calendar.MONTH), myBioCycle.cal_bday.get(Calendar.DAY_OF_MONTH));
    	
    	etv_bday.setOnTouchListener(new View.OnTouchListener() {

			//@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_BDAY);
				return false;
			}
    		
    	});
    	
    	mDateSetListener = new DatePickerDialog.OnDateSetListener(){

			//@Override
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
			
			//@Override
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
    	MyBtnListener btn_listener = new MyBtnListener();
    	
    	btn_pre.setOnClickListener(btn_listener);
    	btn_next.setOnClickListener(btn_listener);
    	
	    myBioCycle.setFrameLayout((FrameLayout)findViewById(R.id.frlayout_chart));
    }
	
	private void addCalendarEvent(String title, String disc, String date, String start, String end, boolean allday) {
		String[] projection = new String[] { "_id", "name" };
		Uri calendars = Uri.parse("content://calendar/calendars");

		//String[] selectionArgs = new String[] {"name=Testing"}
		Cursor managedCursor =  managedQuery(calendars, projection, "selected=1", null, null);
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
    
	@Override
	public void drawPopup(int datePressed) {
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.event_popup, (ViewGroup)findViewById(R.id.popup_event_root));
//		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		layoutParam.setMargins(50, 10, 50, 20);
//		layout.setLayoutParams(layoutParam);
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
	    popup.setFocusable(true);
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
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    
	    etx_event_date = (EditText) layout.findViewById(R.id.etx_event_date);
	    etx_event_date.setOnTouchListener(new View.OnTouchListener() {
			//@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.DATE_EVENT);
				return false;
			}
    		
    	});
	    etx_event_start = (EditText) layout.findViewById(R.id.etx_event_start);
	    etx_event_start.setOnTouchListener(new View.OnTouchListener() {

			//@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(Const.TIME_EVENT_START);
				return false;
			}
    		
    	});
	    etx_event_end = (EditText) layout.findViewById(R.id.etx_event_end);
	    etx_event_end.setOnTouchListener(new View.OnTouchListener() {

			//@Override
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
			
			//@Override
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
			
			//@Override
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
    
    private int birthday = -1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Const.MENU_CHART_ABOUT:
        	Dialog dialog = new Dialog(this);
        	dialog.setContentView(R.layout.custom_dialog);
        	setAboutDialog(dialog);
        	dialog.show();
        	
            return true;
        case Const.MENU_CHART_SAVEAS:
        	//Toast.makeText(this, "Coming Soon", Const.TOASTSHOWTIME).show();
        	birthday = myBioCycle.cal_bday.get(Calendar.YEAR) * 10000
        					+ (myBioCycle.cal_bday.get(Calendar.MONTH) + 1) * 100 
        					+ myBioCycle.cal_bday.get(Calendar.DAY_OF_MONTH);
        	drawPopup();
        	return true;
        case Const.MENU_CHART_CHANGEVIEW:
        	if (myBioCycle.weekly_view) {
        		myBioCycle.weekly_view = false;
        		item.setTitle("To Weekly View");
        	}
        	else {
        		myBioCycle.weekly_view = true;
        		item.setTitle("To Monthly View");
        	}
        	myBioCycle.updateChart(getBaseContext(), Const.DURATION[position], Const.VALUETITLE[position]);
        	
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    //overload drawpopup method
    public void drawPopup() {
    	LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.input_popup, (ViewGroup)findViewById(R.id.popup_input_root));
		
		popup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
		popup.setBackgroundDrawable(new BitmapDrawable()); //essential to catch KeyEvents happen outside popup window
    	layout.setOnKeyListener(new View.OnKeyListener() {
			
			//@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK)) {
					popup.dismiss();
			    }
				return false;
			}
		});
    	((TextView) layout.findViewById(R.id.txv_popup_input_title1)).setText("Who's birthday?");
    	updateDateDisplay((TextView)layout.findViewById(R.id.txv_popup_input_title2), 
    													myBioCycle.cal_bday.get(Calendar.YEAR), 
    													myBioCycle.cal_bday.get(Calendar.MONTH), 
    													myBioCycle.cal_bday.get(Calendar.DAY_OF_MONTH));
	    popup.setFocusable(true);
	    popup.showAtLocation(layout, Gravity.CENTER, 10, 10);
	    final EditText etx_popup_input_value = (EditText)layout.findViewById(R.id.etx_popup_input_value);
	    
	    Button btn_popup_input_create = (Button) layout.findViewById(R.id.btn_popup_input_create);
	    btn_popup_input_create.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String name = etx_popup_input_value.getText().toString();
				if (name != null && !name.equals("")) {
					List<ContentValues> value = dbHelper.getBirthday(name);
					if (value.size() == 0 || value == null ) {
						dbHelper.insertBirthday(birthday, name);
						Toast.makeText(getBaseContext(), "New birthday inserted", Toast.LENGTH_SHORT).show();
					} else {
						dbHelper.updateBirthday(birthday, name);
						Toast.makeText(getBaseContext(), "Updated existing birthday", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(getBaseContext(), "Please indicate whose birthday is this", Toast.LENGTH_SHORT).show();
				}
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
    
    private void setAboutDialog(Dialog dialog) {
    	String chartTitle = myBioCycle.chartRenderer.getChartTitle();
    	TextView text = (TextView) dialog.findViewById(R.id.txv_about);
    	if (chartTitle.equals(Const.CHARTTITLE[0])) {
    		dialog.setTitle("Physical Cycle - 23 days");
        	text.setText("Physical is the dominant cycle in men. " +
        			"It regulates coordination, strength, endurance, sex, stamina, initiative, resistence to and recovery from injury and illnesses.");
    	} else if (chartTitle.equals(Const.CHARTTITLE[1])) {
    		dialog.setTitle("Emotional Cycle - 28 days");
        	text.setText("Emotional is more for women. " +
        			"It regulates feelings, moods, sensitivity, sensation, sexuality, fantasy, reactions and creativity");
    	} else if (chartTitle.equals(Const.CHARTTITLE[2])) {
    		dialog.setTitle("Intelletual Cycle - 33 days");
        	text.setText("Intellectual regualtes logic, mental reaction, judgement, memory, deduction, sense of direction, and ambition");
    	}
    	
    	ImageView image = (ImageView) dialog.findViewById(R.id.img_about);
    	image.setImageResource(android.R.drawable.ic_menu_my_calendar);
    	dialog.setCancelable(true);
    }

    private class MyBtnListener implements View.OnClickListener {

		public void onClick(View v) {
			int id = v.getId();
			if (myBioCycle.weekly_view) {
				if (id == R.id.btn_next) {
					if (myBioCycle.weekly_view_start + 7 > myBioCycle.cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH)){
						Toast.makeText(getBaseContext(), "This is the last week of the month", Const.TOASTSHOWTIME).show();
					} else {
						myBioCycle.weekly_view_start += 7;
					}
				}  else if (id == R.id.btn_pre) {
					if (myBioCycle.weekly_view_start == 0){
						Toast.makeText(getBaseContext(), "This is the first week of the month", Const.TOASTSHOWTIME).show();
					} else {
						myBioCycle.weekly_view_start -= 7;
					}
				}
				//myBioCycle.updateChart(getBaseContext(), Const.DURATION[position], Const.VALUETITLE[position], true);
					
			} else {
				if (id == R.id.btn_next) {
					Toast.makeText(getBaseContext(), "Check Next Month", Const.TOASTSHOWTIME).show();
					if (myBioCycle.cal_chk.get(Calendar.MONTH) == Calendar.DECEMBER) {
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
			}
			myBioCycle.updateChart(getBaseContext(), Const.DURATION[position], Const.VALUETITLE[position]);
			
		}
    	
    }
}
