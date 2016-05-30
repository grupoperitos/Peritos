package com.epsl.peritos;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.epsl.peritos.StructureParametersBBDD.Treatment;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Vallemar on 28/05/2016.
 */
public class MyserviceTwo extends Service implements AdapterTakes.AdapterCallback {
    public Calendar prueba(){        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("hourTake");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 11 );

        cal.set(Calendar.MINUTE, 38 );
        cal.set(Calendar.SECOND, 0 );
        return cal;
    }

    Context context;
    String action;
    MyserviceTwo m;
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
        action = intent.getAction();
        context = getApplicationContext();
        System.out.println("onStartCommand");
        m = this;
        isTaken();
        switch (action) {
            case Constants.INSTALLAPP:
                new BBDDTratamiento(context).delete();
                System.out.println("case Constants.INSTALLAPP: Iniciando  aplicacion....");
                ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment> (getTreatmentListBBDD());
                Toast.makeText(context,"Numero de medicamentos: "+listTreatment.size() , Toast.LENGTH_SHORT).show();
                Treatment tt = null;
                String date = "";
                if(listTreatment.size() != 0 || listTreatment == null){
                for(Treatment nextNotifications : listTreatment){
                     tt =nextNotifications;
                    System.out.println("Medicacion: "+nextNotifications.getNameMedicine());
                    System.out.println("Intervalo entre horas de la medicacion: "+nextNotifications.getIntervalHour());
                     date = nextNotifications.getFirsTakeDay();
                    if(getCountTake(nextNotifications)!= 1) {
                        int count = getCountTake(nextNotifications);
                        System.out.println("Tomas al dia : "+count);
                        int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                        for (int i = 0; i < count; i++) {
                            System.out.println("Hora actual: "+hourActual+ " ¿Esta entre?: "+calculateHourTake(i, nextNotifications).getTime() +" y "+calculateHourTake(i+1, nextNotifications).getTime());
                            if (isContainInterval(calculateHourTake(i, nextNotifications),calculateHourTake(i+1, nextNotifications))) {
                                managerNotify(calculateHourTakeCalendar(i + 1, nextNotifications), nextNotifications, Constants.SENDNOTIFY);
                                i = count;
                            }
                        }
                    }

                }
                if(date != "") {
                    managerNotifyNexDay(nexNotifyDay(date));
                }else{
                    Toast.makeText(context,"Ups. No Tiene ningun medicamento en su historial." , Toast.LENGTH_LONG).show();

                }}else{
                    Toast.makeText(context,"Ups. No Tiene ningun medicamento en su historial." , Toast.LENGTH_LONG).show();

                }
               // System.out.println("PRUEBAAAAAAAAAAAAAAAAAAAAAAAAAA");
               // tt.setIntervalHour("05");
               // managerNotify(prueba(), tt, Constants.SENDNOTIFY);

                return Service.START_NOT_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.

            case Constants.STARTACTIVITY:
                System.out.println("case Constants.STARTACTIVITY:");

                return Service.START_NOT_STICKY;

            case Constants.SENDNOTIFY:
                System.out.println("case Constants.SENDNOTIFY:");
                Toast.makeText(context,"case Constants.SENDNOTIFY: " , Toast.LENGTH_LONG).show();

                Bundle bundle = intent.getExtras();
                Treatment treatment = (Treatment) bundle.getSerializable("treatment");
                runAsForeground(treatment);
                new BBDDTratamiento(context).setTake(new StructureParametersBBDD.TakeTreatment(treatment.getNameMedicine(), treatment.getTypeTreatmentNumeric(), treatment.getFirsTakeDay(), getDate(), false));
System.out.println("Se va a lanzar una notificacion de esta toma."+treatment.getNameMedicine()+" "+treatment.getIdTreatment()+" "+treatment.getQuantifyTake());
                if (isSendNotifyNex(treatment)) {
                    int countTakesDay = new BBDDTratamiento(context).getNumTakeDate(getDate().split(" ")[0], treatment.getTypeTreatmentNumeric());
                    System.out.println("Numero de tomas lanzadas hoy : "+countTakesDay + " Numero de tomas totales: "+getCountTake(treatment));
                    if(countTakesDay < getCountTake(treatment)){
                        int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                        for (int i = 0; i < getCountTake(treatment); i++) {
                            if (isContainInterval(calculateHourTake(i, treatment),calculateHourTake(i+1, treatment))) {

                                System.out.println("Hora actual: "+hourActual+ " ¿Esta entre?: "+calculateHourTake(i, treatment) +" y "+calculateHourTake(i+1, treatment));
                           // if (isContainInterval(hourActual >= calculateHourTake(i, treatment) && hourActual < calculateHourTake(i + 1, treatment)) {
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
                Toast.makeText(context,"case Constants.RUNFIRSNOTIFYDAY: " , Toast.LENGTH_LONG).show();
                System.out.println("case Constants.RUNFIRSNOTIFYDAY:");
                ArrayList<Treatment> list = new ArrayList<Treatment> (getTreatmentListBBDD());
                for(Treatment treatment1:list){
                    runAsForeground(treatment1);
                    new BBDDTratamiento(context).setTake(new StructureParametersBBDD.TakeTreatment(treatment1.getNameMedicine(), treatment1.getTypeTreatmentNumeric(), treatment1.getFirsTakeDay(), getDate(), false));
                }

                return Service.START_STICKY;

            case Constants.REBOOTMOBILE:
                System.out.println("case Constants.REBOOTMOBILE:");
                new BBDDTratamiento(context).delete();
                System.out.println("case Constants.INSTALLAPP: Iniciando  aplicacion....");
                ArrayList<Treatment> listTreatmen = new ArrayList<Treatment> (getTreatmentListBBDD());
                Toast.makeText(context,"Numero de medicamentos: "+listTreatmen.size() , Toast.LENGTH_SHORT).show();
                String datee = "";
                int noTakes = 0;
                ArrayList<StructureParametersBBDD.TakeTreatment> datosFolder = new ArrayList<StructureParametersBBDD.TakeTreatment>();

                if(listTreatmen.size() != 0 || listTreatmen == null){
                    for(Treatment nextNotifications : listTreatmen){
                        System.out.println("Medicacion: "+nextNotifications.getNameMedicine());
                        System.out.println("Intervalo entre horas de la medicacion: "+nextNotifications.getIntervalHour());
                        datee = nextNotifications.getFirsTakeDay();
                        if(getCountTake(nextNotifications)!= 1) {
                            int count = getCountTake(nextNotifications);
                            System.out.println("Tomas al dia : "+count);
                            int hourActual = Integer.parseInt(getDate().split(" ")[1].split("[:]")[0]);
                            for (int i = 0; i < count; i++) {
                                System.out.println("Hora actual: "+hourActual+ " ¿Esta entre?: "+calculateHourTake(i, nextNotifications) +" y "+calculateHourTake(i+1, nextNotifications));
                                if (isContainInterval(calculateHourTake(i, nextNotifications),calculateHourTake(i+1, nextNotifications))) {

                                   // if (hourActual >= calculateHourTake(i, nextNotifications) && hourActual < calculateHourTake(i + 1, nextNotifications)) {
                                    managerNotify(calculateHourTakeCalendar(i + 1, nextNotifications), nextNotifications, Constants.SENDNOTIFY);
                                    i = count;
                                }else{
                                    noTakes++;
                                    datosFolder.add(new StructureParametersBBDD.TakeTreatment(nextNotifications.nameMedicine, nextNotifications.getTypeTreatmentNumeric(), getDate(), getDate(),false));
                                }
                            }
                        }

                    }
                    if(datee != "") {
                        managerNotifyNexDay(nexNotifyDay(datee));
                        if(noTakes != 0){   //Comprobar si las tomas se han tomado o no en la base de datos, y si existen
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
                    }else{
                        Toast.makeText(context,"Ups. No Tiene ningun medicamento en su historial." , Toast.LENGTH_LONG).show();

                    }}else{
                    Toast.makeText(context,"Ups. No Tiene ningun medicamento en su historial." , Toast.LENGTH_LONG).show();

                }
                return Service.START_NOT_STICKY;    // El servicio no se recrea, este comprobara cuando lanze notificaciones.

        }
        return Service.START_STICKY;
    }

public boolean isContainInterval(Calendar someCalendar1, Calendar someCalendar2){
    System.out.println("isContainInterval");
    Date actual = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date());
    System.out.println(someCalendar1.getTime());
    System.out.println(someCalendar2.getTime());
System.out.println(cal.getTime());
    if(new Date().after(someCalendar1.getTime())  && new Date().before(someCalendar2.getTime())  ) {
        System.out.println("SI QUE ENTRAZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        return true;
    }
    return false;
}


    private void managerNotify(Calendar calendar, Treatment treatment, String constants) {
        System.out.println("managerNotify");
        if(treatment.getIntervalHour().equalsIgnoreCase("24") == false) {

            Toast.makeText(context,"Tendra que notificar a las: "+calendar.get(Calendar.HOUR_OF_DAY)+" , "+treatment.getNameMedicine() , Toast.LENGTH_SHORT).show();

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
            PendingIntent pIntent = PendingIntent.getBroadcast(this, treatment.getIdTreatment(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
            Toast.makeText(context,"Hecho " , Toast.LENGTH_SHORT).show();


        }
    }


    public boolean isSendNotifyNex(Treatment treatment){
        System.out.println("isSendNotifyNex");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]) );
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour() ) * getCountTake(treatment));
        if (actual.compareTo(cal.getTime()) < 0) {  //Si la hora actual ha pasado la ultima hora de las tomas del dia no lanza.
            return true;
        }
        return false;
    }

NotificationAlarm notify;
    private void runAsForeground(Treatment treatment) {
        System.out.println("runAsForeground");
        new NotificationAlarm(context).notificationStart(treatment.getIdTreatment(),treatment.getNameMedicine(),"Te tocan "+treatment.getQuantifyTake()+" de tu medicamento preferido",R.mipmap.clock, R.mipmap.ic_launcher);
        //Aqui se va a decidir que tipo de tratamiento es y que poner tipo de notificacion y toda la historia
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.INTERACTNOTIFY);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notify = new NotificationAlarm(context);

        android.support.v4.app.NotificationCompat.Builder builder = notify.notification4(treatment.getNameMedicine(),"Te tocan "+treatment.getQuantifyTake()+" de tu medicamento preferido",R.mipmap.clock,R.mipmap.ic_launcher);

      //  startForeground(treatment.getIdTreatment(),builder.build());// new NotificationAlarm(context).notification4(treatment.getIdTreatment(), "sdfgsdfgsdfg", "sdfgsdfg",R.mipmap.ic_launcher , R.mipmap.ic_launcher));

    }

    public Calendar calculateHourTake(int numTake, Treatment treatment){        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("calculateHourTake");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        System.out.println(  Integer.parseInt(treatment.getIntervalHour() ) * numTake  );
if(cal.get(Calendar.HOUR_OF_DAY) <=0 && cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]) ){
    cal.add(Calendar.DAY_OF_MONTH, -1);
}

            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]));
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour()) * numTake);
            cal.set(Calendar.MINUTE, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]));

        //System.out.println("hourTake return = "+ cal.get(Calendar.HOUR_OF_DAY));
        return cal;
    }
    public Calendar calculateHourTakeCalendar(int numTake, Treatment treatment){        //Se ha pasado la hora de la notificacion siguiente???
        System.out.println("calculateHourTakeCalendar");
        Date actual = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        if(cal.get(Calendar.HOUR_OF_DAY) <=0 && cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]) ){
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        if(cal.get(Calendar.HOUR_OF_DAY) <=0 && cal.get(Calendar.HOUR_OF_DAY) < 0+Integer.parseInt(treatment.getIntervalHour()) ){
//Aqui se quita un dia al calendar1 si esta entre este intervalo por que al multiplicar por el numero de toma que le toca se suben las 2 un dia y la hora actual nunca estara entre ese intervalo.
        }
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]) );
        cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour() ) * numTake);
        cal.set(Calendar.MINUTE, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]) );
        cal.set(Calendar.SECOND, 0 );
        System.out.println("calculateHourTakeCalendar() return = "+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+(cal.get(Calendar.DAY_OF_MONTH)+1)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));

        return cal;
    }

    public int getNumTakeDate(Treatment treatment){        //Metodo que devuelve las tomas que lleva en el dia
        System.out.println("getNumTakeDate");
        return new BBDDTratamiento(context).getNumTakeDate(getDate().split(" ")[0], treatment.getTypeTreatmentNumeric());
    }
    private String getDate() {                        // 2016-04-01 00:00:01
        System.out.println("getDate");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int mes = cal.get(Calendar.MONTH)+1;
        System.out.println("getDate() return = "+cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+(cal.get(Calendar.DAY_OF_MONTH)+1)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));
        return cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
    }
    public ArrayList<StructureParametersBBDD.Treatment> getTreatmentListBBDD(){
        System.out.println("getTreatmentListBBDD");
        ArrayList<StructureParametersBBDD.Treatment> listTreatment = new ArrayList<StructureParametersBBDD.Treatment>(new BBDDTratamiento(context).getListTreatment());
        return listTreatment;
    }

    public int getCountTake(StructureParametersBBDD.Treatment treatment){
        System.out.println("getCountTake");
        int count =0;
        switch (treatment.getIntervalHour().split("[:]")[0]){
            case "0":
                count = 600000;
                break;
            case "1":
                count = 25;
                break;
            case "2":
                count = 9;
                break;
            case "3":
                count = 7;
                break;
            case "4":
                count = 6;
                break;
            case "5":
                count = 5;
                break;
            case "6":
                count = 4;
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
  //  24,8,4,6,12


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

    public void getListTakeBBDD(){
        ArrayList<StructureParametersBBDD.TakeTreatment> lisreatment = new ArrayList<StructureParametersBBDD.TakeTreatment> (new BBDDTratamiento(context).getList());
        for(StructureParametersBBDD.TakeTreatment f: lisreatment){
            System.out.println(f.getNameMedicine()+" , "+f.getDateTake()+" , "+f.getTimestamp());
        }
    }



    public RecyclerView recyclerFolder;

    @Override
    public void elementSelect(StructureParametersBBDD.TakeTreatment componentDevice) {

    }

    @SuppressLint("ValidFragment")
    public   class DialogListFolder extends DialogFragment {
        private ArrayList<StructureParametersBBDD.TakeTreatment> datosFolder;
        private AdapterTakes adaptadorFolder;

        public DialogListFolder(ArrayList<StructureParametersBBDD.TakeTreatment> datosFolder){
            this.datosFolder = datosFolder;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Lista de tomas pasadas del dia");
           // builder.setIcon(R.mipmap.ic_download_dialog_blue);

            //FileFolder files = new FileFolder(context);
            //View v = inflater.inflate(R.layout.layout_recicler_folder, container, false);

            recyclerFolder = new RecyclerView(getContext());
            adaptadorFolder = new AdapterTakes(datosFolder, context, m);
            recyclerFolder.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

//recyclerFolder = (RecyclerView) Context.findViewById(R.id.RecView_Folder);
            builder.setView(recyclerFolder);
            recyclerFolder.setAdapter(adaptadorFolder);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return builder.create();
        }
    }


Thread isTake;
    public void isTaken() {
        Log.d("Sercivio", "Servicio iniciado detectAlarm...");

        if (isTake == null) {
            isTake = new Thread(new Runnable() {
                public void run() {

                    while (exit == false) {
//know
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArrayList<Treatment> listTreatment = new ArrayList<Treatment>(getTreatmentListBBDD());
                        for (Treatment treatment : listTreatment) {
                            Intent notificationIntent = new Intent(context, MainActivity.class);
                            PendingIntent test = PendingIntent.getActivity(context, treatment.getIdTreatment(),
                                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            if (test != null) {

                                System.out.println("isTake Si");
//                                if(new BBDDTratamiento(context).takeTratament(getDate().split(" ")[0] , treatment.getTypeTreatmentNumeric(),treatment.getNameMedicine())!= null){
//                                    System.out.println("existen");
//
//                                }
                                // runAsForeground(treatment);
                            }else{
                                System.out.println("isTake No");
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
}
