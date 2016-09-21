package com.example.lenovopusher.model;

import java.io.Serializable;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class PublicChatBean implements Serializable {

	public static final int ORDINARY_CHAT=0;//普通弹幕
    public static final int VIP_CHAT=1;//会员弹幕
    public static final int SEND_GIFTS_CHAT=2;//送礼物弹幕
    public static final int RECORDER_CHAT=3;//语音弹幕

    private int chat_id;//用来做数据库操作
    private String user_name;//用户名字
    private String chat_content;//聊天内容
    private int type;//聊天类型，普通弹幕、会员弹幕、送礼弹幕
    private int gifts_number;//礼物的数量，送礼弹幕用
    private float time;//时长，语音弹幕专用
    private String filePath;//文件地址，语音弹幕专用


    public PublicChatBean(){}

    //注意这个跟下面这个是不一样的，这个没有设置chat_id，因为这是从自己直接赋值的，而下面那个要设置chat_id的，是从数据库中拿出来的
    public PublicChatBean(String user_name, String chat_content, int type,int gifts_number) {
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number=gifts_number;
    }

    //注意这个跟上面这个是不一样的
    public PublicChatBean(int chat_id, String user_name, String chat_content, int type,int gifts_number) {
        this.chat_id = chat_id;
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number=gifts_number;
    }

    //注意这个跟下面这个是不一样的，这个没有设置chat_id，因为这是从自己直接赋值的，而下面那个要设置chat_id的，是从数据库中拿出来的
    public PublicChatBean(String user_name, String chat_content, int type,int gifts_number, float time, String filePath) {
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number = gifts_number;
        this.time = time;
        this.filePath = filePath;
    }

    //注意这个跟上面这个是不一样的
    public PublicChatBean(int chat_id, String user_name, String chat_content,int type,int gifts_number, float time, String filePath) {
        this.chat_id = chat_id;
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number = gifts_number;
        this.time = time;
        this.filePath = filePath;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getChat_content() {
        return chat_content;
    }

    public void setChat_content(String chat_content) {
        this.chat_content = chat_content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public int getGifts_number() {
        return gifts_number;
    }

    public void setGifts_number(int gifts_number) {
        this.gifts_number = gifts_number;
    }

}

