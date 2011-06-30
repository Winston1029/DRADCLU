package com.moupress.app.dailycycle;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class CustomChart extends AbstractChart {
	
	public CustomChart(String chartTitle, String ytitle, AbstractCycle activity) {
		super(activity);
		chartRenderer = buildRenderer(Const.COLORS, Const.STYLES);
    	//init chart
		chartRenderer.setXTitle("Month");
		chartRenderer.setYTitle(ytitle);
        setChartSettings(chartRenderer, chartTitle, Const.BOUNDARY);
	}
	
	void updateChart(Context context, String[] valueTitles) {
		
		List<double[]> x = new ArrayList<double[]>();
		List<double[]> values = new ArrayList<double[]>();
		double[] dDate = {1,2,3,4,5};
		x.add(dDate);
		values.add(dDate);
		redrawChart(context, valueTitles, x, values);
	}
	
}
