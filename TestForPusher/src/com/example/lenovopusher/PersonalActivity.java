package com.example.lenovopusher;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import com.example.frmwk.CameraActivity;
import com.example.frmwk.CameraManager;
import com.example.frmwk.FrameLooper;
import com.example.frmwk.FrameProcessor;


public class PersonalActivity extends CameraActivity {
	
	private static int START_UP_LIVE555 = 1000;
	
    private FrameLooper mPreviewLooper;
    
    private H264EncoderProcessor H264Encoder;
    
    private Live555PushProcessor Live555Pusher;
    
    public VideoView mVideoView;
    
	private static int queuesize = 5;
	
	public static ArrayBlockingQueue<byte[]> H264Queue = new ArrayBlockingQueue<byte[]>(queuesize);
	
    private static class MyHandler extends Handler {
        private final WeakReference<PersonalActivity> mActivity;
     
        public MyHandler(PersonalActivity activity) {
        	mActivity = new WeakReference<PersonalActivity>(activity);
        }
     
        @Override
        public void handleMessage(Message msg) {
        	PersonalActivity activity = mActivity.get();
        	if (activity != null) {
        		if (msg.what == PersonalActivity.START_UP_LIVE555) {
        			activity.Live555Pusher.InitForServStartUp();
        			FrameProcessor.bHoldProcess = false;
//        			activity.StartVideoView();
        		}
        	}
        }
    }
    
    private final MyHandler mHandler = new MyHandler(this);
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.personal_live_activity);
        Button btn = (Button)findViewById(R.id.take_picture);
        
        btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();
				msg.what = START_UP_LIVE555;
				mHandler.sendMessage(msg);
			}
        	
        });
    }
    
    @Override
    protected void onStop() {
        mPreviewLooper.stopLoop();
        super.onStop();
    }

    @Override  
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    protected void onCameraStarted() {
    	initializeContinuous(); 
    }

    
    private void initializeContinuous() {
    	final int[] delayMillis = { 0, 10, 100, 1000 };
        CameraManager cameraManager = getCameraManager();
        Log.e("PersonalActivity", "enter frameLooper");
        mPreviewLooper = new FrameLooper(cameraManager, delayMillis);

        H264Encoder = new H264EncoderProcessor(getApplicationContext(), getCameraManager().getFrameWidth(), 
        										getCameraManager().getFrameHeight());
        mPreviewLooper.addPreviewProcessor(H264Encoder, 1);
        
        Live555Pusher = new Live555PushProcessor();
        mPreviewLooper.addPreviewProcessor(Live555Pusher, 1);
        
        if (mPreviewLooper.isRunning()) {
            mPreviewLooper.stopLoop();
        }
        mPreviewLooper.initAllProcessors();
        mPreviewLooper.startLoop();     
    }

	@Override
	protected void onCameraStoped() {
		// TODO Auto-generated method stub
		DeinitializeProcessor();
		Log.e("PersonalActivity", "onCameraStoped ......");
	}
	
	private void DeinitializeProcessor() {
		mPreviewLooper.stopLoop();
		FrameProcessor.bHoldProcess = true;
	}

}
