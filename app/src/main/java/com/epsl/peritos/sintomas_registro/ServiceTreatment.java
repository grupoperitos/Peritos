package com.epsl.peritos.sintomas_registro;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.epsl.peritos.sintomas_registro.StructureParametersBBDD.Treatment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vallemar on 28/05/2016.
 */
public class ServiceTreatment extends Service implements NotificationTypes  {

    Context context;
    String action;
    ServiceTreatment m;
    InfoNotification inf;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        System.out.println("onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isTaken();
        if (intent != null) {


        action = intent.getAction();
        context = getApplicationContext();
        System.out.println("onStartCommand");
        m = this;
        isTaken();
        switch (action) {
            case Constants.INSTALLAPP:

                //new BBDDTratamiento(context).delete();
                System.out.println("case Constants.INSTALLAPP: Iniciando  aplicacion....");
                ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>(getTreatmentListBBDD());
                Treatment tt = null;
                String date = "";
                if (listTreatment.size() != 0 || listTreatment == null) {
                    for (Treatment nextNotifications : listTreatment) {
                        date = nextNotifications.getFirsTakeDay();
                        if (getCountTake(nextNotifications) != 1) {
                            int count = getCountTake(nextNotifications);
                            int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                            for (int i = 0; i < count; i++) {
                                if (isContainInterval(calculateHourTake(i, nextNotifications), calculateHourTake(i + 1, nextNotifications))) {
                                    managerNotify(calculateHourTakeCalendar(i + 1, nextNotifications), nextNotifications, Constants.SENDNOTIFY);
                                    i = count;
                                }
                            }
                        }

                    }
                    if (date != "") {

                        managerNotifyNexDay(nexNotifyDay(date));
                    } else {
                        Toast.makeText(context, "Ups. No Tiene ningun medicamento en su historial.", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(context, "Ups. No Tiene ningun medicamento en su historial.", Toast.LENGTH_LONG).show();

                }
                return Service.START_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.

            case Constants.STARTACTIVITY:
                System.out.println("case Constants.STARTACTIVITY:");

                return Service.START_STICKY;

            case Constants.SENDNOTIFY:
                System.out.println("case Constants.SENDNOTIFY:");

                Bundle bundle = intent.getExtras();
                Treatment treatment = (Treatment) bundle.getSerializable("treatment");
                StructureParametersBBDD.TakeTreatment takeTreatment = new StructureParametersBBDD.TakeTreatment(treatment.getNameMedicine(), treatment.getTypeTreatmentNumeric(), getDate(), getDate(), false);
                new BBDDTratamiento(context).setTake(takeTreatment);
                runAsForeground(takeTreatment,NORMAL  ,treatment);

                if (isSendNotifyNex(treatment)) {
                    int countTakesDay = new BBDDTratamiento(context).getNumTakeDate(getDate().split(" ")[0], treatment.getTypeTreatmentNumeric());
                    if (countTakesDay < getCountTake(treatment)) {
                        int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                        for (int i = 0; i < getCountTake(treatment); i++) {
                            if (isContainInterval(calculateHourTake(i, treatment), calculateHourTake(i + 1, treatment))) {

                                managerNotify(calculateHourTakeCalendar(i + 1, treatment), treatment, Constants.SENDNOTIFY);
                                i = getCountTake(treatment);
                            }
                        }

                        //  managerNotify(calculateHourTakeCalendar(countTakesDay+1, treatment), treatment, Constants.SENDNOTIFY);
                    }
                    return Service.START_STICKY;
                } else {
                    return Service.START_NOT_STICKY;    //Para que el servicio no siga ejecutandose despues de la ultima toma
                }


            case Constants.RUNFIRSNOTIFYDAY:
                System.out.println("case Constants.RUNFIRSNOTIFYDAY:");
                ArrayList<Treatment> list = new ArrayList<Treatment>(getTreatmentListBBDD());
                for (Treatment treatment1 : list) {
                    StructureParametersBBDD.TakeTreatment take = new StructureParametersBBDD.TakeTreatment(treatment1.getNameMedicine(), treatment1.getTypeTreatmentNumeric(), getDate(), getDate(), false);
                    runAsForeground(take,FIRST_DAY,treatment1);
                    new BBDDTratamiento(context).setTake(take);
                }

                return Service.START_STICKY;

            case Constants.REBOOTMOBILE:
                System.out.println("case Constants.REBOOTMOBILE:");
                ArrayList<Treatment> listTreatmen = new ArrayList<Treatment>(getTreatmentListBBDD());
                String datee = "";
                int noTakes = 0;
                ArrayList<StructureParametersBBDD.TakeTreatment> datosFolder = new ArrayList<StructureParametersBBDD.TakeTreatment>();
                ArrayList<StructureParametersBBDD.TakeTreatment> tomasNoRegistradas = new ArrayList<StructureParametersBBDD.TakeTreatment>();

                if (listTreatmen.size() != 0 || listTreatmen == null) {
                    for (Treatment nextNotifications : listTreatmen) {
                        datee = nextNotifications.getFirsTakeDay();
                        if (getCountTake(nextNotifications) != 1) {
                            int count = getCountTake(nextNotifications);
                            int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                            for (int i = 0; i < count; i++) {
                                if (isContainInterval(calculateHourTake(i, nextNotifications), calculateHourTake(i + 1, nextNotifications))) {

                                    // if (hourActual >= calculateHourTake(i, nextNotifications) && hourActual < calculateHourTake(i + 1, nextNotifications)) {
                                    managerNotify(calculateHourTakeCalendar(i + 1, nextNotifications), nextNotifications, Constants.SENDNOTIFY);
                                    i = count;
                                } else {
                                    noTakes++;
                                    datosFolder.add(new StructureParametersBBDD.TakeTreatment(nextNotifications.nameMedicine, nextNotifications.getTypeTreatmentNumeric(), getDate(), getDate(), false));
                                }
                            }
                        }

                    }
                    if (datee != "") {
                        managerNotifyNexDay(nexNotifyDay(datee));
                        if (noTakes != 0) {


                            //Comprobar si las tomas se han tomado o no en la base de datos, y si existen
//                            Intent in = new Intent(this,MainActivity.class);
//                            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            in.setFlags(Intent.FLAG_FROM_BACKGROUND);
//                            startActivity(in);
//
//                            MainActivity m = new MainActivity();
//                            FragmentManager fragManager = m.getSupportFragmentManager();
//                            DialogListFolder dialogoFolder = new DialogListFolder(datosFolder);
//                            dialogoFolder.show(fragManager, "tag");
//                            dialogoFolder.setRetainInstance(true);
                        }
                    } else {
                        Toast.makeText(context, "Ups. No Tiene ningun medicamento en su historial.", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(context, "Ups. No Tiene ningun medicamento en su historial.", Toast.LENGTH_LONG).show();

                }
                return Service.START_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.

            case Constants.DELETEALARM:
                System.out.println("case Constants.CONFIG:");
                PendingIntent pIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pIntent);
                ArrayList<StructureParametersBBDD.Treatment> listTreatmenttt = new ArrayList<StructureParametersBBDD.Treatment>(getTreatmentListBBDD());
                if (listTreatmenttt.size() != 0 || listTreatmenttt == null) {
                    for (Treatment nextNotifications : listTreatmenttt) {
                        if (getCountTake(nextNotifications) != 1) {
                            int count = getCountTake(nextNotifications);
                            int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                            for (int i = 0; i < count; i++) {
                                if (isContainInterval(calculateHourTake(i, nextNotifications), calculateHourTake(i + 1, nextNotifications))) {
                                    PendingIntent pIntentt = PendingIntent.getBroadcast(this, nextNotifications.getIdTreatment(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarmManager.cancel(pIntentt);

                                }
                            }
                        }

                    }
                }
                return Service.START_NOT_STICKY;

        }
        }
        return Service.START_STICKY;



    }

    /*Return Boolean, si la hora actual esta entre estas dos tomas return true*/
    public boolean isContainInterval(Calendar someCalendar1, Calendar someCalendar2) {
        System.out.println("isContainInterval");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (new Date().after(someCalendar1.getTime()) && new Date().before(someCalendar2.getTime())) {
            return true;
        }
        return false;
    }
    AlarmManager alarmManager;
/*Lanza el AlarmManager para horas concretas de tomas*/
    private void managerNotify(Calendar calendar, Treatment treatment, String constants) {
        System.out.println("managerNotify");
        if (treatment.getIntervalHour().equalsIgnoreCase("24") == false) {
             alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, ReceiverDateNotify.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable("treatment", treatment);
            intent.putExtras(bundle);
            intent.setAction(Constants.SENDNOTIFY);
            PendingIntent pIntent = PendingIntent.getBroadcast(this, treatment.getIdTreatment(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);


        }
    }

/*Return, se calcula la hora de la ultima toma del tratamiento pasado, si es superior no se lanza la notificacion*/
    public boolean isSendNotifyNex(Treatment treatment) {
        System.out.println("isSendNotifyNex");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour()) * getCountTake(treatment));
        if (actual.compareTo(cal.getTime()) < 0) {  //Si la hora actual ha pasado la ultima hora de las tomas del dia no lanza.
            return true;
        }
        return false;
    }

    NotificationAlarm notify;


    /*Lanza notificaciones de tomas estandar de tratamientos*/
    private void runAsForeground(StructureParametersBBDD.TakeTreatment takeTreatment,int constants, Treatment treatment) {
        System.out.println("runAsForeground");
        new NotificationAlarm(context).notificationStart(treatment.getQuantifyTake(),takeTreatment,constants,treatment.getIdTreatment());
//        //Aqui se va a decidir que tipo de tratamiento es y que poner tipo de notificacion y toda la historia
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(Constants.INTERACTNOTIFY);
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//        notify = new NotificationAlarm(context);
//
//        android.support.v4.app.NotificationCompat.Builder builder = notify.notification4(treatment.getNameMedicine(), "Te tocan " + treatment.getQuantifyTake() + " de tu medicamento preferido", R.mipmap.clock, R.mipmap.ic_launcher);

        //  startForeground(treatment.getIdTreatment(),builder.build());// new NotificationAlarm(context).notification4(treatment.getIdTreatment(), "sdfgsdfgsdfg", "sdfgsdfg",R.mipmap.ic_launcher , R.mipmap.ic_launcher));

    }

    /*Lanza notificaciones de tomas que no se han tomado del tratamientos, hay 3 intervalos**/
    private void runAsForegroundInterval(StructureParametersBBDD.TakeTreatment takeTreatment,int hoursSpent ,int id) {
        System.out.println("runAsForeground idTratamento: de toma: "+id +" , idToma: "+takeTreatment.getIdTake() );
       // System.out.println("runAsForeground id de toma: ");

        new NotificationAlarm(context).notificationStart("",takeTreatment,hoursSpent,id);

    }


    /*Para una toma, este metodo retorna un Calendar con la hora de la toma*/
    public Calendar calculateHourTake(int numTake, Treatment treatment) {        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("calculateHourTake");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (cal.get(Calendar.HOUR_OF_DAY) <= 0 && cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0])) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour()) * numTake);
        cal.set(Calendar.MINUTE, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]));

        //System.out.println("hourTake return = "+ cal.get(Calendar.HOUR_OF_DAY));
        return cal;
    }


    /*Parece que esta igual que es de arriba*/
    public Calendar calculateHourTakeCalendar(int numTake, Treatment treatment) {        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("calculateHourTakeCalendar");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if (cal.get(Calendar.HOUR_OF_DAY) <= 0 && cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0])) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        if (cal.get(Calendar.HOUR_OF_DAY) <= 0 && cal.get(Calendar.HOUR_OF_DAY) < 0 + Integer.parseInt(treatment.getIntervalHour())) {
//Aqui se quita un dia al calendar1 si esta entre este intervalo por que al multiplicar por el numero de toma que le toca se suben las 2 un dia y la hora actual nunca estara entre ese intervalo.
        }
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]));
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour()) * numTake);
        cal.set(Calendar.MINUTE, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]));
        cal.set(Calendar.SECOND, 0);
        System.out.println("calculateHourTakeCalendar() return = " + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH) + 1) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

        return cal;
    }


/*Return tomas del dia*/
    public int getNumTakeDate(Treatment treatment) {        //Metodo que devuelve las tomas que lleva en el dia
        System.out.println("getNumTakeDate");
        return new BBDDTratamiento(context).getNumTakeDate(getDate().split(" ")[0], treatment.getTypeTreatmentNumeric());
    }

    /*Return hora actual en tipo timespan 2016-04-01 00:00:01*/
    private String getDate() {
        System.out.println("getDate");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        System.out.println("getDate() return = " + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH)) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }

/*Return la lista de tipos tratamientos de la base de datos*/
    public ArrayList<StructureParametersBBDD.Treatment> getTreatmentListBBDD() {
        System.out.println("getTreatmentListBBDD");
        ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>(new BBDDTratamiento(context).getListTreatment());
        return listTreatment;
    }

/*Para que nos notifique al dia siguiente la primera toma*/
    private void managerNotifyNexDay(Calendar calendar) {
        System.out.println("managerNotifyNexDay");
         alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReceiverDateNotify.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        intent.setAction(Constants.RUNFIRSNOTIFYDAY);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
    }


    private Calendar nexNotifyDay(String firsHour) {
        System.out.println("nexNotifyDay");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(firsHour.split("[:]")[0]));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if (0 <= cal.get(Calendar.HOUR_OF_DAY) && cal.get(Calendar.HOUR_OF_DAY) < calendar.get(Calendar.HOUR_OF_DAY)) {
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(firsHour.split("[:]")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(firsHour.split("[:]")[1]));
            cal.set(Calendar.SECOND, 0);
        } else {
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(firsHour.split("[:]")[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(firsHour.split("[:]")[1]));
            cal.set(Calendar.SECOND, 0);
        }
        return cal;
    }

    public void getListTakeBBDD() {
        ArrayList<StructureParametersBBDD.TakeTreatment> lisreatment = new ArrayList<StructureParametersBBDD.TakeTreatment>(new BBDDTratamiento(context).getList());
        for (StructureParametersBBDD.TakeTreatment f : lisreatment) {
            System.out.println(f.getNameMedicine() + " , " + f.getDateTake() + " , " + f.getTimestamp());
        }
    }



    Thread isTake;

    public void isTaken() {
        Log.d("Sercivio", "Servicio iniciado detectAlarm...");

        if (isTake == null) {
            isTake = new Thread(new Runnable() {
                public void run() {

                    while (exit == false) {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayList<Treatment> listTreatment = new ArrayList<Treatment>(getTreatmentListBBDD());
                        ArrayList<StructureParametersBBDD.TakeTreatment> listTreatmentTakes = new ArrayList<StructureParametersBBDD.TakeTreatment>(new BBDDTratamiento(context).getListTreamentDay(getDate()));
                        for (StructureParametersBBDD.TakeTreatment takesTreatment : listTreatmentTakes) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date());

                            if (new Date().after(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(0)).getTime()) && new Date().before(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(1)).getTime())) {  //Compruebo que la hora actual este entre el rango de la primera notificacion

                                if (parsTimespanToCalendar(takesTreatment.getTimestamp()).getTime().before(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(0)).getTime())) {    //Compruebo que el timestamp este antes de el 1ª incremento, asi entrara la primera vez ya que si se notifica se incrementa el timespan y ya no entrara
                                    if (takesTreatment.isTaken() == false) {    //Si la toma no es tomada notifico si no, no.
                                        runAsForegroundInterval(takesTreatment,DELAY_1,new BBDDTratamiento(context).getIdTreament(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine()));
                                        new BBDDTratamiento(context).setUpdateTimestamp(getDate(), takesTreatment.getIdTake());
                                    }
                                }

                            } else if (new Date().after(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(1)).getTime()) && new Date().before(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(2)).getTime())) {
                                if (parsTimespanToCalendar(takesTreatment.getTimestamp()).getTime().before(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(1)).getTime())) {
                                    if (takesTreatment.isTaken() == false) {
                                        runAsForegroundInterval(takesTreatment,DELAY_2,new BBDDTratamiento(context).getIdTreament(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine()));
                                        new BBDDTratamiento(context).setUpdateTimestamp(getDate(), takesTreatment.getIdTake());
                                    }
                                }
                            } else if (new Date().after(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(2)).getTime())) {
                                if (parsTimespanToCalendar(takesTreatment.getTimestamp()).getTime().before(getIncrementHours(takesTreatment.getDateTake(), getRangeShotsNotTaken(new BBDDTratamiento(context).getTimeBetweenShots(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine())).get(2)).getTime())) {
                                    if (takesTreatment.isTaken() == false) {
                                        runAsForegroundInterval(takesTreatment,NOT_TAKE,new BBDDTratamiento(context).getIdTreament(takesTreatment.getNameMedicine(), takesTreatment.getTypeMedicine()));
                                        new BBDDTratamiento(context).setUpdateTimestamp(getDate(), takesTreatment.getIdTake());

                                    }
                                }
                            }
                        }

                    }

                }


            });
            isTake.start();

        } else {
            Log.d("Servicio", "ServicioAlarm || No se inicia el hilo");

        }


    }

    boolean exit;


    /*Return calendar para un tipo String Timespan*/
    private Calendar parsTimespanToCalendar(String timespan) {
        System.out.println("parsTimespanToCalendar");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.YEAR, Integer.parseInt(timespan.split(" ")[0].split("[-]")[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(timespan.split(" ")[0].split("[-]")[1]));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timespan.split(" ")[0].split("[-]")[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timespan.split(" ")[1].split("[:]")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timespan.split(" ")[1].split("[:]")[1]));
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

/*Return Calendar de hora de toma mas el incremento*/
    public Calendar getIncrementHours(String dateTake, String increment) {

        System.out.println("getIncrementHours " + dateTake + " , " + increment);
        int incrementHours = Integer.parseInt(dateTake.split(" ")[1].split(":")[0]) + Integer.parseInt(increment.split(":")[0]);
        int inicrementMinutes = Integer.parseInt(dateTake.split(" ")[1].split(":")[1]) + Integer.parseInt(increment.split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if((incrementHours)== 24){
            incrementHours= 00;
        }
        calendar.set(Calendar.HOUR_OF_DAY, incrementHours);
        calendar.set(Calendar.MINUTE, inicrementMinutes);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }


    public int getCountTake(StructureParametersBBDD.Treatment treatment) {
        System.out.println("getCountTake");
        int count = 0;
        switch (treatment.getIntervalHour().split("[:]")[0]) {
            case "0":
                count = 1;
                break;
            case "1":
                count = 16;
                break;
            case "2":
                count = 8;
                break;
            case "3":
                count = 5;
                break;
            case "4":
                count = 4;
                break;
            case "5":
                count = 3;
                break;
            case "6":
                count = 3;
                break;
            case "8":
                count = 3;
                break;
            case "12":
                count = 2;
                break;
            case "24":
                count = 1;
                break;
        }
        return count;
    }


    public ArrayList<String> getRangeShotsNotTaken(int count) {
        System.out.println("getCountTake");
        ArrayList<String> rangeShotsNotTaken = new ArrayList<String>();
        switch (count) {
            case 1:
                rangeShotsNotTaken.add("00:10");
                rangeShotsNotTaken.add("00:15");
                rangeShotsNotTaken.add("00:30");
                break;
            case 2:
                rangeShotsNotTaken.add("00:30");
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("1:30");
                break;
            case 3:
                rangeShotsNotTaken.add("00:30");
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("1:30");
                break;
            case 4:
                rangeShotsNotTaken.add("00:30");
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("1:30");


                break;
            case 5:
                rangeShotsNotTaken.add("00:30");
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("1:30");
                break;
            case 6:
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("1:30");
                rangeShotsNotTaken.add("2:00");
                break;
            case 8:

                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("2:00");
                rangeShotsNotTaken.add("3:00");
                break;
            case 12:

                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("2:00");
                rangeShotsNotTaken.add("3:00");
                break;
            case 24:
                rangeShotsNotTaken.add("1:00");
                rangeShotsNotTaken.add("2:00");
                rangeShotsNotTaken.add("3:00");
                break;
        }
        return rangeShotsNotTaken;
    }
}
