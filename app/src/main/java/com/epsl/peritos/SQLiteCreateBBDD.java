package com.epsl.peritos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vallemar on 25/05/2016.
 */
public class SQLiteCreateBBDD extends SQLiteOpenHelper{

    private static int version = 1;
    private static String name = "Bd";
    private static SQLiteDatabase.CursorFactory factory = null;


    public SQLiteCreateBBDD(Context context) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE Treatment("
                + " ID_TREATMENT INTEGER PRIMARY KEY, "
                + " TYPE_TREATMENT_NUMERIC TEXT NOT NULL, " //para distingir entre los inaladores este sera un tipo int
                + " NAME_MEDICINE  TEXT NOT NULL, " //
                + " QUANTITY  TEXT NOT NULL, " //    //puf
                + " INTERVAL_HOUR TEXT NOT NULL, "
                + " FIRS_TAKE_HOUR_DAY TEXT NOT NULL )");

        db.execSQL("CREATE TABLE TakeTreatment("
                + " ID_TAKE INTEGER PRIMARY KEY, "
                + " NAME_MEDICINE TEXT NOT NULL, "
                + " TYPE_MEDICINE TEXT NOT NULL, "
                + " DATE_TAKE TEXT NOT NULL, "
                + " TIMESTAMP TEXT NOT NULL, "
                + " IS_TAKEN BOOLEAN )");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
