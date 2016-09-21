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
	    private String mDir;//�ļ�������
	    private String mCurrentFilePath;

	    private static AudioManager mInstance;

	    private boolean isPrepared;

	    private AudioManager(String dir){
	        mDir=dir;
	    }

	    /**
	     * �ص�׼�����
	     * */
	    public interface AudioStateListener{
	        void wellPrepared();
	    }

	    public AudioStateListener mListener;

	    public void setOnAudioStateListener(AudioStateListener listener){
	        mListener=listener;
	    }

	    public static AudioManager getInstance(String dir){//����ģʽ
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
	            isPrepared=false;//��ԭһ��
	            File dir = new File(mDir);
	            if (!dir.exists())
	                dir.mkdirs();

	            String fileName = generateFileName();
	            File file = new File(dir, fileName);

	            mCurrentFilePath=file.getAbsolutePath();

	            mMediaRecorder = new MediaRecorder();
	            //��������ļ�
	            mMediaRecorder.setOutputFile(file.getAbsolutePath());
	            //������MediaRecorder����ƵԴΪ��˷�
	            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	            //������Ƶ��ʽ
	            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
	            //������Ƶ����Ϊamr
	            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

	            mMediaRecorder.prepare();
	            mMediaRecorder.start();

	            isPrepared=true;//׼������

	            if(mListener!=null)
	                mListener.wellPrepared();
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	    }

	    public String getCurrentFilePath(){
	        Log.e("===�ļ���ַ��===",mCurrentFilePath);
	        return mCurrentFilePath;
	    }


	    /**
	     * ��������ļ�������
	     * */
	    private String generateFileName() {
	        return UUID.randomUUID().toString()+".amr";//��Ƶ��׺����.amr
	    }

	    public int getVoiceLevel(int maxLevel){
	        if(isPrepared){
	            try {
	                //���һ�����������1~32767��,����maxLevel֮�󣬾Ϳ��Ի��0~maxLevel-1��ֵ
	                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
	            }catch (Exception e){
	                //������ʲô�����ԣ���Ϊ�����ȡ����������С��ֻ��Ҫ¼���Ϳ���������
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

	    public void cancel(){//ȡ��

	        release();
	        if(mCurrentFilePath!=null) {
	            //File file = new File(mCurrentFilePath);
	            //file.delete();//ɾ������ļ�
	            mCurrentFilePath=null;
	        }
	    }
}
