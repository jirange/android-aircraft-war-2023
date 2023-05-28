package edu.hitsz.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;
import edu.hitsz.music.MusicService;
import edu.hitsz.music.MySoundPool;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    public static int difficulty = 1;

    private int gameType = 0;
    private boolean haveAudio = false;
    public static boolean online = false;
    Intent musicIntent;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage");
                if (msg.what == 1) {
                    //todo 关闭音乐bgm
                    if (haveAudio) {
                        stopService(musicIntent);
                    }

                    Toast.makeText(GameActivity.this, "GameOver", Toast.LENGTH_SHORT).show();//原来在下面 地方了
                    if (!online){
                        //todo 游戏结束 打印排行榜  开启RecordsActivity
                        Intent recordsIntent;
                        recordsIntent = new Intent(GameActivity.this, RecordsActivity.class);
                        recordsIntent.putExtra("difficulty", difficulty);
                        recordsIntent.putExtra("score", (int) msg.obj);
                        startActivity(recordsIntent);
                    }else {
                        //todo 等待对方也死去之后  才能开排行榜
                        //todo 游戏结束 打印排行榜  开启RecordsActivity
                        Intent recordsIntent;
                        recordsIntent = new Intent(GameActivity.this, RecordsActivity.class);
                        recordsIntent.putExtra("difficulty", difficulty);
                        recordsIntent.putExtra("score", (int) msg.obj);
                        startActivity(recordsIntent);
                    }

                }
            }
        };
        if (getIntent() != null) {
            gameType = getIntent().getIntExtra("gameType", 1);
            haveAudio = getIntent().getBooleanExtra("have_audio", false);
            online = getIntent().getBooleanExtra("online",false);
        }
        BaseGame basGameView;

        if (gameType == 2) {
            basGameView = new MediumGame(this, handler);
            difficulty = 2;

        } else if (gameType == 3) {
            basGameView = new HardGame(this, handler);
            difficulty = 3;
        } else {
            basGameView = new EasyGame(this, handler);
            difficulty = 1;
        }

        setContentView(basGameView);
        if (haveAudio) {
            musicIntent = new Intent(this, MusicService.class);
            musicIntent.putExtra("action", "play");
            System.out.println("我执行了吗"+haveAudio);
            startService(musicIntent);

            MySoundPool.initialSoundPoolMap(this);
            MySoundPool.haveAudio = true;
        } else {
            MySoundPool.haveAudio = false;
        }

    }


    @Override
    public void onBackPressed() {
        if (HeroAircraft.getHeroAircraft() != null) {
            HeroAircraft.setHeroAircraftNull();
        }
        super.onBackPressed();
    }


}