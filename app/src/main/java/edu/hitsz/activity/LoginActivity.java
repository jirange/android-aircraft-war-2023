package edu.hitsz.activity;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.network.ClientThread;
import edu.hitsz.network.UserClientThread;
import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.pojo.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText l_name;
    private EditText l_pwd;
    private EditText l_host;
    private Button l_login, l_register;
    //    private String LOGIN_URL = "http://192.168.101.10:9090/Login_Server/login";
    private static final String TAG = "LoginActivity";
    private Handler handler;
    //    private ClientThread clientThread;
    private UserClientThread clientThread;
    public static User user;
    private boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        l_login = findViewById(R.id.l_login);
        l_register = findViewById(R.id.l_register);
        l_name = findViewById(R.id.l_name);
        l_pwd = findViewById(R.id.l_pwd);
        l_host = findViewById(R.id.l_host);


        l_login.setOnClickListener(this);
        l_register.setOnClickListener(this);


        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x113) {
                    System.out.println(msg.obj);
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    switch (msg.obj.toString()) {
                        case "login_success":
                            //todo 登录成功
                            System.out.println("登录成功");
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                            //todo 设置为已经登录
                            MainActivity.have_login = true;
                            //todo 返回主界面
                            finish();
                            break;
                        case "login_failed":
                            //todo 登录失败  账号或密码错误
                            System.out.println("登录失败  账号或密码错误");
                            Toast.makeText(LoginActivity.this, "登录失败 账号或密码错误", Toast.LENGTH_SHORT).show();

                            break;
                        case "register_success":
                            //todo 注册成功
                            System.out.println("注册成功");
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

//                            //todo 设置为已经登录
//                            MainActivity.have_login=true;
//                            //todo 返回主界面
//                            finish();

                            break;
                        case "register_failed":
                            //todo 注册失败 有重复的用户名
                            System.out.println("注册失败 有重复的用户名");
                            Toast.makeText(LoginActivity.this, "注册失败 有重复的用户名", Toast.LENGTH_SHORT).show();

                            break;
                    }
                }


            }
        };

//        clientThread = new ClientThread(handler);  //
        UserClientThread.clientThread = new UserClientThread(handler);  //
        // TODO: 2023/5/28 试试这种？？？
        //UserClientThread.getClientThread(handler);
//        new Thread(UserClientThread.clientThread).start();

        flag = false;
    }

    @Override
    public void onClick(View v) {

        UserClientThread.HOST = l_host.getText().toString();
        ClientThread.HOST = l_host.getText().toString();

//        clientThread = UserClientThread.getClientThread(handler);
//        clientThread = UserClientThread.getClientThread(handler);
//        UserClientThread.clientThread = new UserClientThread(handler);  //
//        new Thread(UserClientThread.clientThread).start();

        final View lv = v;
        JSONObject jsonObject = new JSONObject();
        user = new User(l_name.getText().toString(), l_pwd.getText().toString());
        System.out.println(user);
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("name", l_name.getText().toString());
            userJson.put("password", l_pwd.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                Message msg;

                try {
                    switch (lv.getId()) {
                        case R.id.l_login:
                            //如果点的是登录按钮
                            //todo 发送登录请求 向服务器传递输入的数据
                            jsonObject.put("login", userJson);
                            if (!flag) {
                                new Thread(UserClientThread.clientThread).start();
                            }

                            break;
                        case R.id.l_register:
                            jsonObject.put("register", userJson);

                            //todo 发送注册请求 向服务器传递输入的数据
                            new Thread(UserClientThread.clientThread).start();
                            flag = true;
                            break;
                    }
                    msg = new Message();
                    msg.what = 0x456;
                    msg.obj = jsonObject;
                    System.out.println("向数据库发送" + jsonObject.toString());
                    while (true) {
                        if (UserClientThread.clientThread.toserverHandler != null) break;
                    }
                    UserClientThread.clientThread.toserverHandler.sendMessage(msg);//具体信息
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}