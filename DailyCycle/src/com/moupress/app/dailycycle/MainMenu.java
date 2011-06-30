package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainMenu extends ListActivity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        int length = Const.MENU.length;
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
		for (int i = 0; i < length; i++) {
			Map<String, String> v = new HashMap<String, String>();
			v.put(Const.MENUNAME, Const.MENU[i]);
			v.put(Const.MENUDESC, Const.MENU_SUMMARY[i]);
			values.add(v);
		}
		
//		Intent intent = getIntent();
//        if (intent.getData() == null) {
//            intent.setData(Charts.CONTENT_URI);
//        }
        
        setListAdapter(new SimpleAdapter(this, values, android.R.layout.simple_list_item_2,
            new String[] { Const.MENUNAME, Const.MENUDESC }, new int[] { android.R.id.text1, android.R.id.text2 }));
        
        // Inform the list we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
        //registerForContextMenu(this);
        
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(this, EnergyCycle.class);
		intent.putExtra("position", position);
		startActivity(intent);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Const.MENU_CHART_INSERT, 0, R.string.menu_insert)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Const.MENU_CHART_INSERT:
        	createNewChartPopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private PopupWindow newchartPopup;
    private void createNewChartPopup() {
    	//Context mContext = getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
    	final View layout = inflater.inflate(R.layout.new_chart, (ViewGroup) findViewById(R.id.new_chart_layout));
    	
    	newchartPopup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
    	
    	newchartPopup.showAtLocation(layout, Gravity.CENTER, 10, 10);
    	
    	Button btn_newchart_create = (Button) layout.findViewById(R.id.btn_newchart_create);
    	Button btn_newchart_cancel = (Button) layout.findViewById(R.id.btn_newchart_cancel);
    	btn_newchart_create.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View arg0) {
				String title = ((EditText)layout.findViewById(R.id.etx_newchart_title)).getText().toString();
				String yaxis = ((EditText)layout.findViewById(R.id.etx_newchart_yaxis)).getText().toString();
				
				Intent intent = new Intent(getBaseContext(), CustomCycle.class);
				intent.putExtra("Title", title);
				intent.putExtra("yAxis", yaxis);
				newchartPopup.dismiss();
				startActivity (intent);
				
			}
		});
    	btn_newchart_cancel.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				newchartPopup.dismiss();
				Toast.makeText(getBaseContext(), "Cancel New Charts", Const.TOASTSHOWTIME).show();
			}
		});
		
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(Const.TAG, "bad menuInfo", e);
            return;
        }
        // Setup the menu header
        menu.setHeaderTitle(Const.MENU[info.position]);
        // Add a menu item to delete the note
        menu.add(0, Const.MENU_CHART_DELETE, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info;
//        try {
//             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        } catch (ClassCastException e) {
//            Log.e(Const.TAG, "bad menuInfo", e);
//            return false;
//        }

        switch (item.getItemId()) {
            case Const.MENU_CHART_DELETE: {
                // Delete the chart created by user
                return true;
            }
        }
        return false;
    }
}