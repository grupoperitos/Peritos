package com.epsl.peritos;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.LoginActivity;
import com.epsl.peritos.peritos.activity.MainActivity;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener;

import java.util.ArrayList;
import java.util.Calendar;


public class Fragment_Hour extends Fragment implements  OnTimeSetListener{


    ArrayList<StructureParametersBBDD.Treatment> listTreatment;
    static String wakeHour="";
    static Button bot2;
    public static Fragment_Hour newInstance() {


        Bundle args = new Bundle();
        //   args.putSerializable("dataUser", dataUser);
        Fragment_Hour fragment = new Fragment_Hour();
        // fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println(" public void onResume() TWIKIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII ");




    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("enamoraooooooooooooooo");
    }
    @Override
    public void onPause() {
        super.onResume();
        System.out.println(" public void onPause() ");


    }
    @Override
    public void onStop() {
        super.onResume();
        System.out.println(" public void onStop()SDSDSADASDASDDS ");


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        // dataUser = (Data_User) bundle.getSerializable("dataUser");
        context = getActivity();
        System.out.println("TIRIRIRIRIRIRIRIRIIRIRI");



    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        System.out.println("ONTATSADASFDSAFDSDSDSADSD");

    }
    Button bot;

    ImageButton imageButton;
    Context context;
    CustomViewPager vp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final View rootView = inflater.inflate(R.layout.fragment_hour_layout, container, false);
        // dataUser = (Data_User) bundle.getSerializable("dataUser");
        context = getActivity().getApplicationContext();

        bot = (Button) getActivity().findViewById(R.id.button);
        bot.setVisibility(View.INVISIBLE);
        bot2 = (Button) rootView.findViewById(R.id.button2);
        bot2.setEnabled(false);
        imageButton = (ImageButton) rootView.findViewById(R.id.imageButton1);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                FragmentManager fragmentManager = getFragmentManager();
                ///TimePickerFragment dialogo = new TimePickerFragment();
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.time.TimePickerDialog dialogo = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        Fragment_Hour.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true //modo 24 Horas activado
                );
                dialogo.setAccentColor("#4FC3F7");
                dialogo.show(getActivity().getFragmentManager(), "tagAlerta");
                FragmentTransaction ft = fragmentManager.beginTransaction();
                dialogo.setRetainInstance(true);

            }

        });


        bot2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                SharedPreferences pr = getActivity().getSharedPreferences("PRFS", getContext().MODE_PRIVATE);
                boolean comprobar_hora = pr.getBoolean("NEW_HOUR", false);
                if(comprobar_hora==true){

                    Intent h = new Intent(getActivity(),MainActivity.class);
                    startActivity(h);
                    System.out.println("HA ENTRADO EN LA COMPROBACION DE SHAREDPREFERENCES");
                    ///// IMPORTANTE : AQUI IRIA SOLAMENTE UNA CONSULTA PARA MODIFICAR LA WAKE_HOUR



                }else{



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


//                Intent i = new Intent(getContext(),MyserviceTwo.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_FROM_BACKGROUND);
//                i.setAction(Constants.INSTALLAPP);
//                getActivity().startService(i);

                    final SharedPreferences prefs = getActivity().getSharedPreferences("PRFS", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("FIRST_TIME","1");
                    editor.commit();

                    Intent in = new Intent(getContext(),MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    in.setFlags(Intent.FLAG_FROM_BACKGROUND);
                    getActivity().startActivity(in);

                    getActivity().finish();








                }




            }

        });

        FragmentManager fragmentManager = getFragmentManager();

        vp = (CustomViewPager) getActivity().findViewById(R.id.myViewPager);



        return rootView;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        System.out.println(hourOfDay+"    "+minute);
        wakeHour= hourOfDay+":"+minute;
        if(wakeHour.isEmpty()==false){
            bot2.setEnabled(true);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay+"    "+minute);
            wakeHour= hourOfDay+":"+minute;
            if(wakeHour.isEmpty()==false){
                bot2.setEnabled(true);
            }

        }
    }

    public void setListTreatment(ArrayList<StructureParametersBBDD.Treatment> listTreatment) {
        this.listTreatment = listTreatment;
    }




}