package com.epsl.peritos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;
import com.epsl.peritos.peritos.activity.OnlyScanner;
import com.epsl.peritos.peritos.activity.PreferenciasActivity;
import com.epsl.peritos.sintomas_registro.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.dm7.barcodescanner.zbar.ZBarScannerView;


public class Fragment_Qr extends Fragment implements ZBarScannerView.ResultHandler {


    CustomViewPager  vp;


    Button bot;


    ArrayList<StructureParametersBBDD.Treatment> listTreatment = new  ArrayList<StructureParametersBBDD.Treatment> ();

    public static Fragment_Qr newInstance() {


        Bundle args = new Bundle();
        //   args.putSerializable("dataUser", dataUser);
        Fragment_Qr fragment = new Fragment_Qr();
        // fragment.setArguments(args);
        return fragment;
    }

    private ZBarScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getContext(),"ESCANEE SU TRATAMIENTO",Toast.LENGTH_LONG).show();

        vp = (CustomViewPager) getActivity().findViewById(R.id.myViewPager);
        bot = (Button) getActivity().findViewById(R.id.button);
        bot.setVisibility(View.INVISIBLE);




        mScannerView = new ZBarScannerView(getActivity());



        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);


        mScannerView.startCamera();
        System.out.println("METODO ONRESUME DEL QR");
    }



    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        System.out.println("METODO ONRESUME DEL QR");

    }
    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        // Do something with the result here
        String resultado = result.getContents();
        BBDDTratamiento treat = new BBDDTratamiento(getContext());

        String compatible =resultado.substring(0,4);

        System.out.println("fffffffffffffffffffffffffffffffffffffffFFFFFFFFFFFFFFFFFFf    \n"+compatible);
       if(compatible.equalsIgnoreCase("rapp")== true) {


            resultado = resultado.substring(4);

            vp.setCurrentItem(vp.getCurrentItem() + 1);



            String[] tratamiento;
            tratamiento = resultado.split("[-]");
            System.out.println(result.getContents());
            for (int i = 0; i < tratamiento.length; i++) {
                String[] treatFields;
                StructureParametersBBDD.Treatment treatment = new StructureParametersBBDD.Treatment();
                treatFields = tratamiento[i].split("[_]");
                treatment.setNameMedicine(treatFields[0]);
                treatment.setTypeTreatmentNumeric(treatFields[1]);
                treatment.setQuantifyTake(treatFields[2]);
                treatment.setIntervalHour(treatFields[3]);
                // treatment.setIntervalHour("1");
                listTreatment.add(treatment);
                System.out.println(tratamiento[i]);
                Toast.makeText(getContext(), "Nuevo tratamiento escaneado", Toast.LENGTH_LONG).show();

            }
        }else{

           mScannerView.resumeCameraPreview(this);

           SharedPreferences p = getActivity().getSharedPreferences("PRFS", getContext().MODE_PRIVATE);
           final SharedPreferences.Editor ed = p.edit();
           boolean comprobar_trat =  false;
           ed.putBoolean("NEW_TRATAMENT", comprobar_trat);
           ed.apply();


           Toast.makeText(getContext(),"Código QR no válido",Toast.LENGTH_LONG).show();
        }

            SharedPreferences pr = getActivity().getSharedPreferences("PRFS", getContext().MODE_PRIVATE);
            boolean comprobar_tratamiento = pr.getBoolean("NEW_TRATAMENT", false);
            if(comprobar_tratamiento==true){

                String wakeHour="";

                ArrayList<StructureParametersBBDD.Treatment> listTreatment2 = new ArrayList<StructureParametersBBDD.Treatment>(new BBDDTratamiento(getContext()).getListTreatment());
                if(listTreatment2.isEmpty()==false) {
                     wakeHour = listTreatment2.get(0).getFirsTakeDay();

                    System.out.println("Borrado realizado");
                    treat.deleteTreatment();
                    Intent i = new Intent(getActivity(),ServiceTreatment.class);


           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          i.setFlags(Intent.FLAG_FROM_BACKGROUND);
           i.setAction(com.epsl.peritos.sintomas_registro.Constants.DELETEALARM);
          getActivity().startService(i);
                }

                System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                for(StructureParametersBBDD.Treatment listTreatmet: listTreatment ){
                    System.out.println(listTreatmet.getNameMedicine() + "   "+ listTreatmet.getQuantifyTake());
                }
                System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                for(int i =0; i < listTreatment.size();i++){

                    System.out.println(listTreatment.size());


                    StructureParametersBBDD.Treatment t = listTreatment.get(i);
                    t.setFirsTakeDay(wakeHour);
                    new BBDDTratamiento(getContext()).setTreatment(t);
                    // CONSULTA DE INSERCIÓN PARA CADA OBJETO t
                }


                Intent i = new Intent(getActivity(),ServiceTreatment.class);


                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_FROM_BACKGROUND);
                i.setAction(com.epsl.peritos.sintomas_registro.Constants.INSTALLAPP);
                getActivity().startService(i);


                Intent h = new Intent(getActivity(),MainActivity.class);
                startActivity(h);




            }




        // If you would like to resume scanning, call this method below:

    }
//    @Override
//    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(Fragment_Qr.this);
//            }
//        }, 2000);
//    }

    public ArrayList<StructureParametersBBDD.Treatment> getListTreatment() {
        return listTreatment;
    }

}


