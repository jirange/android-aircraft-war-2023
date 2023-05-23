package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;

public class OnlineDifficultyActivity extends AppCompatActivity {

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
        Switch video_btn = (Switch) findViewById(R.id.video_btn);


//        medium_btn.setOnClickListener(view -> {
//            gameType=1;
//            intent.putExtra("gameType",gameType);
//            intent.putExtra("have_audio",have_audio);
//            startActivity(intent);
//        });
//
//        easy_btn.setOnClickListener(view -> {
//            gameType =2;
//            intent.putExtra("gameType",gameType);
//            intent.putExtra("have_audio",have_audio);
//            startActivity(intent);
//        });
//
//        hard_btn.setOnClickListener(view -> {
//            gameType =3;
//            intent.putExtra("gameType",gameType);
//            intent.putExtra("have_audio",have_audio);
//            startActivity(intent);
//
//        });

        video_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //音效开
                    System.out.println("音效开");
                    have_audio=true;
                }else{
                    //音效关闭
                    System.out.println("音效关闭");
                    have_audio=false;
                }
            }
        });

        if (isOnline){

        }else{

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}