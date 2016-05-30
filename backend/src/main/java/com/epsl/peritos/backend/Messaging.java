package com.epsl.peritos.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static com.epsl.peritos.backend.OfyService.ofy;

/**
 * Messaging.java
 * Clase encargada del envío de mensajes GCM a los terminales
 *
 * @author David Miguel Poyatos Reina
 * @version 1.0 - 02/02/2016
 */

public class Messaging {

    private static final Logger log = Logger.getLogger(Messaging.class.getName());
    private static final String API_KEY = System.getProperty("gcm.api.key");


    public void sendMessage(String message, String to) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();

        Result result = sender.send(msg, to, 5);
        if (result.getMessageId() != null) {
            log.info("Message sent to " + to);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                log.info("Registration Id changed for " + to + " updating to " + canonicalRegId);
                Usuarios u = findUserByRegId(to);
                u.setRegId(canonicalRegId);
                ofy().save().entity(u).now();
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                Usuarios u = findUserByRegId(to);
                ofy().delete().entity(u).now();
            } else {
                log.warning("Error when sending message : " + error);
            }
        }
    }


    /**
     * Obtiene el objeto Usuarios de un usuario a partir del identificador GCM
     * @param rid Identificador GCM
     * @return Objeto WortalUser
     */
    public Usuarios findUserByRegId(String rid) {
        List<Usuarios> records = ofy().load().type(Usuarios.class).list();
        for (Usuarios record : records) {
            if (record.getRegId().equals(rid) == true) {
                return record;
            }
        }
        return null;

    }
}

