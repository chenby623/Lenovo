package com.example.live555;


import java.io.FileDescriptor;
import android.util.Log;

public class Stream implements Runnable {
	public static String strIpAddr = "10.5.11.158";
	public static String strRemoteName = "test4.sdp";
	
	private FileDescriptor fAudiosender;
	private FileDescriptor fVideosender;
	
    static {
        try {
            System.loadLibrary("live555");
        }
        catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }
	
	public Stream(FileDescriptor Videofd, FileDescriptor Audiofd) {
		Log.e("Streamer Activity", "new a stream class222...");
		fVideosender = Videofd;
		fAudiosender = Audiofd;
	}
	
	public void run() {
		Log.e("Streamer Activity", "new a stream class333...");
		stream(strIpAddr, strRemoteName, fVideosender, fAudiosender);
		Log.e("Streamer Activity", "new a stream class444...");
	}
	
	private native void stream(String strIpAddr, String strRemoteName, FileDescriptor VideoIn, FileDescriptor AudioIn);
}
