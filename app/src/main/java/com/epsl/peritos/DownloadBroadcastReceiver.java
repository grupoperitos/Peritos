package com.epsl.peritos;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.epsl.peritos.info.InformationManager;
import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.info.MessageList;
import com.epsl.peritos.peritos.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by David on 24/2/16.
 */
public class DownloadBroadcastReceiver extends BroadcastReceiver {
    public static final String MESSAGESFILENAME = "messages";



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String dref = reference+"";

            SharedPreferences prefs = context.getSharedPreferences("PRFS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            String sref =  prefs.getString("DOWNREF", "NULL");
            if(!sref.equals("NULL") && sref.equals(dref)) {
                //Copiar archivo guardado en almacenamiento externo a interno
                File file = new File(context.getFilesDir(), MESSAGESFILENAME);
                if (file.exists()) {
                    file.delete();
                }

                MessageList messages = loadFromFile(context);
                saveInternalFile(context,messages);

                editor.putString("DOWNREF","NULL");
                editor.commit();
            }else{
                return;
            }
        }
    }

    /**
     * Carga los mensajes del fichero interno actualizable
     * @param context
     * @return La lista de mensajes
     */
    private static MessageList loadFromFile(Context context) {

        MessageList result = new MessageList();
        String path = Environment.getExternalStorageDirectory()+"/"+context.getResources().getString(R.string.app_name)+"/messages.txt";
        File a = new File(path);

        try {
            //FileInputStream fin = context.openFileInput(path);
            FileInputStream fin = new FileInputStream (a);

            InputStreamReader is = new InputStreamReader(fin);

            BufferedReader b = new BufferedReader(is);
            String line = "";
            do {
                line = b.readLine();
                if (line != null) {
                    InformationMessage message = new InformationMessage(line);
                    result.add(message);
                }
            } while (line != null);
            b.close();
            is.close();
            fin.close();

            //Eliminar archivo
            a.delete();

            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    /**
     * Almacena los registros le�dos del fichero pasado en los recursos /raw en un fichero en el almacenamiento interno de la aplicaci�n
     *
     * @param messages
     */
    private static boolean saveInternalFile(Context context,MessageList messages) {
        try {

            FileOutputStream fos = context.openFileOutput(MESSAGESFILENAME,Context.MODE_PRIVATE);

            OutputStreamWriter os = new OutputStreamWriter(fos);

            BufferedWriter bw = new BufferedWriter(os);

            for(InformationMessage message:messages){

                bw.write(message.toString());
                bw.newLine();
            }

            bw.close();
            os.close();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();

        }finally {
            return false;
        }
    }

}
