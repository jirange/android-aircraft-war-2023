package edu.hitsz.server;

import edu.hitsz.database.MyDB4login;
import edu.hitsz.database.MyDB4records;
import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.pojo.User;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MyDbServer {
    public static final int PORT = 8899;//端口号
    private ServerSocket server = null;
    ObjectOutputStream out = null;
    List<User> waitMatch = new ArrayList<>();


    public static void main(String[] args) {
        new MyDbServer();
    }

    public MyDbServer() {
        //服务器创建流程如下
        try {
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:" + addr);

            //1.创建ServerSocket
            server = new ServerSocket(PORT);
            //创建线程池
            System.out.println("--服务器开启中--");

            while (true) {
                //2.等待接收请求   这里接收客户端的请求
                Socket client = server.accept();
                System.out.println("得到客户端连接：" + client);
                //初始化完成

                //执行线程
                new Thread(new Service(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Service implements Runnable {
        private Socket socket;
        private BufferedReader in = null;
        private String content = "";
        User user = null;
        PrintWriter poutsc = null;


        public Service(Socket clientsocket) {
            this.socket = clientsocket;
            System.out.println(socket);
            try {
                //3.接收请求后创建链接socket
                //4.通过InputStream  outputStream进行通信
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                poutsc = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void run() {
            try {
                while ((content = in.readLine()) != null) {
                    System.out.println("从客户端接收到的消息为：" + content);
                    parseJson(content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void parseJson(String content) {
            JSONObject jsonObject = JSONObject.fromObject(content);

            if (jsonObject != null) {
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    switch (key) {
                        case "close":
                            if (out != null) {
                                try {
                                    out.close();
                                    socket.close();
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "add":

                            JSONObject recordJson = jsonObject.getJSONObject("add");
                            PlayerRecord record = new PlayerRecord(
                                    recordJson.getInt("difficulty"),
                                    recordJson.getInt("ranking"),
                                    recordJson.getString("playerName"),
                                    recordJson.getInt("score"),
                                    new Date());

                            MyDB4records.createNewAccount(record);

                            ArrayList<PlayerRecord> allUser2 = MyDB4records.getAllUser(recordJson.getInt("difficulty"));
                            if (socket.isConnected()) {
                                try {
                                    out = new ObjectOutputStream(socket.getOutputStream());
                                    out.writeObject(allUser2);
                                    System.out.println("传给客户端 " + allUser2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "deleteByName":
                            String deleteName = jsonObject.getString("deleteByName");
                            MyDB4records.deleteByName(deleteName);
                            break;
                        case "getAll":
                            int diff = jsonObject.getInt("getAll");
                            ArrayList<PlayerRecord> allUser = MyDB4records.getAllUser(diff);
                            if (socket.isConnected()) {
                                try {
                                    out = new ObjectOutputStream(socket.getOutputStream());
                                    out.writeObject(allUser);
                                    System.out.println("传给客户端 " + allUser);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "login":
                            //todo 登陆操作
                            JSONObject login = jsonObject.getJSONObject("login");
                            String name = login.getString("name");
                            String password = login.getString("password");
                            user = new User();
                            user.setName(name);
                            user.setPassword(password);
                            System.out.println("数据库收到了 user" + user);
                            String condition = String.format(" NAME = '%s' AND  PASSWORD = '%s'", user.getName(), user.getPassword());
                            ArrayList<User> query = MyDB4login.query(condition);
                            System.out.println(query);
                            String noticeStr;
                            if (query == null || query.size() == 0) {
                                noticeStr = "login_failed";
                            } else {
                                noticeStr = "login_success";
                            }
                            if (socket.isConnected()) {
                                try {
                                    PrintWriter pout = null;
                                    pout = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                                    pout.println(noticeStr);  //将输出流包装为打印流

                                    System.out.println("传给客户端 " + noticeStr);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;

                        case "register":
                            //todo 注册
                            String registerStr;

                            JSONObject register = jsonObject.getJSONObject("register");
                            User user_register = new User(register.getString("name"), register.getString("password"));
                            System.out.println("数据库收到了 user" + user_register);
                            String condition_register = String.format(" NAME = '%s' ", user_register.getName());
                            ArrayList<User> query_user_register = MyDB4login.query(condition_register);

                            if (query_user_register == null || query_user_register.size() == 0) {
                                //todo 开始正常注册流程
                                MyDB4login.createNewAccount(user_register);
                                registerStr = "register_success";
                            } else {
                                //todo 之前注册过 应该提示用户名已存在 请换一个
                                registerStr = "register_failed";
                            }
                            if (socket.isConnected()) {
                                try {
                                    PrintWriter pout = null;
                                    pout = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                                    pout.println(registerStr);  //将输出流包装为打印流

                                    System.out.println("传给客户端 " + registerStr);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case "askMatch":
                            //todo 请求对战
                            //维护一个匹配清单
                            String difficulty = jsonObject.getString("askMatch");

                            // 每一次请求匹配都将其的原来的匹配对手清空
                            user.matchUser = null;
                            user.matchSocket = null;
                            if (waitMatch!=null&&waitMatch.contains(user)) {
                                waitMatch.remove(user);
                                System.out.println("重复匹配，要从等待中删除原来的请求");
                            }


                            user.socket = socket;
                            user.difficulty = Integer.valueOf(difficulty);
                            System.out.println("user" + user);
                            System.out.println("wait的都是谁啊" + waitMatch);
                            for (User waitMatchUser : waitMatch) {
                                if (user.difficulty == waitMatchUser.difficulty) {
                                    user.matchUser = waitMatchUser;
                                    waitMatchUser.matchUser = user;
                                    user.matchSocket = waitMatchUser.socket;
                                    waitMatchUser.matchSocket = socket;
                                    break;
                                }
                            }


                            System.out.println("匹配jie guo" + user + user.matchUser);
                            if (user.matchUser != null) {
                                waitMatch.remove(user.matchUser);
                                // 向客户端发送信号 表示匹配成功
                                PrintWriter pout = null;
                                PrintWriter pout2 = null;
                                try {
                                    pout = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                                    pout.println("match_success-" + user.matchUser.getName());  //将输出流包装为打印流
                                    pout2 = new PrintWriter(new BufferedWriter(
                                            new OutputStreamWriter(user.matchSocket.getOutputStream(), "UTF-8")), true);
                                    pout2.println("match_success-" + user.getName());  //将输出流包装为打印流
                                    System.out.println("传给客户端 " + "match_success");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // 清空score
                                user.matchScore = 0;
                                user.score = 0;
                                user.matchUser.score = 0;
                                user.matchUser.matchScore = 0;


                            } else {
                                waitMatch.add(user);
                            }

                            break;


                        case "score":
                            //  若匹配成功，
                            if (user.matchSocket != null) {
                                // 每隔一段时间各个客户端向服务器发送自己的得分，服务器将得分同步给另一个客户端。
                                int temp_score = jsonObject.getInt("score");
                                user.score = temp_score;
                                if (temp_score < 0) {
                                    System.out.println("该用户的游戏结束了" + user);
                                    if (temp_score == -1) {
                                        temp_score = 0;
                                    }
                                    PlayerRecord playerRecord = new PlayerRecord(user.difficulty, user.getName(), -temp_score, new Date());
                                    MyDB4records.createNewAccount(playerRecord);
                                    user.matchScore = user.matchUser.score;
                                    poutsc.println(user.matchScore);  //将输出流包装为打印流
                                    System.out.println("传给客户端 " + user.matchScore);
                                    try {
                                        PrintWriter poutsc2 = new PrintWriter(new BufferedWriter(
                                                new OutputStreamWriter(user.matchSocket.getOutputStream(), "UTF-8")), true);
                                        poutsc2.println(user.score);  //将输出流包装为打印流
                                        System.out.println("传给对方客户端 " + user.score);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //当我死了之后我得告诉我死了的对手我多少分啊
                                } else {
                                    user.matchScore = user.matchUser.score;
                                    poutsc.println(user.matchScore);  //将输出流包装为打印流
                                    System.out.println("传给客户端 " + user.matchScore);
                                }


                            }

                            break;
                    }
                }

            }
        }


    }


}
