package com.epsl.peritos;

import java.io.Serializable;

/**
 * Created by Vallemar on 25/05/2016.
 */
public class StructureParametersBBDD {

    public static class Treatment implements Serializable {

        int idTreatment;
        String typeTreatmentNumeric;
        String nameMedicine;
        String quantifyTake;
        String intervalHour;
        String firsTakeDay;


        public Treatment(){
        }
        public Treatment(int idTreatment,String typeTreatmentNumeric, String nameMedicine, String quantifyTake, String intervalHour, String firsTakeDay) {
            this.idTreatment = idTreatment;
            this.typeTreatmentNumeric = typeTreatmentNumeric;
            this.nameMedicine = nameMedicine;
            this.quantifyTake = quantifyTake;
            this.intervalHour = intervalHour;
            this.firsTakeDay = firsTakeDay;
        }

        public Treatment(String typeTreatmentNumeric, String nameMedicine, String quantifyTake, String intervalHour, String firsTakeDay) {
            this.typeTreatmentNumeric = typeTreatmentNumeric;
            this.nameMedicine = nameMedicine;
            this.quantifyTake = quantifyTake;
            this.intervalHour = intervalHour;
            this.firsTakeDay = firsTakeDay;
        }

        public int getIdTreatment() {
            return idTreatment;
        }
        public void setIdTreatment(int id) {
            this.idTreatment = id;
        }

        public String getIntervalHour() {
            return intervalHour;
        }
        public void setIntervalHour(String hora) {
            this.intervalHour = hora;
        }
        public String getTypeTreatmentNumeric() {
            return typeTreatmentNumeric;
        }
        public void setTypeTreatmentNumeric(String typeTreatmentNumeric) {
            this.typeTreatmentNumeric = typeTreatmentNumeric;
        }
        public String getNameMedicine() {
            return nameMedicine;
        }
        public void setNameMedicine(String nameMedicine) {
            this.nameMedicine = nameMedicine;
        }
        public String getQuantifyTake() {
            return quantifyTake;
        }
        public void setQuantifyTake(String quantifyTake) {
            this.quantifyTake = quantifyTake;
        }
        public String getFirsTakeDay() {
            return firsTakeDay;
        }
        public void setFirsTakeDay(String firsTakeDay) {
            this.firsTakeDay = firsTakeDay;
        }


    }




    public static class TakeTreatment implements Serializable {

        int idTake;
        String nameMedicine;
        String typeMedicine;
        String dateTake;
        String timestamp;
        boolean isTaken;

        public TakeTreatment( String nameMedicine, String typeMedicine, String dateTake, String timestamp, boolean isTaken) {
            this.nameMedicine = nameMedicine;
            this.typeMedicine = typeMedicine;

            this.dateTake = dateTake;
            this.timestamp = timestamp;
            this.isTaken = isTaken;
        }

        public int getIdTake() {
            return idTake;
        }

        public void setIdTake(int idTake) {
            this.idTake = idTake;
        }

        public String getNameMedicine() {
            return nameMedicine;
        }

        public void setNameMedicine(String nameMedicine) {
            this.nameMedicine = nameMedicine;
        }

        public String getTypeMedicine() {
            return typeMedicine;
        }

        public void setTypeMedicine(String typeMedicine) {
            this.typeMedicine = typeMedicine;
        }





        public String getDateTake() {
            return dateTake;
        }

        public void setDateTake(String dateTake) {
            this.dateTake = dateTake;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isTaken() {
            return isTaken;
        }

        public void setTaken(boolean taken) {
            isTaken = taken;
        }
    }


}