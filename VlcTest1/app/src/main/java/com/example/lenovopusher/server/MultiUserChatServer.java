package com.example.lenovopusher.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.lenovopusher.utils.Config;
import com.example.lenovopusher.utils.MyConnection;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class MultiUserChatServer extends Service {
    String roomName;
    public MyBinder myBinder=new MyBinder();
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("===========","监听群聊的server已经创建了");


        roomName=intent.getStringExtra("roomName");
        roomName="111";
        roomName= Config.roomName;//下下策

        XMPPTCPConnection connection= MyConnection.getInstance().getConnection();

        MultiUserChatManager multiUserChatManager=MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat multiUserChat=multiUserChatManager.getMultiUserChat(roomName+"@conference."+connection.getServiceName());

        //给该聊天室设置监听
        multiUserChat.addMessageListener(new MyGroupChatListner());

        Log.e("=================","server已经走到了这里");
        return null;
    }

    public class MyBinder extends Binder {
        public MultiUserChatServer getService(){return MultiUserChatServer.this;}
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message m) {
            super.handleMessage(m);
            Message msg=new Message();
            msg=(Message)m.obj;
            //把从服务器获得的消息通过广播发送
            Intent intent=new Intent();
            intent.setAction("new_group_chat");
            String[] message=new String[]{
                    msg.getFrom(),
                    msg.getBody(),
                    msg.getSubject("chat_type"),
                    msg.getSubject("gift_number"),
                    msg.getSubject("time")
            };
            if (message[0]!=null){
                Log.e("===服务器收到消息 From===",message[0].toString());
            }
            if(message[1]!=null){
                //Log.e("===服务器收到消息 Body===",message[1].toString());
            }
            if(message[2]==null){
                message[2]="text";
                Log.e("===服务器收到的消息类型是===",message[2].toString());
            }
            if(message[3]==null){
                message[3]="0";
            }
            if(message[4]==null){
                message[4]="0";
            }
            intent.putExtra("message",message);

            sendBroadcast(intent);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

    }

    class MyGroupChatListner implements MessageListener {
        @Override
        public void processMessage(Message message) {
            Log.e("===============","接收到了群组消息");
            /**通过handler转发消息*/
            android.os.Message m=handler.obtainMessage();
            m.obj=message;
            m.sendToTarget();
        }

        public void processPacket(Packet packet) {
            Log.e("=============","接收到了群组消息");
            Message message = (Message) packet;
            // 接收来自聊天室的聊天信息
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }
}

