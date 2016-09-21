package com.example.lenovopusher.manager;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.util.UUID;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class AudioManager {
	 private MediaRecorder mMediaRecorder;
	    private String mDir;//文件夹名称
	    private String mCurrentFilePath;

	    private static AudioManager mInstance;

	    private boolean isPrepared;

	    private AudioManager(String dir){
	        mDir=dir;
	    }

	    /**
	     * 回调准备完毕
	     * */
	    public interface AudioStateListener{
	        void wellPrepared();
	    }

	    public AudioStateListener mListener;

	    public void setOnAudioStateListener(AudioStateListener listener){
	        mListener=listener;
	    }

	    public static AudioManager getInstance(String dir){//单例模式
	        if(mInstance==null){
	            synchronized (AudioManager.class){
	                if(mInstance==null){
	                    mInstance=new AudioManager(dir);
	                }
	            }
	        }
	        return mInstance;
	    }


	    public void prepareAudio(){
	        try {
	            isPrepared=false;//还原一下
	            File dir = new File(mDir);
	            if (!dir.exists())
	                dir.mkdirs();

	            String fileName = generateFileName();
	            File file = new File(dir, fileName);

	            mCurrentFilePath=file.getAbsolutePath();

	            mMediaRecorder = new MediaRecorder();
	            //设置输出文件
	            mMediaRecorder.setOutputFile(file.getAbsolutePath());
	            //设置子MediaRecorder的音频源为麦克风
	            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	            //设置音频格式
	            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
	            //设置音频编码为amr
	            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	            mMediaRecorder.prepare();
	            mMediaRecorder.start();

	            isPrepared=true;//准备结束

	            if(mListener!=null)
	                mListener.wellPrepared();
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	    }

	    public String getCurrentFilePath(){
	        Log.e("===文件地址是===",mCurrentFilePath);
	        return mCurrentFilePath;
	    }


	    /**
	     * 随机生成文件的名称
	     * */
	    private String generateFileName() {
	        return UUID.randomUUID().toString()+".amr";//音频后缀名是.amr
	    }

	    public int getVoiceLevel(int maxLevel){
	        if(isPrepared){
	            try {
	                //获得一个最大的振幅（1~32767）,乘上maxLevel之后，就可以获得0~maxLevel-1的值
	                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
	            }catch (Exception e){
	                //不用做什么都可以，因为就算获取不到音量大小，只需要录音就可正常工作
	            }
	        }
	        return 1;
	    }

	    public void release(){
	        if(mMediaRecorder!=null) {
	            mMediaRecorder.stop();
	            mMediaRecorder.release();
	            mMediaRecorder = null;
	        }
	    }

	    public void cancel(){//取消

	        release();
	        if(mCurrentFilePath!=null) {
	            //File file = new File(mCurrentFilePath);
	            //file.delete();//删除这个文件
	            mCurrentFilePath=null;
	        }
	    }
}
