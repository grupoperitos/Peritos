package com.epsl.peritos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Vallemar on 25/05/2016.
 */
public class BBDDTratamiento {

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    Context context;
    SQLiteCreateBBDD dbHelper;
    public BBDDTratamiento(Context context) {
        this.context = context;
        this.dbHelper = new SQLiteCreateBBDD(context.getApplicationContext());
    }


    public String getfirsHourTake() {
        r.lock();
        try {

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String firsHour = "";
            StructureParametersBBDD.Treatment tratamiento;
            Cursor c = db.rawQuery(" SELECT distinct FIRS_TAKE_HOUR_DAY FROM Treatment ", null);
            if (c.moveToFirst()) {
                do {

                    firsHour= c.getString(0);


                } while (c.moveToNext());
            }
            db.close();
            return firsHour;
        } finally {
            r.unlock();
        }
    }

    public String getTreatment(StructureParametersBBDD.Treatment treatment) {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String firsHour = "";

            Cursor c = db.rawQuery(" SELECT distinct FIRS_TAKE_HOUR_DAY FROM Treatment ", null);
            if (c.moveToFirst()) {
                do {

                    firsHour= c.getString(0);


                } while (c.moveToNext());
            }
            db.close();
            return firsHour;
        } finally {
            r.unlock();
        }
    }

    public ArrayList<StructureParametersBBDD.Treatment> getListTreatment() {
        r.lock();
        try {

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>();
            StructureParametersBBDD.Treatment tratamiento;
            Cursor c = db.rawQuery(" SELECT * FROM Treatment ", null);
            if (c.moveToFirst()) {
                do {
//String typeTreatmentNumeric, String nameMedicine, String quantifyTake, String intervalHour, String firsTakeDay) {

                        listTreatment.add(new StructureParametersBBDD.Treatment(
                                c.getInt(0),
                            c.getString(1),
                            c.getString(2),
                            c.getString(3),
                            c.getString(4),
                            c.getString(5)));

                } while (c.moveToNext());
            }
            db.close();
            return listTreatment;
        } finally {
            r.unlock();
        }
    }

//Metodo para sacar una lista completa de las tomas
    public ArrayList<StructureParametersBBDD.TakeTreatment> getList() {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ArrayList<StructureParametersBBDD.TakeTreatment> listTreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>();
            StructureParametersBBDD.TakeTreatment tratamiento;

            Cursor c = db.rawQuery(" SELECT * FROM TakeTreatment ", null);

            if (c.moveToFirst()) {
                do {
// String nameMedicine, String typeMedicine, String dateTake, String timestamp, boolean isTaken
                    listTreatment.add(new StructureParametersBBDD.TakeTreatment(

                            c.getString(1)
                            ,c.getString(2)
                            ,c.getString(3)
                            ,c.getString(4)
                            ,c.getInt(5) > 0));
                } while (c.moveToNext());
            }
            db.close();
            return listTreatment;
        } finally {
            r.unlock();
        }
    }
    //Metodo para devolver las tomas de un dia de un medicamento
    public int getNumTakeDate(String yearMonhtDay , String typeTake) {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ArrayList<StructureParametersBBDD.TakeTreatment> listTreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>();
            StructureParametersBBDD.TakeTreatment tratamiento;

            Cursor c = db.rawQuery(" SELECT COUNT(TIMESTAMP) FROM TakeTreatment " +
                    "WHERE TIMESTAMP LIKE '%"+yearMonhtDay+"%'" +
                    " AND TYPE_MEDICINE = '"+typeTake+"'", null);
            c.moveToFirst();
            int count= c.getInt(0);

            db.close();
            return count;
        } finally {
            r.unlock();
        }
    }


    public StructureParametersBBDD.TakeTreatment takeTratament(String yearMonhtDay , String typeTake, String nameMedicine) {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ArrayList<StructureParametersBBDD.TakeTreatment> listTreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>();
            StructureParametersBBDD.TakeTreatment tratamiento = null;

            Cursor c = db.rawQuery(" SELECT COUNT(TIMESTAMP) FROM TakeTreatment " +
                    "WHERE TIMESTAMP LIKE '%"+yearMonhtDay+"%'" +
                    " AND TYPE_MEDICINE = '"+typeTake+"'"+
                    " AND NAME_MEDICINE = '"+nameMedicine+"'", null);
            if (c.moveToFirst()) {
                do {
                    if(!c.isNull( 0 ) ) {
// String nameMedicine, String typeMedicine, String dateTake, String timestamp, boolean isTaken

                        tratamiento = new StructureParametersBBDD.TakeTreatment(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getInt(5) > 0);
                    }
                } while (c.moveToNext());
            }
            db.close();
            return tratamiento;
        } finally {
            r.unlock();
        }
    }

    //Metodo para sacar una lista completa de las tomas
    public ArrayList<StructureParametersBBDD.TakeTreatment> getListTreamentDay(String timespam) {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ArrayList<StructureParametersBBDD.TakeTreatment> listTreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>();
            StructureParametersBBDD.TakeTreatment tratamiento;

            Cursor c = db.rawQuery(" SELECT * FROM TakeTreatment " +
                    "WHERE CONTAINS(TIMESTAMP, '"+timespam+"')", null);

            if (c.moveToFirst()) {
                do {

                    listTreatment.add(new StructureParametersBBDD.TakeTreatment(
// String nameMedicine, String typeMedicine, String dateTake, String timestamp, boolean isTaken
                            c.getString(1)
                            ,c.getString(2)
                            ,c.getString(3)
                            ,c.getString(4)
                            ,c.getInt(5) > 0));
                } while (c.moveToNext());
            }
            db.close();
            return listTreatment;
        } finally {
            r.unlock();
        }
    }


    //Consulta con la que sacaremos la lista dada un intervalo de fechas
    public ArrayList<StructureParametersBBDD.TakeTreatment> getListDateInterval(Date firs, Date latest) {
        r.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ArrayList<StructureParametersBBDD.TakeTreatment> listTreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>();
            StructureParametersBBDD.TakeTreatment tratamiento;


            Cursor c = db.rawQuery(" SELECT * FROM TakeTreatment " +
                    "WHERE DATE_TAKE >= '"+ firs +"'"
                    +"AND DATE_TAKE < '"+latest +"'", null);

            if (c.moveToFirst()) {
                do {

                    listTreatment.add(new StructureParametersBBDD.TakeTreatment(
// String nameMedicine, String typeMedicine, String dateTake, String timestamp, boolean isTaken
                            c.getString(1)
                            ,c.getString(2)
                            ,c.getString(3)
                            ,c.getString(4)
                            ,c.getInt(5) > 0));
                } while (c.moveToNext());
            }
            db.close();
            return listTreatment;
        } finally {
            r.unlock();
        }
    }

    public void setTreatment(StructureParametersBBDD.Treatment take) {
        w.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
System.out.println("Insertando datos ");
//(int idTake, String nameMedicine, String typeMedicine, int typeTreatmentNumeric, String dateTake, String timestamp, boolean isTaken)
            db.execSQL("INSERT INTO Treatment(TYPE_TREATMENT_NUMERIC,NAME_MEDICINE,QUANTITY,INTERVAL_HOUR,FIRS_TAKE_HOUR_DAY)" +
                    " VALUES(" +
                    "'" + take.getTypeTreatmentNumeric() + "'," +
                    "'" + take.getNameMedicine() + "'," +
                    "'" + take.getQuantifyTake() + "'," +
                    "'" + take.getIntervalHour() + "'," +
                    "'" + take.getFirsTakeDay() + "')");

//            db.execSQL("CREATE TABLE Treatment("
//                    + " ID_TREATMENT INTEGER PRIMARY KEY, "
//                    + " TYPE_TREATMENT_NUMERIC TEXT NOT NULL, " //para distingir entre los inaladores este sera un tipo int
//                    + " NAME_MEDICINE  TEXT NOT NULL, " //
//                    + " QUANTITY  TEXT NOT NULL, " //    //puf
//                    + " INTERVAL_HOUR TEXT NOT NULL, "
//                    + " FIRS_TAKE_HOUR_DAY TEXT NOT NULL, ");

            db.close();

        } finally {
            w.unlock();
        }
    }
//Insertamos una toma nueva
    public void setTake(StructureParametersBBDD.TakeTreatment take) {
        w.lock();
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

//(int idTake, String nameMedicine, String typeMedicine, int typeTreatmentNumeric, String dateTake, String timestamp, boolean isTaken)
            db.execSQL("INSERT INTO TakeTreatment(  NAME_MEDICINE,TYPE_MEDICINE,DATE_TAKE,TIMESTAMP,IS_TAKEN)" +
                    " VALUES(" +
                    "'" + take.getNameMedicine() + "'," +
                    "'" + take.getTypeMedicine() + "'," +
                    "'" + take.getDateTake() + "'," +
                    "'" + take.getTimestamp() + "'," +
                    "'" + take.isTaken() + "')");
            db.close();

        } finally {
            w.unlock();
        }
    }

    //Insertamos que la toma ha sido tomada
    public void setTrueTake(int idTake) {
        w.lock();
        try {
//(int idTake, String nameMedicine, String typeMedicine, int typeTreatmentNumeric, String dateTake, String timestamp, boolean isTaken)
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE TakeTreatment SET IS_TAKEN= '"+ true +"' WHERE ID_TAKE='" + idTake +"'");
            db.close();

        } finally {
            w.unlock();
        }
    }
    //delete
    public void delete() {
        w.lock();
        try {
//(int idTake, String nameMedicine, String typeMedicine, int typeTreatmentNumeric, String dateTake, String timestamp, boolean isTaken)
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE   FROM  TakeTreatment");
            db.close();

        } finally {
            w.unlock();
        }
    }




    //Actualizamos el timestamp
    public void setUpdateTimestamp(String timestamp, int idTake) {
        w.lock();
        try {
//(int idTake, String nameMedicine, String typeMedicine, int typeTreatmentNumeric, String dateTake, String timestamp, boolean isTaken)
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE TakeTreatment SET TIMESTAMP= '"+ timestamp +"' WHERE ID_TAKE='" + idTake +"'");
            db.close();

        } finally {
            w.unlock();
        }
    }


}
