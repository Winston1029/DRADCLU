package Time.Calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class TimeCalculator extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cal_date);
        
        //drawUIMain();
    }
    
    private void drawUIMain() {
    	fillSpinner();
    }
    
    private void fillSpinner() {
    	Spinner sp_startMM = (Spinner) findViewById(R.id.sp_startMM);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.mm, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_startMM.setAdapter(adapter);
        sp_startMM.setOnItemSelectedListener(new MyOnItemSelectedListener());
        //adapter.
        Spinner sp_startDD = (Spinner) findViewById(R.id.sp_startDD);
        
    }
    
    class MyOnItemSelectedListener implements OnItemSelectedListener {
    	@Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	Toast.makeText(parent.getContext(), "The planet is " +
            parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

    	@Override
        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
}