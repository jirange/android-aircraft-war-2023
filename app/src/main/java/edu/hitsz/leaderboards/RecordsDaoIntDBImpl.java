package edu.hitsz.leaderboards;


import static android.os.Looper.getMainLooper;

import java.util.List;

import android.os.Handler;
import android.os.Message;


import org.json.JSONException;
import org.json.JSONObject;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.activity.RecordsActivity;
import edu.hitsz.network.ClientThread;
import edu.hitsz.pojo.PlayerRecord;

public class RecordsDaoIntDBImpl implements RecordDao {
    private Handler handler;
    private ClientThread clientThread;

    List<PlayerRecord> records;
    int diff = GameActivity.difficulty;

    public RecordsDaoIntDBImpl(RecordsActivity recordsActivity) {
        this.diff = GameActivity.difficulty;
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x123) {
                    System.out.println(msg.obj);
                    records = (List<PlayerRecord>) msg.obj;
                    System.out.println(System.currentTimeMillis());
                    System.out.println(records);
                }
            }
        };

        clientThread = new ClientThread(handler);  //
        new Thread(clientThread).start();
        //getAll(diff)
    }


    @Override
    public void doAdd(PlayerRecord record) {
        JSONObject jsonObject = new JSONObject();
        JSONObject recordJson = new JSONObject();

        try {
            recordJson.put("difficulty",record.getDifficulty());
            recordJson.put("ranking",record.getRanking());
            recordJson.put("playerName",record.getPlayerName());
            recordJson.put("score",record.getScore());
            recordJson.put("recordTime",record.getRecordTimeStr());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            jsonObject.put("add", recordJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //这个传到clientThread后还要通过序列化传递  不可以直接通过json传吗

        Message msg = new Message();
        msg.what = 0x456;
        msg.obj = jsonObject;
        clientThread.toserverHandler.sendMessage(msg);//具体信息
        getAll(diff);

    }

    @Override
    public List<PlayerRecord> getAllRecords(int diff) {
        this.diff = diff;
        getAll(diff);

        while (true)
        {
            if (records != null)
                break;
        }
        System.out.println("我返回的records是："+records);
        return records;

    }

    private void getAll(int diff) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("getAll", diff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        msg.what = 0x456;
        msg.obj = jsonObject;
        while (true){
            if (clientThread.toserverHandler!=null){
                break;
            }
        }
        clientThread.toserverHandler.sendMessage(msg);//具体信息

    }

    @Override
    public void doDeleteByName(String playerName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deleteByName", playerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        msg.what = 0x456;
        msg.obj = jsonObject;
        clientThread.toserverHandler.sendMessage(msg);//具体信息
        getAll(diff);
    }

    @Override
    public void doDeleteByRanking(int ranking) {

    }

}
