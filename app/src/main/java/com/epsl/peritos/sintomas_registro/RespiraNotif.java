package com.epsl.peritos.sintomas_registro;

/**
 * Created by noni_ on 31/05/2016.
 */
public class RespiraNotif {

    String notifTitle;
    String notifBody;
    int    notifImag;

    public RespiraNotif(String notifTitle, String notifBody, int notifImag) {
        this.notifTitle = notifTitle;
        this.notifBody = notifBody;
        this.notifImag = notifImag;
    }


    public String getNotifTitle() {
        return notifTitle;
    }

    public void setNotifTitle(String notifTitle) {
        this.notifTitle = notifTitle;
    }

    public String getNotifBody() {
        return notifBody;
    }

    public void setNotifBody(String notifBody) {
        this.notifBody = notifBody;
    }

    public int getNotifImag() {
        return notifImag;
    }

    public void setNotifImag(int notifImag) {
        this.notifImag = notifImag;
    }
}
