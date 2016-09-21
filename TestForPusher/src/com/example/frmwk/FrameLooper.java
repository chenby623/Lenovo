package com.example.frmwk;


import android.util.Log;
import java.util.ArrayList;
import com.example.frmwk.Size;
import com.example.frmwk.FrameProducer.Frame;
import com.example.frmwk.FrameProducer.FrameReceiver;

public class FrameLooper implements FrameReceiver {
    private static final String TAG = "FrameLooper";

    private final FrameProducer frameProducer;

    // The thread that does the actual frame processing. Also acts as a flag for
    // whether the processing loop is active or not.
    private final ProcessingThread processingThreads[];

    // The number of preview frames that have been received from the camera.
    private int numPreviewFrames;

    private boolean firstRun;

    private boolean running;

    private Size size;

    ArrayList<FrameProcessor> allPreviewProcessors;

    public FrameLooper(
            final FrameProducer cameraManager, final int[] delays) {
        this.frameProducer = cameraManager;
   

        this.firstRun = true;
        this.running = false;

 //       int width = frameProducer.getFrameWidth();
 //       int height = frameProducer.getFrameHeight();

        this.processingThreads = new ProcessingThread[delays.length];

        for (int level = 0; level < delays.length; ++level) {
            final int priority = Thread.MIN_PRIORITY + delays.length - (level + 1);
            processingThreads[level] = new ProcessingThread(this, level, delays[level], priority);
        }
    }

    public void addPreviewProcessor(final FrameProcessor handler, final int level) {
        synchronized (processingThreads[level]) {
            processingThreads[level].addProcessor(handler);
            allPreviewProcessors = null;
        }
    }

    /**
     * @return A copy of the current processor list that is safe to modify.
     */
    public ArrayList<FrameProcessor> getAllProcessors() {
        if (allPreviewProcessors == null) {
            allPreviewProcessors = new ArrayList<FrameProcessor>();

            for (final ProcessingThread thread : processingThreads) {
                allPreviewProcessors.addAll(thread.getProcessors());
            }
        }
        return allPreviewProcessors;
    }

    final ArrayList<ProcessingThread> readyThreads = new ArrayList<ProcessingThread>();

    @Override
    public synchronized void onFrameReceived(final Frame frame) {
        ++numPreviewFrames;

        // Create a new TimestampedFrame to wrap the raw byte[].
        final TimestampedFrame previewFrame = new TimestampedFrame(frame);

        // TODO(alanv): Why does this run on the main thread?!
        processingThreads[0].preprocess(previewFrame);

        // readyThreads is a list of all threads that are done processing the
        // last
        // iteration.
        readyThreads.clear();
        for (int i = 1; i < processingThreads.length; ++i) {
            final ProcessingThread thread = processingThreads[i];
            if (thread.isReady()) {
                readyThreads.add(thread);
                thread.preprocess(previewFrame);
            }
        }

        // This is synchronized so that stopLoop is guaranteed to be called (if
        // desired) before preview frame processing finishes.
        processingThreads[0].processFrame(previewFrame);
        if (running) {
            frameProducer.requestFrame(this);
        }

        for (final ProcessingThread thread : readyThreads) {
            thread.sendFrame(previewFrame);
        }
    }

    public synchronized void doneProcessing(final TimestampedFrame frame) {
        if (frame.allThreadsDone()) {
//            frame.clearRawData();
        }
    }


    public synchronized void startLoop() {
        running = true;

        numPreviewFrames = 0;

        Log.d(TAG, "Starting frame loop.");

        if (firstRun) {
            for (int i = 1; i < processingThreads.length; ++i) {
                processingThreads[i].start();
            }
        }
        firstRun = false;

        startAllProcessors();

        frameProducer.requestFrame(this);
    }

    public synchronized void stopLoop() {
        if (running == false) {
            Log.w(TAG, "Stopping a FrameLooper that was already stoppped.");
            return;
        }

        // Reset the state of preview requests.
        frameProducer.requestFrame(null);

        stopAllProcessors();
        running = false;
    }

    public void initAllProcessors() {
        int width = frameProducer.getFrameWidth();
        int height = frameProducer.getFrameHeight();
        size = new Size(width, height);

        for (final FrameProcessor processor : getAllProcessors()) {
            processor.init(size);
        }
    }

    private void startAllProcessors() {
        for (final FrameProcessor processor : getAllProcessors()) {
            processor.start();
        }
    }

    private void stopAllProcessors() {
        for (final FrameProcessor processor : getAllProcessors()) {
            processor.stop();
        }
    }
    

    public boolean isRunning() {
        return running;
    }

}
