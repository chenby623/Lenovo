package com.example.frmwk;

import java.util.Vector;

import com.example.frmwk.Size;


public abstract class FrameProcessor {
	static public boolean bHoldProcess = true;
	
    private boolean initialized;

    private boolean firstFrame;

    protected long getTimeBetweenFramesMillis() {
        // Greedy by default; always process available frames.
        return 0;
    }

    protected final synchronized void init(final Size size) {
        firstFrame = true;
        initialized = true;

        onInit(size);
    }

    protected final boolean isInitialized() {
        return initialized;
    }

    protected final synchronized void shutdown() {
        initialized = false;

        onShutdown();
    }

    protected final synchronized void start() {
        onStart();
    }

    protected final synchronized void stop() {
        onStop();
    }

    protected final synchronized void processFrame(final TimestampedFrame frame) {
        firstFrame = false;

        onProcessFrame(frame);
    }

    protected void onInit(final Size size) {
    }

    protected void onShutdown() {
    }


    protected void onStart() {
    }


    protected void onStop() {
    }


    protected abstract void onProcessFrame(final TimestampedFrame frame);

    protected String getName() {
        return this.getClass().getSimpleName();
    }

    protected Vector<String> getDebugText() {
        return new Vector<String>();
    }

    protected final void preprocessFrame(TimestampedFrame frame) {
        onPreprocess(frame);
    }

    protected void onPreprocess(TimestampedFrame frame) {
    }
}
