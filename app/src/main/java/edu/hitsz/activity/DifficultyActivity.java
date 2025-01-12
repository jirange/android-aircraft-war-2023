package edu.hitsz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;

public class DifficultyActivity extends AppCompatActivity {

    private static final String TAG = "DifficultyActivity";

    private int gameType=0;
    private boolean isOnline;
    private boolean have_audio=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_choice);
        Button medium_btn = findViewById(R.id.medium_btn);
        Button easy_btn = findViewById(R.id.easy_btn);
        Button hard_btn = findViewById(R.id.hard_btn);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch video_btn = findViewById(R.id.video_btn);


        Intent intent = new Intent(DifficultyActivity.this, GameActivity.class);

        easy_btn.setOnClickListener(view -> {
            gameType =1;
            intent.putExtra("gameType",gameType);
            intent.putExtra("have_audio",have_audio);
            startActivity(intent);
        });

        medium_btn.setOnClickListener(view -> {
            gameType=2;
            intent.putExtra("gameType",gameType);
            intent.putExtra("have_audio",have_audio);
            startActivity(intent);
        });

        hard_btn.setOnClickListener(view -> {
            gameType =3;
            intent.putExtra("gameType",gameType);
            intent.putExtra("have_audio",have_audio);
            startActivity(intent);

        });

        video_btn.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){
                //音效开
                Log.i(TAG,"音效开");
                have_audio=true;
            }else{
                //音效关闭
                Log.i(TAG,"音效关");
                have_audio=false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}