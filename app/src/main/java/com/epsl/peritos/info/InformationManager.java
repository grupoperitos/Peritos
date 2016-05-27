package com.epsl.peritos.info;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.epsl.peritos.peritos.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Juan Carlos on 03/05/2016.
 */
public class InformationManager {

    public static final String MESSAGESFILENAME = "messages";

    /**
     * Obtiene la lista de mensajes, ya sea del recurso /raw o del fichero interno
     *
     * @return La lista de mensajes
     */
    public static MessageList loadInformation(Context context) {
        File file = new File(context.getFilesDir(), MESSAGESFILENAME);

        if (file.exists()) {//The file was already created
            MessageList messages = loadFromFile(context);
            return messages;
        } else {//The file does not exists, mainly due to a first run
            MessageList messages = loadFromResources(context, R.raw.messages);
            saveInternalFile(context,messages);
            return messages;
        }
    }

    /**
     * Almacena los registros leídos del fichero pasado en los recursos /raw en un fichero en el almacenamiento interno de la aplicación
     *
     * @param context Contexto de la aplicación
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

    public static String readMessagesFile(Context context, int resource) {

        String content = "";
        try {
            InputStreamReader is = new InputStreamReader(context
                    .getResources().openRawResource(resource), "UTF-8");

            BufferedReader b = new BufferedReader(is);
            String line = "";


            String[] fields = null;
            do {
                line = b.readLine();
                if (line != null) {
                    fields = line.split("\t");
                    InformationMessage message = new InformationMessage(line);
                    content = content + message.toString() + "\r\n";
                }
            } while (line != null);
            b.close();
            is.close();
            return content;
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
            return null;
        } finally {
            content = content + "</body></html>";
        }

    }

    private static MessageList loadFromResources(Context context, int resource) {

        MessageList result = new MessageList();

        try {
            InputStreamReader is = new InputStreamReader(context
                    .getResources().openRawResource(resource), "UTF-8");

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
            return result;
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * Carga los mensajes del fichero interno actualizable
     * @param context
     * @return La lista de mensajes
     */
    private static MessageList loadFromFile(Context context) {

        MessageList result = new MessageList();


        try {
            FileInputStream fin = context.openFileInput(MESSAGESFILENAME);

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
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(
                    context,
                    context.getString(R.string.error_IputOutput),
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

}
