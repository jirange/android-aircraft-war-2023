package edu.hitsz.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.hitsz.R;
import edu.hitsz.leaderboards.RecordDao;
import edu.hitsz.leaderboards.RecordsDaoIntDBImpl;
import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.leaderboards.RecordsDaoDBImpl;

public class RecordsActivity extends AppCompatActivity {
    private static final String TAG = "RecordsActivity";
    private int difficulty = 0;
    private int score = 0;
    RecordDao recordsDaoDB;
    ArrayList<HashMap<String, String>> data;
    SimpleAdapter listItemAdapter;
    //Layout里面的ListView
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getIntent() != null) {
            difficulty = getIntent().getIntExtra("difficulty", 1);
            score = getIntent().getIntExtra("score", 0);
        }
        if (GameActivity.online) {
            recordsDaoDB = new RecordsDaoIntDBImpl(RecordsActivity.this);
        } else {
            recordsDaoDB = new RecordsDaoDBImpl(RecordsActivity.this);

        }

        data = getData();

        // 游戏结束 打印排行榜
        setContentView(R.layout.activity_leaderboards);

        //设置返回主界面键
        Button return_btn = findViewById(R.id.return_btn);

        return_btn.setOnClickListener(view -> {
            Intent intent = new Intent(RecordsActivity.this, StartPageActivity.class);
            startActivity(intent);
        });


        //获得Layout里面的TextView 设置标题 显示难度
        TextView titleView = findViewById(R.id.title);
        titleView.setText(getString(R.string.difficultyChoice, PlayerRecord.getDifficultyStr(difficulty)));


        if (GameActivity.online) {
            // 询问是否加入记录
            onlineAddRecord(score);
        } else {
            update();
            // 询问是否加入记录
            addRecordsAfterGame(score);
            update();

            // 添加长按删除
            deleteRecords();
        }
    }

    public void deleteRecords() {
        list.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(RecordsActivity.this);
            builder.setMessage("确定删除？");
            builder.setTitle("提示？");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                //删除数据
                String deleteName = data.get(position).get("name");
                data.remove(position);
                //重新对数据进行排序
                recordsDaoDB.doDeleteByName(deleteName);
                data = getData();        //重新获取数据
                //更新排行榜
                listItemAdapter.notifyDataSetChanged();
                update();
            });
            builder.setNegativeButton("No", null);
            builder.create().show();
            return false;
        });
    }

    public void update() {
        //获得Layout里面的ListView
        list = findViewById(R.id.leaderboards_list);
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(
                RecordsActivity.this,
                getData(),
                R.layout.listitem,
                new String[]{"rank", "name", "score", "date"},
                new int[]{R.id.rank, R.id.user, R.id.score, R.id.date});
        //添加并且显示
        list.setAdapter(listItemAdapter);

    }


    private ArrayList<HashMap<String, String>> getData() {
        if (GameActivity.online) {
            recordsDaoDB = new RecordsDaoIntDBImpl(RecordsActivity.this);
        }
        //装载数据到data
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        List<PlayerRecord> allRecords = recordsDaoDB.getAllRecords(difficulty);
        if (allRecords != null && allRecords.size() != 0) {
            int i = 0;
            for (PlayerRecord record : allRecords) {
                i++;
                HashMap<String, String> map = new HashMap<>();
                map.put("rank", String.valueOf(i));
                String playerName = record.getPlayerName();
                if (playerName.equals(LoginActivity.user.getName())){
                    map.put("name", record.getPlayerName()+"(自己)");
                }else if (playerName.equals(LoginActivity.user.matchName)){
                    map.put("name", record.getPlayerName()+"(对手)");
                }else {
                    map.put("name", record.getPlayerName());
                }
                map.put("score", String.valueOf(record.getScore()));
                map.put("date", record.getRecordTimeStr());
                data.add(map);
            }
            Log.i(TAG,"uploading data:"+ allRecords);
        } else {
            Log.i(TAG," no data can upload");
        }
        return data;
    }


    public void addRecordsAfterGame(int score) {
        final EditText edt = new EditText(this);
        edt.setMinLines(3);
        new AlertDialog.Builder(this)
                .setTitle("请输入你的姓名 不可重复")
                .setMessage("确定将此条游戏记录加入排行榜？")
                .setView(edt)
                .setPositiveButton("YES", (arg0, arg1) -> {
                    String input_name = edt.getText().toString();
                    PlayerRecord record = new PlayerRecord(difficulty, input_name, score, new Date());
                    //加入数据
                    recordsDaoDB.doAdd(record);
                    //重新对数据进行排序
                    data = getData();        //重新获取数据
                    //更新排行榜
                    update();
                })
                .setNegativeButton("NO", null)
                .show();
    }

    public void onlineAddRecord(int score) {
        String input_name = LoginActivity.user.getName();
        PlayerRecord record = new PlayerRecord(difficulty, input_name, score, new Date());
        //加入数据
        recordsDaoDB.doAdd(record);
        //重新对数据进行排序
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        data = getData();        //重新获取数据
        //更新排行榜
        update();
    }

}
