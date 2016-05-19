package com.epsl.peritos.peritos.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epsl.peritos.info.InformationManager;
import com.epsl.peritos.info.MessageList;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.fragments.DietaFragment;
import com.epsl.peritos.peritos.fragments.EjercicioFragment;
import com.epsl.peritos.peritos.fragments.InfoEpocFragment;
import com.epsl.peritos.peritos.fragments.InfoFragment;
import com.epsl.peritos.peritos.fragments.TratamientoFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.net.Uri;

import com.wdullaer.materialdatetimepicker.*;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    private com.getbase.floatingactionbutton.FloatingActionsMenu FAB_emergencia;

    private com.getbase.floatingactionbutton.FloatingActionButton miniFAB_SR;
    private com.getbase.floatingactionbutton.FloatingActionButton miniFAB_Sintomas;
    private com.getbase.floatingactionbutton.FloatingActionButton miniFAB_Cuidador;


    //Mensajes
    public static MessageList messageList = null;

    int ano, mes, dia;
    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageList = InformationManager.loadInformation(this);

        setContentView(R.layout.activity_main);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        //MiniFAB salud Responde
        miniFAB_SR = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_llamar_SR);
        miniFAB_SR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //902505060
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+953018799));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);


                Snackbar.make(view, getString(R.string.interfaz_saludresponde), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //MiniFAB Control Sintomas
        miniFAB_Sintomas = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_control_sintomas);
        miniFAB_Sintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "CONTROL SINTOMAS", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //MiniFAB llamar cuidador
        miniFAB_Cuidador = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_llamar_cuidador);
        miniFAB_Cuidador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////////*****CREAR FORMULARIO/agenda PARA ELEGIR NUMERO DEL CUIDADOR!!!!
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+34000000000"));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);

                Snackbar.make(view, "Llamando a CUIDADOR...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        //Registro del día y hora de último uso de la app
        SharedPreferences p = getSharedPreferences("PRFS", MODE_PRIVATE);
        final SharedPreferences.Editor ed = p.edit();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha = sdf.format(new Date());
        ed.putString("LAST_USE", fecha);
        ed.apply();
    }


    //CONFIGURACION TABS Y SUS FRAGMENTS CON VIEWPAGER
    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.pills,
                R.drawable.food,
                R.drawable.running,
                R.drawable.epoc
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(InfoFragment.newInstance(0,"Tratamiento","¡Toma tu medicación!","Tomar la medicación es importante para su enfermedad","<html><body><H1>&iexcl;Toma tu medicaci&oacute;n!</h1></body></html>"), "Tab_Tratamiento");
        adapter.addFrag(InfoFragment.newInstance(0,"Dieta","¡Come sano siempre!","Hay alimentos que le sentarán mejor","<html><body><h1>Come muy sano</h1></body></html>"), "Tab_Dieta");
        adapter.addFrag(InfoFragment.newInstance(0,"Ejercicio","¡Haga ejercicio!","Haga ejercicio con regularidad y adaptado a su nivel de ahogo","<html><body><h1>Haga ejercicio con regularidad y adaptado a su nivel de ahogo</h1></body></html>"), "Tab_Ejercicio");
        adapter.addFrag(InfoFragment.newInstance(0,"Dieta","¿Qué es la EPOC?","Es una enfermedad que provoca la obstrucción de los bronquios","<html><body><h1>Es una enfermedad que provoca la obstrucción de los bronquios</h1></body></html>"), "Tab_Epoc");
        viewPager.setAdapter(adapter);
    }




    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {


            return null;
        }


    }


    //Menú que se despliega del médico

    public void openBottomSheet(View v) {

        View view = getLayoutInflater().inflate(R.layout.bottom_dialog_menu, null);
        TextView txtCalendar = (TextView) view.findViewById(R.id.txt_calendar);
        TextView txtInforme = (TextView) view.findViewById(R.id.txt_informe);
        TextView txtLogros = (TextView) view.findViewById(R.id.txt_logros);
        TextView txtSettings = (TextView) view.findViewById(R.id.txt_settings);



        final Dialog bottomDialogMenu = new Dialog(MainActivity.this,
                R.style.MaterialDialogMenu);
        bottomDialogMenu.setContentView(view);
        bottomDialogMenu.setCancelable(true);
        bottomDialogMenu.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomDialogMenu.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialogMenu.show();



        //BOTON LLAMADAS CALENDARIO CITAS
        txtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DIA
                Calendar fecha_actual = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MainActivity.this,
                        fecha_actual.get(Calendar.YEAR),
                        fecha_actual.get(Calendar.MONTH),
                        fecha_actual.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor("#4FC3F7");
                dpd.show(getFragmentManager(), "Selector Fecha");

            }
        });





        txtInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "INFORME MEDICO", Toast.LENGTH_SHORT).show();
                bottomDialogMenu.dismiss();
            }
        });

        txtLogros.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "LOGROS", Toast.LENGTH_SHORT).show();
                bottomDialogMenu.dismiss();
            }
        });

        txtSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "CONFIGURACION", Toast.LENGTH_SHORT).show();
                bottomDialogMenu.dismiss();
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog v, int ano, int mes_ano, int dia_mes) {
        //POR DEFECTO EN JAVA LOS MESES VAN DE 0 A 11, POR ESO SUMAMOS 1, PARA MOSTRARLO BIEN
        mes_ano=mes_ano+1;
        String mes="";
        String dia="";
        //Añadir 0 delante si mes es menor que 10
        if(mes_ano<10){
            mes="0"+mes_ano;
        }
        //Aádir 0 delante si dia es menor que 10
        if(dia_mes<10){
            dia="0"+dia_mes;
        }
        Toast.makeText(
                this, "El dia de la cita es: " + dia + "-" + mes + "-" + ano,
                Toast.LENGTH_LONG).show();


        //Una vez se introduce el dia, aparece al hora
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                MainActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true //modo 24 Horas activado
        );
        tpd.setAccentColor("#4FC3F7");
        tpd.show(getFragmentManager(), "Selector Hora");

    }

    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");

        if(dpd != null) dpd.setOnDateSetListener(this);
        if(tpd != null) tpd.setOnTimeSetListener(this);
    }


    @Override
    public void onTimeSet(RadialPickerLayout v, int hora_dia, int min, int sec) {
        String h="";
        String m="";
        //Añadir 0 delante si hora es menor que 10
        if(hora_dia<10){
            h="0"+hora_dia;
        }
        //Añadir 0 delante si minuto es menor que 10
        if(min<10){
            m="0"+min;
        }
        Toast.makeText(
                this, "La hora de la cita es: " + h + ":" + m, Toast.LENGTH_LONG).show();
    }

}


