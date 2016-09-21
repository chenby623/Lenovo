package com.example.lenovopusher.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovopusher.R;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class DialogManager {
	private Dialog mDialog;

    private ImageView mIcon;
    private ImageView mVoice;

    private TextView mLable;

    private Context mContext;

    public DialogManager(Context context){
        mContext=context;
    }

    public void showRecordingDialog(){
        mDialog=new Dialog(mContext, R.style.Theme_AudioDialog);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.dialog_recorder,null);
        mDialog.setContentView(view);
        mIcon= (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_icon);
        mVoice=(ImageView)mDialog.findViewById(R.id.id_recorder_dialog_voice);
        mLable=(TextView)mDialog.findViewById(R.id.id_recorder_dialog_label);

        mDialog.show();
    }

    public void recording(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText(R.string.str_slipe_to_cancel);
        }
    }

    public void wantToCancel(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText(R.string.str_recorder_want_cancel);
        }
    }

    public void tooShort(){
        if(mDialog!=null&&mDialog.isShowing()){
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_too_short);
            mLable.setText(R.string.str_voice_too_short);
        }
    }

    public void dimissDialog(){
        if(mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog=null;
        }
    }

    /**
     * 通过lecel去更新voice上的图片
     * @param level 1-7
     * */
    public void updateVoiceLevel(int level){
        if(mDialog!=null&&mDialog.isShowing()){
            //更新状态的时候无需改变控件的显示状态，有的话就显示，没有的话就算了
            //mIcon.setVisibility(View.VISIBLE);
            //mVoice.setVisibility(View.VISIBLE);
            //mLable.setVisibility(View.VISIBLE);

            int resId=mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());//通过方法名找资源
            mVoice.setImageResource(resId);
        }
    }
}
