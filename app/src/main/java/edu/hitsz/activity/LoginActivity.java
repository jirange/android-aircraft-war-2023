package edu.hitsz.activity;

import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.network.ClientThread;
import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.pojo.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText l_name;
    private EditText l_pwd;
    private Button l_login, l_register;
    private String LOGIN_URL = "http://192.168.101.10:9090/Login_Server/login";
    private static final String TAG = "LoginActivity";
    private Handler handler;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        l_login = findViewById(R.id.l_login);
        l_register = findViewById(R.id.l_register);
        l_name = findViewById(R.id.l_name);
        l_pwd = findViewById(R.id.l_pwd);


        l_login.setOnClickListener(this);
        l_register.setOnClickListener(this);


        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x113) {
                    System.out.println(msg.obj);
                    switch (msg.obj.toString()) {
                        case "login_success":
                            //todo 登录成功
                            System.out.println("登录成功");
                            break;
                        case "login_failed":
                            //todo 登录失败  账号或密码错误
                            System.out.println("登录失败  账号或密码错误");
                            break;
                        case "register_success":
                            //todo 注册成功
                            System.out.println("注册成功");
                            break;
                        case "register_failed":
                            //todo 注册失败 有重复的用户名
                            System.out.println("注册失败 有重复的用户名");
                            break;
                    }
                }


            }
        };

        clientThread = new ClientThread(handler);  //
        new Thread(clientThread).start();

    }

    @Override
    public void onClick(View v) {
        final View lv = v;
        final Map<String, String> paramsmap = new HashMap<>();
        paramsmap.put("username", l_name.getText().toString());
        paramsmap.put("password", l_pwd.getText().toString());

        new Thread() {
            @Override
            public void run() {
                String loginresult = "";//存储服务器返回的消息
                Message msg;

                try {
                    switch (lv.getId()) {
                        case R.id.l_login:
                            //如果点的是登录按钮
                            //todo 发送登录请求 向服务器传递输入的数据
                            JSONObject jsonObject = new JSONObject();
                            User user = new User(l_name.getText().toString(), l_pwd.getText().toString());
                            System.out.println(user);
                            jsonObject.put("login", l_name.getText().toString());
                            msg = new Message();
                            msg.what = 0x456;
                            msg.obj = jsonObject;
                            System.out.println("向数据库发送"+user);
                            clientThread.toserverHandler.sendMessage(msg);//具体信息


                            break;
                        case R.id.l_register:
//                            JSONObject jsonObject = new JSONObject();
//
//                            //todo 发送注册请求 向服务器传递输入的数据
//                            jsonObject.put("register", user);
//                            msg = new Message();
//                            msg.what = 0x456;
//                            msg.obj = jsonObject;
//                            clientThread.toserverHandler.sendMessage(msg);//具体信息

                            break;
                    }
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