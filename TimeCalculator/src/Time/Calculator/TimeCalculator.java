package Time.Calculator;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class TimeCalculator extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_date);
        
        drawUICalDate();
        drawUIBtnCompute();
        drawUICkbField();
    }
    
    final Calendar c = Calendar.getInstance();

	//vars for textbook dates
    static final int DATE_DIALOG_ID_START = 0;
    static final int DATE_DIALOG_ID_END = 1;
    private int istart_yy, istart_mm, istart_dd;
    private int iend_yy, iend_mm, iend_dd;
    private int idiff_yy, idiff_mm, idiff_dd;
    private int iupdate_etx;
    private EditText etx_start, etx_end;
    private EditText etx_dd, etx_mm, etx_yy;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    
    private void drawUICalDate() {
    	// get the current date
        iend_yy = istart_yy = c.get(Calendar.YEAR);
        iend_mm = istart_mm = c.get(Calendar.MONTH);
        istart_dd = c.get(Calendar.DAY_OF_MONTH);
        iend_dd = istart_dd + 1;
        
        // init dates when app start
    	etx_start = (EditText) findViewById(R.id.etx_start);
    	etx_end = (EditText) findViewById(R.id.etw_end);
    	updateDateDisplay(etx_start, istart_yy, istart_mm, istart_dd);
    	updateDateDisplay(etx_end, iend_yy, iend_mm, iend_dd);
    	
    	// create event to handle new dates
    	mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            	switch(iupdate_etx) {
            		case DATE_DIALOG_ID_START:
            			istart_yy = yy;
            			istart_mm = mm;
            			istart_dd = dd;
            			Toast.makeText(getBaseContext(), "Update Start Change", Toast.LENGTH_SHORT).show();
            			updateDateDisplay(etx_start, yy, mm, dd);
            			break;
            		case DATE_DIALOG_ID_END:
            			iend_yy = yy;
            			iend_mm = mm;
            			iend_dd = dd;
            			Toast.makeText(getBaseContext(), "Update End Change", Toast.LENGTH_SHORT).show();
            			updateDateDisplay(etx_end, yy, mm, dd);
            			break;
            	}
            }
        };
    	
    	// add edit listener to init datepicker
    	etx_start.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(DATE_DIALOG_ID_START);
				return false;
			}
		});
    	etx_end.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				showDialog(DATE_DIALOG_ID_END);
				return false;
			}
		});
    	
    	etx_dd = (EditText) findViewById(R.id.etx_dd);
		etx_mm = (EditText) findViewById(R.id.etx_mm);
		etx_yy = (EditText) findViewById(R.id.etx_yy);
    }
    
    // vars for checkboxes
    static final int CAL_START_DATE = 1;
    static final int CAL_END_DATE = 2;
    static final int CAL_DIFF_DAY = 3;
    static final int CAL_DIFF_MONTH = 4;
    static final int CAL_DIFF_YEAR = 5;
    private CheckBox ckb_start;
    private CheckBox ckb_end;
	private CheckBox ckb_diff;
	private CheckBox ckb_diff_dd;
	private CheckBox ckb_diff_mm;
	private CheckBox ckb_diff_yy;
	private int ical_ckb;
    
    // based on checkbox status to determin which field to calculate
    private void drawUICkbField() {
    	//app defaul is calculate endate
    	ical_ckb = CAL_END_DATE;
    	
    	// get ckb reference
    	ckb_start = (CheckBox)findViewById(R.id.ckb_start);
    	ckb_end = (CheckBox)findViewById(R.id.ckb_end);
    	ckb_diff = (CheckBox)findViewById(R.id.ckb_diff);
    	ckb_diff_dd = (CheckBox)findViewById(R.id.ckb_diff_dd);
    	ckb_diff_mm = (CheckBox)findViewById(R.id.ckb_diff_mm);
    	ckb_diff_yy = (CheckBox)findViewById(R.id.ckb_diff_yy);
    	
    	// add event handler when checked status changed
    	OnClickListener myckbClick = new myViewOnClickListener();
		
    	ckb_start.setOnClickListener(myckbClick);
    	ckb_end.setOnClickListener(myckbClick);
    	ckb_diff.setOnClickListener(myckbClick);
    }
    
    private void drawUIBtnCompute() {
		// add event listener to compute results
    	Button btn_compute = (Button) findViewById(R.id.btn_compute);
    	
    	OnClickListener mybtnClick = new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ComputeDate();
			}
    	};
    	Toast.makeText(getBaseContext(), "Add Compute Listener", Toast.LENGTH_SHORT).show();
    	btn_compute.setOnClickListener(mybtnClick);
		
	}
    
    private void ComputeDate() {
		switch(ical_ckb) {
		case CAL_START_DATE:
		case CAL_END_DATE:
			getDateValue(ical_ckb);
			break;
		case CAL_DIFF_DAY:
			getDiffValue();
			break;
		}
	}
    
	private void getDateValue(int ical) {
    	// retrieve diff value
    	idiff_dd = Integer.parseInt(etx_dd.getText().toString());
    	idiff_mm = Integer.parseInt(etx_mm.getText().toString());
    	idiff_yy = Integer.parseInt(etx_yy.getText().toString());
    	
    	// calculate end or start date
    	switch(ical) {
	    case CAL_START_DATE:
	    	istart_yy = iend_yy - idiff_yy;
	    	istart_mm = iend_mm - idiff_mm;
	    	istart_dd = iend_dd - idiff_dd;
	    	c.set(istart_yy, istart_mm, istart_dd);
	    	//Toast.makeText(getBaseContext(), "iEnd:" + iend_mm + "--"+iend_dd, Toast.LENGTH_SHORT).show();
	    	Toast.makeText(getBaseContext(), "Update Start Date", Toast.LENGTH_SHORT).show();
	    	updateDateDisplay(etx_start, 
	    			c.get(Calendar.YEAR), 
	    			c.get(Calendar.MONTH), 
	    			c.get(Calendar.DAY_OF_MONTH));
			break;
		case CAL_END_DATE:
			iend_yy = istart_yy + idiff_yy;
			iend_mm = istart_mm + idiff_mm;
			iend_dd = istart_dd + idiff_dd;
			c.set(iend_yy, iend_mm, iend_dd);
			Toast.makeText(getBaseContext(), "Update End Date", Toast.LENGTH_SHORT).show();
			updateDateDisplay(etx_end, 
	    			c.get(Calendar.YEAR), 
	    			c.get(Calendar.MONTH), 
	    			c.get(Calendar.DAY_OF_MONTH));
			break;
    	}
	}
	
	private void getDiffValue() {
		
	}

	// updates the date in the TextView
    private void updateDateDisplay(EditText etx, int y, int m, int d) {
    	etx.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(m + 1).append("-")
                    .append(d).append("-")
                    .append(y).append(" "));
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	DatePickerDialog dpd = null;
        switch (id) {
	        case DATE_DIALOG_ID_START:
		        	iupdate_etx = DATE_DIALOG_ID_START;
		        	dpd = new DatePickerDialog(this,
			                mDateSetListener,
			                istart_yy, istart_mm, istart_dd);
		        	break;
			case DATE_DIALOG_ID_END:
		        	iupdate_etx = DATE_DIALOG_ID_END;
		        	dpd = new DatePickerDialog(this,
			                mDateSetListener,
			                iend_yy, iend_mm, iend_dd);
		        	break;
        }
        return dpd;
    }

    class myViewOnClickListener implements View.OnClickListener {
    	@Override
		public void onClick(View ckb) {
			if (ckb.equals(ckb_start)) {
				ical_ckb = CAL_START_DATE;
				ckb_start.setChecked(true);
				ckb_end.setChecked(false);
				ckb_diff.setChecked(false);
			} else if (ckb.equals(ckb_end)) {
				ical_ckb = CAL_END_DATE;
				ckb_start.setChecked(false);
				ckb_end.setChecked(true);
				ckb_diff.setChecked(false);
			}
			else if (ckb.equals(ckb_diff)) {
				ical_ckb = 0;
				if (ckb_diff_dd.isChecked())
					ical_ckb += CAL_DIFF_DAY;
				if (ckb_diff_mm.isChecked())
					ical_ckb += CAL_DIFF_MONTH;
				if (ckb_diff_yy.isChecked())
					ical_ckb += CAL_DIFF_YEAR;
				ckb_start.setChecked(false);
				ckb_end.setChecked(false);
				ckb_diff.setChecked(true);
			}
			
			// enable diff checkboxes when compute difference
			if (ckb_diff.isChecked()) {
				ckb_diff_yy.setEnabled(true);
				ckb_diff_dd.setEnabled(true);
				ckb_diff_dd.setEnabled(true);
			}
			else {
				ckb_diff_yy.setEnabled(false);
				ckb_diff_dd.setEnabled(false);
				ckb_diff_dd.setEnabled(false);
			}
		}
    }
}