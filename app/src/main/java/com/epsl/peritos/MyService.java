package com.epsl.peritos;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;


import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {

    Context context;
    boolean isRunService = false;
    TimerTask timerTask;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("onCreate()");

        isRunService = false;
        timeCheck = 60000;

    }

    String action;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        action = intent.getAction();
        context = getApplicationContext();
        System.out.println("onStartCommand");


        //En este if se entrara en las siguientes condiciones:
        // 1- Si el Servicio ha sido despertado por android.
        //  Este es el caso para ver si hay tomas por atender
        if (intent == null) { //Se comprueba si esta la notificacion cuando despierta el servicio. Si esta, lanzamos otra diciendo que tiene que tomarsela esto pasara si no ha atendido la anterior,si no, se compruba en la BBDD si se la ha tomado o no.
////Esto es gracias a devolver START_STICKY del caso SENDNOTIFY
//
//            ArrayList<TakeTreatment> listNotifyUntaken = new ArrayList<TakeTreatment>(new BBDDTratamiento(context).getListDateInterval());
//
//            for (TakeTreatment takeTreatment : listNotifyUntaken) {
////Comprobamos las notificacion con boolean isTaken a false con rango menor de 3 horas que es el rango critico
//float diference = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0])  - Integer.parseInt(takeTreatment.getTimestamp().split(" ")[1].split("[:]")[0]);
//                if (takeTreatment.getDateTake().equalsIgnoreCase(getDate().split(" ")[0])  && diference > 1 ) {  //Esto hay que cambiarlo para hacerlo si es menor del rango critico que en nuestro caso es 3, el && es para que no entre en bucle por haber lanzado una notificacion anterior la diferencia entre timestamp y hora actual nos dira si hay que lanzar o no
//                    if (takeTreatment.isTaken() == false) {  //Si se la ha tomado pasamos a la siguiente si no, entramos y comprobamos la diferencia de tiempo
////Aqui tenemos que sacar la direferencia de tiempos para lanzar en cuestion una notificacion
//                        switch ((int)Math.abs(diference)) {
//                            case Constants.INTERVARMIN:
////Lanzamos notificacion y actualizamos en la base de datos el timeStamp
//
//                                break;
//                            case Constants.INTERVARMEAN:
////Lanzamos notificacion y actualizamos en la base de datos el timeStamp
//
//                                break;
//                            case Constants.INTERVARMAX:
////Lanzamos notificacion de que se ha colado de tiempo
//
//                                break;
//                        }
//                    }
//                }
//            }
//            //Salimos del servicio para no seguir ejecutando los metodos de abajo, Porque? porque nuestra intencion es que al recibir el intent nulo de android lo unico que haga el servicio el comprobar notificaciones, razones
//            // 1- para iniciarlo por 1º el servicio vez esta el activity enviando action = STARTACTIVITY.
//            // 2- para lanzar las notificacones de toma nueva esta AlarManager.
//            // 3- Falta un action mas, con este android nos notifica para la primera toma del dia. este AlarManager se hara todos los dias justo despues de lanzar la primera toma.
            System.out.println(" if (intent == null) {");

            System.out.println("Entra y comprueba que hay un tratamiento no tocado");

            return Service.START_STICKY;
        }



        switch (action) {
            /**
             * Nueva instancia a la aplicacion. Se lanza el metodo nextNotify,
             * se comprueba la hora de la primera notificacion
             */
            case Constants.STARTACTIVITY:
                System.out.println("case Constants.STARTACTIVITY:");

//                if (isRunService == false) {    //Si es servicio esta iniciado  o si ya esta corriendo alarm manager no se lanzara para no relanzar alertas
//                    isRunService = true;
//                    ArrayList<TakeTreatment> listNotifyUntaken = new ArrayList<TakeTreatment>(new BBDDTratamiento(context).getListDateInterval());
//                    if(listNotifyUntaken.size() == 0 || listNotifyUntaken == null && getCalendar().get(Calendar.HOUR_OF_DAY)< Integer.parseInt(new BBDDTratamiento(context).getfirsHourTake().split("[:]")[0])){
//                        ArrayList<Treatment>  listTreatment = new ArrayList<Treatment> (getTreatmentList());
//
//                        for(Treatment nextNotifications : listTreatment){
//                            runAsForeground(nextNotifications);
//                            new BBDDTratamiento(context).setTake(new TakeTreatment(nextNotifications.getNameMedicine(),nextNotifications.getTypeTreatment(),nextNotifications.getTypeTreatmentNumeric(),nextNotifications.getFirsTakeDay(),getDate(),false));
//                        }
//                    }
//
//                    return Service.START_NOT_STICKY;
//                }
                return Service.START_NOT_STICKY;

            case Constants.INSTALLAPP:
                System.out.println("case Constants.INSTALLAPP:");

                ArrayList<StructureParametersBBDD.Treatment>  listTreatment = new ArrayList<StructureParametersBBDD.Treatment> (getTreatmentList());
                String date=null ;
                for(StructureParametersBBDD.Treatment nextNotifications : listTreatment){
                    int count = getCountTake(nextNotifications);
                    managerNotify(nextNotify(nextNotifications),nextNotifications,Constants.SENDNOTIFY);
                    date = nextNotifications.getFirsTakeDay();
                }
                managerNotifyNexDay(nexNotifyDay(date));

                return Service.START_NOT_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.

            case Constants.SENDNOTIFY:
                System.out.println("case Constants.SENDNOTIFY:");

                //Si son mas de las 12 no recordar,
                Bundle bundle = intent.getExtras();
                StructureParametersBBDD.Treatment treatment = (StructureParametersBBDD.Treatment) bundle.getSerializable("treatment");



                    runAsForeground(treatment);
                    if (isSendNotify(treatment)) {
                        managerNotify(nextNotify(treatment), treatment, Constants.SENDNOTIFY);
                        new BBDDTratamiento(context).setTake(new StructureParametersBBDD.TakeTreatment(treatment.getNameMedicine(), treatment.getTypeTreatmentNumeric(), treatment.getFirsTakeDay(), getDate(), false));
                        return Service.START_STICKY;
                    } else {
                        return Service.START_NOT_STICKY;
                    }


                //Guardar en la base de datos que toma ha sido lanzada.
                //Retoma un instent nulo. Tras despertar se comprueba si se ha tocado la notificacion.
            //Tras lanzar la notificacion esta sera guardada en la base de datos con un flag boolean a false de no tocado
            //cuando el servicio despierte lo comprobara si hay alguna notificacion a 0, si la hora de la notificacion ha pasado de 2 horas volvera a notificar,
            //Si a lo largo de otras 2 horas no la ha tocado lo volvera a lanzar mas enfadado, si despues de otra hora no la tocado se volvera a notificar
            //Si despues de este rango no la ha tocado se notificara cada hora
            case Constants.RUNFIRSNOTIFYDAY:
                System.out.println("case Constants.RUNFIRSNOTIFYDAY:");

                ArrayList<StructureParametersBBDD.Treatment>  listTreat = new ArrayList<StructureParametersBBDD.Treatment> (getTreatmentList());
                managerNotifyNexDay(nexNotifyDay(listTreat.get(0).getFirsTakeDay())); //para que me notifique al dia siguiente

                for(StructureParametersBBDD.Treatment nextNotifications : listTreat){
                    runAsForeground(nextNotifications);
                    new BBDDTratamiento(context).setTake(new StructureParametersBBDD.TakeTreatment(nextNotifications.getNameMedicine(),nextNotifications.getTypeTreatmentNumeric(),nextNotifications.getFirsTakeDay(),getDate(),false));
                }
                //salto el alarma manager y guardo la toma en la base de datos
                return Service.START_STICKY;

            case Constants.REBOOTMOBILE:
                System.out.println("case Constants.REBOOTMOBILE:");

                ArrayList<StructureParametersBBDD.Treatment>  lis = new ArrayList<StructureParametersBBDD.Treatment> (getTreatmentList());

                ArrayList<StructureParametersBBDD.TakeTreatment>  list = new ArrayList<StructureParametersBBDD.TakeTreatment> (new BBDDTratamiento(context).getListTreamentDay(getDate()));
//                if(new BBDDTratamiento(context).getListTreamentDay(getDate()) )
//                for(Treatment nextNotifications : list){
//                    int count = getCountTake(nextNotifications);
//                    managerNotify(nextNotify(nextNotifications),nextNotifications,Constants.SENDNOTIFY);
//                }
//                managerNotifyNexDay(nexNotifyDay(listTreatment.get(0).getFirsTakeDay()));
                return Service.START_NOT_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.
        }

        return Service.START_STICKY;
    }




    @Override
    public void onDestroy() {
        System.out.println("onDestroy");

        timerTask.cancel();
        isRunService = false;
    }

    private void runAsForeground(StructureParametersBBDD.Treatment treatment) {
        System.out.println("runAsForeground");

        //Aqui se va a decidir que tipo de tratamiento es y que poner tipo de notificacion y toda la historia
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.INTERACTNOTIFY);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(treatment.getNameMedicine())
                .setContentIntent(pendingIntent).build();
        startForeground(treatment.getIdTreatment(), notification);

    }




    private void runActivity() {
        System.out.println("runActivity");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(notificationIntent);
    }

    public ArrayList<StructureParametersBBDD.Treatment> getTreatmentList(){
        System.out.println("getTreatmentList");

        ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>(new BBDDTratamiento(context).getListTreatment());
        return listTreatment;
    }


    private String getDate() {
        System.out.println("getDate");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
       // 2016-04-01 00:00:01
        return cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
    }
    private Calendar getCalendar() {
        System.out.println("getCalendar");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 2016-04-01 00:00:01
        return cal;
    }

    private boolean getCompareDates(Calendar someCalendar1){
        System.out.println("getCompareDates");

        someCalendar1 = Calendar.getInstance(); // current date/time
        Date someDate = new Date();
        if (someDate.compareTo(someCalendar1.getTime()) < 0) {
            return true;
        }else{
            return false;
        }

    }


    private Calendar nextNotify(StructureParametersBBDD.Treatment treatment) {
        System.out.println("nextNotify");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY , Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]) + Integer.parseInt(treatment.getIntervalHour()));
       // cal.set(Calendar.HOUR_OF_DAY , cal.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(treatment.getIntervalHour()));
        cal.set(Calendar.MINUTE , Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]));
        cal.set(Calendar.SECOND , 0);
        return cal;
    }

    private Calendar nexNotifyDay(String firsHour) {
        System.out.println("nexNotifyDay");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        //dt = c.getTime();
        cal.set(Calendar.HOUR_OF_DAY ,Integer.parseInt(firsHour.split("[:]")[0]));
        cal.set(Calendar.MINUTE , Integer.parseInt(firsHour.split("[:]")[1]));
        cal.set(Calendar.SECOND , 0);
        return cal;
    }

    public Calendar parseStringToCalendar(String timespasn){    //Le paso el TimesPan y me devuelve un Calendar
        System.out.println("parseStringToCalendar");

        String[] parse = timespasn.split(" ");
        String[] date = parse[0].split("[:]");
        String[] hour = parse[1].split("[:]");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR ,  Integer.parseInt(date[0]));
        cal.set(Calendar.MONTH , Integer.parseInt(date[1]));
        cal.set(Calendar.DAY_OF_MONTH , Integer.parseInt(date[2]));
        cal.set(Calendar.HOUR_OF_DAY , Integer.parseInt(hour[0]));
        cal.set(Calendar.MINUTE , Integer.parseInt(hour[1]));
        cal.set(Calendar.SECOND , Integer.parseInt(hour[2]));
        return cal;
    }
    private void managerNotify(Calendar calendar, StructureParametersBBDD.Treatment treatment, String constants) {
        System.out.println("managerNotify");
        if(treatment.getIntervalHour().equalsIgnoreCase("24") == false) {


                System.out.println(treatment.getFirsTakeDay() + " , intervalo; " + treatment.getIntervalHour());
                System.out.println("frcha: " + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                System.out.println("Este el hora: " + calendar.get(Calendar.HOUR_OF_DAY));
                System.out.println("Este el minutos: " + calendar.get(Calendar.MINUTE));
                System.out.println("Este el segundos: " + calendar.get(Calendar.SECOND));
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, ReceiverDateNotify.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable("treatment", treatment);
                intent.putExtras(bundle);
                intent.setAction(Constants.SENDNOTIFY);
                PendingIntent pIntent = PendingIntent.getBroadcast(this, Integer.parseInt(treatment.getTypeTreatmentNumeric()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

        }
    }

    private void managerNotifyNexDay(Calendar calendar) {
        System.out.println("managerNotifyNexDay");

        System.out.println("Este el hora: " + calendar.get(Calendar.HOUR_OF_DAY));
            System.out.println("Este el minutos: " + calendar.get(Calendar.MINUTE));
            System.out.println("Este el segundos: " + calendar.get(Calendar.SECOND));
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, ReceiverDateNotify.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            intent.setAction(Constants.RUNFIRSNOTIFYDAY);
            PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);

    }


    /**
     * Lanza AlarmManager para ser notificado a la hora del tratamiento.
     */


    public void sendBroadcast() {
        System.out.println("sendBroadcast");

        Intent i = new Intent("com.example.juandedios.clientknxraspbian.BroadcastReceiver");
        i.putExtra("final", "photo");
        i.putExtra("namPhoto", "datos a pasar");
        this.sendBroadcast(i);
    }


    int timeCheck;

    public void checkInteract() {
        Timer timer = new Timer();
        if (timerTask == null) {
            System.out.println("checkInteract");
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println(timeCheck);


                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, timeCheck);
        }


    }




    public int getCountTake(StructureParametersBBDD.Treatment treatment){
        System.out.println("getCountTake");

        int count =0;
        switch (treatment.getIntervalHour().split("[:]")[0]){
            case "08":
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

    public boolean isSendNotify(StructureParametersBBDD.Treatment treatment){
        System.out.println("isSendNotify");

        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay()) );
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour() ) * getCountTake(treatment));
        if (actual.compareTo(cal.getTime()) < 0) {  //Si la hora actual ha pasado la ultima hora de las tomas del dia no lanza.
            return true;
        }
        return false;
    }

    public int getNumTakeDate(StructureParametersBBDD.Treatment treatment){        //Metodo que devuelve las tomas que lleva en el dia
        return new BBDDTratamiento(context).getNumTakeDate(getDate().split(" ")[0], treatment.getTypeTreatmentNumeric());
    }


    public boolean isTreatmentLast(StructureParametersBBDD.Treatment treatment){        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("isSendNotify");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay()) );
        cal.add(Calendar.MINUTE, Integer.parseInt(treatment.getIntervalHour() ) * getCountTake(treatment));
        if (actual.compareTo(cal.getTime()) < 0) {  //Si la hora actual ha pasado la ultima hora de las tomas del dia no lanza.
            return true;
        }
        return false;
    }
}

