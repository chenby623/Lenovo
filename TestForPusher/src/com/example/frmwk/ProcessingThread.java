package com.example.frmwk;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Vector;


public class ProcessingThread extends Thread {
    private static final int STOP_CODE = -1;

    private final Vector<FrameProcessor> previewProcessors;

    private Handler processingHandler;

    private volatile boolean isProcessing;


    private final String name;

    private final FrameLooper previewLooper;

    private final int delay;

    private final int priority;

    public ProcessingThread(
            final FrameLooper previewLooper, final int level, final int delay, final int priority) {
        this.previewLooper = previewLooper;
        this.delay = delay;
        this.priority = priority;

        this.isProcessing = false;
  
        this.previewProcessors = new Vector<FrameProcessor>();
        this.name = "FrameProcessingThread" + level;

        setName(name);
    }

    protected void kill() {
        processingHandler.sendEmptyMessage(STOP_CODE);
        processingHandler = null;
    }

    protected void sendFrame(final TimestampedFrame previewFrame) {
        final Message message = new Message();
        message.obj = previewFrame;
        // TODO(mrcasey): Figure out when this is null... it doesn't seem like
        // it should be.
        if (processingHandler != null) {
            processingHandler.sendMessage(message);
        }
    }

    protected boolean isReady() {
        return !isProcessing;
    }

    protected void preprocess(final TimestampedFrame previewFrame) {
        previewFrame.threadStart();

        isProcessing = true;
        for (final FrameProcessor processor : previewProcessors) {
            synchronized (processor) {
                processor.preprocessFrame(previewFrame);
            }
        }
    }

    /**
     * Processes the frame in a the processing thread.
     */
    protected void processFrame(final TimestampedFrame frame) {

        final Vector<FrameProcessor> processors = getProcessors();

        // This is where the frame handling happens.
        for (final FrameProcessor processor : processors) {
            synchronized (processor) {
                if (!processor.isInitialized()) {
                    // Initialize any processors that have been added lately.
                    processor.init(frame.getSize());
                }

                processor.processFrame(frame);
            }
        }

        frame.threadDone();
        previewLooper.doneProcessing(frame);
        isProcessing = false;
    }

    @Override
    public void run() {
        setPriority(priority);
        Looper.prepare();
        processingHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                // Stop the loop if stopLoop has been called (and cleared
                // processingHandler).
                if (processingHandler == null) {
                    final Vector<FrameProcessor> processors = getProcessors();
                    for (final FrameProcessor processor : processors) {
                        synchronized (processor) {
                            processor.shutdown();
                        }
                    }
                    Looper.myLooper().quit();
                    return;
                }

                processFrame((TimestampedFrame) msg.obj);
            }
        };
        Looper.loop();
    }

    public void addProcessor(final FrameProcessor handler) {
        previewProcessors.add(handler);
    }

    public Vector<FrameProcessor> getProcessors() {
        return new Vector<FrameProcessor>(previewProcessors);
    }
}
