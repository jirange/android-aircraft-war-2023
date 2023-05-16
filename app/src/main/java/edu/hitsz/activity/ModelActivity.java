package edu.hitsz.activity;

import androidx.appcompat.app.AppCompatActivity;

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
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.single_btn:
                //todo 开启单击模式
                break;

            case R.id.online_btn:  // 进行网络连接
                clientThread = new ClientThread(handler);  //
                new Thread(clientThread).start();
                break;
        }
    }


}
