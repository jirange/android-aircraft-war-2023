package edu.hitsz.leaderboards;

import android.content.Context;


import java.util.List;
import edu.hitsz.leaderboards.database.RecordsDBHelper;

public class RecordsDaoDBImpl implements RecordDao{
    private RecordsDBHelper mHelper;
    public RecordsDaoDBImpl(Context context){
        mHelper=RecordsDBHelper.getInstance(context,2);
    }

    @Override
    public void doAdd(PlayerRecord record) {
        mHelper.openWriteLink();
        long insert = mHelper.insert(record);
        System.out.println("add insert "+insert);
        mHelper.closeLink();
    }

    @Override
    public List<PlayerRecord> getAllRecords(int diff) {
        mHelper.openReadLink();
        String sql = "r_difficulty="+diff+" ORDER BY r_score DESC";
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
