package com.epsl.peritos;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.epsl.peritos.peritos.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;


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
    public static final String MESSAGESFILENAME = "messages";

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

        String[] ms = message.split("#");
        String type = ms[0];

        if(type.equals("UPDATE")){
            String url = ms[1];
            downloadFile(url);
        }

        if(type.equals("NEW_PHONE_SALUD")){
            String tel = ms[1];
        }
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void downloadFile(String uRl) {
        String path = "/"+getApplicationContext().getResources().getString(R.string.app_name);

        File direct = new File(Environment.getExternalStorageDirectory() + path);
        if (!direct.exists()) {
            direct.mkdirs();
        }

        File file = new File(direct, "messages.txt");
        if(file.exists()){
            file.delete();
        }


        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Actualización de contenidos")
                .setDescription("Descargando archivo de actualización ...")
                .setDestinationInExternalPublicDir(path, "messages.txt");


        long downloadReference = mgr.enqueue(request);
        String ref = downloadReference+"";

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PRFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DOWNREF", ref);
        editor.commit();

    }


}