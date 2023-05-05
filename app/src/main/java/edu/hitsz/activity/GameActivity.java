package edu.hitsz.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.game.BaseGame;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.HardGame;
import edu.hitsz.game.MediumGame;
import edu.hitsz.leaderboards.RecordDaoImpl;
import edu.hitsz.music.MusicService;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    public static int difficulty = 1;

    private int gameType = 0;

    public static SoundPool mysp;
    public static HashMap<String, Integer> soundPoolMap;

    @Override
    protected void onDestroy() {
        System.out.println("de  de   de   de   de   de   de d e ");
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
                    //todo 游戏结束 打印排行榜  开启RecordsActivity
                    Intent recordsIntent = new Intent(GameActivity.this, RecordsActivity.class);
                    recordsIntent.putExtra("difficulty",difficulty);
                    recordsIntent.putExtra("score",(int)msg.obj);
                    startActivity(recordsIntent);

                    Toast.makeText(GameActivity.this, "GameOver", Toast.LENGTH_SHORT).show();

                }
            }
        };
        if (getIntent() != null) {
            gameType = getIntent().getIntExtra("gameType", 1);

        }
        BaseGame basGameView;
        if (gameType == 1) {
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

//        Intent musicIntent = new Intent(this, MusicService.class);
//        musicIntent.putExtra("action","play");
//        startService(musicIntent);

        //todo 创建SoundPool对象
        createSoundPool();
        soundPoolMap = new HashMap<String, Integer>();
        soundPoolMap.put("bgm", mysp.load(this, R.raw.bgm, 1));
        soundPoolMap.put("bullet_hit", mysp.load(this, R.raw.bullet_hit, 1));
        soundPoolMap.put("get_supply", mysp.load(this, R.raw.get_supply, 1));
        soundPoolMap.put("bomb_explosion", mysp.load(this, R.raw.bomb_explosion, 1));
        soundPoolMap.put("game_over", mysp.load(this, R.raw.game_over, 1));
        soundPoolMap.put("bgm_boss", mysp.load(this, R.raw.bgm_boss, 1));


    }


    @Override
    public void onBackPressed() {
        if (HeroAircraft.getHeroAircraft() != null) {
            HeroAircraft.setHeroAircraftNull();
        }
        super.onBackPressed();
    }

    public void showRecords() {
        //todo 游戏结束 打印排行榜
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> user = new HashMap<>();
        user.put("rank", "3");
        user.put("name", "iiighh");
        user.put("score", "80");
        user.put("date", "11-11");
        data.add(user);
        setContentView(R.layout.activity_leaderboards);
        //获得Layout里面的ListView
        ListView list = (ListView) findViewById(R.id.leaderboards_list);
        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(
                GameActivity.this,
                data,
                R.layout.listitem,
                new String[]{"rank", "name", "score", "date"},
                new int[]{R.id.rank, R.id.user, R.id.score, R.id.date});

        //添加并且显示
        list.setAdapter(listItemAdapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setMessage("确定删除？");
                builder.setTitle("提示？");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除数据
                        data.remove(position);
                        //重新对数据进行排序
//                        update();
                        //更新排行榜
                        listItemAdapter.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return false;
            }
        });
        //添加单击监听

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


//    //生成多维动态数组，并加入数据
//    private List<Map<String, Object>> getData() {
//        ArrayList<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("info", "http://www.baidu.com/");
//        listitem.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("info", "http://www.sina.com.cn/");
//        listitem.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("info", "http://www.qq.com/");
//        listitem.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("info", "http://www.163.com/");
//        listitem.add(map);
//
//        return listitem;
//    }



}