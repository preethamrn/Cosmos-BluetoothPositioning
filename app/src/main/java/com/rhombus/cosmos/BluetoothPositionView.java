package com.rhombus.cosmos;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class BluetoothPositionView extends View {

    private ShapeDrawable mDrawable;
    public int x = 10;
    public int y = 10;
    public int width = 300;
    public int height = 50;



    public BluetoothPositionView(Context context) {
        super(context);
    }
    public BluetoothPositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public BluetoothPositionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas) {
        mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(0xff74AC23);
        mDrawable.setBounds(x, y, x + width, y + height);
        mDrawable.draw(canvas);
    }

}
