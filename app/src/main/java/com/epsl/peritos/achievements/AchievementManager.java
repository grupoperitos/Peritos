package com.epsl.peritos.achievements;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Juan Carlos on 26/05/2016.
 */
public class AchievementManager {
    public static final String ACHIEVEMENTS_FILE ="ACHIEVEMENTS";

    public static final int DEFAULT_ACIEVEMNTPOINTS = 50;
    public static final String START_DATE = "startdate";
    public static final String CURRENT_DATE = "currentdate";
    public static final String ACHIEVEMENT_POINTS = "achievementpoints";
    public static final String PATIET_LEVEL = "patientlevel";


    /**
     * Establece en punto inicial de la aplicación para el control de logros
     * @param context
     */
    static public void setStartDate(Context context)
    {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        long time=System.currentTimeMillis();
        ed.putLong(START_DATE,time);
        ed.putLong(CURRENT_DATE,time);
        ed.commit();

    }
    /**
     * Actualiza los puntos de logro del usuario
     * @param context Contexto de la aplicación
     * @param points putos a añadir o restar
     */
    static public void modifyAchievementPoints(Context context,int points)
    {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp=p.getInt(ACHIEVEMENT_POINTS,DEFAULT_ACIEVEMNTPOINTS);
        temp=temp+points;
        if(temp>100)
            temp=100;
        if(temp<0)
            temp=0;
        ed.putInt(ACHIEVEMENT_POINTS,temp);
        ed.commit();
    }

    /**
     * Actualiza los puntos de logro del usuario
     * @param context Contexto de la aplicación
     *
     */
    static public int getAchievementPoints(Context context)
    {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
         return p.getInt(ACHIEVEMENT_POINTS,DEFAULT_ACIEVEMNTPOINTS);

    }

    /**
     * Actualiza los puntos de logro del usuario
     * @param context Contexto de la aplicación
     * @param points putos a añadir o restar
     */
    static public void modifyPatientLevel(Context context,int points)
    {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp=p.getInt(PATIET_LEVEL,DEFAULT_ACIEVEMNTPOINTS);
        temp=temp+points;
        if(temp>12)
            temp=12;
        if(temp<0)
            temp=0;
        ed.putInt(PATIET_LEVEL,temp);
        ed.commit();
    }
}
