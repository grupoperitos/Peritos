package com.epsl.peritos.sintomas_registro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.epsl.peritos.peritos.activity.MainActivity;
import com.epsl.peritos.peritos.R;

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


    public  void notificationStart(String ntomas,StructureParametersBBDD.TakeTreatment take,int hoursSpent ,int id) { //Esta es visible en la pantalla de bloqueo
        // Estructurar la notificación


        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Constants.INTERACTNOTIFY);
        // Crear pending intent
        Bundle bundle = new Bundle();
        bundle.putSerializable("take", take);
        intent.putExtras(bundle);
        intent.putExtra("hourspass",hoursSpent);
        SharedPreferences pr = context.getSharedPreferences("PRFS", context.MODE_PRIVATE);
        InfoNotification inf = new InfoNotification(pr,ntomas,Integer.parseInt(take.getTypeMedicine()),take.getNameMedicine(),take.getTimestamp(),hoursSpent);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String aux= inf.getRespNotif().getNotifBody();
        String nombre =aux.split("[,]")[0];
        String aux2 =aux.split("[,]")[1];
        // Asignar intent y establecer true para notificar como aviso
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(inf.getRespNotif().getNotifImag())
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.notify))
                        .setContentTitle(inf.getRespNotif().getNotifTitle())
                        .setContentText(nombre)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))

                        .setStyle(new NotificationCompat.BigTextStyle().bigText(aux2))
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
        builder.setLights(Color.BLUE, 1, 0);
         notifyMgr.notify(id, builder.build());

    }



}
