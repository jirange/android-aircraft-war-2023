package edu.hitsz.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import edu.hitsz.pojo.PlayerRecord;
import edu.hitsz.pojo.User;

public class MyDB4login {
    public static void main(String args[]){
        createDataBase();
        createUser();
        createNewAccount("wowowo","123");
        getAllUser();
    }
    private static final String DB_NAME = "customer_serve.db";
    private static final String url="jdbc:sqlite:"+DB_NAME;

    private static final String TABLE_NAME = "user_accounts_info";

    private static void createDataBase() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Create database successfully");
    }

    private static void createUser(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE "+TABLE_NAME+" " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " NAME           TEXT    NOT NULL, " +
                    " PASSWORD        TEXT     NOT NULL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public static void createNewAccount(String name,String password){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO "+TABLE_NAME+" (NAME,PASSWORD) " +
                    "VALUES ('"+
                    name + "','" +
                    password + "');";

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Account created successfully");
    }
    public static void createNewAccount(User user){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO "+TABLE_NAME+" (NAME,PASSWORD) " +
                    "VALUES ('"+
                    user.getName() + "','" +
                    user.getPassword() + "');";

            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Account created successfully");
    }
    public static ArrayList<User> getAllUser(){
        Connection c = null;
        Statement stmt = null;
        ArrayList<User> users = new ArrayList<>();

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM "+TABLE_NAME+";" );
            while(rs.next()){
                System.out.println("name = " + rs.getString("name"));
                users.add(new User(rs.getString("name"), rs.getString("password")));
                System.out.println("password = " + rs.getString("password"));
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Get users successfully");
        return users;
    }

    public static ArrayList<User> query(String condition){
        Connection c = null;
        Statement stmt = null;
        ArrayList<User> users = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(url);
            System.out.println("Opened database successfully");

            String sql = String.format("select name,password" +
                    " from %s where %s;", TABLE_NAME, condition);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                users.add(new User(rs.getString("name"), rs.getString("password")));
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("query records successfully  by condition");
        return users;
    }
}