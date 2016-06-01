package com.epsl.peritos.sintomas_registro;

import android.content.SharedPreferences;

import com.epsl.peritos.peritos.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rla_city on 31/05/2016.
 */
public class InfoNotification implements NotificationTypes {


    SharedPreferences prefs;
    String nombrePaciente;
    String tipoMedicamentos [] = {"Pastillas","Inhalador"};
    String medicamento;
    String nombreMedicina;
    String tipoTomas;
    String numTomas;
    String horaToma;
    int tipoDialogo;



    RespiraNotif respNotif;
    int imageNotif;

    public InfoNotification(SharedPreferences prefe,String numTomas,int tipomedicamento,String nombreMedicina, String horaToma,int tipoDialogo) {
        prefs = prefe;
        nombrePaciente = prefs.getString("NOM_USUARIO", "");
        if(tipomedicamento == 0){
            medicamento = tipoMedicamentos[0];
            tipoTomas = "pastillas";
        }else{
            medicamento = tipoMedicamentos[1];
            tipoTomas = "puff";
        }

        this.nombreMedicina = nombreMedicina;
if(horaToma.equalsIgnoreCase("") == false) {
    this.horaToma = parsTimespanToCalendar(horaToma);
}else{
    this.horaToma = horaToma;
}

        this.tipoDialogo = tipoDialogo;

        if(medicamento.equalsIgnoreCase("Pastillas") == true){
            this.imageNotif = R.drawable.drug;

        }else{


            this.imageNotif = R.drawable.inhaler;
        }


        this.numTomas = numTomas;
        this.respNotif = new RespiraNotif(nombreMedicina,getNotificationType(tipoDialogo),imageNotif);


    }


   String  getNotificationType(int tipoDialogo){

        String notifText = "";

       switch (tipoDialogo){

           case FIRST_DAY:

               notifText = "Buenos Días "+nombrePaciente+",Le toca tomarse  "+numTomas +""+tipoTomas+" de " +nombreMedicina+" ("+medicamento+") "+ "a las "+horaToma;

               break;


           case NORMAL:

               notifText = "Hola " +nombrePaciente+"  ,Le toca tomarse " +numTomas +""+tipoTomas+" de " +nombreMedicina+" ("+medicamento+") "+ "a las "+horaToma;

               break;

           case DELAY_1:

               notifText ="Hola " + nombrePaciente+",Parece que se le ha olvidado tomarse " +nombreMedicina+" ("+medicamento+") "+ "a las "+horaToma+ ". Es buen momento para tomársela";



               break;

           case DELAY_2:

               notifText ="Hola " + nombrePaciente+",Tenga cuidado, recuerde tomar "+nombreMedicina+" ("+medicamento+") "+ "a las "+horaToma + ". Debería de tomarsela, ya que más adelante podría no ser efectiva.";


               break;

           case NOT_TAKE:

               notifText ="Hola " + nombrePaciente+",Ha perdido una toma, ya NO debe tomar "+nombreMedicina+" ("+medicamento+") "+ "a las "+horaToma + ". Permanezca atento a su siguiente toma";


               break;


       }



       return notifText;




   }

    /*Return calendar para un tipo String Timespan*/
    private String parsTimespanToCalendar(String timespan) {
        System.out.println("parsTimespanToCalendar");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.YEAR, Integer.parseInt(timespan.split(" ")[0].split("[-]")[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(timespan.split(" ")[0].split("[-]")[1])+1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(timespan.split(" ")[0].split("[-]")[2]) );
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timespan.split(" ")[1].split("[:]")[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timespan.split(" ")[1].split("[:]")[1]));
        calendar.set(Calendar.SECOND, 0);
        return getDate(calendar);
    }
    /*Return hora actual en tipo timespan 2016-04-01 00:00:01*/
    private String getDate(Calendar cal) {
        return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
    }
    public RespiraNotif getRespNotif() {
        return respNotif;
    }

    public void setRespNotif(RespiraNotif respNotif) {
        this.respNotif = respNotif;
    }



}
