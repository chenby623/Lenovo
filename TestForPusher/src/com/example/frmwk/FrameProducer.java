package com.example.frmwk;


public interface FrameProducer {
    public int getFrameWidth();

    public int getFrameHeight();

    public void requestFrame(FrameReceiver listener);

    public interface FrameReceiver {
        public void onFrameReceived(Frame frame);
    }

    public static class Frame {
        public final byte[] data;

        public final int width;

        public final int height;

        public final int format;

        public final long timestamp;

        public Frame(byte[] data,int width, int height, int type, long timestamp) {
        	this.data = data;
        	this.width = width;
        	this.height = height;
            this.format = type;
            this.timestamp = timestamp;
        }

        public void recycle() {
            // Does nothing in this implementation.
        }
    }
}
