package com.epsl.peritos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.epsl.peritos.StructureParametersBBDD.Treatment;


/**
 * Created by Vallemar on 25/05/2016.
 */
public class ReceiverDateNotify extends android.content.BroadcastReceiver {

    /**
     * Clase BroadcastReceiver que sera notificada por AlarmManager a la hora de toma de tratamiento.
     */

    @Override
    public void onReceive(android.content.Context context, Intent intent) {
        Toast.makeText(context, "ReceiverDateNotify", Toast.LENGTH_LONG).show();

        Intent intentService = new Intent(context.getApplicationContext(),MyserviceTwo.class);
        Bundle bundle = intent.getExtras();
        System.out.println("ReceiverDateNotify extends android.content.BroadcastReceiver");

        switch (intent.getAction()){
            case Constants.SENDNOTIFY:
                bundle.putSerializable("treatment", (Treatment) bundle.getSerializable("treatment"));
                System.out.println("ReceiverDateNotify extends android.content.BroadcastReceiver  case Constants.SENDNOTIFY:");
                intentService.setAction(Constants.SENDNOTIFY);
                break;
            case Constants.RUNFIRSNOTIFYDAY:
                intentService.setAction(Constants.RUNFIRSNOTIFYDAY);
                break;

        }

       // MyService.Tratamiento tratamiento = (MyService.Tratamiento) bundle.getSerializable("tratamiento");

        intentService.putExtras(bundle);
        context.startService(intentService);
    }
}
