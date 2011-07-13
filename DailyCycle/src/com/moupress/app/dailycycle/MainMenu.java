package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
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
	
	public static DBHelper dbHelper;
	List<Map<String, String>> values;
	List<ContentValues> custom_charts;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (dbHelper == null)
    		dbHelper = new DBHelper(this);
        
        custom_charts = dbHelper.getCustomCharts();
        
        int length = Const.MENU.length ;
        values = new ArrayList<Map<String, String>>();
		for (int i = 0; i < length; i++) {
			Map<String, String> v = new HashMap<String, String>();
			v.put(Const.MENUNAME, Const.MENU[i]);
			v.put(Const.MENUDESC, Const.MENU_SUMMARY[i]);
			values.add(v);
		}
		if (custom_charts.size() > 0) {
			for (int i = 0; i < custom_charts.size(); i++) {
				Map<String, String> v = new HashMap<String, String>();
				v.put(Const.MENUNAME, custom_charts.get(i).get(Charts.TITLE).toString());
				v.put(Const.MENUDESC, custom_charts.get(i).get(Charts.TITLE).toString());
				values.add(v);
			}
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
		Intent intent;
		int defaultmenulength = Const.MENU.length;
		if (position < defaultmenulength) {
			intent = new Intent(this, BioCycle.class);
			intent.putExtra("position", position);
		} else {
			intent = new Intent(this, CustomCycle.class);
			intent.putExtra("Title", custom_charts.get(position - defaultmenulength).get(Charts.TITLE).toString());
			intent.putExtra("yAxis", custom_charts.get(position - defaultmenulength).get(Charts.YAXIS).toString());
		}
		
		startActivity(intent);
		
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Const.MENU_CHART_INSERT, 0, R.string.menu_insert)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        
//        menu.add(0, Const.MENU_SAVED_BDAY, 0, R.string.menu_saved_bday)
//		        .setShortcut('3', 'a')
//		        .setIcon(android.R.drawable.btn_default);
        
        menu.add(0, Const.MENU_SAVED_BDAY, 0, R.string.menu_saved_bday)
	        .setShortcut('3', 'a')
	        .setIcon(R.drawable.savedbday);

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case Const.MENU_CHART_INSERT:
        	createNewChartPopup();
            return true;
        case Const.MENU_SAVED_BDAY:
        	viewSavedBirthday();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private String[] items = {"Red", "Green", "Blue"};
    private AlertDialog alert;
    private void viewSavedBirthday() {
    	
    	items  = dbHelper.getAllBirthday();
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("View Saved Birthday");
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int index) {
    	        Toast.makeText(getApplicationContext(), items[index].toString(), Toast.LENGTH_SHORT).show();
    	        List<ContentValues> birthdayDataSet = dbHelper.getBirthday(items[index].toString());
    	        int birthday = Integer.parseInt(birthdayDataSet.get(0).get(Birthday.BIRTHDAY).toString());
    	        //String name = birthdayDataSet.get(0).get(Birthday.NAME).toString();
    	        alert.dismiss();
    	        Intent intent = new Intent(getBaseContext(), BioCycle.class);
    	        intent.putExtra(Birthday.BIRTHDAY, birthday);
    	        startActivity(intent);
    	    }
    	});
    	alert = builder.create();
    	alert.show();
    }
    
    private PopupWindow newchartPopup;
    private void createNewChartPopup() {
    	//Context mContext = getApplicationContext();
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
    	final View layout = inflater.inflate(R.layout.new_chart, (ViewGroup) findViewById(R.id.new_chart_layout));
    	
    	newchartPopup = new PopupWindow(layout,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, true);
    	
    	
    	newchartPopup.setBackgroundDrawable(new BitmapDrawable()); //essential to catch KeyEvents happen outside popup window
    	layout.setOnKeyListener(new View.OnKeyListener() {
			
			//@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			    	newchartPopup.dismiss();
			        //Log.d(this.getClass().getName(), "back button pressed");
			    }
			    //return super.onKeyDown(keyCode, event);
				return false;
			}
		});
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

    private String titleToDelete;
	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
             if (info.position < Const.MENU.length) return;
        } catch (ClassCastException e) {
            Log.e(Const.TAG, "bad menuInfo", e);
            return;
        }
        // Setup the menu header
        titleToDelete = values.get(info.position).get(Const.MENUNAME);
    	menu.setHeaderTitle(titleToDelete);
        //else menu.setHeaderTitle();
        // Add a menu item to delete the note
        menu.add(0, Const.MENU_CHART_DELETE, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Const.MENU_CHART_DELETE: {
                dbHelper.deleteCustomChart(titleToDelete);
                startActivity(new Intent(this, this.getClass()));
                return true;
            }
        }
        return false;
    }
}