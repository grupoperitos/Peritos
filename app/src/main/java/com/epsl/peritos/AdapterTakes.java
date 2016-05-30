package com.epsl.peritos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epsl.peritos.peritos.R;

import java.util.ArrayList;

/**
 * Created by Vallemar on 29/05/2016.
 */
public class AdapterTakes extends RecyclerView.Adapter<AdapterTakes.TakesViewHolder> {
    private AdapterCallback mAdapterCallback;
    private ArrayList<StructureParametersBBDD.TakeTreatment> datos = new ArrayList<StructureParametersBBDD.TakeTreatment>();
    static Context context;
    //...

    public AdapterTakes(ArrayList<StructureParametersBBDD.TakeTreatment> datos, Context context, AdapterCallback callback) {
        this.datos = datos;
        this.context = context;
        this.mAdapterCallback = callback;
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
        final StructureParametersBBDD.TakeTreatment componentDevice = datos.get(pos);

        viewHolder.bindWifi(componentDevice);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallback.elementSelect(componentDevice);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datos.size();
    }


    public interface AdapterCallback {
        public void elementSelect(StructureParametersBBDD.TakeTreatment componentDevice);
    }

    public static class TakesViewHolder extends RecyclerView.ViewHolder {

        private TextView nameMedicine;
        private TextView inform;
        private ImageView img;




        public TakesViewHolder(View itemView) {
            super(itemView);

            nameMedicine = (TextView) itemView.findViewById(R.id.name_medicine);
            inform = (TextView) itemView.findViewById(R.id.inform);
            img = (ImageView) itemView.findViewById(R.id.imagen);

        }

        public void bindWifi(StructureParametersBBDD.TakeTreatment takes) {

            nameMedicine.setText( takes.getNameMedicine().toString());
            inform.setText("Hora de la toma: "+takes.getDateTake().toString());//componentDevice.getName());
            img.setImageResource(R.mipmap.ic_launcher);
            //System.out.println("\n\nFrecuencia : "+wifi.getFrecuencia()+"\n\nFrecuencia : "+"Name AP : "+wifi.getNameAP());


        }
    }



}
