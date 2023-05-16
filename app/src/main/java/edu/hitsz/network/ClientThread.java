package edu.hitsz.network;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

import edu.hitsz.pojo.PlayerRecord;


public class ClientThread implements Runnable {
    private static final String HOST = "10.250.230.230";//必须和服务器一样 不然就连不上去啊
    private static final int PORT = 8899;
    private Socket socket = null;
    private Handler toclientHandler;     // 向UI线程发送消息的Handler对象
    public Handler toserverHandler;  // 接收UI线程消息的Handler对象
    //    private BufferedReader in = null;
    private ObjectInputStream in = null;
    private PrintWriter out = null;

    private PrintWriter writer;
    private static final String TAG = "ClientThread";

    public ClientThread(Handler myhandler) {
        System.out.println("ooeeppp");
        this.toclientHandler = myhandler;
    }

    public void run() {
        try {
//            socket = new Socket(HOST, PORT);  //建立连接到远程服务器的Socket
            socket = new Socket();
            //运行时修改成服务器的IP
            socket.connect(new InetSocketAddress(HOST, PORT));
            System.out.println("ooooooooooooooooooooo");
            //初始化输入输出流
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
//            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("nnnnnnnnnnnnnn");

            System.out.println("ppppppppppppppppppppppppppppp");

            //创建子线程，
            new Thread() {
                @Override
                public void run() {
                    String fromserver = null;
                    try {
                        in = new ObjectInputStream(socket.getInputStream());
                        while (in != null) {
//                            in = new ObjectInputStream(socket.getInputStream());
                            List<PlayerRecord> playerRecordList = null;
                            try {
                                playerRecordList = (List<PlayerRecord>) in.readObject();
                            } catch (ClassNotFoundException e) {
                                System.out.println("类找不到了！！！");
                                e.printStackTrace();
                            }
                            Message servermsg = new Message();
                            servermsg.what = 0x123;
                            servermsg.obj = playerRecordList;//服务器返回的东西  操作的返回值
                            toclientHandler.sendMessage(servermsg);//将其返回给操作类
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuu");

            Looper.prepare();  //在子线程中初始化一个Looper对象，即为当前线程创建消息队列
            System.out.println("rrrrrrrrrrrrrrrrrrr" + toserverHandler);
            toserverHandler = new Handler(Looper.myLooper()) {  //实例化Handler对象
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x456) {//456是操作类发给该类clientThread的，即需要传输的对象
                        try {
                            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                                    socket.getOutputStream(), "UTF-8")), true);
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            out.println(jsonObject.toString());  //将输出流包装为打印流  对象为键值对 or json
                            Log.i(TAG, "json = " + jsonObject.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();  //启动Looper，运行刚才初始化的Looper对象，循环取消息队列的消息
        } catch (SocketTimeoutException el) {
            System.out.println("网络连接超时！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnect() {
        try {
            if (out != null) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("close",1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                out.println(jsonObject.toString());
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
