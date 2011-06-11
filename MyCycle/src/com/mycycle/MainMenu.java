package com.mycycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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
        
        setListAdapter(new SimpleAdapter(this, values, android.R.layout.simple_list_item_2,
            new String[] { Const.MENUNAME, Const.MENUDESC }, new int[] { android.R.id.text1, android.R.id.text2 }));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(this, EnergyCycle.class);
		intent.putExtra("position", position);
		startActivity(intent);
    }
}