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
import java.util.Date;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * An abstract class for the demo charts to extend.
 */
public class BioChart extends AbstractChart{
	
	private Activity activity;
	
	public BioChart(String chartTitle, Activity activity) {
		super();
		chartRenderer = buildRenderer(Const.COLORS, Const.STYLES);
    	//init chart
		xtitle = new String("Month");
		ytitle = new String("Energy");
        setChartSettings(chartRenderer, chartTitle, xtitle, ytitle, Const.BOUNDARY);
        
        
    	this.activity = activity;
	}
	
	void updateChart(Context context, int[] cycle, String[] valueTitles) {
    	// clear previous chart
    	List<double[]> x = new ArrayList<double[]>();
        List<double[]> values = new ArrayList<double[]>();
        
        int idays_in_month = cal_chk.getActualMaximum(Calendar.DAY_OF_MONTH);
        double[] dDate = new double[idays_in_month];
        double[] dEnergy;
        
        Calendar cal_tmp = Calendar.getInstance();
        for (int i = 0; i < valueTitles.length; i++) {
        	dEnergy = new double[idays_in_month];
        	for (int j = 1; j <= idays_in_month; j++) {
        		dDate[j - 1] = j;
        		cal_tmp.set(cal_chk.get(Calendar.YEAR), cal_chk.get(Calendar.MONTH), (int)dDate[j - 1]);
        		
        		long idiff_days 	= (cal_tmp.getTimeInMillis() - cal_bday.getTimeInMillis()) / Const.DAYS_IN_MILLSECOND;
        		int irest		= (int) (idiff_days % cycle[i]);
        		dEnergy[j - 1] = Math.sin((double)irest/cycle[i] * 2 * Math.PI) * 15 + 20.0;
        	}
        	x.add(dDate);
        	values.add(dEnergy);
        }
        redrawBioChart(context, valueTitles, x, values);
    }
   
	private void redrawBioChart(Context context, String[] titles, List<double[]> x, List<double[]> values) {
		
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values); 
        GraphicalView mView = new GraphicalView(context, (XYChart)new LineChart(dataset, chartRenderer));
        
        mView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					xCoorChartPress[0] = (int) event.getX();
					yCoorChartPress[0] = (int) event.getY();
					tCoorChartPress[0] = System.currentTimeMillis();
				} 
				if (action == MotionEvent.ACTION_UP) {
					xCoorChartPress[1] = (int) event.getX();
					yCoorChartPress[1] = (int) event.getY();
					tCoorChartPress[1] = System.currentTimeMillis();
				}
				
				//if movement is less than 10, and holding time is longer than 3s, trigger add calendar events
				//long dist = (x[1] - x[0]) * (x[1] - x[0]) + (y[1] - y[0]) * (y[1] - y[0]); 
				long delta = 300;
				if ( (xCoorChartPress[1] - xCoorChartPress[0]) * (xCoorChartPress[1] - xCoorChartPress[0]) 
						+ (yCoorChartPress[1] - yCoorChartPress[0]) * (yCoorChartPress[1] - yCoorChartPress[0]) < delta // down & up distance 
						&& (tCoorChartPress[1] - tCoorChartPress[0]) > 3 /* down & up time */) {
					//Toast.makeText(getBaseContext(), "Create Calendar Events", Toast.LENGTH_SHORT).show();
					double xstart = chartRenderer.getXAxisMin();
					int datePressed = (int) Math.round((xCoorChartPress[1] + xCoorChartPress[0]) / 
														(2.0 * v.getWidth() / chartRenderer.getXLabels())
														+ xstart);
					((EnergyCycle)activity).drawPopupEvent(datePressed);
					
					return true;
				}
				
				// action == move when be handle by AChartEng API
				return false;
			}
		});

        layout.removeAllViews();
        layout.addView(mView);
        
    }


	public void setFrameLayout(FrameLayout findViewById) {
		this.layout = findViewById;
	}

}
