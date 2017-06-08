package com.rhombus.cosmos;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * TODO: document your custom view class.
 */
public class BluetoothPositionView extends View {

    private ShapeDrawable myPos;
    private ShapeDrawable beacon1;
    private ShapeDrawable beacon2;
    private ShapeDrawable beacon3;
    private ShapeDrawable borderBlack, borderWhite;
    public int x = 10;
    public int y = 10;
    public int width;
    public int height;

    public int b1x, b1y, b2x, b2y, b3x, b3y;


    public BluetoothPositionView(Context context) {
        super(context);
        init(context);
    }
    public BluetoothPositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public BluetoothPositionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void onDraw(Canvas canvas) {

        final int borderSize = 20;
        final int ovalSize = 10;

        borderBlack = new ShapeDrawable(new RectShape());
        borderBlack.getPaint().setColor(0xff000000);
        borderBlack.setBounds(0, 0, width, height);
        borderBlack.draw(canvas);
        borderWhite = new ShapeDrawable(new RectShape());
        borderWhite.getPaint().setColor(0xffffffff);
        borderWhite.setBounds(borderSize, borderSize, width-borderSize, height-borderSize);
        borderWhite.draw(canvas);

        myPos = new ShapeDrawable(new OvalShape());
        myPos.getPaint().setColor(0xff00ff00);
        myPos.setBounds(width/2 + x, height/2 + y, width/2 + x + ovalSize, height/2 + y + ovalSize);
        myPos.draw(canvas);
        beacon1 = new ShapeDrawable(new OvalShape());
        beacon1.getPaint().setColor(0xffff0000);
        beacon1.setBounds(width/2 + b1x, height/2 + b1y, width/2 + b1x + ovalSize, height/2 + b1y + ovalSize);
        beacon1.draw(canvas);
        beacon2 = new ShapeDrawable(new OvalShape());
        beacon2.getPaint().setColor(0xffff0000);
        beacon2.setBounds(width/2 + b2x, height/2 + b2y, width/2 + b2x + ovalSize, height/2 + b2y + ovalSize);
        beacon2.draw(canvas);
        beacon3 = new ShapeDrawable(new OvalShape());
        beacon3.getPaint().setColor(0xffff0000);
        beacon3.setBounds(width/2 + b3x, height/2 + b3y, width/2 + b3x + ovalSize, height/2 + b3y + ovalSize);
        beacon3.draw(canvas);
    }

    private void init(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = 500;//Math.min(size.x, size.y);
        height = 500;//Math.min(size.x, size.y);
        Log.i("BPV", width + ", " + height);
    }
}
