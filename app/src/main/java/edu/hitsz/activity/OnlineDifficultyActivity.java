package edu.hitsz.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import edu.hitsz.R;
import edu.hitsz.network.UserClientThread;

public class OnlineDifficultyActivity extends AppCompatActivity {

    private static final String TAG = "DifficultyActivity";

    private int gameType = 0;
    private boolean isOnline;
    private boolean have_audio = true;
    private Spinner difficultySp;
    private Handler handler;
//    private UserClientThread clientThread;
    public static boolean have_match;
    private TextView matching_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_difficulty);

        have_match = false;

        matching_textView = findViewById(R.id.matching_textView);
        matching_textView.setVisibility(View.INVISIBLE);


        Switch video_btn = (Switch) findViewById(R.id.online_video_btn);
        Button match_btn = (Button) findViewById(R.id.match_btn);
        this.difficultySp = (Spinner) findViewById(R.id.sp_difficulty);
        this.difficultySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String diff = adapterView.getItemAtPosition(position).toString();
                gameType = position + 1;
                System.out.println("联机模式 玩家选择的难度为" + diff);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //todo  auto
            }
        });


        video_btn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                //音效开
                System.out.println("音效开");
                have_audio = true;
            } else {
                //音效关闭
                System.out.println("音效关闭");
                have_audio = false;
            }
        });


        match_btn.setOnClickListener(view -> {
            //todo 发送匹配请求
            Toast.makeText(OnlineDifficultyActivity.this, "开始匹配...", Toast.LENGTH_SHORT).show();
            if (!have_match) {
                matching_textView.setVisibility(View.VISIBLE);
            }
            askMatch();
        });


        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x113) {
                    System.out.println("在online diff 中处理消息了" + msg.obj);
                    String msgStr = (String) msg.obj;
                    if (msgStr.equals("match_fail")) {
                        have_match = false;
                        System.out.println("看看我执行了吗");
                    } else if (msgStr != null) {
                        String s1 = msgStr.split("-")[0];
                        String s2 = msgStr.split("-")[1];
                        if (s1.equals("match_success")) {
                            have_match = true;
                            matching_textView.setVisibility(View.INVISIBLE);

                            System.out.println("看看我执行了吗");
                            LoginActivity.user.matchName = s2;

                            Intent gameIntent = new Intent(OnlineDifficultyActivity.this, GameActivity.class);
                            gameIntent.putExtra("gameType", gameType);
                            gameIntent.putExtra("have_audio", have_audio);
                            gameIntent.putExtra("online", true);
                            //匹配成功的话
                            startActivity(gameIntent);
                        }
                    }

                }
            }
        };
    }

    private void askMatch() {
        have_match = false;
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