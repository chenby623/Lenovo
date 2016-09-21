package com.example.lenovopusher.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovopusher.model.PublicChatBean;
import com.example.lenovopusher.R;

import java.util.ArrayList;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class PublicChatAdapter extends BaseAdapter {
    public static final int TYPE_COUNT=4;//子布局的总数目

    public static final int ORDINARY_CHAT=0;//普通弹幕
    public static final int VIP_CHAT=1;//会员弹幕
    public static final int SEND_GIFTS_CHAT=2;//送礼物弹幕
    public static final int RECORDER_CHAT=3;//语音弹幕

    private int mMinItemWidth;
    private int mMaxItemWidth;

    private ArrayList<PublicChatBean> mData;
    private LayoutInflater mInflater;

    public PublicChatAdapter(Context context, ArrayList<PublicChatBean> data){
        this.mData=data;
        mInflater=LayoutInflater.from(context);

        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth=(int)(outMetrics.widthPixels*0.7f);
        mMinItemWidth=(int)(outMetrics.widthPixels*0.16f);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {//返回布局类型的总数目
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {//返回子项对应的布局类型
        PublicChatBean bean=mData.get(position);//取出bean
        int type=bean.getType();//从bean中获取到该子项对应的消息类型
        if(type==PublicChatBean.ORDINARY_CHAT){//普通弹幕
            return ORDINARY_CHAT;
        }else if(type==PublicChatBean.VIP_CHAT){//会员弹幕
            return VIP_CHAT;
        }else if(type==PublicChatBean.SEND_GIFTS_CHAT){//送礼弹幕
            return SEND_GIFTS_CHAT;
        }else{//语音弹幕
            return RECORDER_CHAT;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //因为有4种类型的消息，所以就有4种类型的viewHolder
        OrdinaryChatViewHolder ordViewHolder;
        VipChatViewHolder vipViewHolder;
        SendGiftsChatViewHolder giftsViewHolder;
        RecorderChatViewHolder recorderViewHolder;

        //首先看是什么类型的消息
        int itemViewType=getItemViewType(position);
        if(itemViewType==ORDINARY_CHAT){//普通弹幕
            if(convertView==null){//convertView为空
                ordViewHolder=new OrdinaryChatViewHolder();
                convertView=mInflater.inflate(R.layout.ordinary_chat_item,null);
                ordViewHolder.name=(TextView)convertView.findViewById(R.id.name_textview);
                ordViewHolder.content=(TextView)convertView.findViewById(R.id.content_textview);
                convertView.setTag(ordViewHolder);
            }else{
                ordViewHolder= (OrdinaryChatViewHolder) convertView.getTag();
            }

            ordViewHolder.name.setText(mData.get(position).getUser_name());
            ordViewHolder.content.setText(mData.get(position).getChat_content());
        }else if(itemViewType==VIP_CHAT){//会员弹幕
            if(convertView==null){
                vipViewHolder=new VipChatViewHolder();
                convertView=mInflater.inflate(R.layout.vip_chat_item,null);
                vipViewHolder.vip_head=(ImageView)convertView.findViewById(R.id.vip_icon);
                vipViewHolder.name=(TextView)convertView.findViewById(R.id.name_textview);
                vipViewHolder.content=(TextView)convertView.findViewById(R.id.content_textview);
                convertView.setTag(vipViewHolder);
            }else{
                vipViewHolder=(VipChatViewHolder)convertView.getTag();
            }
            vipViewHolder.name.setText(mData.get(position).getUser_name());
            vipViewHolder.content.setText(mData.get(position).getChat_content());
            vipViewHolder.vip_head.setImageResource(R.drawable.ic_launcher);
        }else if(itemViewType==SEND_GIFTS_CHAT){//送礼物弹幕
            if(convertView==null){
                giftsViewHolder=new SendGiftsChatViewHolder();
                convertView=mInflater.inflate(R.layout.gifts_chat_item,null);
                giftsViewHolder.name=(TextView)convertView.findViewById(R.id.name_textview);
                giftsViewHolder.number=(TextView)convertView.findViewById(R.id.gift_num);
                convertView.setTag(giftsViewHolder);
            }else{
                giftsViewHolder=(SendGiftsChatViewHolder)convertView.getTag();
            }
            giftsViewHolder.name.setText(mData.get(position).getUser_name());
            giftsViewHolder.number.setText(mData.get(position).getGifts_number()+"");

        }else{
            if(convertView==null){
                recorderViewHolder=new RecorderChatViewHolder();
                convertView=mInflater.inflate(R.layout.item_recorder,null);
                recorderViewHolder.name=(TextView)convertView.findViewById(R.id.name_textview);
                recorderViewHolder.seconds=(TextView)convertView.findViewById(R.id.id_recorder_time);
                recorderViewHolder.length=convertView.findViewById(R.id.id_recorder_length);

                convertView.setTag(recorderViewHolder);
            }else {
                recorderViewHolder=(RecorderChatViewHolder)convertView.getTag();
            }
            recorderViewHolder.name.setText(mData.get(position).getUser_name());
            recorderViewHolder.seconds.setText(Math.round(mData.get(position).getTime())+"\"");
            ViewGroup.LayoutParams lp=recorderViewHolder.length.getLayoutParams();
            //到时候需要控制一下录制的最长时间为60s
            lp.width=(int)(mMinItemWidth+(mMaxItemWidth/60f*(mData.get(position).getTime())));
        }

        return convertView;
    }

    public class OrdinaryChatViewHolder{//普通弹幕的viewHolder
        public TextView name;
        public TextView content;
    }

    public class VipChatViewHolder{//会员弹幕的viewHolder
        public ImageView vip_head;//会员的头衔小图标
        public TextView name;
        public TextView content;
    }

    public class SendGiftsChatViewHolder{//送礼弹幕的viewHolder
        public TextView name;//送礼用户的名字
        public ImageView gifts_icon;//礼物的小图标
        public TextView number;//送出礼物的数目
    }

    public class RecorderChatViewHolder{//音频弹幕的viewHolder
        TextView name;
        TextView seconds;
        View length;
    }
}
