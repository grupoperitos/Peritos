package com.epsl.peritos;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;


/**
 * GCMIntentService
 * Clase encargada de recibir las notificaciones push enviadas a traves de Google Cloud Messaging
 *
 * @author David Miguel Poyatos Reina
 * @version 1.0 - 02/02/2016
 */

public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static boolean nc;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private int counter;

	public GCMIntentService() {
		super("GcmIntentService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
			// Since we're not using two way messaging, this is all we really to check for
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String message = extras.getString("message");
                try {
                    //Process the incoming message in function of message type
                    processIncomingMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	protected void showToast(final String message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
	}


    private void processIncomingMessage(String message) throws Exception {
        Log.e("GCMRECEIVED",message);
        showToast(message);
    }





    /**
     * Este metodo lo que hace es visualizar una notificación en la barra de
     * notificaciones con el mensaje pasado por parametro
     *
     * @param msg mensaje que se muestra en la notificación
     */
    /*private void sendNotification(String title, String msg, String id, int c) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent j = new Intent(getApplicationContext(), MainActivity.class);
        j.putExtra("chatID",id);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, j, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        if(c!=1 && c!=0){
            mBuilder.setNumber(c);
            mBuilder.setContentText(c+" mensajes nuevos");
        }

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public boolean checkChat(String _c) {
        SQLiteHelper sqlh = new SQLiteHelper(getApplicationContext());
        SQLiteDatabase rdb = sqlh.getReadableDatabase();
        Cursor c = rdb.rawQuery("SELECT * FROM chats WHERE id=?", new String[]{_c});
        boolean exists = c.moveToFirst();
        c.close();
        rdb.close();
        sqlh.close();
        return exists;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void downloadFile(String uRl, String namefile, String path) {
        File direct = new File(Environment.getExternalStorageDirectory() + path);
            if (!direct.exists()) {
                direct.mkdirs();
            }

        File imageFile = new File(direct, namefile);
        if(imageFile.exists()){
            imageFile.delete();
        }

        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Descarga")
                .setDescription("Descargando archivo...")
                .setDestinationInExternalPublicDir(path, namefile);

        Long a = mgr.enqueue(request);

    }*/


}