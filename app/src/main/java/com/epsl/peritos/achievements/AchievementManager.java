package com.epsl.peritos.achievements;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by Juan Carlos on 26/05/2016.
 */
public class AchievementManager {

    public static final long DAY = 3600 * 24 * 1000;
    public static final long WEEK = DAY * 7;

    public static final String ACHIEVEMENTS_FILE = "ACHIEVEMENTS";

    public static final int DEFAULT_ACIEVEMNTPOINTS = 50;
    public static final String START_DATE = "startdate";
    public static final String START_CYCLE = "startcycle";
    public static final String LAST_DATE = "lastdate";
    public static final String CURRENT_DATE = "currentdate";
    public static final String ACHIEVEMENT_POINTS = "achievementpoints";
    public static final String PATIENT_LEVEL = "patientlevel";
    public static final String FIRST_RUN = "firstrun";

    //Points of each week of the cicle
    public static final String WEEK_1 = "week1";
    public static final String WEEK_2 = "week2";
    public static final String WEEK_3 = "week3";
    public static final String WEEK_4 = "week4";

    public static final String CURRENT_WEEK = "cweek";

    //Puntos de logro
    public static final int ACHIEVE_VIDEO = 2;
    public static final int ACHIEVE_MESSAGE = 1;
    public static final int ACHIEVE_SINTOMAS = 5;
    public static final int ACHIEVE_MEDICINE = 2;
    public static final int ACHIEVE_NOENTER = -25;
    public static final int ACHIEVE_NOENTERWEEK = -51;
    public static final int ACHIEVE_NOMEDICINE = -10;

    public static final String[] PATIENT_LEVEL_TEXT = {"F-", "F", "E-", "E", "D", "D+", "C", "C+", "B", "B+", "A", "A+", "Sin logro", "Medalla de bronce", "Medalla de plata", "Medalla de oro"};

    //Medallas
    public static final int MEDAL_GOLD = 3;
    public static final int MEDAL_SILVER = 2;
    public static final int MEDAL_BRONZE = 1;
    public static final int MEDAL_NO = 0;


    /**
     * Establece en punto inicial de la aplicación para el control de logros
     *
     * @param context
     */
    static public void setStartDate(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        long time = System.currentTimeMillis();
        ed.putLong(START_DATE, time);
        ed.putLong(CURRENT_DATE, time);
        ed.commit();

    }


    static public void setStartCycleDate(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        long time = System.currentTimeMillis();
        ed.putLong(START_CYCLE, time);
        ed.commit();

    }

    static public void setFirstRun(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        ed.putBoolean(FIRST_RUN, false);
        ed.commit();

        setStartDate(context);
        setStartCycleDate(context);

    }

    static public void setLastDate(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        long time = System.currentTimeMillis();
        ed.putLong(LAST_DATE,time);
        ed.commit();
    }

    public static long getLastDate(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        long time = System.currentTimeMillis();
        long last = p.getLong(LAST_DATE, time);
        return last;

    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        return p.getBoolean(FIRST_RUN,true);


    }

    public static long getCurrentCycle(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int potins = p.getInt(ACHIEVEMENT_POINTS, DEFAULT_ACIEVEMNTPOINTS);
        long time = System.currentTimeMillis();
        long start = p.getLong(START_CYCLE, time);
        long cycle = time / WEEK;


        return cycle;

    }


    /**
     * Actualiza los puntos de logro del usuario
     *
     * @param context Contexto de la aplicación
     * @param points  putos a añadir o restar
     */
    static public void modifyAchievementPoints(Context context, int points) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp = p.getInt(ACHIEVEMENT_POINTS, DEFAULT_ACIEVEMNTPOINTS);
        temp = temp + points;
        if (temp > 100)
            temp = 100;
        if (temp < 0)
            temp = 0;
        ed.putInt(ACHIEVEMENT_POINTS, temp);
        ed.commit();


    }

    /**
     * Actualiza los puntos de logro del usuario
     *
     * @param context Contexto de la aplicación
     */
    static public int getAchievementPoints(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        return p.getInt(ACHIEVEMENT_POINTS, DEFAULT_ACIEVEMNTPOINTS);

    }

    /**
     * Actualiza los puntos de logro del usuario
     *
     * @param context Contexto de la aplicación
     * @param points  putos a añadir o restar
     */
    static public void modifyPatientLevel(Context context, int points) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp = p.getInt(PATIENT_LEVEL, DEFAULT_ACIEVEMNTPOINTS);
        temp = temp + points;
        if (temp > 12)
            temp = 12;
        if (temp < 0)
            temp = 0;
        ed.putInt(PATIENT_LEVEL, temp);
        ed.commit();
    }

    public static int getWeek(Context context, int week) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp = 0;
        switch (week) {
            case 0:
                temp = p.getInt(WEEK_1, 0);
                break;
            case 1:
                temp = p.getInt(WEEK_2, 0);
                break;
            case 2:
                temp = p.getInt(WEEK_3, 0);
                break;
            case 3:
                temp = p.getInt(WEEK_4, 0);
                break;
            default:
                break;
        }
        return temp;
    }

    public static int getWeeklyAchievement(Context context, int week) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp = 0;
        switch (week) {
            case 0:
                temp = p.getInt(WEEK_1, -1);
                break;
            case 1:
                temp = p.getInt(WEEK_2, -1);
                break;
            case 2:
                temp = p.getInt(WEEK_3, -1);
                break;
            case 3:
                temp = p.getInt(WEEK_4, -1);
                break;
            default:
                break;
        }
        return temp;
    }

    public static int getWeeklyAchievemntMedal(int achvpoints) {
        if (achvpoints >= 90)
            return MEDAL_GOLD;
        if (achvpoints >= 75)
            return MEDAL_SILVER;
        if (achvpoints >= 50)
            return MEDAL_BRONZE;
        return MEDAL_NO;

    }

    public static void setPointsToWeek(Context context, int week, int points) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        int temp = 0;
        String key = "";
        switch (week) {
            case 0:
                key = WEEK_1;
                break;
            case 1:
                key = WEEK_2;
                break;
            case 2:
                key = WEEK_3;
                break;
            case 3:
                key = WEEK_4;
                break;

        }

        ed.putInt(key, points);
        ed.commit();
    }

    public static void resetWeeks(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        ed.putInt(WEEK_1, -1);
        ed.putInt(WEEK_2, -1);
        ed.putInt(WEEK_3, -1);
        ed.putInt(WEEK_4, -1);
        ed.commit();
    }

    public static int getPatientLevel(Context context) {
        int pl = 0;
        for (int i = 0; i < 4; i++) {
            pl = pl + getWeek(context, i);
        }
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        ed.putInt(PATIENT_LEVEL, pl);
        ed.commit();
        return pl;
    }

    public static void resetPatientLevel(Context context) {
        SharedPreferences p = context.getSharedPreferences(ACHIEVEMENTS_FILE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        ed.putInt(PATIENT_LEVEL, 0);
        ed.commit();

    }
}
