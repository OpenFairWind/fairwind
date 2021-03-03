package it.uniparthenope.fairwind.services.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.util.Log;

/**
 * Created by raffaelemontella on 14/05/2017.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper implements DBHelper {

    public static final String LOG_TAG="DBHELPER";

    public static final String DATABASE_NAME = "fairwind.db";
    public static final String FILES_TABLE_NAME = "toUpload";
    // public static final String UPLOADING_TABLE_NAME = "uploading";
    public static final String FILES_COLUMN_PATH = "path";


    private HashMap hp;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table if not exists "+FILES_TABLE_NAME+" " +
                        "("+FILES_COLUMN_PATH+" text primary key)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+FILES_TABLE_NAME);
        onCreate(db);
    }

    @Override
    public Integer insertFile (String path) {
        Integer result=0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FILES_COLUMN_PATH, path);
            db.insert(FILES_TABLE_NAME, null, contentValues);
            result=(int) DatabaseUtils.queryNumEntries(db, FILES_TABLE_NAME);
        } catch (SQLiteConstraintException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        db.close();
        return result;
    }

    @Override
    public Integer deleteFile (String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FILES_TABLE_NAME,
                FILES_COLUMN_PATH+" = ? ",
                new String[] { path });
    }

    @Override
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, FILES_TABLE_NAME);
        db.close();
        return numRows;
    }

    @Override
    public ArrayList<String> getAllFiles() {
        ArrayList<String> array_list = new ArrayList<String>();

        //try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + FILES_TABLE_NAME, null);

            res.moveToFirst();

            while (res.isAfterLast() == false) {
                array_list.add(res.getString(res.getColumnIndex(FILES_COLUMN_PATH)));
                res.moveToNext();
            }
            res.close();
            db.close();
        //} catch (SQLiteCantOpenDatabaseException ex1) {
        //    Log.e(LOG_TAG,ex1.getMessage());
        //} catch (SQLiteReadOnlyDatabaseException ex2) {
        //    Log.e(LOG_TAG,ex2.getMessage());
        //}

        return array_list;
    }
}