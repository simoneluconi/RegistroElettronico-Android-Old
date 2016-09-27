package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MyUsers extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_UTENTI =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                     UserEntry._ID + " INTEGER PRIMARY KEY," +
                     UserEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    UserEntry.COLUMN_NAME_CODICESCUOLA + TEXT_TYPE + COMMA_SEP +
                     UserEntry.COLUMN_NAME_USERNAME + TEXT_TYPE + COMMA_SEP +
                     UserEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + " )";
    

    private static final String SQL_DELETE_UTENTI =
            "DROP TABLE IF EXISTS " +  UserEntry.TABLE_NAME;
    
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyUsers.db";

    public MyUsers(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_UTENTI);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_UTENTI);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public class UserEntry implements BaseColumns {
        public UserEntry() {
        }

        public static final String TABLE_NAME = "Utenti";
        public static final String COLUMN_NAME_NAME = "Nome";
        public static final String COLUMN_NAME_CODICESCUOLA = "Codicescuola";
        public static final String COLUMN_NAME_USERNAME = "Username";
        public static final String COLUMN_NAME_PASSWORD = "Password";
        public static final String COLUMN_NAME_NULLABLE = "Empty";
    }
}
