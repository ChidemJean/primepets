package com.monacoprime.primepets.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SwipeDetectLinearLayout extends LinearLayout {

    private float oldY = 0;
    private onSwipeEventDetected mSwipeDetectedListener;
    private boolean toolbarOptionsOpened;
    private boolean toolbarOptionsUp;

    public SwipeDetectLinearLayout(Context context) {
        super(context);
    }

    public SwipeDetectLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDetectLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                oldY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float scrollY = ev.getY();
                Log.i("Main", "open: " + toolbarOptionsOpened);
                if (scrollY < oldY) {
                    Log.i("Main", "SUBINDO");
                    toolbarOptionsUp = true;
                    float deltaY = Math.abs(scrollY - oldY);
                    oldY = scrollY;
                    if (toolbarOptionsOpened || toolbarOptionsUp) {
                        if(mSwipeDetectedListener!=null) {
                            mSwipeDetectedListener.swipeEventDetected(true, (int) Math.abs(deltaY));
                        }
                        return super.onInterceptTouchEvent(ev);
                    } else {
                        return false;
                    }
                } else if (scrollY > oldY) {
                    Log.i("Main", "DESCENDO");
                    toolbarOptionsUp = false;
                    float deltaY = Math.abs(oldY - scrollY);
                    oldY = scrollY;
                    if (!toolbarOptionsOpened || !toolbarOptionsUp) {
                        if(mSwipeDetectedListener!=null) {
                            mSwipeDetectedListener.swipeEventDetected(false, (int) Math.abs(deltaY));
                        }
                        return super.onInterceptTouchEvent(ev);
                    } else {
                        return false;
                    }
                }

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface onSwipeEventDetected
    {
        public void swipeEventDetected(boolean hideToolbarOptions, int y);
    }

    public void registerToSwipeEvents(onSwipeEventDetected listener)
    {
        this.mSwipeDetectedListener=listener;
    }

    public boolean isToolbarOptionsOpened() {
        return toolbarOptionsOpened;
    }

    public void setToolbarOptionsOpened(boolean toolbarOptionsOpened) {
        this.toolbarOptionsOpened = toolbarOptionsOpened;
    }
}
