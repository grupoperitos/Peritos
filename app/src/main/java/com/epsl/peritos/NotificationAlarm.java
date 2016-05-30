package com.epsl.peritos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;


/**
 * Created by JuanDeDios on 06/03/2016.
 */
@SuppressLint("ParcelCreator")
public class NotificationAlarm extends Notification {

    private NotificationManager notifyMgr;
    Context context;

    public NotificationAlarm(Context context) {
        this.context = context;
    }


    public  NotificationCompat.Builder notification4(String titulo, String contenido, int smallicon, int iconlarge) { //Esta es visible en la pantalla de bloqueo
        // Estructurar la notificación
        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Crear pending intent
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignar intent y establecer true para notificar como aviso
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(smallicon)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), iconlarge))
                        .setContentTitle(titulo)
                        .setContentText(contenido)
                        .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setSubText("asdfasdf")
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Crear intent

        builder.setFullScreenIntent(fullScreenPendingIntent, true);


        // Crear pila
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Añadir actividad padre
        stackBuilder.addParentStack(MainActivity.class);

        // Referenciar Intent para la notificación
        stackBuilder.addNextIntent(intent);

        // Obtener PendingIntent resultante de la pila
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignación del pending intent
        builder.setContentIntent(resultPendingIntent);

        // Remover notificacion al interactuar con ella
        builder.setAutoCancel(true);


        // Construir la notificación y emitirla
        // notifyMgr.notify(id, builder.build());

        // Sonido por defecto de notificaciones, podemos usar otro
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// Uso en API 10 o menor
        // notification.sound = defaultSound


// Uso en API 11 o mayor
        builder.setSound(defaultSound);

        //Bibracion
        // Patrón de vibración: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
        long[] pattern = new long[]{1000, 500, 1000};

// Uso en API 10 o menor
        // notification.vibrate = pattern;

// Uso en API 11 o mayor
        builder.setVibrate(pattern);
        // API 10 o menor

        //Led
        //       notification.ledARGB = Color.RED;
        //       notification.ledOnMS = 1;
        //      notification.ledOffMS = 0;
        //       notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

// API 11 o mayor
        builder.setLights(Color.RED, 1, 0);
       // notifyMgr.notify(id, builder.build());
return builder;

    }





    public  void notificationStart(int id,String titulo, String contenido, int smallicon, int iconlarge) { //Esta es visible en la pantalla de bloqueo
        // Estructurar la notificación
        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Crear pending intent
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignar intent y establecer true para notificar como aviso
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(smallicon)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), iconlarge))
                        .setContentTitle(titulo)
                        .setContentText(contenido)
                        .setColor(context.getResources().getColor(R.color.colorPrimaryDark))
                        .setSubText("asdfasdf")
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Crear intent

        builder.setFullScreenIntent(fullScreenPendingIntent, true);


        // Crear pila
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Añadir actividad padre
        stackBuilder.addParentStack(MainActivity.class);

        // Referenciar Intent para la notificación
        stackBuilder.addNextIntent(intent);

        // Obtener PendingIntent resultante de la pila
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignación del pending intent
        builder.setContentIntent(resultPendingIntent);

        // Remover notificacion al interactuar con ella
        builder.setAutoCancel(true);


        // Construir la notificación y emitirla
        // notifyMgr.notify(id, builder.build());

        // Sonido por defecto de notificaciones, podemos usar otro
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// Uso en API 10 o menor
        // notification.sound = defaultSound


// Uso en API 11 o mayor
        builder.setSound(defaultSound);

        //Bibracion
        // Patrón de vibración: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
        long[] pattern = new long[]{1000, 500, 1000};

// Uso en API 10 o menor
        // notification.vibrate = pattern;

// Uso en API 11 o mayor
        builder.setVibrate(pattern);
        // API 10 o menor

        //Led
        //       notification.ledARGB = Color.RED;
        //       notification.ledOnMS = 1;
        //      notification.ledOffMS = 0;
        //       notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

// API 11 o mayor
        builder.setLights(Color.RED, 1, 0);
         notifyMgr.notify(id, builder.build());

    }

















    public void notificationIntruso(int id, String titulo, String contenido, int smallicon, int iconlarge) { //Esta es visible en la pantalla de bloqueo
        // Estructurar la notificación
        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);






        // Crear pending intent
         PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


       Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:+34"));

    PendingIntent call = PendingIntent.getActivity(
            context,
            0,
            callIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

       // Uri numero = Uri.parse("tel:+34628672809");

//        PendingIntent intentt = (PendingIntent) new Intent(Intent.ACTION_CALL, numero);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        context.startActivity(intentt);

        // Asignar intent y establecer true para notificar como aviso
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(smallicon)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), iconlarge))
                        .setContentTitle(titulo)
                        .setContentText(contenido)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText("Esta notificicacion indica que se ha detectado un movimiento en su hogar.\r\n" +
                                                "¿Que desea hacer?"))
                        .addAction(R.mipmap.ic_launcher,
                                "IMAGEN", fullScreenPendingIntent)
                        .addAction(R.mipmap.ic_launcher,
                                "SOS", call)
                        .setAutoCancel(false);

        // Crear intent



        // Crear pila

        // Añadir actividad padre

        // Referenciar Intent para la notificación

        // Obtener PendingIntent resultante de la pila


        // Asignación del pending intent


        // Remover notificacion al interactuar con ella
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Añadir actividad padre
        stackBuilder.addParentStack(MainActivity.class);

        // Referenciar Intent para la notificación
        stackBuilder.addNextIntent(intent);

        // Obtener PendingIntent resultante de la pila
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Asignación del pending intent
        builder.setContentIntent(resultPendingIntent);

        // Remover notificacion al interactuar con ella
        builder.setAutoCancel(true);



        // Construir la notificación y emitirla
        // notifyMgr.notify(id, builder.build());

        // Sonido por defecto de notificaciones, podemos usar otro
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// Uso en API 10 o menor
        // notification.sound = defaultSound


// Uso en API 11 o mayor
        builder.setSound(defaultSound);

        //Bibracion
        // Patrón de vibración: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
        long[] pattern = new long[]{1000, 500, 1000};

// Uso en API 10 o menor
        // notification.vibrate = pattern;

// Uso en API 11 o mayor
        builder.setVibrate(pattern);
        // API 10 o menor

        //Led
        //       notification.ledARGB = Color.RED;
        //       notification.ledOnMS = 1;
        //      notification.ledOffMS = 0;
        //       notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

// API 11 o mayor
        builder.setLights(Color.RED, 1, 0);
        notifyMgr.notify(id, builder.build());


    }

    public void callEmergencias() {
        Uri numero = Uri.parse("628672509");
        Intent intent = new Intent(Intent.ACTION_CALL, numero);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(intent);
}


    public void notificationProgress(final int id, int iconId, String titulo, String contenido) {
        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setContentTitle(titulo);
                        //.setContentText(contenido).setNumber(noNotificaciones);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int i;

                        // Ciclo para la simulación de progreso
                        for (i = 0; i <= 100; i += 5) {
                            // Setear 100% como medida máxima
                            builder.setProgress(100, i, false);
                            // Emitir la notificación
                            notifyMgr.notify(id, builder.build());
                            // Retardar la ejecución del hilo
                            try {
                                // Retardo de 1s
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                //Log.d(context, "Falló sleep(1000) ");
                            }
                        }

                    /*
                    ACTUALIZACIÓN DE LA NOTIFICACION
                     */

                        // Desplegar mensaje de éxito al terminar
                        builder.setContentText("Sincronización Completa")
                                // Quitar la barra de progreso
                                .setProgress(0, 0, false);
                        notifyMgr.notify(id, builder.build());
                    }
                }

        ).start();

    }



}
