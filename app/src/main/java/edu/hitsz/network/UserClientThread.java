package edu.hitsz.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.pojo.User;

public class UserClientThread implements Runnable {
    public static String HOST = "192.168.56.1";//必须和服务器一样 不然就连不上去啊
    private static final int PORT = 8899;
    private Socket socket = null;
    public static UserClientThread clientThread;

    public static UserClientThread getClientThread(Handler myhandler) {
        if (clientThread==null){
            clientThread = new UserClientThread(myhandler);
        }
        clientThread.toclientHandler = myhandler;
        return clientThread;
    }



    public void setToclientHandler(Handler toclientHandler) {
        this.toclientHandler = toclientHandler;
    }

    private Handler toclientHandler;     // 向UI线程发送消息的Handler对象
    public Handler toserverHandler;  // 接收UI线程消息的Handler对象
    private BufferedReader in = null;
    private PrintWriter out = null;

    private static final String TAG = "UserClientThread";

    public UserClientThread(Handler myhandler) {
        this.toclientHandler = myhandler;
    }

    public void run() {
        try {
            socket = new Socket();
            //运行时修改成服务器的IP
            try {
                socket.connect(new InetSocketAddress(HOST, PORT));
            } catch (UnknownHostException e) {
                Log.i(TAG,"HOST 不正确");
                e.printStackTrace();
            }
            //初始化输入输出流
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8)), true);
            Log.i("UserClientThread", "in" + in + "@@" + out);


            //创建子线程，
            new Thread(() -> {
                String fromserver;
                try {
                    while ((fromserver = in.readLine()) != null) {
                        if (fromserver.endsWith("0")||fromserver.endsWith("1")) {
                            Message servermsg = new Message();
                            servermsg.what = 0x103;
                            servermsg.obj = fromserver;
                            toclientHandler.sendMessage(servermsg);
                            Log.i(TAG,"client get score :"+fromserver);

                        } else {
                            Message servermsg = new Message();
                            servermsg.what = 0x113;
                            servermsg.obj = fromserver;
                            toclientHandler.sendMessage(servermsg);
                            Log.i(TAG,"client get message :"+fromserver);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();


            Looper.prepare();  //在子线程中初始化一个Looper对象，即为当前线程创建消息队列
            toserverHandler = new Handler(Looper.myLooper()) {  //实例化Handler对象
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x111) {//传分数的
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("score", msg.obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        out.println(jsonObject.toString());  //将输出流包装为打印流  对象为键值对 or json
                        Log.i(TAG, "向数据库发送json score= " + jsonObject.toString());
                    } else if (msg.what == 0x456) {//456是操作类发给该类clientThread的，即需要传输的对象
                        try {
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            out.println(jsonObject.toString());  //将输出流包装为打印流  对象为键值对 or json
                            Log.i(TAG, "向数据库发送json = " + jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();  //启动Looper，运行刚才初始化的Looper对象，循环取消息队列的消息

        } catch (SocketTimeoutException el) {
            Log.i(TAG,"网络连接超时！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnect() {
        try {
            if (out != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("close", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                out.println(jsonObject.toString());
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
