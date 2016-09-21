package com.example.lenovopusher.model;

import java.io.Serializable;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class FriendBean implements Serializable {
	private String jid;//����jid
    private String friendName;//��������
    private String liveAddr;//ֱ����ַ
    private boolean is_living;//�Ƿ�����ֱ��

    public FriendBean(){}

    public FriendBean(String friendName) {
        this.friendName = friendName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getLiveAddr() {
        return liveAddr;
    }

    public void setLiveAddr(String liveAddr) {
        this.liveAddr = liveAddr;
    }

    public boolean is_living() {
        return is_living;
    }

    public void setIs_living(boolean is_living) {
        this.is_living = is_living;
    }
}

