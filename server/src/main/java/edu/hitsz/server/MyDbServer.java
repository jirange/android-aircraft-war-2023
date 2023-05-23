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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MyDbServer {
    public static final int PORT = 8899;//端口号
    private ServerSocket server = null;
    ObjectOutputStream out = null;
    BufferedWriter bw = null;
    List<User> users = new ArrayList<>();
    List<User> waitMatch = new ArrayList<>();
    List<User> matchers = new ArrayList<>();


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
                //一下就是服务器与数据库连接的方式
                //这是登录名和密码的登录逻辑
                //还要添加排行榜数据

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

        public Service(Socket clientsocket) {
            this.socket = clientsocket;
            try {
                //3.接收请求后创建链接socket
                //4.通过InputStream  outputStream进行通信
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
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
//                    out.close();
                }
//                socket.close();
                //in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void parseJson(String content) {
            JSONObject jsonObject = JSONObject.fromObject(content);
            User user = null;

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
                            PlayerRecord record = (PlayerRecord) jsonObject.get("add");
                            MyDB4records.createNewAccount(record);
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
//                            String login = (String) jsonObject.get("login");
//                            user = (User) JSONObject.toBean(login, User.class);
//                            parseJson(login)
//                            String u_name= login.split("-")[0];
//                            String u_password = login.split("-")[0];
                            System.out.println("数据库收到了 user"+user);
                            users.add(user);
                            //todo user.socket = socket;
//                            String condition = "  NAME = '" + "nmy" +"' AND " +" PASSWORD = '" + "123456"+"'";
                            String condition = String.format(" NAME = '%s' AND  PASSWORD = '%s'",user.getName(),user.getPassword());
//                            String condition = " name = " + user.getName() + " password = " + user.getPassword();
                            ArrayList<User> query = MyDB4login.query(condition);
                            System.out.println(query);
                            String noticeStr;
                            if (query == null || query.size() == 0) {
                                noticeStr="login_failed";
                                //todo 提示用户名和密码错误
                            }else{
                                noticeStr="login_success";
                            }
//todo 可不可以这样呢  不用序列化了  用个新流，比如正常的字符输入输出流
                            if (socket.isConnected()) {
                                try {
//                                    out = new ObjectOutputStream(socket.getOutputStream());
//                                    out.writeObject(noticeStr);

                                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bw.write(noticeStr);
                                    bw.newLine();

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
                            User user_register = new User(register.getString("name"),register.getString("password"));
                            System.out.println("数据库收到了 user"+user_register);
                            String condition_register = String.format(" NAME = '%s' ",user_register.getName());
                            ArrayList<User> query_user_register = MyDB4login.query(condition_register);

                            if (query_user_register == null || query_user_register.size() == 0) {
                                //todo 开始正常注册流程
                                System.out.println("查无此人");
                                MyDB4login.createNewAccount(user_register);
                                registerStr = "register_success";
                                users.add(user_register);
                            } else {
                                System.out.println("查有此人");

                                //todo 之前注册过 应该提示用户名已存在 请换一个
                                registerStr = "register_failed";
                            }
                            if (socket.isConnected()) {
                                try {
//                                    out = new ObjectOutputStream(socket.getOutputStream());
//                                    out.writeObject(registerStr);

                                    bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                                    bw.write(registerStr);
                                    bw.newLine();


                                    System.out.println("传给客户端 " + registerStr);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;
                        case "askMatch":
                            //todo 请求对战
                            //维护一个匹配清单
                            if (user.matchSocket == null) waitMatch.add(user);//这句话应该在login里面  ！！！
                            for (User waitMatch : waitMatch) {
                                if (waitMatch != user) ;
                                user.matchUser = waitMatch;
                                waitMatch.matchUser = user;
                                user.matchSocket = waitMatch.socket;
                                waitMatch.socket = user.socket;
                            }
                            if (user.matchSocket != null) {
                                waitMatch.remove(user);
                                waitMatch.remove(user.matchUser);
                            }

                            //选择对战则需等待对手加入
                            //• 实现联网对战同步对手得分
                            //• 得分排行榜界面实现（联网）


                            //两个客户端和服务器建立连接
                            //后，若匹配成功，每隔一段时
                            //间各个客户端向服务器发送自
                            //己的得分，服务器将得分同步
                            //给另一个客户端。
                            //todo 进行匹配
                            break;
                    }
                }

            }
        }


    }


}
