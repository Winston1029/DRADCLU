package com.moupress.app.dailycycle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PinchDetector {
	private Finger initialLeftFinger;
    private Finger initialRightFinger;
    private OnPinchListener pinchListener;
    private static final float minimumDistanceForEachFinger = 30;
    private static final float minimumDistanceBetweenFingers = 50;
	
	public PinchDetector(OnPinchListener listner){
		this.pinchListener = listner;
	}

	protected void scaleChart(View v, MotionEvent e) {
		int firstIndex = e.getX(0) < e.getX(1) ? 0: 1;
        int secondIndex = e.getX(0) < e.getX(1) ? 1: 0;

        Finger currentLeftFinger = new Finger(e.getX(firstIndex), e.getY(firstIndex));
        Finger currentRightFinger = new Finger(e.getX(secondIndex), e.getY(secondIndex));

        float yDifference = Math.abs(currentLeftFinger.y - currentRightFinger.y);
        if(yDifference > 80) {
            Log.d(Const.TAG, "not pinching - fingers too vertically-oriented");
            clearPinch();
            return;
        }

        if(initialLeftFinger == null) {
            initialLeftFinger = currentLeftFinger;
            initialRightFinger = currentRightFinger;
            Log.d(Const.TAG, "not pinching, but might be starting a pinch...");
            return;
        }

        float leftFingerDistance = initialLeftFinger.x - currentLeftFinger.x;
        float rightFingerDistance = currentRightFinger.x - initialRightFinger.x;

        float xDistanceBetweenFingers = Math.abs(currentLeftFinger.x - currentRightFinger.x);
        if(xDistanceBetweenFingers < minimumDistanceBetweenFingers) {
            Log.d(Const.TAG, "pinching, but fingers are not far enough apart...");
            return;
        }

        if(leftFingerDistance < minimumDistanceForEachFinger) {
            Log.d(Const.TAG, "pinching, but left finger has not moved enough...");
            return;
        }
        if(rightFingerDistance < minimumDistanceForEachFinger) {
            Log.d(Const.TAG, "pinching, but right finger has not moved enough...");
            return;
        }

        pinchCompleted();
		
	}

	private static class Finger {

        public Finger(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float x;
        public float y;
    }
    
    public static interface OnPinchListener {
        void onPinch();
    }
    
    private void pinchCompleted() {
        Log.d(Const.TAG, "pinch completed");
        if(pinchListener != null) pinchListener.onPinch();
        clearPinch();
    }

    private void clearPinch() {
    	initialLeftFinger = null;
        initialRightFinger = null;
    }
}
