package edu.hitsz.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;


import edu.hitsz.R;
import edu.hitsz.network.ClientThread;

public class ModelActivity extends AppCompatActivity implements View.OnClickListener {


    private Button single_btn;
    private Button online_btn;
    private Handler handler;
    private ClientThread clientThread;
    Intent DifficultyIntent;
    Intent onlineDifficultyIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_choice);

        single_btn = findViewById(R.id.single_btn);
        online_btn = findViewById(R.id.online_btn);

        single_btn.setOnClickListener(this);
        online_btn.setOnClickListener(this);

        //用于发送接收到的服务端的消息，显示在界面上
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程
                if (msg.what == 0x123) {
                    //todo 处理来自clientThread的消息
                    //todo 提示网络连接成功
                }
            }
        };


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.single_btn:
                //todo 开启单机模式
                DifficultyIntent = new Intent(ModelActivity.this, DifficultyActivity.class);
                // 跳转到难度选择页面 DifficultyActivity
                DifficultyIntent.putExtra("model",false);//告诉DifficultyActivity是单机游戏
                startActivity(DifficultyIntent);

                break;

            case R.id.online_btn:  // 进行网络连接
                clientThread = new ClientThread(handler);  //
                new Thread(clientThread).start();
                //todo 核验是否登录

                //todo 若没有登录 提示前去登录 并跳转

                //todo 若已经登录成功，进入匹配页面  match
                onlineDifficultyIntent = new Intent(ModelActivity.this, OnlineDifficultyActivity.class);
                onlineDifficultyIntent.putExtra("model",true);//告诉DifficultyActivity是联网游戏
                startActivity(onlineDifficultyIntent);

                break;
        }
    }


}
