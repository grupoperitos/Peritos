package com.epsl.peritos.eventsregister;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteHelper.java
 * Clase encargada de crear y actualizar (en su caso) la base de datos y tablas creadas con SQLite
 * 
 * @author David Miguel Poyatos Reina
 * @version 1.0 - 02/02/2016
 */

public class SQLiteHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "PeritosDB.db";
	private static final int DATABASE_VERSION = 1;
 
    //Sentencia SQL para crear las tablas
    //Añadir tantos String como tablas tenga la base de datos
    String sqlCreate1 = "CREATE TABLE events (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, event_type TEXT, event_info TEXT, object_id TEXT, date TEXT);";


    public SQLiteHelper(Context con) {
        super(con, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate1);
    }

 
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        //NOTA: Por simplicidad del ejemplo aqui utilizamos directamente la opcion de
        //      eliminar la tabla anterior y crearla de nuevo vacia con el nuevo formato.
        //      Sin embargo lo normal sera que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este metodo deberia ser mas elaborado.
 
        //Se elimina la version anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS events");

 
        //Se crea la nueva version de la tabla
        db.execSQL(sqlCreate1);
    }

}