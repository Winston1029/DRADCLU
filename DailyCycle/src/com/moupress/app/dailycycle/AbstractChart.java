package com.moupress.app.dailycycle;

import java.util.Calendar;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class AbstractChart {
	public XYMultipleSeriesRenderer chartRenderer;
	String xtitle;
	String ytitle;
	public Calendar cal_bday, cal_chk;
	protected FrameLayout layout;
	protected int xCoorChartPress[] = new int[2];
	protected int yCoorChartPress[] = new int[2];
	// timing of the press
	protected long tCoorChartPress[] = new long[2];
	
	private AbstractCycle activity;
	
	public AbstractChart(AbstractCycle activity) {
		cal_bday =  Calendar.getInstance();
		cal_chk =  Calendar.getInstance();
		this.activity = activity;
	}
	
	public void setFrameLayout(FrameLayout findViewById) {
		this.layout = findViewById;
	}
	
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, double[] dBoundary) {
		renderer.setChartTitle(title);
		
		int iXMin = 0;
		int iXMax = 1;
		int iYMin = 2;
		int iYMax = 3;
		renderer.setXAxisMin(dBoundary[iXMin]);
		renderer.setXAxisMax(dBoundary[iXMax]);
		renderer.setYAxisMin(dBoundary[iYMin]);
		renderer.setYAxisMax(dBoundary[iYMax]);
		
		
		renderer.setAxesColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanLimits(Const.PANLIMIT);
		//renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		//disable zoom
		renderer.setZoomLimits(null);
		renderer.setZoomEnabled(true, false);
	}
	
	protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(true);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
	
	protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues,
		List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i]);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
	
	protected boolean isMultiTouch = false;
	private float oldDist = 0;
	protected void redrawChart(Context context, String[] titles, List<double[]> x, List<double[]> values) {
		
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, values); 
        GraphicalView mView = new GraphicalView(context, (XYChart)new LineChart(dataset, chartRenderer));
        
        mView.setOnTouchListener(new View.OnTouchListener() {
			//@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction() & MotionEvent.ACTION_MASK;
				switch(action) {
//				    case MotionEvent.ACTION_POINTER_DOWN:
//				        oldDist = spacing(event);
//				        if (oldDist > 10f) {
//				        	isMultiTouch = true;
//				        }
//				        break;
//				    case MotionEvent.ACTION_POINTER_UP:
//				        isMultiTouch = false;
//				        scaleChart(v, event);
//				        break;
//				    case MotionEvent.ACTION_MOVE:
//				    	if(isMultiTouch) {
//				    		//scaleChart(v, event);
//				        }
//				    	break;
				    case MotionEvent.ACTION_DOWN:
				    case MotionEvent.ACTION_UP:
				    	moveChart(v, event);
				    	break;
				}
				return false;
			}
		});

        layout.removeAllViews();
        layout.addView(mView);
        
    }
	
	protected void moveChart(View v, MotionEvent event) {
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
			double xstart = Math.ceil(chartRenderer.getXAxisMin());
			double widthPerLabel = (v.getWidth() - chartRenderer.getMargins()[0] - chartRenderer.getMargins()[2])/chartRenderer.getXLabels();
			int datePressed = (int) Math.round((xCoorChartPress[1] + xCoorChartPress[0] - chartRenderer.getMargins()[0] * 2) / 2.0 / widthPerLabel
												+ xstart - 1);
//			Toast.makeText(activity.getBaseContext(), "xstart: "+xstart 
//					+ " datePressed: "+ datePressed 
//					+ " #lables: "+chartRenderer.getXLabels()
//					+ " Coor0: "+xCoorChartPress[0]
//                    + " Coor1: "+xCoorChartPress[1] 
//                    + " Width: "+v.getWidth()
//                    + " width per cell:" + widthPerLabel, 
//                    Toast.LENGTH_LONG).show();
			activity.drawPopup(datePressed);	
		}
	}

	protected void scaleChart(View v, MotionEvent e) {
		float newDist = spacing(e);
		if (newDist > 10f) {
			double[] orgPanLimit = chartRenderer.getPanLimits();
			
			double scaling = Math.max(Math.min( oldDist / newDist / 10.0, 5), 0.5) ;
			orgPanLimit[1] = Math.max( orgPanLimit[0] + (orgPanLimit[1] - orgPanLimit[0]) * scaling, orgPanLimit[0] + 7);
			chartRenderer.setPanLimits(orgPanLimit);
		}
		
	}
	
	private float spacing(MotionEvent event) {
		   float x = event.getX(0) - event.getX(1);
		   float y = event.getY(0) - event.getY(1);
		   return FloatMath.sqrt(x * x + y * y);
		}
	
}
