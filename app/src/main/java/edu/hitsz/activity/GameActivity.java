package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    public static  int difficulty = 1;

    private int gameType=0;

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

//        Intent intent = new Intent(this, MusicService.class);
//        intent.putExtra("action","play");
//        startService(intent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}