package edu.hitsz.leaderboards;

import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.hitsz.activity.RecordsActivity;
import edu.hitsz.leaderboards.database.RecordsDBHelper;

public class RecordsDaoDBImpl implements RecordDao{
    private RecordsDBHelper mHelper;
    public RecordsDaoDBImpl(Context context){
        mHelper=RecordsDBHelper.getInstance(context,2);
    }

    int diff;
    @Override
    public void doAdd(PlayerRecord record) {
        mHelper.openWriteLink();
        long insert = mHelper.insert(record);
        System.out.println("add insert "+insert);
        mHelper.closeLink();
    }
//    private void updateDatas() {
//        List<PlayerRecord> playerRecords = getAllRecords(diff);
//        playerRecords = playerRecords.stream().sorted((r1, r2) -> r2.getScore() - r1.getScore()).collect(Collectors.toList());
//        List<PlayerRecord> finalPlayerRecords = playerRecords;
//        playerRecords.forEach(record -> record.setRanking(finalPlayerRecords.indexOf(record) + 1));
//        System.out.println(playerRecords);
//        System.out.println(finalPlayerRecords);
//    }
    @Override
    public List<PlayerRecord> getAllRecords(int diff) {
        mHelper.openReadLink();
        String sql = "r_difficulty="+diff+" ORDER BY r_score";
        List<PlayerRecord> query = mHelper.query(sql);
        mHelper.closeLink();
        return query;
    }

    @Override
    public void doDeleteByName(String playerName) {
        System.out.println(getAllRecords(1));
        mHelper.openWriteLink();
        String whereClause="r_playerName=?";
        String[] whereArgs={playerName};
        int delete = mHelper.delete(whereClause, whereArgs);
        System.out.println("delete"+delete+"条");
        mHelper.closeLink();
        System.out.println(getAllRecords(1));

    }

    @Override
    public void doDeleteByRanking(int ranking) {
        mHelper.openWriteLink();
        String whereClause="r_ranking=?";
        String[] whereArgs={String.valueOf(ranking)};
        int delete = mHelper.delete(whereClause, whereArgs);
        System.out.println("delete"+delete+"条");
        mHelper.closeLink();
    }

}
