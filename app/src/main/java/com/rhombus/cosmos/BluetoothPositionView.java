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
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class BluetoothPositionView extends View {

    private ShapeDrawable myPos;
    private ShapeDrawable beacon1;
    private ShapeDrawable beacon2;
    private ShapeDrawable beacon3;
    public int x = 10;
    public int y = 10;
    public int width = 500;
    public int height = 500;

    public int b1x, b1y, b2x, b2y, b3x, b3y;


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
        Log.i("BPV pos", ""+x+y+b1x+b1y+b2x+b2y+b3x+b3y);
        myPos = new ShapeDrawable(new OvalShape());
        myPos.getPaint().setColor(0xff74AC23);
        myPos.setBounds(width/2 + x, height/2 + y, width/2 + x + 10, height/2 + y + 10);
        myPos.draw(canvas);
        beacon1 = new ShapeDrawable(new OvalShape());
        beacon1.getPaint().setColor(0xff000023);
        beacon1.setBounds(width/2 + b1x, height/2 + b1y, width/2 + b1x + 10, height/2 + b1y + 10);
        beacon1.draw(canvas);
        beacon2 = new ShapeDrawable(new OvalShape());
        beacon2.getPaint().setColor(0xff000023);
        beacon2.setBounds(width/2 + b2x, height/2 + b2y, width/2 + b2x + 10, height/2 + b2y + 10);
        beacon2.draw(canvas);
        beacon3 = new ShapeDrawable(new OvalShape());
        beacon3.getPaint().setColor(0xff000023);
        beacon3.setBounds(width/2 + b3x, height/2 + b3y, width/2 + b3x + 10, height/2 + b3y + 10);
        beacon3.draw(canvas);
    }
}
