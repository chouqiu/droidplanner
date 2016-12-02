package com.test.Crazepony;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.droidplanner.android.NovaPlannerApp;
import org.droidplanner.android.utils.prefs.DroidPlannerPrefs;
import org.droidplanner.android.vlcutil.VLCInstance;
import org.droidplanner.core.model.Drone;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;

//import com.test.BTClient.BTClient;
//Runnable接口方法创建线程，匿名类
@SuppressLint("NewApi")
public class MyVideoSurfaceView extends SurfaceView  implements Callback, IVideoPlayer {

    private final static String TAG = MyVideoSurfaceView.class.getSimpleName();

	private SurfaceHolder sfh;

    // vlc
    private LibVLC mMediaPlayer;
    private View mLoadingView;

    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;
    private String videoIp;

    private final DroidPlannerPrefs prefs;

	public MyVideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        try {
            mMediaPlayer = VLCInstance.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
        }

        sfh = this.getHolder();
        sfh.setFormat(PixelFormat.RGBX_8888);
        //setZOrderOnTop(true);
        //sfh.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        sfh.addCallback(this);

        mMediaPlayer.eventVideoPlayerActivityCreated(true);

        EventHandler em = EventHandler.getInstance();
        em.addHandler(mVlcHandler);

        //this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.setKeepScreenOn(true);
        //mMediaPlayer.playMRL("http://live.3gv.ifeng.com/zixun.m3u8")

        prefs = new DroidPlannerPrefs(context);
        videoIp = prefs.prefs.getString("pref_server_ip", "");
    }

	public void surfaceCreated(SurfaceHolder holder) {
        //sfh = holder;

        if (mMediaPlayer != null) {
            //mSurfaceHolder = holder;
            mMediaPlayer.attachSurface(holder.getSurface(), this);
            mMediaPlayer.playMRL("rtsp://"+videoIp+":8554/unicast");
        }
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int WMwidth = this.getWidth(); //wm.getDefaultDisplay().getWidth();
        int WMheight = this.getHeight(); //wm.getDefaultDisplay().getHeight();

        sfh = holder;

        if (width > 0) {
            mVideoHeight = WMheight;
            mVideoWidth = WMwidth;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.attachSurface(holder.getSurface(), this);
        }
	}

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("Himi", "videosurfaceDestroyed");

        if (mMediaPlayer != null) {
            mMediaPlayer.detachSurface();

            mMediaPlayer.eventVideoPlayerActivityCreated(false);
            EventHandler em = EventHandler.getInstance();
            em.removeHandler(mVlcHandler);
        }
	}

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        mHandler.removeMessages(HANDLER_SURFACE_SIZE);
        mHandler.sendEmptyMessage(HANDLER_SURFACE_SIZE);
    }

    private static final int HANDLER_BUFFER_START = 1;
    private static final int HANDLER_BUFFER_END = 2;
    private static final int HANDLER_SURFACE_SIZE = 3;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_FILL; //SURFACE_BEST_FIT;

    private Handler mVlcHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.getData() == null)
                return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerTimeChanged:
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    break;
                case EventHandler.MediaPlayerPlaying:
                    mHandler.removeMessages(HANDLER_BUFFER_END);
                    mHandler.sendEmptyMessage(HANDLER_BUFFER_END);
                    break;
                case EventHandler.MediaPlayerBuffering:
                    break;
                case EventHandler.MediaPlayerLengthChanged:
                    break;
                case EventHandler.MediaPlayerEndReached:
                    //播放完成
                    break;
            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_BUFFER_START:
                    //showLoading();
                    break;
                case HANDLER_BUFFER_END:
                    //hideLoading();
                    break;
                case HANDLER_SURFACE_SIZE:
                    changeSurfaceSize();
                    break;
            }
        }
    };

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    private void changeSurfaceSize() {
        // get screen size
        int dw = this.getWidth(); //getWindowManager().getDefaultDisplay().getWidth();
        int dh = this.getHeight(); //getWindowManager().getDefaultDisplay().getHeight();

        // calculate aspect ratio
        double ar = (double) mVideoWidth / (double) mVideoHeight;
        // calculate display aspect ratio
        double dar = (double) dw / (double) dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = (int) (dw / ar);
                break;
            case SURFACE_FIT_VERTICAL:
                dw = (int) (dh * ar);
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = (int) (dw / ar);
                else
                    dw = (int) (dh * ar);
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoHeight;
                dw = mVideoWidth;
                break;
        }

        sfh.setFixedSize(mVideoWidth, mVideoHeight);
        ViewGroup.LayoutParams lp = this.getLayoutParams();
        lp.width = dw;
        lp.height = dh;
        this.setLayoutParams(lp);
        this.invalidate();
    }
}