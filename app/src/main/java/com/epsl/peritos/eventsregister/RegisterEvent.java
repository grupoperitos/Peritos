package com.epsl.peritos.eventsregister;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RegisterEvent.java
 * Clase encargada de registrar en la base de datos todos los eventos que se produzcan en la aplicación
 *
 * @author David Miguel Poyatos Reina
 * @version 1.0 - 17/05/2016
 */

public class RegisterEvent {

    /**
     * Método para registrar en la tabla cualquier evento de interés que suceda en la aplicación
     * @param context Contexto de la aplicación
     * @param event_type Tipo de evento
     * @param event_info Información del evento
     * @param object_id Objeto relacionado con el evento
     */
    public static void register(Context context, String event_type, String event_info, String object_id){
        SQLiteHelper sqlh = new SQLiteHelper(context);
        SQLiteDatabase wdb = sqlh.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha = sdf.format(new Date());
        wdb.execSQL("INSERT INTO events (id,event_type,event_info,date) VALUES ('','" + event_type + "','" + event_info + "','" + object_id + "','" + fecha + "')");
        wdb.close();
        sqlh.close();
    }


}
