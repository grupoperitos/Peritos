package com.epsl.peritos.communications;

import android.content.Context;
import android.os.AsyncTask;

import com.epsl.peritos.backend.endpoint.Endpoint;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

/**
 * CommunicationAsynctask.java
 * Clase encargada de establecer la conexión con el servidor a través del Endpoint
 *
 * @author David Miguel Poyatos Reina
 * @version 1.0 - 02/02/2016
 */

public class CommunicationAsynctask extends AsyncTask<String, Void, String> {
    private Endpoint cservice = null;
    private Context context;
    private String ms = "";


    public CommunicationAsynctask(Context co) {
        context = co;
    }

    @Override
    protected String doInBackground(String... arg0) {
        final String msgtyp = arg0[0];
        final String msgreg = arg0[1];

        cservice = null;
        Endpoint.Builder builder = new Endpoint.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null).setRootUrl("https://peritosapp.appspot.com/_ah/api/");
        builder.setApplicationName("PeritosAPP");
        cservice = builder.build();

        try {
            ms = msgtyp+"#"+msgreg;
            System.out.println(ms);
            cservice.endpoint(ms).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ms;
    }

    @Override
    protected void onPostExecute(String msg) {
    }


}



