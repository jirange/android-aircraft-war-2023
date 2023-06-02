package edu.hitsz.activity;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
    private static final String TAG = "LoginActivity";
    private Handler handler;
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
                    switch (msg.obj.toString()) {
                        case "login_success":
                            // 登录成功
                            Log.i(TAG,"login_success");
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            // 设置为已经登录
                            MainActivity.have_login = true;
                            // 返回主界面
                            finish();
                            break;
                        case "login_failed":
                            // 登录失败  账号或密码错误
                            Log.i(TAG,"login_failed");
                            Toast.makeText(LoginActivity.this, "登录失败 账号或密码错误", Toast.LENGTH_SHORT).show();
                            break;
                        case "register_success":
                            // 注册成功
                            Log.i(TAG,"register_success");
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            break;
                        case "register_failed":
                            // 注册失败 有重复的用户名
                            Log.i(TAG,"register_failed");
                            Toast.makeText(LoginActivity.this, "注册失败 有重复的用户名", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }


            }
        };

        UserClientThread.clientThread = new UserClientThread(handler);
        flag = false;
    }

    @Override
    public void onClick(View v) {

        UserClientThread.HOST = l_host.getText().toString();
        ClientThread.HOST = l_host.getText().toString();
        final View lv = v;
        JSONObject jsonObject = new JSONObject();
        user = new User(l_name.getText().toString(), l_pwd.getText().toString());
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("name", l_name.getText().toString());
            userJson.put("password", l_pwd.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            Message msg;

            try {
                switch (lv.getId()) {
                    case R.id.l_login:
                        // 发送登录请求 向服务器传递输入的数据
                        jsonObject.put("login", userJson);
                        if (!flag) {
                            new Thread(UserClientThread.clientThread).start();
                        }

                        break;
                    case R.id.l_register:
                        // 发送注册请求 向服务器传递输入的数据
                        jsonObject.put("register", userJson);
                        new Thread(UserClientThread.clientThread).start();
                        flag = true;
                        break;
                }
                msg = new Message();
                msg.what = 0x456;
                msg.obj = jsonObject;
                Log.i(TAG,"向数据库发送" + jsonObject.toString());
                while (true) {
                    if (UserClientThread.clientThread.toserverHandler != null) break;
                }
                UserClientThread.clientThread.toserverHandler.sendMessage(msg);//具体信息
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}