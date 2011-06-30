package com.moupress.app.dailycycle;

import java.util.Calendar;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
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
	
	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xtitle, String ytitle, double[] dBoundary) {
		renderer.setChartTitle(title);
		
		renderer.setXTitle(xtitle);
		renderer.setYTitle(ytitle);
		
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
		renderer.setZoomEnabled(false, false);
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
	

	public AbstractChart() {
		cal_bday =  Calendar.getInstance();
		cal_chk =  Calendar.getInstance();
	}
}
