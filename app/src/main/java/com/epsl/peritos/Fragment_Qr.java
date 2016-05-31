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

import java.util.ArrayList;

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M   && getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    0);
        }


        mScannerView = new ZBarScannerView(getActivity());



        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M   && getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    0);
        }
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



        vp.setCurrentItem(vp.getCurrentItem()+1);


        String [] tratamiento;
        tratamiento = resultado.split("[-]");
        System.out.println(result.getContents());
        for (int i=0; i < tratamiento.length ; i++){
            String [] treatFields;
            StructureParametersBBDD.Treatment treatment = new StructureParametersBBDD.Treatment () ;
            treatFields = tratamiento[i].split("[_]");
            treatment.setNameMedicine(treatFields[0]);
            treatment.setTypeTreatmentNumeric(treatFields[1]);
            treatment.setQuantifyTake(treatFields[2]);
            treatment.setIntervalHour(treatFields[3]);
           // treatment.setIntervalHour("1");
            listTreatment.add(treatment);
            System.out.println(tratamiento[i]);


            SharedPreferences pr = getActivity().getSharedPreferences("PRFS", getContext().MODE_PRIVATE);
            boolean comprobar_tratamiento = pr.getBoolean("NEW_TRATAMENT", false);
            if(comprobar_tratamiento==true){

                Intent h = new Intent(getActivity(),MainActivity.class);
                startActivity(h);



            }

        }
        Toast.makeText(getContext(),"Nuevo tratamiento escaneado",Toast.LENGTH_LONG).show();

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


