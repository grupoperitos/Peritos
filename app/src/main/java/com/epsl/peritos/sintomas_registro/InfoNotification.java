package com.epsl.peritos.sintomas_registro;

import android.content.Context;
import android.content.SharedPreferences;
import com.epsl.peritos.peritos.R;
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

    public InfoNotification(SharedPreferences prefe,int tipomedicamento,String nombreMedicina, String horaToma,int tipoDialogo) {
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
        this.horaToma = horaToma;

        this.tipoDialogo = tipoDialogo;

        if(medicamento.equalsIgnoreCase("Pastillas") == true){
            this.imageNotif = R.drawable.drug;

        }else{


            this.imageNotif = R.drawable.inhaler;
        }



        this.respNotif = new RespiraNotif(nombreMedicina,getNotificationType(tipoDialogo),imageNotif);


    }


   String  getNotificationType(int tipoDialogo){

        String notifText = "";

       switch (tipoDialogo){

           case FIRST_DAY:

               notifText = "Buenos Días "+nombrePaciente+", le toca tomarse 1 "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma;

               break;


           case NORMAL:

               notifText = "Hola " +nombrePaciente+"  , le toca tomarse 1 "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma;

               break;

           case DELAY_1:

               notifText ="Hola " + nombrePaciente+", parece que se le ha olvidado tomarse 1 "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma+ ".Es buen momento para tomársela";



               break;

           case DELAY_2:

               notifText ="Hola " + nombrePaciente+", tenga cuidado, recuerde tomar 1 "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma + ". Debería de tomarsela, ya que más adelante podría no ser efectiva.";


               break;

           case NOT_TAKE:

               notifText ="Hola " + nombrePaciente+", ha perdido una toma, ya NO debe tomar 1"+" "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma + ". Permanezca atento a su siguiente toma";


               break;


       }



       return notifText;




   }



    public RespiraNotif getRespNotif() {
        return respNotif;
    }

    public void setRespNotif(RespiraNotif respNotif) {
        this.respNotif = respNotif;
    }



}
