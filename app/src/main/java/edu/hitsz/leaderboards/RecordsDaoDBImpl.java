package edu.hitsz.leaderboards;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;


import java.util.List;
import edu.hitsz.leaderboards.database.RecordsDBHelper;
import edu.hitsz.pojo.PlayerRecord;

public class RecordsDaoDBImpl implements RecordDao{
    private RecordsDBHelper mHelper;
    private String TAG = "RecordsActivity";
    public RecordsDaoDBImpl(Context context){
        mHelper=RecordsDBHelper.getInstance(context,2);
    }

    @Override
    public void doAdd(PlayerRecord record) {
        mHelper.openWriteLink();
        long insert = mHelper.insert(record);
        Log.i(TAG,"add insert "+insert);
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
        mHelper.openWriteLink();
        String whereClause="r_playerName=?";
        String[] whereArgs={playerName};
        int delete = mHelper.delete(whereClause, whereArgs);
        Log.i(TAG,"delete"+delete+"条");
        mHelper.closeLink();

    }

    @Override
    public void doDeleteByRanking(int ranking) {
        mHelper.openWriteLink();
        String whereClause="r_ranking=?";
        String[] whereArgs={String.valueOf(ranking)};
        int delete = mHelper.delete(whereClause, whereArgs);
        Log.i(TAG,"delete"+delete+"条");
        mHelper.closeLink();
    }

}
