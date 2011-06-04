package Time.Calculator;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ForwardAlarm extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_alarm);
        
        //drawTimeNow();
        drawForwardTime();
        drawSetButton();
    }
	
	private ToggleButton btn_fwd_set;
	
	private void drawSetButton() {
		btn_fwd_set = (ToggleButton) findViewById(R.id.btn_fwd_set);
		btn_fwd_set.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Calendar cNow = Calendar.getInstance();
				Calendar cFwd = Calendar.getInstance();;
				if (btn_fwd_set.isChecked()) {
					setForwardAlarm(cFwd, cNow.getTimeInMillis());
					updateTime(cFwd);
				} else {
					updateTime(cNow);
				}
			}
			
		});
	}
	
	private void setForwardAlarm (Calendar cFwd, Long lNow) {
		//Calendar cFwd = Calendar.getInstance();
		Long lFwd;
		
		if (ckb_fwd_hr.isChecked()) {
			ifwd_hr = Integer.parseInt(etx_fwd_hr.getText().toString());
		} else {
			ifwd_hr = 0;
			//etx_fwd_hr.setText(0);
		}
		if (ckb_fwd_min.isChecked()) {
			ifwd_min = Integer.parseInt(etx_fwd_min.getText().toString());
		} else {
			ifwd_min = 0;
			//etx_fwd_min.setText(0);
		}
		if (ckb_fwd_sec.isChecked()) {
			ifwd_sec = Integer.parseInt(etx_fwd_sec.getText().toString());
		} else {
			ifwd_sec = 0;
			//etx_fwd_sec.setText(0);
		}
		
		lFwd = (long) ((ifwd_hr * 3600 + ifwd_min * 60 + ifwd_sec) * 1000);
		cFwd.setTimeInMillis(lFwd + lNow);
		
	}
	
	private void updateTime(Calendar c) {
		if (DateFormat.is24HourFormat(this)) {
			updateDateDisplay(txv_alm, c.get(Calendar.HOUR_OF_DAY), 
					c.get(Calendar.MINUTE),
					c.get(Calendar.SECOND),
					""); 
		} else {
			String am_pm = "";
			if (c.get(Calendar.AM_PM) == 0) am_pm = "AM"; else am_pm= "PM";
			updateDateDisplay(txv_alm, c.get(Calendar.HOUR), 
					c.get(Calendar.MINUTE),
					c.get(Calendar.SECOND),
					am_pm); 
		}
	}
	
	// updates the date in the TextView
    private void updateDateDisplay(TextView txv, int hr, int min, int sec, String am) {
    	txv.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(hr).append(":")
                    .append(min).append(":")
                    .append(sec).append(" ")
                    .append(am));
    }

	private int ifwd_sec, ifwd_min, ifwd_hr;
	private CheckBox ckb_fwd_sec, ckb_fwd_min, ckb_fwd_hr;
	private EditText etx_fwd_sec, etx_fwd_min, etx_fwd_hr;
	private TextView txv_alm;
	
	private void drawForwardTime() {
		ckb_fwd_sec = (CheckBox) findViewById(R.id.ckb_fwd_sec);
		ckb_fwd_min = (CheckBox) findViewById(R.id.ckb_fwd_min);
		ckb_fwd_hr = (CheckBox) findViewById(R.id.ckb_fwd_hr);
		
		etx_fwd_sec = (EditText) findViewById(R.id.etx_fwd_sec);
		etx_fwd_min = (EditText) findViewById(R.id.etx_fwd_min);
		etx_fwd_hr = (EditText) findViewById(R.id.etx_fwd_hr);
		
		txv_alm = (TextView) findViewById(R.id.txv_alm);
	}
	
	//Menu events
    private static final int TO_CALTIME = Menu.FIRST;
    private static final int TO_CalDate = Menu.FIRST + 1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, TO_CALTIME, 0, R.string.menu_time);
        menu.add(0, TO_CalDate, 0, R.string.menu_fwdAlarm);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case TO_CalDate:
                toCalDate();
                return true;
            case TO_CALTIME:
                toCalTime();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

	private void toCalDate() {
		Toast.makeText(getBaseContext(), "Switch to Date Calculator", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName(this, DateCalculator.class.getName());
		startActivity(intent);
	}
	
	private void toCalTime() {
		Toast.makeText(getBaseContext(), "Switch to Calculate Time", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName(this, TimeCalculator.class.getName());
		startActivity(intent);
	}

	private void toFwdAlarm() {
		//Do Nothing
	}
}
