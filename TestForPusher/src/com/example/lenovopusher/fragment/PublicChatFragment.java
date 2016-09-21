package com.example.lenovopusher.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.lenovopusher.adapter.PublicChatAdapter;
import com.example.lenovopusher.manager.MediaManager;
import com.example.lenovopusher.model.PublicChatBean;
import com.example.lenovopusher.server.MultiUserChatServer;
import com.example.lenovopusher.utils.Base64Operator;
import com.example.lenovopusher.utils.Config;
import com.example.lenovopusher.utils.MyConnection;
import com.example.lenovopusher.utils.UserInformation;
import com.example.lenovopusher.view.AudioRecorderButton;
import com.example.lenovopusher.R;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class PublicChatFragment extends Fragment implements View.OnClickListener {

    private String jid;
    private String roomName;
    private ArrayList<PublicChatBean> mData;
    private static PublicChatAdapter adapter;
    private ListView mListView;
    private EditText editText;
    private Button btnSend;
    private ImageView btnSendGifts;
    private ImageView btnSendMessage;
    private ImageView btnSendRecorder;

    private LinearLayout sendGiftsLayout;
    private LinearLayout bottomInputLayout;
    private LinearLayout sendRecorderLayout;
    private MyUIBroadcastReceiver myUIBroadcastReceiver;

    private AudioRecorderButton mAudioRecorderButton;
    private View mAnimView;

    MultiUserChatServer multiUserChatServer;
    MultiUserChat multiUserChat;



    private LinearLayout huojian;
    private LinearLayout feiji;
    private LinearLayout zan;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //将PublicChatBean从msg中取出来
            PublicChatBean newChat=(PublicChatBean)msg.getData().getSerializable("newChat");
            mData.add(newChat);
            adapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomName="111";
        roomName= Config.roomName;

        MyConnection.getInstance().createChatRoom(roomName, UserInformation.getUsername(),"");
        multiUserChat=MyConnection.getInstance().joinChatRoom(roomName,UserInformation.getUsername(),"");

        Intent intent=new Intent(getActivity().getApplicationContext(),MultiUserChatServer.class);
        intent.putExtra("roomName",roomName);
        getActivity().getApplicationContext().bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.public_chat_fragment,null,false);

        //初始化控件
        editText=(EditText)view.findViewById(R.id.message_editText);
        btnSend=(Button)view.findViewById(R.id.btn_send_message);
        mListView=(ListView)view.findViewById(R.id.public_chat_listview);
        btnSendGifts=(ImageView)view.findViewById(R.id.send_gift_icon);
        btnSendMessage=(ImageView)view.findViewById(R.id.send_message_icon);
        btnSendRecorder=(ImageView)view.findViewById(R.id.send_recorder_icon);
        sendGiftsLayout=(LinearLayout)view.findViewById(R.id.send_gifts_layout);
        bottomInputLayout=(LinearLayout)view.findViewById(R.id.bottom_input_layout);
        sendRecorderLayout=(LinearLayout)view.findViewById(R.id.send_recorder_layout);

        huojian=(LinearLayout)view.findViewById(R.id.huojian);
        feiji=(LinearLayout)view.findViewById(R.id.feiji);
        zan=(LinearLayout)view.findViewById(R.id.zan);

        huojian.setOnClickListener(this);
        feiji.setOnClickListener(this);
        zan.setOnClickListener(this);

        mAudioRecorderButton=(AudioRecorderButton)view.findViewById(R.id.id_recorder_button);

        //开始设置数据和adapter
        mData=new ArrayList<PublicChatBean>();

        PublicChatBean bean1=new PublicChatBean("小小明","发出的第一条消息",PublicChatBean.ORDINARY_CHAT,0);
        PublicChatBean bean2=new PublicChatBean("vip先森","发出的第一条消息",PublicChatBean.VIP_CHAT,0);
        PublicChatBean bean3=new PublicChatBean("小小明","这是一个礼物哦",PublicChatBean.SEND_GIFTS_CHAT,15);

        mData.add(bean1);
        mData.add(bean2);
        mData.add(bean3);

        adapter=new PublicChatAdapter(getActivity().getApplicationContext(),mData);
        mListView.setAdapter(adapter);

        btnSend.setOnClickListener(this);
        btnSendGifts.setOnClickListener(this);
        btnSendRecorder.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);

        //在这里动态注册一个广播
        IntentFilter filter=new IntentFilter();
        //filter.addAction("newchat_come");
        filter.addAction("new_group_chat");
        myUIBroadcastReceiver=new MyUIBroadcastReceiver();
        getActivity().registerReceiver(myUIBroadcastReceiver,filter);

//        if(getActivity() instanceof PersonalLiveActivity){
//            roomName= UserInformation.getUsername();
//        }else if(getActivity() instanceof OthersLiveActivity){
//            roomName=((OthersLiveActivity)getActivity()).friendName;
//            Log.e("=======PublicChatFragment好友名字是====",roomName);
//        }
//


//        MyConnection.getInstance().createChatRoom(roomName, UserInformation.getUsername(),"123");
//        multiUserChat=MyConnection.getInstance().joinChatRoom(roomName,UserInformation.getUsername(),"123");



        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(final float seconds, final String filePath) {
                //真正处理音频录制完毕之后的逻辑的地方
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String amrString = Base64Operator.getAudioBinary(filePath);
                            //新建一个音频类型的消息
                            //PublicChatBean newBean = new PublicChatBean(UserInformation.getUsername(), amrString, 0, seconds, filePath, PublicChatBean.RECORDER_CHAT);

                            if(multiUserChat==null)
                                Log.e("===PublicChatFragment=","mutiUserChat为空耶！！！");
                            if(multiUserChat!=null){
                                org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                                m.setBody(amrString);//将编码后的字符串放进body中
                                m.addSubject("chat_type","recorder");//设置消息类型为recorder
                                m.addSubject("time",seconds+"");//记录语音时长
                                multiUserChat.sendMessage(m);
                                editText.setText("");//重新初始化输入框
                                Log.e("===PublicChatFragment==","发完消息了，能收到吗？？？");
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm!=null){
                    imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                if(sendGiftsLayout.getVisibility()==View.VISIBLE){//如果礼物控件是可见的，则隐藏掉
                    sendGiftsLayout.setVisibility(View.GONE);
                    bottomInputLayout.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mData.get(position).getType()==PublicChatBean.RECORDER_CHAT) {
                    if (mAnimView != null) {
                        //先停止一个动画
                        mAnimView.setBackgroundResource(R.drawable.adj);
                        mAnimView = null;
                    }
                    //播放动画
                    mAnimView = view.findViewById(R.id.id_recorder_anim);
                    mAnimView.setBackgroundResource(R.drawable.play_anim);
                    final AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                    anim.start();
                    //播放音频
                    MediaManager.playSound(mData.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mAnimView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                }
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(myUIBroadcastReceiver);//解绑广播接收器
        getActivity().getApplicationContext().unbindService(conn);
        MediaManager.release();
        super.onDestroyView();
    }

    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            multiUserChatServer=((MultiUserChatServer.MyBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onPause() {
        super.onPause();
        MediaManager.pause();
    }


    @Override
    public void onResume() {
        super.onResume();
        MediaManager.resume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_message:{
                final String inputMessage=editText.getText().toString();//获取输入的内容
                if(inputMessage!=null&&inputMessage!=""){//输入的消息不为空
                    //用Handler来更新UI，所以ChatBean的实例化放进Handler里面,因为群组消息会再次下发，所以自己这边已经不用处理更新UI的逻辑
//                    Message msgSend=new Message();
//                    PublicChatBean newChat=new PublicChatBean("小小明",inputMessage,PublicChatBean.ORDINARY_CHAT,0);
//                    Bundle bundle=new Bundle();
//                    bundle.putSerializable("newChat",newChat);
//                    msgSend.setData(bundle);
//                    handler.handleMessage(msgSend);//发送给Handler

//                    //接下来用smack发送这条消息给好友
//                    try {
//                        jid="live1@127.0.0.1/Smack";
//                        XMPPTCPConnection connection = MyConnection.getInstance().getConnection();
//                        ChatManager cm = ChatManager.getInstanceFor(connection);
//                        Chat chat = cm.createChat(jid, new ChatMessageListener() {
//                            @Override
//                            public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
//                                message.setBody(inputMessage);
//                            }
//                        });
//                        org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
//                        m.setBody(inputMessage);
//                        chat.sendMessage(m);
//                        editText.setText("");//重新初始化输入框
//                    } catch (SmackException.NotConnectedException e) {
//                        e.printStackTrace();
//                    }

                    try{
                        if(multiUserChat==null)
                            Log.e("===PublicChatFragment==","mutiUserChat为空耶！！！");
                        if(multiUserChat!=null){
                            org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                            m.setBody(inputMessage);
                            m.addSubject("chat_type","text");
                            multiUserChat.sendMessage(m);
                            editText.setText("");//重新初始化输入框
                            Log.e("===PublicChatFragment==","发完消息了，能收到吗？？？");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                break;
            }
            case R.id.send_gift_icon:{
                sendGiftsLayout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.send_message_icon:{
                sendRecorderLayout.setVisibility(View.GONE);
                bottomInputLayout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.send_recorder_icon:{
                bottomInputLayout.setVisibility(View.GONE);
                sendRecorderLayout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.huojian:{
                try{
                    if(multiUserChat==null)
                        Log.e("===PublicChatFragment==","mutiUserChat为空耶！！！");
                    if(multiUserChat!=null){
                        org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                        m.setBody("送出了一个火箭");
                        m.addSubject("chat_type","gift");
                        m.addSubject("gift_number","100");
                        multiUserChat.sendMessage(m);
                        Log.e("===PublicChatFragment==","发完消息了，能收到吗？？？");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            case R.id.feiji:{
                try{
                if(multiUserChat==null)
                    Log.e("===PublicChatFragment==","mutiUserChat为空耶！！！");
                if(multiUserChat!=null){
                    org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                    m.setBody("送出了一架飞机");
                    m.addSubject("chat_type","gift");
                    m.addSubject("gift_number","30");
                    multiUserChat.sendMessage(m);
                    Log.e("===PublicChatFragment==","发完消息了，能收到吗？？？");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
                break;
            }
            case R.id.zan:{
                try{
                    if(multiUserChat==null)
                        Log.e("===PublicChatFragment==","mutiUserChat为空耶！！！");
                    if(multiUserChat!=null){
                        org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                        m.setBody("点了一个赞");
                        m.addSubject("chat_type","gift");
                        m.addSubject("gift_number","1");
                        multiUserChat.sendMessage(m);
                        Log.e("===PublicChatFragment==","发完消息了，能收到吗？？？");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //getActivity().unregisterReceiver(myUIBroadcastReceiver);
    }

    //获取有新消息的广播
    private class MyUIBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("===PublicChatFragment==","broadcast接收到消息啦！");
            //接收到新消息的广播后处理的逻辑，发送到handler中进行更新UI
            String[] from=intent.getStringArrayExtra("message");
            if (from[0]!=null&&from[1]!=null){
                Log.e("PublicChatFragment消息类型是",from[2].toString());
                //Log.e("=====",from[0].toString()+"说:"+from[1].toString());
                Message msgReceived=new Message();

                String user_name=from[0].toString();//用户名字
                final String chat_content=from[1].toString();//聊天内容
                int type=PublicChatBean.ORDINARY_CHAT;//聊天类型，普通弹幕、会员弹幕、送礼弹幕
                int gifts_number=0;//礼物的数量，送礼弹幕用
                float time=0.0f;//时长，语音弹幕专用
                String filePath="";//文件地址，语音弹幕专用

                String type_string=from[2].toString();
                if(type_string.equals("text")) {
                    type = PublicChatBean.ORDINARY_CHAT;
                } else if(type_string.equals("recorder")) {
                    type = PublicChatBean.RECORDER_CHAT;
                    time=Float.valueOf(from[4].toString());

                    //在这里将获取到的string转变成为audio录音
                    //首先是一个随机的文件名
                    final String dir= Environment.getExternalStorageDirectory()+"/recorder_audios/"+ UUID.randomUUID().toString()+".amr";
                    filePath=dir;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Base64Operator.getAudio(chat_content,dir);
                        }
                    }).start();

                } else if(type_string.equals("gift")) {
                    type = PublicChatBean.SEND_GIFTS_CHAT;
                    gifts_number=Integer.valueOf(from[3].toString());
                }

                //新建一个newChat Bean，用Handler来传输并更新UI
                PublicChatBean newChat=new PublicChatBean(user_name,chat_content,type,gifts_number,time,filePath);
                //PublicChatBean需要放进Bundle里面才能在Handle中进行传输
                Bundle bundle=new Bundle();
                bundle.putSerializable("newChat",newChat);
                msgReceived.setData(bundle);
                //发送给handle就好
                handler.handleMessage(msgReceived);
            }
        }
    }
}

