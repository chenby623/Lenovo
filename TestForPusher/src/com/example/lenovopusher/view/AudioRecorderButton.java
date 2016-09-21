package com.example.lenovopusher.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.lenovopusher.manager.AudioManager;
import com.example.lenovopusher.manager.DialogManager;
import com.example.lenovopusher.R;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{

	private static final int DISTANCE_Y_CANCEL=50;//正常情况下应该定义为dp，再在构造方法里面转成px，在这里偷懒了一下

    private static final int STATE_NORMAL=1;//默认状态
    private static final int STATE_RECORDING=2;
    private static final int STATE_WANT_CANCEL=3;

    private int mCurState=STATE_NORMAL;//默认下是STATE_NORMAL
    private boolean isRecording=false;//是否已经开始录音了

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    private float mTime;
    //是否触发long click，知道后看是否释放资源
    private boolean mReady;

    public AudioRecorderButton(Context context) {
        this(context,null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager=new DialogManager(getContext());

        String dir= Environment.getExternalStorageDirectory()+"/recorder_audios";
        mAudioManager=AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady=true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     * */
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds,String filePath);
    }

    private AudioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener= listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int x=(int)event.getX();
        int y=(int)event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording) {
                    //根据x,y的坐标，判断是否想要取消
                    if (wantToCancel(x, y)){
                        changeState(STATE_WANT_CANCEL);
                    }else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!mReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if(!isRecording||mTime<0.6f){
                    mDialogManager.tooShort();//录音时间太短
                    //isRecording=false;//已经在reset中做了
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS,1300);//通过handler发送延迟，关闭dialog
                }
                if(mCurState==STATE_RECORDING){//正常录制结束
                    mDialogManager.dimissDialog();
                    if(mListener!=null){
                        mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                    mAudioManager.release();
                    //release
                    //callbackToActivity
                }else if(mCurState==STATE_WANT_CANCEL){
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                    //cancel
                }
                reset();//最后需要reset
                break;
        }
        return super.onTouchEvent(event);
    }

    private void changeState(int state) {
        if(mCurState!=state){
            mCurState=state;
            switch (state){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_recording);
                    if(isRecording){
                        //TODO Dialog.recording();
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_want_cancel);
                    //TODO Dialog.wantCancel();
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    private boolean wantToCancel(int x, int y) {
        if(x<0||x>getWidth())//手指已经离开了我们这个按钮
            return true;
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL)
            return true;
        return false;
    }

    /**
     * 恢复状态及标志位
     * */
    private void reset(){
        isRecording=false;
        mReady=false;
        mTime=0;
        changeState(STATE_NORMAL);
    }

    /**
     * 获取音量大小的runnable
     * */
    private Runnable mGetVoiceLevelRunnable=new Runnable() {
        @Override
        public void run() {
            while(isRecording){
                try {
                    Thread.sleep(100);
                    mTime+=0.1f;//mTime计时
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };
    //handler要处理三个事件
    private static final int MSG_AUDIO_PREPARED=0X110;
    private static final int MSG_VOICE_CHANGED=0X111;
    private static final int MSG_DIALOG_DIMISS=0X112;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    //TODO 真正显示应该在audio end prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording=true;

                    //开启一个新的线程，断断续续地获取音量
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }
}

