package Time.Calculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DigitalClock;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimeCalculator extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_time);
        
    }
	
	
	
	
	




	//Menu events
    private static final int TO_FWDALM = Menu.FIRST;
    private static final int TO_CalDate = Menu.FIRST + 1;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, TO_FWDALM, 0, R.string.menu_time);
        menu.add(0, TO_CalDate, 0, R.string.menu_fwdAlarm);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case TO_CalDate:
                toCalDate();
                return true;
            case TO_FWDALM:
            	toFwdAlarm();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void toFwdAlarm() {
		Toast.makeText(getBaseContext(), "Switch to Forward Alarm", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName(this, ForwardAlarm.class.getName());
		startActivity(intent);
	}
	
	private void toCalDate() {
		Toast.makeText(getBaseContext(), "Switch to Date Calculator", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName(this, DateCalculator.class.getName());
		startActivity(intent);
	}
	
	private void toCalTime() {
		//Do Nothing
	}
}
