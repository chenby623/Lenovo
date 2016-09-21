package com.example.lenovopusher.model;

import java.io.Serializable;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class PublicChatBean implements Serializable {

	public static final int ORDINARY_CHAT=0;//��ͨ��Ļ
    public static final int VIP_CHAT=1;//��Ա��Ļ
    public static final int SEND_GIFTS_CHAT=2;//�����ﵯĻ
    public static final int RECORDER_CHAT=3;//������Ļ

    private int chat_id;//���������ݿ����
    private String user_name;//�û�����
    private String chat_content;//��������
    private int type;//�������ͣ���ͨ��Ļ����Ա��Ļ������Ļ
    private int gifts_number;//���������������Ļ��
    private float time;//ʱ����������Ļר��
    private String filePath;//�ļ���ַ��������Ļר��


    public PublicChatBean(){}

    //ע���������������ǲ�һ���ģ����û������chat_id����Ϊ���Ǵ��Լ�ֱ�Ӹ�ֵ�ģ��������Ǹ�Ҫ����chat_id�ģ��Ǵ����ݿ����ó�����
    public PublicChatBean(String user_name, String chat_content, int type,int gifts_number) {
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number=gifts_number;
    }

    //ע���������������ǲ�һ����
    public PublicChatBean(int chat_id, String user_name, String chat_content, int type,int gifts_number) {
        this.chat_id = chat_id;
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number=gifts_number;
    }

    //ע���������������ǲ�һ���ģ����û������chat_id����Ϊ���Ǵ��Լ�ֱ�Ӹ�ֵ�ģ��������Ǹ�Ҫ����chat_id�ģ��Ǵ����ݿ����ó�����
    public PublicChatBean(String user_name, String chat_content, int type,int gifts_number, float time, String filePath) {
        this.user_name = user_name;
        this.chat_content = chat_content;
        this.type = type;
        this.gifts_number = gifts_number;
        this.time = time;
        this.filePath = filePath;
    }

    //ע���������������ǲ�һ����
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

