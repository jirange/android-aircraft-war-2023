package edu.hitsz.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import edu.hitsz.R;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;
import edu.hitsz.music.MusicService;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    public static  int difficulty = 1;

    private int gameType=0;

    public static SoundPool mysp;
    public static HashMap<String, Integer> soundPoolMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG,"handleMessage");
                if (msg.what == 1) {
                    Toast.makeText(GameActivity.this,"GameOver",Toast.LENGTH_SHORT).show();
                }
            }
        };
        if(getIntent() != null){
            gameType = getIntent().getIntExtra("gameType",1);

        }
        BaseGame basGameView;
        if(gameType == 1){
            basGameView = new MediumGame(this,handler);
            difficulty=2;

        }else if(gameType == 3){
            basGameView = new HardGame(this,handler);
            difficulty=3;
        }else{
            basGameView = new EasyGame(this,handler);
            difficulty=1;
        }
        setContentView(basGameView);

//        Intent musicIntent = new Intent(this, MusicService.class);
//        musicIntent.putExtra("action","play");
//        startService(musicIntent);

        //todo 创建SoundPool对象
        createSoundPool();
        soundPoolMap = new HashMap<String ,Integer>();
        soundPoolMap.put("bgm",mysp.load(this, R.raw.bgm,1));
        soundPoolMap.put("bullet_hit",mysp.load(this, R.raw.bullet_hit,1));
        soundPoolMap.put("get_supply",mysp.load(this, R.raw.get_supply,1));
        soundPoolMap.put("bomb_explosion",mysp.load(this, R.raw.bomb_explosion,1));
        soundPoolMap.put("game_over",mysp.load(this, R.raw.game_over,1));
        soundPoolMap.put("bgm_boss",mysp.load(this, R.raw.bgm_boss,1));


    }


    @Override
    public void onBackPressed() {
        System.out.println("nishuone   nishuone   nishuone ");
        if (HeroAircraft.getHeroAircraft()!=null){
            HeroAircraft.setHeroAircraftNull();
        }
        super.onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createSoundPool() {
        if (mysp == null) {
            // Android 5.0 及 之后版本
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes;
                audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mysp = new SoundPool.Builder()
                        .setMaxStreams(10)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else { //Android 5.0 以前版本
                mysp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);  // 创建SoundPool
            }
        }
    }
}