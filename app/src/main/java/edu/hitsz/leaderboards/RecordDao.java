package edu.hitsz.leaderboards;

import java.util.List;

/**
 * @author leng
 */
public interface RecordDao {
    /**
     * 新增玩家数据记录
     * @param record :玩家记录
     */
    void doAdd(PlayerRecord record);

    /**
     * 获取所有玩家数据记录
     * @return 玩家记录列表
     */
    List<PlayerRecord> getAllRecords();

    /**
     * 按照玩家姓名删除玩家记录
     * @param playerName 玩家姓名
     */
    void doDeleteByName(String playerName);

    /**
     * 按照玩家排名删除玩家数据
     * @param ranking 玩家排名
     */
    void doDeleteByRanking(int ranking);

    /**
     * 打印所有记录数据
     */
    void printAll();


}
