package edu.hitsz.pojo;

import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private static final long serialVersionUID = 2893508510942871469L;


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", password='" + password + '\'' +
//                ", matchUser=" + matchUser +
                ", socket=" + socket +
                ", matchSocket=" + matchSocket +
                ", matchName='" + matchName + '\'' +
                '}';
    }

    private String name;
    private int id;
    private String password;
    public User matchUser;
    public Socket socket;
    public Socket matchSocket;
    public String matchName;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
