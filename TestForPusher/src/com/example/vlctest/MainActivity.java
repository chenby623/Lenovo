package com.example.vlctest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.vlc.Util;
import org.videolan.vlc.WeakHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovopusher.R;
import com.example.lenovopusher.adapter.LiveContentAdapter;
import com.example.lenovopusher.fragment.Fragment2;
import com.example.lenovopusher.fragment.Fragment3;
import com.example.lenovopusher.fragment.PublicChatFragment;

public class MainActivity extends FragmentActivity implements IVideoPlayer, SurfaceHolder.Callback,View.OnClickListener,ViewPager.OnPageChangeListener
{
	public final static String TAG = "VLC/VideoPlayerActivity";
    private static final int VIDEO_LOADING = 3001;

    private LibVLC mLibVLC;

    private int mVideoHeight;
    private int mVideoWidth;

    private boolean mEndReached = false;

    private final Handler eventHandler = new VideoPlayerEventHandler(this);

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private long aa = 0;

    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;



    //3个TextView
    private TextView tab_textview_1;
    private TextView tab_textview_2;
    private TextView tab_textview_3;

    //中间内容区域
    private ViewPager viewPager;

    //ViewPager适配器LiveContentAdapter
    private LiveContentAdapter adapter;

    private List<Fragment> fragmentList;

    private List<View> views;

//    private String sLocation = "http://live.3gv.ifeng.com/live/zixun.m3u8";// http://121.199.27.14:8050/real/01_422149596-1-0.m3u8";//
                                                                           // "http://live.3gv.ifeng.com/live/zixun.m3u8";

//    private String sLocation = "rtsp://192.168.1.100:554/test.sdp";
    private String sLocation = "rtsp://10.5.11.158:554/111.sdp";


    private static class VideoPlayerEventHandler extends WeakHandler<MainActivity>
    {
        public VideoPlayerEventHandler(MainActivity owner)
        {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MainActivity activity = getOwner();
            if (activity == null)
                return;

            switch (msg.getData().getInt("event"))
            {
            case EventHandler.MediaPlayerPlaying:
                activity.mEndReached = false;
                break;
            case EventHandler.MediaPlayerPaused:
                break;
            case EventHandler.MediaPlayerStopped:
                break;
            case EventHandler.MediaPlayerEndReached:
                activity.endReached();
                break;
            case EventHandler.MediaPlayerVout:
                activity.handleVout(msg);
                break;
            case EventHandler.MediaPlayerPositionChanged:
                // don't spam the logs
                // PublicUtils.writeLogFile("vlc play EventHandler.MediaPlayerPositionChanged");
                break;
            case EventHandler.MediaPlayerEncounteredError:
                activity.encounteredError();
                break;
            case EventHandler.HardwareAccelerationError:
                // activity.handleHardwareAccelerationError();
                break;
            default:

                break;
            }
        }
    };

    private void encounteredError()
    {
        /* Encountered Error, exit player with a message */
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        finish();
                    }
                }).setTitle(R.string.encountered_error_title).setMessage(R.string.encountered_error_message).create();
        dialog.show();
    }

    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.others_live_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        Intent intent=getIntent();
        String friendName=intent.getStringExtra("friendName");
        sLocation="rtsp://10.5.11.158:554/"+friendName+".sdp";//根据传过来的friendName打开直播间
        surfaceView = (SurfaceView) findViewById(R.id.sv_play_view);
        surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.setFixedSize(400, 400);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        surfaceHolder.addCallback(this);
        try
        {
            mLibVLC = Util.getLibVlcInstance();
        }
        catch (LibVlcException e)
        {
            Log.d(TAG, "LibVLC initialisation failed");
            return;
        }

        mEndReached = false;

        EventHandler em = EventHandler.getInstance();
        em.addHandler(eventHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        btnPause = (Button) findViewById(R.id.btn_pause);
        btnPlay = (Button) findViewById(R.id.btn_play);
        btnStop = (Button) findViewById(R.id.btn_stop);
        btnPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pause();
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                play();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                stop();
            }
        });
    }

    //其实这里初始化的是与openfire有关的Views
    private void initViews(){
        //初始化控件
        tab_textview_1= (TextView) findViewById(R.id.tab_tv_1);
        tab_textview_2= (TextView) findViewById(R.id.tab_tv_2);
        tab_textview_3= (TextView) findViewById(R.id.tab_tv_3);

        //初始化viewpager按钮事件，设置按钮监听
        tab_textview_1.setOnClickListener(this);
        tab_textview_2.setOnClickListener(this);
        tab_textview_3.setOnClickListener(this);

        //初始化fragment和adapter
        fragmentList=new ArrayList<Fragment>();

//        PublicChatFragment fragment1=new PublicChatFragment();
//        Fragment2 fragment2=new Fragment2();
//        Fragment3 fragment3=new Fragment3();
//
//        fragmentList.add(fragment1);
//        fragmentList.add(fragment2);
//        fragmentList.add(fragment3);
//
//        adapter=new LiveContentAdapter(getSupportFragmentManager(),fragmentList);
//
//        //设置viewPager滑动监听和adapter
//        viewPager=(ViewPager)findViewById(R.id.live_viewpager);
//        viewPager.setAdapter(adapter);
//        viewPager.setOnPageChangeListener(this);
//        viewPager.setCurrentItem(0);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tab_tv_1: {
                Toast.makeText(getApplicationContext(),"tab_tv_1被点击了",Toast.LENGTH_SHORT).show();;
                viewPager.setCurrentItem(0);
                break;
            }
            case R.id.tab_tv_2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.tab_tv_3:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Resources resources=(Resources)getBaseContext().getResources();

        //通过颜色选择器选中在colors.xml中自定义的颜色
        ColorStateList colorDarkOrange=(ColorStateList)resources.getColorStateList(R.color.colorDarkOrange);

        //根据选中的不同pager改变tab上字体的颜色
        switch (position){
            case 0:
                tab_textview_1.setTextColor(colorDarkOrange);
                tab_textview_2.setTextColor(Color.BLACK);
                tab_textview_3.setTextColor(Color.BLACK);
                break;
            case 1:
                tab_textview_1.setTextColor(Color.BLACK);
                tab_textview_2.setTextColor(colorDarkOrange);
                tab_textview_3.setTextColor(Color.BLACK);
                break;
            case 2:
                tab_textview_1.setTextColor(Color.BLACK);
                tab_textview_2.setTextColor(Color.BLACK);
                tab_textview_3.setTextColor(colorDarkOrange);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStart()
    {
        load();
        super.onStart();
    }

    private void handleVout(Message msg)
    {
        if (msg.getData().getInt("data") == 0 && !mEndReached)
        {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
            finish();
        }
    }

    private void endReached()
    {
        Log.d(TAG, "Found a video playlist, expanding it");
        mEndReached = true;
        mLibVLC.stop();
        eventHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mLibVLC.playMRL(sLocation);
            }
        }, 1000);

    }

    private void load()
    {

        mLibVLC.playMRL(sLocation);// .readMedia(mLocation,
                                   // false);
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k)
    {
        Log.d(TAG, "surfaceChanged called");
        mLibVLC.attachSurface(surfaceView.getHolder().getSurface(), this);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder)
    {
        mLibVLC.detachSurface();
        Log.d(TAG, "surfaceDestroyed called");

    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surfaceCreated called");
    }

    @Override
    protected void onPause()
    {
        if (mLibVLC != null)
        {
            mLibVLC.pause();
            long nStartTime = Calendar.getInstance().getTimeInMillis();
            mLibVLC.stop();
            long nEndTime = Calendar.getInstance().getTimeInMillis();
            PublicUtils.writeLogFile(aa + " vlc stop time = " + (nEndTime - nStartTime));
            // mLibVLC.destroy();
        }
        super.onPause();

    }

    @Override
    protected void onDestroy()
    {
        // ThreadPool.getInstance().execute(new Runnable()
        // {
        //
        // @Override
        // public void run()
        // {
        // PublicUtils.writeLogFile(aa + " vlc playing = " +
        // mLibVLC.isPlaying());
        // long nStartTime = Calendar.getInstance().getTimeInMillis();
        // mLibVLC.stop();
        // long nEndTime = Calendar.getInstance().getTimeInMillis();
        // PublicUtils.writeLogFile(aa + " vlc stop time = " + (nEndTime -
        // nStartTime));
        // mLibVLC.destroy();
        // }
        // });

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventHandler em = EventHandler.getInstance();
        em.removeHandler(eventHandler);
        super.onDestroy();

    }

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SURFACE_SIZE = 3;
    private static final int FADE_OUT_INFO = 4;
    /**
     * Handle resize of the surface and the overlay
     */
    private final Handler mHandler = new VideoPlayerHandler(this);

    private static class VideoPlayerHandler extends WeakHandler<MainActivity>
    {
        public VideoPlayerHandler(MainActivity owner)
        {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg)
        {
            MainActivity activity = getOwner();
            if (activity == null) // WeakReference could be GC'ed early
                return;

            switch (msg.what)
            {
            case FADE_OUT:
                break;
            case SHOW_PROGRESS:
                break;
            case SURFACE_SIZE:

                activity.changeSurfaceSize();
                break;
            case FADE_OUT_INFO:
                break;
            }
        }
    };

    private void changeSurfaceSize()
    {
        surfaceView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den)
    {
        if (width * height == 0)
            return;

        // store video size
        mVideoHeight = height;
        mVideoWidth = width;
        Message msg = mHandler.obtainMessage(SURFACE_SIZE);
        mHandler.sendMessage(msg);
    }

    private void pause()
    {
        if (mLibVLC.isPlaying())
        {
            mLibVLC.pause();
        }
    }

    private void play()
    {
        if (!mLibVLC.isPlaying())
        {
            mLibVLC.play();
        }
    }

    private void stop()
    {
        long nStartTime = Calendar.getInstance().getTimeInMillis();
        mLibVLC.stop();
        long nEndTime = Calendar.getInstance().getTimeInMillis();
        Toast.makeText(this, "stop time = " + (nEndTime - nStartTime), Toast.LENGTH_SHORT).show();
    }
}
