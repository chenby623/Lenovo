package com.example.lenovopusher.utils;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class MyConnection {

    private XMPPTCPConnection connection=null;
    private static MyConnection myConnection=new MyConnection();

    /**
     * 单例模式
     * */
    synchronized public static MyConnection getInstance(){return myConnection;}

    /**
     * 返回服务器实例
     * */
    public XMPPTCPConnection getConnection(){
        if (connection==null){
            openConnect();
        }
        return connection;
    }

    /**
     * 连接服务器
     * */
    public boolean openConnect(){
        try {
            XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration.builder()
                    .setHost(Config.SERVER_HOST)
                    .setPort(Config.SERVER_PORT)
                    .setServiceName(Config.SERVER_NAME)
                    //是否开启安全模式
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    //是否开启压缩
                    .setCompressionEnabled(false)
                    //是否开启调试模式
                    .setDebuggerEnabled(true).build();
            connection=new XMPPTCPConnection(config);
            connection.connect();
            return true;
        }catch (SmackException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (XMPPException e){
            e.printStackTrace();
        }
        connection=null;
        return false;
    }


    /**
     * 关闭服务器连接
     * */
    public void closeConnection(){
        if (connection!=null){
            connection.disconnect();
        }
    }


    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     * */
    public boolean login(String username,String password) throws IOException,SmackException,XMPPException{
        Log.e("=====","已经进入登录页面");
        if(getConnection()==null)
            return false;
        Log.e("用户名是： ",username+"   密码是:"+password);
        connection.login(username,password);
        Log.e("=====","注册成功");
        return true;
    }


    /**
     * 注册用户信息
     * @param username 账号
     * @param password 账户
     * */
    public boolean register(String username,String password) {
        if (getConnection() == null)
            return false;
        try {
            AccountManager.getInstance(connection).createAccount(username,password);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MyConnection","注册失败");
            return false;
        }
    }


    /**
     * 获取当前登录用户的所有好友信息
     * @return
     */
    public ArrayList<RosterEntry> getAllFriends(){
        ArrayList<RosterEntry> rosterEntryList=new ArrayList<RosterEntry>();
        Roster roster=Roster.getInstanceFor(connection);
        Collection<RosterEntry> entries=roster.getEntries();
        for(RosterEntry entry:entries){
            rosterEntryList.add(entry);
        }
        return rosterEntryList;
    }


    /**
     * 创建单人聊天窗口，创建完成后在代码中使用chat.sendMessage(message)
     * @param jid   好友的jid
     * @return
     */
    public Chat createChat(String jid){
        if(getConnection()!=null){
            ChatManager chatManager=ChatManager.getInstanceFor(connection);
            return chatManager.createChat(jid);
        }
        throw new NullPointerException("服务器连接失败，清先连接服务器");
    }


    /**
     * 获取聊天对象管理器
     * @return
     */
    public ChatManager getChatManager(){
        if(getConnection()!=null){
            ChatManager chatManager=ChatManager.getInstanceFor(connection);
            return  chatManager;
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }


    /**
     * 接受文本信息
     */
    //首先创建聊天对象管理器监听


    /**
     * 添加好友，无分组
     */
    public static boolean addUser(Roster roster, String userName, String name) {
        try {
            roster.createEntry(userName, name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 创建群聊聊天室
     * */
    public MultiUserChat createChatRoom(String roomName, String nickName, String password){
        Log.e("==MyConnection==","进入到创建会议的代码里面来了");
        if(getConnection()==null){
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        MultiUserChat muc=null;
        try{
            //创建一个MultiUserChat
            muc= MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName+"@conference."+connection.getServiceName());
            Log.e("===MyConnection==创建的语句是===",roomName+"@conference."+connection.getServiceName());
            //创建聊天室
            boolean isCreate=muc.createOrJoin(nickName);
            if(isCreate&&!muc.isJoined()) {
                //获得聊天室的配置表单
                Form form = muc.getConfigurationForm();
                //根据原始表单创建一个要提交的新表单
                Form submitForm = form.createAnswerForm();
                //向要提交的表单添加默认答复
                List<FormField> fields = form.getFields();
                for (int i = 0; fields != null && i < fields.size(); i++) {
                    if (FormField.Type.hidden != fields.get(i).getType() && fields.get(i).getVariable() != null) {
                        //设置默认值作为答复
                        submitForm.setDefaultAnswer(fields.get(i).getVariable());
                    }
                }
                //设置聊天室的新拥有者
                List<String> owners = new ArrayList();
                owners.add(connection.getUser());//用户JID
                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                // 设置聊天室是持久聊天室，即将要被保存下来
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                // 房间仅对成员开放
                submitForm.setAnswer("muc#roomconfig_membersonly", false);
                // 允许占有者邀请其他人
                submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                if (password != null && password.length() != 0) {
                    // 进入是否需要密码
                    submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                    // 设置进入密码
                    submitForm.setAnswer("muc#roomconfig_roomsecret", password);
                }
                // 能够发现占有者真实 JID 的角色
                // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
                // 登录房间对话
                submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                // 仅允许注册的昵称登录
                submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                // 允许使用者修改昵称
                submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
                // 允许用户注册房间
                submitForm.setAnswer("x-muc#roomconfig_registration", false);
                // 发送已完成的表单（有默认值）到服务器来配置聊天室
                muc.sendConfigurationForm(submitForm);
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
//        catch (SmackException.NoResponseException e) {
//            e.printStackTrace();
//            return null;
//        } catch (SmackException.NotConnectedException e) {
//            e.printStackTrace();
//            return null;
//        } catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//            return null;
//        } catch (SmackException e) {
//            e.printStackTrace();
//            return null;
//        }
        return muc;
    }



    /**
     * 加入一个群聊聊天室
     * */
    public MultiUserChat joinChatRoom(String roomName,String nickName,String password){
        if(getConnection()==null){
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        try{
            //使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc=MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName+"@conference."+connection.getServiceName());
            //聊天室服务竟会决定要接受的历史记录数量
            DiscussionHistory history=new DiscussionHistory();
            history.setMaxChars(0);
            //history.setSince(new Date());

            if(!muc.isJoined()) {
                //用户加入聊天室
                muc.join(nickName, password);
            }
            return muc;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
//        catch (XMPPException.XMPPErrorException e) {
//            e.printStackTrace();
//            return null;
//        } catch (SmackException e) {
//            e.printStackTrace();
//            return null;
//        }
    }




}

