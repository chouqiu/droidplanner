package com.test.Crazepony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;

import org.droidplanner.android.NovaPlannerApp;
import org.droidplanner.core.model.Drone;

//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.view.ViewGroup;
/*
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.droidplanner.android.vlcutil.VLCInstance;
*/

//import com.test.BTClient.BTClient;
//Runnable接口方法创建线程，匿名类
@SuppressLint("NewApi")
public class TestSurfaceView extends SurfaceView  implements Callback, Runnable {

    private final static String TAG = TestSurfaceView.class.getSimpleName();

	private Thread th;
	private SurfaceHolder sfh;
	private Canvas canvas;
	private Paint paint;
	private boolean flag;
	private static int x = 311;

	public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.i(TAG, "TestSurfaceView");
        sfh = this.getHolder();

        paint = new Paint();
        paint.setAntiAlias(true);
        //setFocusable(true);
        //setFocusableInTouchMode(true);

        //setZOrderOnTop(true);
        //sfh.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        sfh.addCallback(this);
    }

	public void surfaceCreated(SurfaceHolder holder) {
		th = new Thread(this);
		flag = true;
		th.start();
	}

	public void draw() {
		try {
			canvas = sfh.lockCanvas();
            //设置背景颜色
            //canvas.drawColor(Color.BLACK);
            paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            canvas.drawCircle(x++, 210, 60, paint);


        } catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (canvas != null)
					sfh.unlockCanvasAndPost(canvas);
			} catch (Exception e2) {

			}
		}
	}

    //线程的run操作，当surface被创建后，线程开启
	public void run() {
		// TODO Auto-generated method stub
		//
		while (flag) {	
			draw();
			try {
				Thread.sleep(1);
			} catch (Exception ex) {
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.v("Himi", "surfaceChanged");

        //WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int WMwidth = this.getWidth(); //wm.getDefaultDisplay().getWidth();
        int WMheight = this.getHeight(); //wm.getDefaultDisplay().getHeight();

        Log.v("viewSize","height:"+ WMheight + "  Width:"+WMwidth);
		sfh = holder;
		//draw();
	}

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
		Log.v("Himi", "surfaceDestroyed");

        /*
        if (mMediaPlayer != null) {
            mMediaPlayer.detachSurface();
        }
        */
	}
}