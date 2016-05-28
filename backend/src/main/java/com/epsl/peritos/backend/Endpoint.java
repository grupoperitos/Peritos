/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.epsl.peritos.backend;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.epsl.peritos.backend.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "endpoint",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.peritos.epsl.com",
    ownerName = "backend.peritos.epsl.com",
    packagePath=""
  )
)
public class Endpoint {

    private static final Logger log = Logger.getLogger(Endpoint.class.getName());
    private static final String API_KEY = System.getProperty("gcm.api.key");
    private static final String servingUrl = "http://storage.googleapis.com/peritosapp.appspot.com/contenidos.txt";

    /**
     * Punto de entrada de las comunicaciones
     *
     */
    @ApiMethod(name = "endpoint")
    public void endpoint(@Named("ms") String ms) throws IOException {
        String[] mss = ms.split("#");
        String msgtype=mss[0];
        String msgcontent=mss[1];

        //Mensaje para registrarse en el servidor
        if(msgtype.equals("REG")){
            String[] regdata=msgcontent.split("%");
            String regId=regdata[0];
            String regFc=regdata[1];

            if(findRecord(regId) != null) {
                return;
            }

            Usuarios record = new Usuarios();
            record.setRegId(regId);
            record.setRegFc(regFc);
            ofy().save().entity(record).now();


            Sender sender = new Sender(API_KEY);
            Message msg = new Message.Builder().addData("message", "OK").build();
            Result result = sender.send(msg, regId, 5);
            if (result.getMessageId() != null) {
                log.warning("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            }
        }

        //Mensaje para comprobar si descargar última versión del archivo de contenidos
        if(msgtype.equals("UPD")){
            String gcmid=msgcontent;
            Messaging wm = new Messaging();
            String message = "UPDATE#"+servingUrl;
            wm.sendMessage(message,gcmid);
            return;
        }
    }



    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregister(@Named("regId") String regId) {
        Usuarios record = findRecord(regId);
        if(record == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }

    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<Usuarios> listDevices(@Named("count") int count) {
        List<Usuarios> records = ofy().load().type(Usuarios.class).limit(count).list();
        return CollectionResponse.<Usuarios>builder().setItems(records).build();
    }

    private Usuarios findRecord(String regId) {
        //return ofy().load().type(Usuarios.class).filter("regId", regId).first().now();
        List<Usuarios> records = ofy().load().type(Usuarios.class).list();
        for (Usuarios record : records) {
            if (record.getRegId().toString().equals(regId)) {
                return record;
            }
        }
        return null;
    }

}
