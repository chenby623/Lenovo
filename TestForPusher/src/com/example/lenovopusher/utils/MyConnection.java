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
     * ����ģʽ
     * */
    synchronized public static MyConnection getInstance(){return myConnection;}

    /**
     * ���ط�����ʵ��
     * */
    public XMPPTCPConnection getConnection(){
        if (connection==null){
            openConnect();
        }
        return connection;
    }

    /**
     * ���ӷ�����
     * */
    public boolean openConnect(){
        try {
            XMPPTCPConnectionConfiguration config=XMPPTCPConnectionConfiguration.builder()
                    .setHost(Config.SERVER_HOST)
                    .setPort(Config.SERVER_PORT)
                    .setServiceName(Config.SERVER_NAME)
                    //�Ƿ�����ȫģʽ
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    //�Ƿ���ѹ��
                    .setCompressionEnabled(false)
                    //�Ƿ�������ģʽ
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
     * �رշ���������
     * */
    public void closeConnection(){
        if (connection!=null){
            connection.disconnect();
        }
    }


    /**
     * �û���¼
     *
     * @param username
     * @param password
     * @return
     * */
    public boolean login(String username,String password) throws IOException,SmackException,XMPPException{
        Log.e("=====","�Ѿ������¼ҳ��");
        if(getConnection()==null)
            return false;
        Log.e("�û����ǣ� ",username+"   ������:"+password);
        connection.login(username,password);
        Log.e("=====","ע��ɹ�");
        return true;
    }


    /**
     * ע���û���Ϣ
     * @param username �˺�
     * @param password �˻�
     * */
    public boolean register(String username,String password) {
        if (getConnection() == null)
            return false;
        try {
            AccountManager.getInstance(connection).createAccount(username,password);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("MyConnection","ע��ʧ��");
            return false;
        }
    }


    /**
     * ��ȡ��ǰ��¼�û������к�����Ϣ
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
     * �����������촰�ڣ�������ɺ��ڴ�����ʹ��chat.sendMessage(message)
     * @param jid   ���ѵ�jid
     * @return
     */
    public Chat createChat(String jid){
        if(getConnection()!=null){
            ChatManager chatManager=ChatManager.getInstanceFor(connection);
            return chatManager.createChat(jid);
        }
        throw new NullPointerException("����������ʧ�ܣ��������ӷ�����");
    }


    /**
     * ��ȡ������������
     * @return
     */
    public ChatManager getChatManager(){
        if(getConnection()!=null){
            ChatManager chatManager=ChatManager.getInstanceFor(connection);
            return  chatManager;
        }
        throw new NullPointerException("����������ʧ�ܣ��������ӷ�����");
    }


    /**
     * �����ı���Ϣ
     */
    //���ȴ�������������������


    /**
     * ��Ӻ��ѣ��޷���
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
     * ����Ⱥ��������
     * */
    public MultiUserChat createChatRoom(String roomName, String nickName, String password){
        Log.e("==MyConnection==","���뵽��������Ĵ�����������");
        if(getConnection()==null){
            throw new NullPointerException("����������ʧ�ܣ��������ӷ�����");
        }
        MultiUserChat muc=null;
        try{
            //����һ��MultiUserChat
            muc= MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName+"@conference."+connection.getServiceName());
            Log.e("===MyConnection==�����������===",roomName+"@conference."+connection.getServiceName());
            //����������
            boolean isCreate=muc.createOrJoin(nickName);
            if(isCreate&&!muc.isJoined()) {
                //��������ҵ����ñ�
                Form form = muc.getConfigurationForm();
                //����ԭʼ������һ��Ҫ�ύ���±�
                Form submitForm = form.createAnswerForm();
                //��Ҫ�ύ�ı����Ĭ�ϴ�
                List<FormField> fields = form.getFields();
                for (int i = 0; fields != null && i < fields.size(); i++) {
                    if (FormField.Type.hidden != fields.get(i).getType() && fields.get(i).getVariable() != null) {
                        //����Ĭ��ֵ��Ϊ��
                        submitForm.setDefaultAnswer(fields.get(i).getVariable());
                    }
                }
                //���������ҵ���ӵ����
                List<String> owners = new ArrayList();
                owners.add(connection.getUser());//�û�JID
                submitForm.setAnswer("muc#roomconfig_roomowners", owners);
                // �����������ǳ־������ң�����Ҫ����������
                submitForm.setAnswer("muc#roomconfig_persistentroom", true);
                // ������Գ�Ա����
                submitForm.setAnswer("muc#roomconfig_membersonly", false);
                // ����ռ��������������
                submitForm.setAnswer("muc#roomconfig_allowinvites", true);
                if (password != null && password.length() != 0) {
                    // �����Ƿ���Ҫ����
                    submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                    // ���ý�������
                    submitForm.setAnswer("muc#roomconfig_roomsecret", password);
                }
                // �ܹ�����ռ������ʵ JID �Ľ�ɫ
                // submitForm.setAnswer("muc#roomconfig_whois", "anyone");
                // ��¼����Ի�
                submitForm.setAnswer("muc#roomconfig_enablelogging", true);
                // ������ע����ǳƵ�¼
                submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
                // ����ʹ�����޸��ǳ�
                submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
                // �����û�ע�᷿��
                submitForm.setAnswer("x-muc#roomconfig_registration", false);
                // ��������ɵı�����Ĭ��ֵ����������������������
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
     * ����һ��Ⱥ��������
     * */
    public MultiUserChat joinChatRoom(String roomName,String nickName,String password){
        if(getConnection()==null){
            throw new NullPointerException("����������ʧ�ܣ��������ӷ�����");
        }
        try{
            //ʹ��XMPPConnection����һ��MultiUserChat����
            MultiUserChat muc=MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName+"@conference."+connection.getServiceName());
            //�����ҷ��񾹻����Ҫ���ܵ���ʷ��¼����
            DiscussionHistory history=new DiscussionHistory();
            history.setMaxChars(0);
            //history.setSince(new Date());

            if(!muc.isJoined()) {
                //�û�����������
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

