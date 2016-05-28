package com.epsl.peritos.peritos.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.epsl.peritos.communications.CommunicationAsynctask;
import com.epsl.peritos.peritos.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        //Código para obtener el GCM_ID y registrarse en el servidor en GAE
        //En caso de que no haya conexión, decirle al usuario que active el WiFi y vuelva a inicar la app cuando tenga conexión
        final SharedPreferences prefs = getSharedPreferences("PRFS", Context.MODE_PRIVATE);
        String registrationId = prefs.getString("REG_ID", "");
        if (registrationId.isEmpty()) {
            if (isOnline()) {
                new GcmRegistrationAsyncTask(LoginActivity.this).execute();
            }
        }



        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }


    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btnLogin:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                break;
        }
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * Método para descargar la última versión del archivo de contenidos de forma manual
     * @param context Contexto de la actividad
     */
    public void checkUpdate(Context context){
        SharedPreferences prefs = getSharedPreferences("PRFS", Context.MODE_PRIVATE);
        String gcmid = prefs.getString("REG_ID","");
        new CommunicationAsynctask(context).execute("UPD",gcmid);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }

        }
    }



    //********************************************************************************************//
    //******** Clase asíncrona para la obtención del GCM_ID y el posterior registro en GAE *******//
    //********************************************************************************************//
    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private GoogleCloudMessaging gcm;
        private Context context;
        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String SENDER_ID = "108686228916";
            String msg = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                SharedPreferences prefs = getSharedPreferences("PRFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                //Obtain Google GCM ID and save it
                String regId = gcm.register(SENDER_ID);
                editor.putString("REG_ID", regId);
                editor.apply();

                //Get actual date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String fecha = sdf.format(new Date());

                //Mensaje que se enviará al servidor -> GCM_REG_ID@FECHA_ACTUAL
                msg = regId+"%"+fecha;

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            new CommunicationAsynctask(context).execute("REG",msg);
        }
    }
}
