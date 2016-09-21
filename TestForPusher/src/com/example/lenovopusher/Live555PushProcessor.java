package com.example.lenovopusher;

import java.io.IOException;
import java.io.OutputStream;
import com.example.frmwk.FrameProcessor;
import com.example.frmwk.Size;
import com.example.frmwk.TimestampedFrame;
import com.example.live555.Stream;

import android.util.Log;

public class Live555PushProcessor extends FrameProcessor{

	private OutputStream VideoStreamWriter;
	private OutputStream AudioStreamWriter;
	
	private Live555StreamLoop streamVideo;
	private Live555StreamLoop streamAudio;

	
	public Live555PushProcessor() {
		
	}
	
	public void InitForServStartUp() {
		Thread thread = new Thread(new Stream(streamVideo.getReceiverFileDescriptor(), 
									streamAudio.getReceiverFileDescriptor()));
		
    	thread.setPriority(Thread.MAX_PRIORITY);
    	thread.start();
	}

    @Override
    protected void onInit(final Size size) {
    	streamVideo = new Live555StreamLoop("Live555Video");
    	streamVideo.InitLoop();
    	streamAudio = new Live555StreamLoop("Live555Audio");
    	streamAudio.InitLoop();
		try {
			VideoStreamWriter = streamVideo.getOutputStream();
			AudioStreamWriter = streamAudio.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void onStart() {
    	
    }
    
    @Override
    protected void onStop() {
    	streamVideo.ReleaseLoop();
    	streamAudio.ReleaseLoop();
    }
    

	@Override
	protected void onProcessFrame(TimestampedFrame frame) {
		// TODO Auto-generated method stub
		if (FrameProcessor.bHoldProcess) {
            return;
        }
		
		byte[] input = null;
		if (PersonalActivity.H264Queue.size() >0){
			input = PersonalActivity.H264Queue.poll();
			Log.e("Pusher", "enter data to queue1......");
		}
		if (input != null) {
			try {
				Log.e("Pusher", "enter data to queue2......");
				if (VideoStreamWriter != null) {
					VideoStreamWriter.write(input);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
}