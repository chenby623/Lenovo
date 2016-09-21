package com.example.lenovopusher.utils;

import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class Base64Operator {
	 static String TAG="====Base64Operator.java===";
	    public static String getAudioBinary(String path){
	        String amrString=null;
//	        String path= Environment.getExternalStorageDirectory().getAbsolutePath();
//	        path+="/test.amr";
	        File file=new File(path);
	        if(!file.exists()){
	            Log.e(TAG,"û�ҵ����ļ�");
	            return null;
	        }
	        byte[] data=null;
	        try {
	            InputStream inputStream=new FileInputStream(file);
	            data=new byte[inputStream.available()];
	            inputStream.read(data);
	            inputStream.close();

	            amrString=new String(Base64.encodeToString(data,Base64.DEFAULT));
	            //Log.e(TAG+"ת������ַ�Ϊ===",amrString);
	            return amrString;
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	        return amrString;
	    }


	    public static void getAudio(String str,String path){
	        try{
	            byte[] data=Base64.decode(str,Base64.DEFAULT);
	            OutputStream outputStream=new FileOutputStream(path);
	            outputStream.write(data);
	            outputStream.flush();
	            outputStream.close();

	            Log.e(TAG,"��Ƶ���ճɹ�");
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	    }
}


