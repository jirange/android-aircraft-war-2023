package edu.hitsz.pojo;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 2893508510942871469L;


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    private String name;
    private int id;
    private String password;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
