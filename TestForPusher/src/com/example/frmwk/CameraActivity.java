package com.example.frmwk;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.io.IOException;

import com.example.lenovopusher.R;


public abstract class CameraActivity extends Activity {
    /** Used for controlling the camera. */
    private CameraManager mCameraManager;

    /** Whether the preview is ready for drawing. */
    private boolean mHasSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mCameraManager = new CameraManager(this);
        mHasSurface = false;
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels / 2)); 

        if (mHasSurface) {
            initializeCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(surfaceHolderCallback);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        synchronized (mCameraManager) {
            mHasSurface = false;
            mCameraManager.closeDriver();
        }

        super.onPause();
    }

    /**
     * @return the camera manager
     */
    protected CameraManager getCameraManager() {
        return mCameraManager;
    }

    /**
     * Initializes the camera by opening the surface holder, starting preview,
     * and starting focusing.
     *
     * @param surfaceHolder
     */
    private void initializeCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager.openDriver(surfaceHolder);
            mCameraManager.startPreview();
            onCameraStarted();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void DeinitializeCamera() {
    	mCameraManager.stopPreview();
    	mCameraManager.closeDriver();
    	onCameraStoped();
    }

    private final SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!mHasSurface) {
                mHasSurface = true;
                initializeCamera(holder);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mHasSurface = false;
            DeinitializeCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Do nothing
        }
    };

    protected abstract void onCameraStarted();
    protected abstract void onCameraStoped();
}
