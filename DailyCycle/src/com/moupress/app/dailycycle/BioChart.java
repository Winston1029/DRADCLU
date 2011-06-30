/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.Context;

/**
 * An abstract class for the demo charts to extend.
 */
public class BioChart extends AbstractChart{
	
	public BioChart(String chartTitle, AbstractCycle activity) {
		super(activity);
		chartRenderer = buildRenderer(Const.COLORS, Const.STYLES);
    	//init chart
		chartRenderer.setYTitle("Month");
		chartRenderer.setYTitle("Energy");
        setChartSettings(chartRenderer, chartTitle, Const.BOUNDARY);
	}
	
	void updateChart(Context context, int[] cycle, String[] valueTitles) {
    	// clear previous chart
    	List<double[]> x = new ArrayList<double[]>();
        List<double[]> values = new ArrayList<double[]>();
        
        int idaysToDraw = cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH); 
        if (weekly_view) {
        	chartRenderer.setXTitle("Week");
        	chartRenderer.setXAxisMin(weekly_view_start + 0.5);
        	chartRenderer.setXAxisMax(weekly_view_start + 7.5);
        } else {
        	weekly_view_start = 0;
        	chartRenderer.setXAxisMin(0.5);
        	chartRenderer.setXAxisMax(12.5);
        }
        double[] dDate = new double[idaysToDraw];
        double[] dEnergy;
        
        Calendar cal_tmp = Calendar.getInstance();
        for (int i = 0; i < valueTitles.length; i++) {
        	dEnergy = new double[idaysToDraw];
        	for (int j = 1; j <= idaysToDraw; j++) {
        		dDate[j - 1] = j;
        		cal_tmp.set(cal_chk.get(Calendar.YEAR), cal_chk.get(Calendar.MONTH), (int)dDate[j - 1]);
        		
        		long idiff_days 	= (cal_tmp.getTimeInMillis() - cal_bday.getTimeInMillis()) / Const.DAYS_IN_MILLSECOND;
        		int irest		= (int) (idiff_days % cycle[i]);
        		dEnergy[j - 1] = Math.sin((double)irest/cycle[i] * 2 * Math.PI) * 15 + 20.0;
        	}
        	x.add(dDate);
        	values.add(dEnergy);
        }
        redrawChart(context, valueTitles, x, values);
    }
	
	public int weekly_view_start = 0;
	public boolean weekly_view = false;
//	void updateChart(Context context, int[] cycle, String[] valueTitles, boolean weekly) {
//		List<double[]> x = new ArrayList<double[]>();
//        List<double[]> values = new ArrayList<double[]>();
//        int idays_in_week = 7;
//        double[] dDate = new double[idays_in_week];
//        double[] dEnergy;
//        
//        Calendar cal_tmp = Calendar.getInstance();
//        for (int i = 0; i < valueTitles.length; i++) {
//        	dEnergy = new double[idays_in_week];
//        	for (int j = 1; j <= idays_in_week; j++) {
//        		dDate[j - 1] = j;
//        		cal_tmp.set(cal_chk.get(Calendar.YEAR), cal_chk.get(Calendar.MONTH), (int)dDate[j - 1]);
//        		
//        		long idiff_days 	= (cal_tmp.getTimeInMillis() - cal_bday.getTimeInMillis()) / Const.DAYS_IN_MILLSECOND;
//        		int irest		= (int) (idiff_days % cycle[i]);
//        		dEnergy[j - 1] = Math.sin((double)irest/cycle[i] * 2 * Math.PI) * 15 + 20.0;
//        	}
//        	x.add(dDate);
//        	values.add(dEnergy);
//        }
//        redrawChart(context, valueTitles, x, values);
//	}

}
