package edu.hitsz.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.hitsz.R;
import edu.hitsz.network.ClientThread;
import edu.hitsz.network.UserClientThread;
import edu.hitsz.pojo.PlayerRecord;

public class OnlineDifficultyActivity extends AppCompatActivity {

    private static final String TAG = "DifficultyActivity";

    private int gameType = 0;
    private boolean isOnline;
    private boolean have_audio = true;
    private Spinner difficultySp;
    private Handler handler;
    private UserClientThread clientThread;
    public static boolean have_match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_difficulty);
        Switch video_btn = (Switch) findViewById(R.id.online_video_btn);
        Button match_btn = (Button) findViewById(R.id.match_btn);
        this.difficultySp = (Spinner) findViewById(R.id.sp_difficulty);
        this.difficultySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String diff = adapterView.getItemAtPosition(position).toString();
//                System.out.println(position);//0  1  2
                gameType = position + 1;
                System.out.println("联机模式 玩家选择的难度为" + diff);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //to do  auto
            }
        });


        video_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //音效开
                    System.out.println("音效开");
                    have_audio = true;
                } else {
                    //音效关闭
                    System.out.println("音效关闭");
                    have_audio = false;
                }
            }
        });


        match_btn.setOnClickListener(view -> {


            //todo 发送匹配请求
            Toast.makeText(OnlineDifficultyActivity.this, "匹配中...", Toast.LENGTH_SHORT).show();
            askMatch();

            new Thread(() -> {
                while (true) {
                    if (have_match) {
                        Intent gameIntent = new Intent(OnlineDifficultyActivity.this, GameActivity.class);
                        gameIntent.putExtra("gameType", gameType);
                        gameIntent.putExtra("have_audio", have_audio);
                        gameIntent.putExtra("online", true);
                        Looper.prepare();
                        Toast.makeText(OnlineDifficultyActivity.this, "匹配成功 进入游戏", Toast.LENGTH_SHORT).show();
                        //匹配成功的话
                        startActivity(gameIntent);
                        break;
                    }
                }
            }).start();

        });





        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x113) {
                    System.out.println("在online diff 中处理消息了" + msg.obj);
                    String msgStr = (String) msg.obj;
                    if (msgStr.equals("match_success")) {
                        have_match = true;
                        System.out.println("看看我执行了吗");
                    }else if (msgStr.equals("match_fail")){
                        have_match = false;
                        System.out.println("看看我执行了吗");
                    }
                }
            }
        };

//        clientThread = new ClientThread(handler);
//        new Thread(clientThread).start();

    }

    private void askMatch() {
        UserClientThread.clientThread.setToclientHandler(handler);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("askMatch", gameType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        msg.what = 0x456;
        msg.obj = jsonObject;
        UserClientThread.clientThread.toserverHandler.sendMessage(msg);//具体信息


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}