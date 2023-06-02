package edu.hitsz.leaderboards;


import static android.os.Looper.getMainLooper;

import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.activity.RecordsActivity;
import edu.hitsz.network.ClientThread;
import edu.hitsz.pojo.PlayerRecord;

public class RecordsDaoIntDBImpl implements RecordDao {
    private Handler handler;
    private String TAG="RecordsDaoIntDBImpl";
    private ClientThread clientThread;

    public static void setRecords(List<PlayerRecord> recordsa) {
        records = recordsa;
    }

    static List<PlayerRecord>  records;
//    int diff = GameActivity.difficulty;

    public RecordsDaoIntDBImpl(RecordsActivity recordsActivity) {
//        this.diff = GameActivity.difficulty;
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x123) {
                    records = (List<PlayerRecord>) msg.obj;
                    Log.i(TAG,"get records: "+records);
                }
            }
        };

        clientThread = new ClientThread(handler);  //
        new Thread(clientThread).start();
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
//        getAll(GameActivity.difficulty);
    }

    @Override
    public List<PlayerRecord> getAllRecords(int diff) {
        getAll(diff);
        Log.i(TAG,"getAllRecords return records"+records);
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
        new Thread(()->{
            while (true){
                if (clientThread.toserverHandler!=null){
                    break;
                }
            }
            clientThread.toserverHandler.sendMessage(msg);//具体信息
        }).start();


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
//        getAll(diff);
    }

    @Override
    public void doDeleteByRanking(int ranking) {

    }

}
