package com.example.lenovopusher.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lenovopusher.fragment.Fragment1;
import com.example.lenovopusher.fragment.FriendsListFragment;
import com.example.lenovopusher.fragment.PersonalPageFragment;
import com.example.lenovopusher.model.FriendBean;
import com.example.vlctest.R;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class HomePageActivity extends FragmentActivity implements View.OnClickListener{
    private Fragment1 messageFragment;
    private FriendsListFragment friendsFragment;
    private PersonalPageFragment personalPageFragment;

    private RelativeLayout messageLayout;
    private RelativeLayout friendsLayout;
    private RelativeLayout moreLayout;

    private ImageView messageImg;
    private ImageView friendsImg;
    private ImageView moreImg;

    private TextView messageText;
    private TextView friendsText;
    private TextView moreText;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.home_page_activity);
        //初始化界面布局元素
        initViews();
        fragmentManager=getSupportFragmentManager();
        setTabSelection(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //其实是要在这个线程中处理登录信息的
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initViews(){
        messageLayout= (RelativeLayout) findViewById(R.id.message_layout);
        friendsLayout= (RelativeLayout) findViewById(R.id.friends_layout);
        moreLayout= (RelativeLayout) findViewById(R.id.more_layout);
        messageImg=(ImageView)findViewById(R.id.message_image);
        friendsImg=(ImageView)findViewById(R.id.friends_image);
        moreImg=(ImageView)findViewById(R.id.more_image);
        messageText=(TextView)findViewById(R.id.message_text);
        friendsText=(TextView)findViewById(R.id.friends_text);
        moreText=(TextView)findViewById(R.id.more_text);

        messageLayout.setOnClickListener(this);
        friendsLayout.setOnClickListener(this);
        moreLayout.setOnClickListener(this);
    }

    /**
     * 根据传入的index参数来设置选中的tab页
     * */
    private void setTabSelection(int index){
        //每次选中前都先清除掉上次的选中状态
        clearSelection();
        //开启一个fragment事务
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        //先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index){
            case 0:{
                messageText.setTextColor(Color.BLUE);
                if(messageFragment==null){
                    //如果fragment为空，则创建一个并添加到界面上
                    messageFragment=new Fragment1();
                    transaction.add(R.id.content,messageFragment);
                }else {
                    //如果fragment不为空，则直接将它显示出来
                    transaction.show(messageFragment);
                }
                break;
            }
            case 1:{
                friendsText.setTextColor(Color.BLUE);
                if(friendsFragment==null){
                    //如果fragment为空，则创建一个并添加到界面上
                    friendsFragment=new FriendsListFragment();
                    transaction.add(R.id.content,friendsFragment);
                }else {
                    //如果fragment不为空，则直接将它显示出来
                    transaction.show(friendsFragment);
                }
                break;
            }
            case 2:{
                moreText.setTextColor(Color.BLUE);
                if(personalPageFragment==null){
                    //如果fragment为空，则创建一个并添加到界面上
                    personalPageFragment=new PersonalPageFragment();
                    transaction.add(R.id.content,personalPageFragment);
                }else {
                    //如果fragment不为空，则直接将它显示出来
                    transaction.show(personalPageFragment);
                }
                break;
            }
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 清除所有选中状态
     * */
    private void clearSelection(){
        messageText.setTextColor(Color.parseColor("#82858b"));
        friendsText.setTextColor(Color.parseColor("#82858b"));
        moreText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的fragment都设为隐藏状态
     *
     * @param transaction 用于对Fragment执行操作的事务
     * */
    private void hideFragments(FragmentTransaction transaction){
        if(messageFragment!=null){
            transaction.hide(messageFragment);
        }
        if (friendsFragment!=null) {
            transaction.hide(friendsFragment);
        }
        if (personalPageFragment!=null){
            transaction.hide(personalPageFragment);
        }
    }

    /**
     * 点击具体的tab，跳转到对应的tab上
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.message_layout:{
                setTabSelection(0);
                break;
            }
            case R.id.friends_layout:{
                setTabSelection(1);
                break;
            }
            case R.id.more_layout:{
                setTabSelection(2);
                break;
            }
            default:
                break;
        }
    }
}
