package com.epsl.peritos.sintomas_registro;

import android.content.Context;
import android.content.SharedPreferences;

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


    public InfoNotification(SharedPreferences prefe,int tipomedicamento,String nombreMedicina, String numTomas, String horaToma,int tipoDialogo) {
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
        this.numTomas = numTomas;
        this.tipoDialogo = tipoDialogo;

    }


   String  getNotificationType(int tipoDialogo){

        String notifText = "";

       switch (tipoDialogo){

           case FIRST:

               notifText = nombrePaciente+", le toca tomarse "+numTomas+" "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma;

               break;

           case DELAY_1:

               notifText = nombrePaciente+", parece que se le ha olvidado tomarse "+numTomas+" "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma+ ".Es buen momento para tomársela";



               break;

           case DELAY_2:

               notifText = nombrePaciente+", tenga cuidado, recuerde tomar "+numTomas+" "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma + ". Debería de tomarsela, ya que más adelante podría no ser efectiva.";


               break;

           case NOT_TAKE:

               notifText = nombrePaciente+", ha perdido una toma, ya NO debe tomar "+numTomas+" "+tipoTomas+" de " +nombreMedicina+"("+medicamento+")"+ "a las "+horaToma + ". Permanezca atento a su siguiente toma";


               break;


       }



       return notifText;




   }







}
