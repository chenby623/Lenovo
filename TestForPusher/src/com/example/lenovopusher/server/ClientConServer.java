package com.example.lenovopusher.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.lenovopusher.utils.MyConnection;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class ClientConServer extends Service {
	/**������handler�յ���Ϣ֮��ͨ���㲥����Ϣ���͵���Ҫ�ĵط����ø���������Ӧ�Ĵ���*/
    private DownloadBinder mBinder=new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message m) {
            super.handleMessage(m);
            org.jivesoftware.smack.packet.Message msg=new org.jivesoftware.smack.packet.Message();
            msg=(org.jivesoftware.smack.packet.Message) m.obj;
            //�Ѵӷ�������õ���Ϣͨ���㲥����
            Intent intent=new Intent();
            intent.setAction("newchat_come");
            String[] message=new String[]{
                    msg.getFrom(),
                    msg.getBody()
            };
            if (message[0]!=null){
                Log.i("===�������յ���Ϣ From",message[0].toString());
            }
            if(message[1]!=null){
                Log.i("===�������յ���Ϣ Body",message[1].toString());
            }
            intent.putExtra("message",message);
            sendBroadcast(intent);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //�ڷ��������洴��һ��������Ϣ�ļ����������������յ�����Ϣ
        XMPPTCPConnection connection= MyConnection.getInstance().getConnection();
        ChatManager chatManager=ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(new MyChatListener());
        return super.onStartCommand(intent, flags, startId);
    }

    class MyChatListener implements ChatManagerListener {
        @Override
        public void chatCreated(Chat chat, boolean b) {
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
                    /**ͨ��handlerת����Ϣ*/
                    Message m=handler.obtainMessage();
                    m.obj=message;
                    m.sendToTarget();
                }
            });
        }
    }

    class DownloadBinder extends Binder {
        /**
         * ��ʼ����  */
        public void startDownload(){

        }

        /**
         * ��ȡ���ؽ���
         * @return
         */
        public int getProgress(){
            return 0;
        }

        public IBinder onBind(Intent intent){
            return mBinder;
        }
    }


}

