/**
 * CameraManager
 */
package com.example.frmwk;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


public class CameraManager implements FrameProducer {
    private static final String TAG = "CameraManager";
    
    private static int PREVIEW_SZ_WIDTH = 640;
    private static int PREVIEW_SZ_HEIGHT = 480;

    private static final int PREVIEW_FORMAT = ImageFormat.NV21;

    private static enum CameraState {
        NOT_READY, IDLE, FOCUSING, FOCUSED
    }

    private Camera mCamera;

    private final WeakReference<Context> mContext;

    private boolean mInitialized;

    private boolean mPreviewing;

    private CameraState mState;

    private boolean mSupportsFocus;

    public int mOrientation;

    private LinkedList<FrameReceiver> mFrameRequesters;

    private LinkedList<byte[]> mPreviewBuffers;

    private Size mPreviewSize;

    private int mPreviewFormat;

    private int mPreviewBufferSize;
    
    
    static public float mScale;

    public CameraManager(Context context) {
        mContext = new WeakReference<Context>(context);
        mCamera = null;
        mState = CameraState.NOT_READY;

        mFrameRequesters = new LinkedList<FrameReceiver>();
        mPreviewBuffers = new LinkedList<byte[]>();     
    }


    public void openDriver(SurfaceHolder holder) throws IOException {
        if (mCamera != null) {
            return;
        }
        mCamera = Camera.open();
        mCamera.setPreviewDisplay(holder);

        if (!mInitialized) {
            mInitialized = true;
        }
        setPreviewParameters();
        mState = CameraState.IDLE;
    }

    public void closeDriver() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mPreviewing = false;
            mState = CameraState.NOT_READY;
        }
        mFrameRequesters.clear();
    }

    public void startPreview() {
        if (mCamera != null && !mPreviewing) {
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
            mPreviewing = true;
        }
    }

    public void stopPreview() {
        if (mCamera != null && mPreviewing) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mPreviewing = false;
        }
    }

    public boolean isFocusSupported() {
        return mSupportsFocus;
    }

    @Override
    public int getFrameWidth() {
        return mPreviewSize.width;
    }

    @Override
    public int getFrameHeight() {
        return mPreviewSize.height;
    }

    @Override
    public void requestFrame(FrameReceiver callback) {
        if (mCamera == null) {
            return;
        }

        if (callback == null) {
            synchronized (mPreviewBuffers) {
                mPreviewBuffers.clear();
                // mCamera.setPreviewCallback(null);
            }
            return;
        }

        synchronized (this) {
            mFrameRequesters.addLast(callback);
        }

        byte[] buffer = null;

        synchronized (mPreviewBuffers) {
            if (mPreviewBuffers.isEmpty()) {
                buffer = new byte[mPreviewBufferSize];
            } else {
                buffer = mPreviewBuffers.remove();
            }
        }
        mCamera.addCallbackBuffer(buffer);
    }

    public void releaseData(byte[] buffer) {
        synchronized (mPreviewBuffers) {
            mPreviewBuffers.add(buffer);
        }
    }

    
    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			if(success){  
				setPreviewParameters();//实现相机的参数初始化  
				camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。  
			}  
		}
	};
    
    private final Camera.PreviewCallback frameCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {

            FrameReceiver callback;

            synchronized (this) {
                if (mFrameRequesters.isEmpty()) {
                    Log.e(TAG, "Preview callback was null, discarding results");
                    return;
                } else {
                    callback = mFrameRequesters.removeFirst();
                }
            }

            long timestamp = SystemClock.currentThreadTimeMillis();
            
            Frame frame = new Frame(data, PREVIEW_SZ_WIDTH, PREVIEW_SZ_HEIGHT, PREVIEW_FORMAT, timestamp) {
                @Override
                public void recycle() {
                    synchronized (mPreviewBuffers) {
                        mPreviewBuffers.add(data);
                    }
                }
            };
            
            callback.onFrameReceived(frame);
        }
    };

    private void setPreviewParameters() {
    	mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
/*       
        List<Size> supported = parameters.getSupportedPreviewSizes();  
        Size selectedSize = mCamera.new Size(0, 0);
        for (Size size : supported) {
            if (size.width * size.height > selectedSize.width * selectedSize.height && size.height <= 640) {
                selectedSize = size;
            }
        }
        Log.e("PreviewParameter", "size width " + selectedSize.width + "height " + selectedSize.height);

        PREVIEW_SZ_WIDTH = selectedSize.width;
        PREVIEW_SZ_HEIGHT = selectedSize.height;        
        parameters.setPreviewSize(selectedSize.width, selectedSize.height);
*/        
        Size selectedSize = mCamera.new Size(PREVIEW_SZ_WIDTH, PREVIEW_SZ_HEIGHT);
        parameters.setPreviewSize(selectedSize.width, selectedSize.height);
        parameters.setPreviewFrameRate(20);
        parameters.setPreviewFormat(PREVIEW_FORMAT);
        
        int bitsPerPixel = ImageFormat.getBitsPerPixel(PREVIEW_FORMAT);
        mPreviewBufferSize = selectedSize.width * selectedSize.height * bitsPerPixel / 8;
        mPreviewSize = selectedSize;
        
//        parameters.setFlashMode(parameters.FLASH_MODE_TORCH);     
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);	//连续对焦。

        mCamera.setPreviewCallbackWithBuffer(frameCallback);
        mCamera.setParameters(parameters);
    }
}
