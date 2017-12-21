package com.transsion.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 卢伟斌.
 * @date 2017/12/19.
 * ==================================
 * Copyright (c) 2017 TRANSSION.Co.Ltd.
 * All rights reserved.
 */
public class GestureLockView extends View {

    private float pointRadius;

    private Paint pointPaint;

    private List<Point> allPoint;

    private List<Point> selectPoint;

    private float touchX, touchY;

    private float inRadius;

    private int gridCount = 3;

    private int defaultColor = 0xff333333;

    private int selectColor = 0xff895164;

    private OnGestureListener onGestureListener;
    
    public GestureLockView(Context context) {
        super(context);
        init();
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        width = height = Math.min(width, height);
        pointRadius = ((float) width / (gridCount * 2.0f)) * (2.0f / 3.0f);
        inRadius = pointRadius / 3;
        setMeasuredDimension(width, height);
    
        if (allPoint == null) {
            allPoint = new ArrayList<>();
        } else {
            allPoint.clear();
        }

        float p = (float) width / (gridCount * 2.0f);
        for (int i = 1; i <= gridCount; i++) {
            for (int j = 1; j <= gridCount; j++) {
                allPoint.add(new Point((j * 2 - 1) * p, (i * 2 - 1) * p, (i - 1) * gridCount + j));
            }
        }
    }
    
     @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectPoint != null) {
                    selectPoint.clear();
                }
                addPoint(getPointIndex(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_MOVE:
                addPoint(getPointIndex(event.getX(), event.getY()));
                break;
            case MotionEvent.ACTION_UP:
                if (selectPoint != null && selectPoint.size() > 0) {
                    Point lastPoint = selectPoint.get(selectPoint.size() - 1);
                    touchX = lastPoint.pointX;
                    touchY = lastPoint.pointY;
                    invalidate();
                }
    
                if (onGestureListener != null && selectPoint != null) {
                    if (selectPoint.size() > 3) {
                        int[] result = new int[selectPoint.size()];
                        for (int i = 0; i < selectPoint.size(); i++) {
                            result[i] = selectPoint.get(i).index;
                        }
                        onGestureListener.onGestureAccept(result);
                    } else {
                        onGestureListener.onGestureNotAccept();
                    }
                }
                break;    
            default:
                break;
        }
        return true;
    } 
    
   private void addPoint(int index) {
        if (selectPoint == null) {
            selectPoint = new ArrayList<>();
        }
        if (index != 0) {
            if (!selectPoint.contains(allPoint.get(index - 1))) {
                selectPoint.add(allPoint.get(index - 1));
            }
        }
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawNinePoint(canvas);
        drawLineBetweenPoint(canvas);
    }    
    
     private void drawNinePoint(Canvas canvas) {
        pointPaint.setStyle(Paint.Style.STROKE);
        if (allPoint != null) {
            for (Point point : allPoint) {
                if (selectPoint != null && selectPoint.contains(point)) {
                    pointPaint.setColor(selectColor);
                } else {
                    pointPaint.setColor(defaultColor);
                }  
                pointPaint.setStrokeWidth(2);
                canvas.drawCircle(point.pointX, point.pointY, pointRadius, pointPaint);
                pointPaint.setStrokeWidth(5);
                canvas.drawCircle(point.pointX, point.pointY, pointRadius / 3, pointPaint);
            }
        }
    }    
    
    private void drawLineBetweenPoint(Canvas canvas) {
        pointPaint.setColor(selectColor);
        pointPaint.setStrokeWidth(inRadius);
        if (selectPoint != null && selectPoint.size() > 1) {
            for (int i = 1; i < selectPoint.size(); i++) {
                Point startPoint = selectPoint.get(i - 1);
                Point endPoint = selectPoint.get(i);   
                canvas.drawLine(startPoint.pointX, startPoint.pointY, endPoint.pointX, endPoint.pointY, pointPaint);
            }
        }

        if (selectPoint != null && selectPoint.size() > 0) {
            Point lastPoint = selectPoint.get(selectPoint.size() - 1);
            canvas.drawLine(lastPoint.pointX, lastPoint.pointY, touchX, touchY, pointPaint);
            pointPaint.setStyle(Paint.Style.FILL);
            for (Point point : selectPoint) {
                canvas.drawCircle(point.pointX, point.pointY, inRadius, pointPaint);
            }
            canvas.drawCircle(touchX, touchY, inRadius / 2, pointPaint);
        }
    }
    private static class Point {
        float pointX;
        float pointY;
        int index;

        public Point(float pointX, float pointY, int index) {
            this.pointX = pointX;
            this.pointY = pointY;
            this.index = index;
        }
    }
    
   private int getPointIndex(float x, float y) {
        int px = 0, py = 0;
        float p = (float) getMeasuredWidth() / (gridCount * 2.0f);

        for (int i = 1; i <= gridCount; i++) {
            if (x > (i - 1) * 2 * p + p * (1.0f / 2.0f) && x < (i - 1) * 2 * p + 2 * p * (3.0f / 4.0f)) {
                px = i;
                if (py != 0) {
                    break;
                }
            }    
            if (y > (i - 1) * 2 * p + p * (1.0f / 2.0f) && y < (i - 1) * 2 * p + 2 * p * (3.0f / 4.0f)) {
                py = i;
                if (px != 0) {
                    break;
                }
            }
        }

        if (px != 0 && py != 0) {
            return px + (py - 1) * gridCount;
        } else {
            return 0;
        }
    }    
    
    /**
     * 设置画出团过后的回调
     * @param onGestureListener
     */
    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.onGestureListener = onGestureListener;
    }    
    
      /**
     * 设置选择点的颜色和默认颜色
     * @param selectColor
     * @param defaultColor
     */
    public void setColor(int selectColor, int defaultColor) {
        this.selectColor = selectColor;
        this.defaultColor = defaultColor;
        if (this.isAttachedToWindow()) {
            requestLayout();
            invalidate();
        }
    }  
    
    /**
     * 设置方格的列数，eg：3 --> 3x3  5--> 5x5
     * @param gridCount
     */
    public void setGridCount(int gridCount) {
        this.gridCount = gridCount;
        if (this.isAttachedToWindow()) {
            requestLayout();
            invalidate();
        }
    }
    
   public interface OnGestureListener {
        /**
         * 图案大于3个节点的时候回调
         * @param gestureCode
         */
        public void onGestureAccept(int[] gestureCode);

        /**
         * 图案不大于3个节点的时候回调
         */
        public void onGestureNotAccept();
    }
}
