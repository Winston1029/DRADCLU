package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class CustomChart extends AbstractChart {
	
	private Activity activity;
	private FrameLayout layout;
	
	public CustomChart(String chartTitle, String ytitle, Activity activity) {
		super();
		chartRenderer = buildRenderer(Const.COLORS, Const.STYLES);
    	//init chart
		xtitle = new String("Month");
		this.ytitle = ytitle;
        setChartSettings(chartRenderer, chartTitle, xtitle, ytitle, Const.BOUNDARY);
    	this.activity = activity;
	}
	
	void updateChart(Context context, String[] valueTitles) {
		
		List<double[]> x = new ArrayList<double[]>();
		List<double[]> values = new ArrayList<double[]>();
		double[] dDate = {1,2,3,4,5};
		x.add(dDate);
		values.add(dDate);
		redrawCustomChart(context, valueTitles, x, values);
	}
	
	private void redrawCustomChart(Context context, String[] titles, List<double[]> x, List<double[]> values) {
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
					((CustomCycle)activity).drawPopupInput(datePressed);
					
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
