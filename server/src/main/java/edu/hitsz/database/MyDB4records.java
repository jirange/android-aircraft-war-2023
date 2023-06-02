package edu.hitsz.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import edu.hitsz.pojo.PlayerRecord;

public class MyDB4records {
    private static final String TAG = "MyDB4records";

    private static final String DB_NAME = "customer_serve.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "user_records_info";

    public static void main(String args[]){
//        createDataBase();
//        createTable();
//        createNewAccount(new PlayerRecord(1,1,"nihao",250,new Date()));
//        getAllUser(1);
    }

    private static void createDataBase() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Create database successfully");
    }

    private static void createTable(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + "r_difficulty INTEGER  NOT NULL,"
                    + "r_ranking INTEGER  NOT NULL,"
                    + "r_playerName VARCHAR PRIMARY KEY  NOT NULL,"
                    + "r_score INTEGER NOT NULL,"
                    + "r_recordTime VARCHAR NOT NULL"
                    + ");";

            stmt.executeUpdate(create_sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    public static void createNewAccount(ArrayList<PlayerRecord> recordsArray){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            for (PlayerRecord record : recordsArray) {

                ArrayList<PlayerRecord> tempArray = new ArrayList<>();
                // 如果存在同名记录，则更新记录
                if (record.getPlayerName() != null && record.getPlayerName().length() > 0) {
                    String condition = String.format("r_playerName='%s'", record.getPlayerName());
                    tempArray = query(condition);
                    if (tempArray.size() > 0) {
                        updateRecord(record, condition);
//                        result = tempArray.get(0).getRanking();
                        continue;
                    }
                }

                String sql = "INSERT INTO "+ TABLE_NAME +" (r_difficulty,r_ranking,r_playerName,r_score,r_recordTime) " +
                        "VALUES ('"+
                        record.getDifficulty() + "','" +
                        record.getRanking() + "','" +
                        record.getPlayerName() + "','" +
                        record.getScore() + "','" +
                        record.getRecordTimeStr() + "');";
                int result = stmt.executeUpdate(sql);
                if (result == -1) {
                    stmt.close();
                    c.close();
                    System.out.println(result);
                    return;
                }
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Account created successfully");
    }

    public static ArrayList<PlayerRecord> getAllUser(int diff){
//        Connection c = null;
//        Statement stmt = null;
//        String condition = " r_difficulty = "+diff+" ORDER BY r_score DESC ";
//        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
//        try {
//            Class.forName("org.sqlite.JDBC");
//            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
//            System.out.println("Opened database successfully");
//
//            String sql = String.format("select r_difficulty,r_ranking,r_playerName,r_score,r_recordTime" +
//                    " from %s where %s;", TABLE_NAME, condition);
//
//            stmt = c.createStatement();
//            ResultSet rs = stmt.executeQuery( sql);
//            while(rs.next()){
//                PlayerRecord playerRecord = new PlayerRecord(rs.getInt("r_difficulty"), rs.getInt("r_ranking"), rs.getString("r_playerName"), rs.getInt("r_score"), rs.getString("r_recordTime"));
//                recordsArray.add(playerRecord);
//            }
//            stmt.close();
//            c.close();
//        } catch ( Exception e ) {
//            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
//            System.exit(0);
//        }
//        System.out.println("Get all users successfully  by difficulty");
//        return recordsArray;
        String condition = " r_difficulty = "+diff+" ORDER BY r_score DESC ";
        System.out.println("Get all users successfully  by difficulty");
        return query(condition);
    }

    public static void updateRecord(PlayerRecord record, String condition){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            System.out.println(record);
            String sql = "UPDATE "+ TABLE_NAME +" SET r_difficulty = "+record.getDifficulty()+
                    " ,r_ranking = "+record.getRanking()+
                    " ,r_score = "+record.getScore()+
                    " ,r_recordTime = '"+record.getRecordTimeStr()+
                    "' WHERE "+condition+";";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Record update successfully");
    }

    public static void createNewAccount(PlayerRecord record) {
        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
        recordsArray.add(record);
        createNewAccount(recordsArray);
    }

    public static ArrayList<PlayerRecord> query(String condition){
        Connection c = null;
        Statement stmt = null;
        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
            System.out.println("Opened database successfully");

            String sql = String.format("select r_difficulty,r_ranking,r_playerName,r_score,r_recordTime" +
                    " from %s where %s;", TABLE_NAME, condition);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                PlayerRecord playerRecord = new PlayerRecord(rs.getInt("r_difficulty"), rs.getInt("r_ranking"), rs.getString("r_playerName"), rs.getInt("r_score"), rs.getString("r_recordTime"));
                recordsArray.add(playerRecord);
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("query records successfully  by condition");
        return recordsArray;
    }

    public static void delete(String condition){
        Connection c = null;
        Statement stmt = null;
        int i=-1;
        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:customer_serve.db");
            System.out.println("Opened database successfully");

            String sql = String.format("delete from "+TABLE_NAME+
                    "  where %s;", condition);

            stmt = c.createStatement();
            i = stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("delete records successfully  by condition "+i);
    }

    public static void deleteByName(String playerName) {
        String condition="r_playerName="+playerName;
        delete(condition);
    }

}