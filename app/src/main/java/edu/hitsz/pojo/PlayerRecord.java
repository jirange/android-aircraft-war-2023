package edu.hitsz.pojo;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 玩家的游戏记录
 * @author leng
 */
public class PlayerRecord implements Serializable {
    private static final long serialVersionUID =-2740839109752637650L;
    /**
     * 游戏难度
     */
    private int difficulty;

    /**
     * 玩家名次
     */
    private int ranking;

    /**
     * 玩家名
     */
    private String playerName;


    /**
     * 玩家得分
     */
    private int score;

    /**
     * 记录时间:Date
     */
    private Date recordTime;


    public PlayerRecord() {
    }



    public PlayerRecord(int difficulty, int ranking, String playerName, int score, Date recordTime) {
        this.difficulty = difficulty;
        this.ranking = ranking;
        this.playerName = playerName;
        this.score = score;
        this.recordTime = recordTime;
    }

    public PlayerRecord(int difficulty,String playerName, int score, Date recordTime) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.score = score;
        this.recordTime = recordTime;
    }

    public PlayerRecord(int difficulty,int score, Date recordTime) {
        this.difficulty = difficulty;
        this.score = score;
        this.recordTime = recordTime;
    }

    /**
     * 获取
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * 获取 难度 字符串
     * @return difficulty str
     */
    public static String getDifficultyStr(int difficulty) {
        switch (difficulty){
            case 1:return "EASY";
            case 2:return "Medium";
            case 3:return "Hard";
        }
        return "";
    }


    /**
     * 设置
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 获取
     * @return ranking
     */
    public int getRanking() {
        return ranking;
    }

    /**
     * 设置
     */
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    /**
     * 获取
     * @return playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * 设置
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * 获取
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * 设置
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 获取
     * @return recordTime
     */
    public Date getRecordTime() {
        return recordTime;
    }
    /**
     * 获取  字符串
     * @return recordTime  string
     */
    public String getRecordTimeStr() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(recordTime);
    }
    /**
     * 设置
     */
    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }


    public void setRecordTime(String recordTime) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        try {
            this.recordTime = format.parse(recordTime);
        } catch (ParseException e) {
            System.out.println("字符串解析出现错误！！！");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PlayerRecord{" +
                "difficulty=" + difficulty +
                ", ranking=" + ranking +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                ", recordTime=" + getRecordTimeStr() +
                '}';
    }

}
