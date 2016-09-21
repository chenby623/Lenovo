package com.example.lenovopusher;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import com.example.Encode.EncoderDebugger;
import com.example.Encode.NV21Convertor;
import com.example.Encode.Util;
import com.example.frmwk.FrameProcessor;
import com.example.frmwk.Size;
import com.example.frmwk.TimestampedFrame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

@SuppressLint("NewApi")
public class H264EncoderProcessor extends FrameProcessor {
	String path = Environment.getExternalStorageDirectory() + "/easy.h264";
	
	private int width;
    
	private int height;
    
    private int framerate;
    
    private int bitrate;
    
    private MediaCodec mMediaCodec;

    private NV21Convertor mConvertor;

    boolean started = false;
    
    private Context context;
	
	byte[] h264;
	
	private byte[] mPpsSps = new byte[0];
	
	public H264EncoderProcessor(Context cxt, int wid, int hei) {
		this.context = cxt;
		this.width = wid;
		this.height = hei;
		h264 = new byte[width*height*3/2];
	}
	
	public void ReleaseAvcEncoder() {
	}
	
    private void initMediaCodec() throws IOException {
        int dgree = 0;
        framerate = 15;
        bitrate = 2 * width * height * framerate / 30;
        EncoderDebugger debugger = EncoderDebugger.debug(context, width, height);
        mConvertor = debugger.getNV21Convertor();
        mMediaCodec = MediaCodec.createByCodecName(debugger.getEncoderName());
		MediaFormat mediaFormat;
		if (dgree == 0) {
		    mediaFormat = MediaFormat.createVideoFormat("video/avc", height, width);
		} else {
		    mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
		}
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, debugger.getEncoderColorFormat());
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mMediaCodec.start();
    }

    @Override
    protected void onInit(final Size size) {
    	try {
			initMediaCodec();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void onStop() {
    	if (mMediaCodec != null) {
    		mMediaCodec.stop();
    	}
    }
    
    @Override
    protected void onStart() {
        onInit(null);
    }
	
	@Override
	protected void onProcessFrame(TimestampedFrame frame) {
		// TODO Auto-generated method stub
		if (frame.getRawData() == null || FrameProcessor.bHoldProcess) {
            return;
        }
		ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
        byte[] dst = new byte[frame.getRawData().length];
        
        Log.e("H264 begin", "H264 begin process.. " + frame.getTimestamp());
      
/*         
         if (getDgree() == 0) {
             dst = Util.rotateNV21Degree90(data, previewSize.width, previewSize.height);
         } else {
             dst = frame.getRawData();
         }
*/       
         dst = Util.rotateNV21Degree90(frame.getRawData(), width, height);
 //        dst = frame.getRawData();
         
         try {
             int bufferIndex = mMediaCodec.dequeueInputBuffer(5000000);
             if (bufferIndex >= 0) {
                 inputBuffers[bufferIndex].clear();
                 mConvertor.convert(dst, inputBuffers[bufferIndex]);
                 mMediaCodec.queueInputBuffer(bufferIndex, 0,inputBuffers[bufferIndex].position(), System.nanoTime() / 1000, 0);
                 MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                 int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                 while (outputBufferIndex >= 0) {
                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                    byte[] outData = new byte[bufferInfo.size];
                    outputBuffer.get(outData);
                    if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 103) {
                        mPpsSps = outData;
                    } else if (outData[0] == 0 && outData[1] == 0 && outData[2] == 0 && outData[3] == 1 && outData[4] == 101) {
                        byte[] iframeData = new byte[mPpsSps.length + outData.length];
                        System.arraycopy(mPpsSps, 0, iframeData, 0, mPpsSps.length);
                        System.arraycopy(outData, 0, iframeData, mPpsSps.length, outData.length);
                        outData = iframeData;
                    }
//                     Util.save(outData, 0, outData.length, path, true);
//                    frame.h264 = outData;
                     
                    if (PersonalActivity.H264Queue.size() >= 5) {
                    	PersonalActivity.H264Queue.poll();
             		}
                    PersonalActivity.H264Queue.add(outData);
                    
                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
                 }
             } else {
                 Log.e("Pusher", "No buffer available !");
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
         }
	}
/*	
    private int getDgree() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }
        return degrees;
    }
*/    
}