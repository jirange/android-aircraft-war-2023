package edu.hitsz.leaderboards.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.hitsz.pojo.PlayerRecord;

public class RecordsDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "RecordsDBHelper";
    private static final String DB_NAME = "customerserve.db";
    private static final int DB_VERSION = 1;
    private static RecordsDBHelper mHelper = null;
    private SQLiteDatabase mDB = null;
    private static final String TABLE_NAME = "user_records_info";

    private RecordsDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private RecordsDBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    public static RecordsDBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new RecordsDBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new RecordsDBHelper(context);
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mDB == null || mDB.isOpen() != true) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    public void closeLink() {
        if (mDB != null && mDB.isOpen() == true) {
            mDB.close();
            mDB = null;
        }
    }

    public String getDBName() {
        if (mHelper != null) {
            return mHelper.getDatabaseName();
        } else {
            return DB_NAME;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String drop_sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        Log.d(TAG, "drop_sql:" + drop_sql);
        db.execSQL(drop_sql);
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "r_difficulty INTEGER  NOT NULL,"
                + "r_ranking INTEGER  NOT NULL,"
                + "r_playerName VARCHAR PRIMARY KEY  NOT NULL,"
                + "r_score INTEGER NOT NULL,"
                + "r_recordTime VARCHAR NOT NULL"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public int delete(String whereClause, String[] whereArgs) {
        int count = mDB.delete(TABLE_NAME, whereClause, whereArgs);
        return count;
    }

    public int deleteAll() {
        int count = mDB.delete(TABLE_NAME, "1=1", null);
        return count;
    }

    public long insert(PlayerRecord record) {
        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
        recordsArray.add(record);
        return insert(recordsArray);
    }

    public long insert(ArrayList<PlayerRecord> recordsArray) {
        long result = -1;
        for (int i = 0; i < recordsArray.size(); i++) {
            PlayerRecord record = recordsArray.get(i);
            ArrayList<PlayerRecord> tempArray = new ArrayList<>();
            // 如果存在同名记录，则更新记录
            if (record.getPlayerName() != null && record.getPlayerName().length() > 0) {
                String condition = String.format("r_playerName='%s'", record.getPlayerName());
                tempArray = query(condition);
                if (tempArray.size() > 0) {
                    update(record, condition);
                    result = tempArray.get(0).getRanking();
                    continue;
                }
            }
            // 不存在唯一性重复的记录，则插入新记录
            ContentValues cv = new ContentValues();
            cv.put("r_difficulty", record.getDifficulty());
            cv.put("r_ranking", record.getRanking());
            cv.put("r_playerName", record.getPlayerName());
            cv.put("r_score", record.getScore());
            cv.put("r_recordTime",record.getRecordTimeStr());
            result = mDB.insert(TABLE_NAME, "", cv);
            // 添加成功后返回行号，失败后返回-1
            if (result == -1) {
                return result;
            }
        }
        return result;
    }

    public int update(PlayerRecord record, String condition) {
        ContentValues cv = new ContentValues();
        cv.put("r_difficulty", record.getDifficulty());
        cv.put("r_ranking", record.getRanking());
        cv.put("r_playerName", record.getPlayerName());
        cv.put("r_score", record.getScore());
        cv.put("r_recordTime",record.getRecordTimeStr());
        int count = mDB.update(TABLE_NAME, cv, condition, null);
        return count;
    }

    public ArrayList<PlayerRecord> query(String condition) {
        String sql = String.format("select r_difficulty,r_ranking,r_playerName,r_score,r_recordTime" +
                " from %s where %s;", TABLE_NAME, condition);
        Log.d(TAG, "query sql: " + sql);
        ArrayList<PlayerRecord> recordsArray = new ArrayList<>();
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            for (; ; cursor.moveToNext()) {
                PlayerRecord record = new PlayerRecord();
                record.setDifficulty(cursor.getInt(0));
                record.setRanking(cursor.getInt(1));
                record.setPlayerName(cursor.getString(2));
                record.setScore(cursor.getInt(3));
                record.setRecordTime(cursor.getString(4));

                recordsArray.add(record);
                if (cursor.isLast() == true) {
                    break;
                }
            }
        }
        cursor.close();
        return recordsArray;
    }
}
