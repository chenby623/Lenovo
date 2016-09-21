package com.example.frmwk;

import com.example.frmwk.Size;
import com.example.frmwk.FrameProducer.Frame;

import android.util.Log;


public class TimestampedFrame {
    private static final String TAG = "TimestampedFrame";

    private int threadsLeft;

    private final Frame originalFrame;

    // TODO(andrewharp): Create pool of TimestampedFrames so that used frames
    // can be recycled.
    protected TimestampedFrame(final Frame originalFrame) {
        this.originalFrame = originalFrame;
    }

    public long getTimestamp() {
        return originalFrame.timestamp;
    }

    public Size getSize() {
        return new Size(originalFrame.width, originalFrame.height);
    }

    public int getWidth() {
        return originalFrame.width;
    }

    public int getHeight() {
        return originalFrame.height;
    }

    /**
     * @return Whether or not rawFrameData is null.
     */
    protected synchronized boolean hasRawData() {
        return originalFrame.data != null;
    }


    public synchronized byte[] getRawData() {
        if (!hasRawData()) {
            Log.e(TAG, "Frame data for frame is no longer available.");
        }
        return originalFrame.data;
    }


    public void threadDone() {
        --threadsLeft;
        if (threadsLeft < 0) {
            Log.w(TAG, "Negative number of threads remaining.");
        }
    }

    public boolean allThreadsDone() {
        return threadsLeft == 0;
    }

    public void threadStart() {
        ++threadsLeft;
    }
}
