package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;

public class StartPageActivity extends AppCompatActivity {

    private static final String TAG = "StartPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Button start_btn = findViewById(R.id.start_btn);
        Button login_btn = findViewById(R.id.login_btn);


        start_btn.setOnClickListener(view -> {
//            intent.putExtra("gameType",gameType);
            Intent modelIntent = new Intent(StartPageActivity.this, ModelActivity.class);
            startActivity(modelIntent);
        });

        login_btn.setOnClickListener(view -> {
            //todo 转到登录界面
            Intent loginIntent = new Intent(StartPageActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}