package com.epsl.peritos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Vallemar on 29/05/2016.
 */

public class ReceiverBoot extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        //LANZAR SERVICIO

        Toast.makeText(context, "REBOOT", Toast.LENGTH_LONG).show();
        Intent intentService = new Intent(context.getApplicationContext(),MyserviceTwo.class);
        intentService.setAction(Constants.REBOOTMOBILE);
        context.startService(intentService);
    }
}
