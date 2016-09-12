package com.example.lenovopusher.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovopusher.server.ClientConServer;
import com.example.lenovopusher.utils.MyConnection;
import com.example.lenovopusher.utils.UserInformation;
import com.example.vlctest.MainActivity;
import com.example.vlctest.R;

/**
 * Created by ChenBy on 2016/8/31.
 */
public class LoginActivity  extends Activity implements View.OnClickListener{
    EditText editName;
    EditText editPassword;
    Button loginButton;
    Button registButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        editName=(EditText)findViewById(R.id.edit_name);
        editPassword=(EditText)findViewById(R.id.edit_password);
        loginButton=(Button)findViewById(R.id.login_button);
        registButton=(Button)findViewById(R.id.regist_button);

        loginButton.setOnClickListener(this);
        registButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //开始登录,如果成功则开启后台服务 和 跳转到主页
                            String name=editName.getText().toString();
                            String password=editPassword.getText().toString();
                            if(MyConnection.getInstance().login(name,password)){
                                UserInformation.setUsername(name);
                                Intent intent1=new Intent(LoginActivity.this,ClientConServer.class);
                                startService(intent1);
                                Intent intent2=new Intent(LoginActivity.this,HomePageActivity.class);
                                //Intent intent2=new Intent(LoginActivity.this,TestForMultiChatActivity.class);
                                startActivity(intent2);
                                finish();//在跳转后自动销毁该页面
                            }else{
                                Toast.makeText(LoginActivity.this,"无法连接服务器，请检查你的网络",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
            default:
                break;
        }
    }
}