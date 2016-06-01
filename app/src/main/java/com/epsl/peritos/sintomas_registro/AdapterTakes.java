package com.epsl.peritos.sintomas_registro;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epsl.peritos.peritos.R;
import com.epsl.peritos.sintomas_registro.StructureParametersBBDD.Treatment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdapterTakes extends RecyclerView.Adapter<AdapterTakes.TakesViewHolder> {
    private ArrayList<Treatment> datos = new ArrayList<Treatment>();
    static Context context;
    //...

    public AdapterTakes(ArrayList<Treatment> datos, Context context) {
        this.datos = datos;
        this.context = context;
    }

    @Override
    public TakesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.component_device, viewGroup, false);

        TakesViewHolder tvh = new TakesViewHolder(itemView);

        return tvh;
    }

    @Override
    public void onBindViewHolder(TakesViewHolder viewHolder, final int pos) {
        final Treatment componentDevice = datos.get(pos);

        viewHolder.bindWifi(componentDevice);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }


    public interface AdapterCallback {
        public void elementSelect(Treatment componentDevice);
    }

    public static class TakesViewHolder extends RecyclerView.ViewHolder {

        int idTreatment;
        private TextView nameMedicine;
        private TextView quantifyTake;
        private TextView intervalHour;
        private TextView hoursTakes;

        private ImageView img;




        public TakesViewHolder(View itemView) {
            super(itemView);

            nameMedicine = (TextView) itemView.findViewById(R.id.name_medicine);
            quantifyTake = (TextView) itemView.findViewById(R.id.quantify_take);
            intervalHour = (TextView) itemView.findViewById(R.id.interval_hour);
            hoursTakes = (TextView) itemView.findViewById(R.id.hours_takes);

            img = (ImageView) itemView.findViewById(R.id.imagen);

        }

        public void bindWifi(Treatment takes) {
            SharedPreferences pr = context.getSharedPreferences("PRFS", context.MODE_PRIVATE);
            InfoNotification inf = new InfoNotification(pr,"",Integer.parseInt(takes.getTypeTreatmentNumeric()),takes.getNameMedicine(),"",1);
            String cantidad = "";
            if(takes.getTypeTreatmentNumeric().equalsIgnoreCase("0")){
                cantidad = "pastillas";
            }
            else{
                cantidad = "puf";
            }
            nameMedicine.setText( "Nombre de Medicamento: "+takes.getNameMedicine().toString());
            quantifyTake.setText( "Cantidad por toma: "+takes.getQuantifyTake().toString() +" "+cantidad );
            intervalHour.setText( "Intervalo entre tomas: "+takes.getIntervalHour().toString()+" horas");
            hoursTakes.setText( "Horario de Tomas: \n");

            img.setBackgroundResource(inf.getRespNotif().getNotifImag());
            String hours ="";
            if (takes != null) {
                if (getCountTake(takes) != 0) {
                    int count = getCountTake(takes);
                    for (int i = 0; i < count; i++) {
                        Calendar c =    calculateHourTake(i, takes);
                        hours = hours+"\n- "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
                    }
                }
            } else {
                Toast.makeText(context, "Ups. No Tiene ningun medicamento en su historial.", Toast.LENGTH_LONG).show();

            }
            hoursTakes.setText( hoursTakes.getText()+hours);

        }

        /*Para una toma, este metodo retorna un Calendar con la hora de la toma*/
        public Calendar calculateHourTake(int numTake, Treatment treatment) {        //Se ha pasado la hora de la notificacion siguiente???
            System.out.println("calculateHourTake");
            Date actual = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            if (cal.get(Calendar.HOUR_OF_DAY) <= 0 && cal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0])) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }

            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[0]));
            cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(treatment.getIntervalHour()) * numTake);
            cal.set(Calendar.MINUTE, Integer.parseInt(treatment.getFirsTakeDay().split("[:]")[1]));

            //System.out.println("hourTake return = "+ cal.get(Calendar.HOUR_OF_DAY));
            return cal;
        }
        /*Return hora actual en tipo timespan 2016-04-01 00:00:01*/
        private String getDate() {
            System.out.println("getDate");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            System.out.println("getDate() return = " + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + (cal.get(Calendar.DAY_OF_MONTH)) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));
            return cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);
        }
        public int getCountTake(StructureParametersBBDD.Treatment treatment) {
            System.out.println("getCountTake");
            int count = 0;
            switch (treatment.getIntervalHour().split("[:]")[0]) {
                case "0":
                    count = 1;
                    break;
                case "1":
                    count = 16;
                    break;
                case "2":
                    count = 8;
                    break;
                case "3":
                    count = 5;
                    break;
                case "4":
                    count = 4;
                    break;
                case "5":
                    count = 3;
                    break;
                case "6":
                    count = 3;
                    break;
                case "8":
                    count = 3;
                    break;
                case "12":
                    count = 2;
                    break;
                case "24":
                    count = 1;
                    break;
            }
            return count;
        }

    }



}
