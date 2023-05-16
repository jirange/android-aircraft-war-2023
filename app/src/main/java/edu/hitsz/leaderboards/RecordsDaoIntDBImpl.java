package edu.hitsz.leaderboards;


import static android.os.Looper.getMainLooper;

import java.util.List;

import android.os.Handler;
import android.os.Message;


import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import edu.hitsz.activity.GameActivity;
import edu.hitsz.network.ClientThread;
import edu.hitsz.pojo.PlayerRecord;

public class RecordsDaoIntDBImpl implements RecordDao {
    private Handler handler;
    private ClientThread clientThread;

    List<PlayerRecord> records;
    int diff = GameActivity.difficulty;

    public RecordsDaoIntDBImpl() {
        this.diff = GameActivity.difficulty;
        getAll(diff);
    }


    @Override
    public void doAdd(PlayerRecord record) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("add", record);
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
        return records;
    }

    private void getAll(int diff) {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //如果消息来自于子线程  clientThread  即数据库操作的返回值
                if (msg.what == 0x123) {
                    System.out.println(msg.obj);
                    records = (List<PlayerRecord>) msg.obj;

//                    JSONObject jsonObject = JSONObject.fromObject(msg.obj);
//                    if (jsonObject!=null){
//                        records = (List<PlayerRecord>) jsonObject.get("getAll");
//                        System.out.println(records);
//                    }

                }
            }
        };

        clientThread = new ClientThread(handler);  //
        new Thread(clientThread).start();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("getAll", diff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Message msg = new Message();
        msg.what = 0x456;
        msg.obj = jsonObject;
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
