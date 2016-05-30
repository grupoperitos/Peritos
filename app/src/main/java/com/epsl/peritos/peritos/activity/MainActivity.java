package com.epsl.peritos.peritos.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.epsl.peritos.info.InformationManager;

import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.info.MessageList;
import com.epsl.peritos.info.MessageTypes;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.fragments.InfoFragment;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import android.content.Intent;
import android.net.Uri;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.colindodd.toggleimagebutton.ToggleImageButton;


public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, TabLayout.OnTabSelectedListener {
    private final List<InfoFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public Handler mHandler=null;//Handled to manage tab iteration
    public static final String CARRUSEL = "carrusel";


    public static final long MAXPAGE_WAIT = 65000;//milisegundos Control del tiempo que está cada tab activa
    public static final long MAXTEXT_WAIT = 30000;//milisegundos Control del tiempo que está cada tab activo
    public static final long MAIN_MESSAGE_TIME = 5700;
    public static final int HANDLER_MESSAGE_CHANGETAB   = 1;
    public static final int HANLDER_MESSAGE_CAPTION     = 2;
    public static final int HANLDER_MESSAGE_COMMENTARY  = 3;
    public static final int HANLDER_MESSAGE_CHANGE_TEXT = 4;
    public static final int HANLDER_MESSAGE_PREV_TEXT   = 5;
    public static final int HANLDER_MESSAGE_NEXT_TEXT   = 6;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }


    private FloatingActionButton miniFAB_SR;
    private FloatingActionButton miniFAB_Sintomas;
    private FloatingActionButton miniFAB_Cuidador;


    //Variables para Seekbars Control sintomas.
    //Control de estados inicial, si acaban en 1000, tras darle a aceptar, el valor es
    //el central del seekbar. (Valor inicial).
    int valor_esputo_estado_inicial=1000;
    int valor_pacientes_estado_inicial=1000;
    int valor_fatiga_estado_inicial=1000;
    int estados_seekbar[]=new int[4];
    String str_fichero="";


    //Numero del cuidador que recojemos mas adelante del sharedpreferences
    String tlf_cuidador=null;
    String nom_cuidador=null;

    //CITAS
    String dia_cita="";
    String hora_cita="";



    //Mensajes
    public static MessageList messageList = null;
    public static MessageList[] messageTabs = new MessageList[4];



    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageList = InformationManager.loadInformation(this);
        if (messageList != null) {
            messageTabs[MessageTypes.INFO_TRATAMIENTO] = messageList.getMessagesByType(MessageTypes.INFO_TRATAMIENTO);
            messageTabs[MessageTypes.INFO_DIETA] = messageList.getMessagesByType(MessageTypes.INFO_DIETA);
            messageTabs[MessageTypes.INFO_EJERCICIO] = messageList.getMessagesByType(MessageTypes.INFO_EJERCICIO);
            messageTabs[MessageTypes.INFO_EPOC] = messageList.getMessagesByType(MessageTypes.INFO_EPOC);
        } else {
            Toast.makeText(this, getString(R.string.error_cargarrecursos), Toast.LENGTH_LONG).show();
            finish();
        }


        setContentView(R.layout.activity_main);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);


        mHandler = new Handler(Looper.getMainLooper()) {
            /*
                     * handleMessage() defines the operations to perform when
                     * the Handler receives a new Message to process.
                     */
            @Override
            public void handleMessage(Message inputMessage) {

                switch(inputMessage.what){
                    case HANDLER_MESSAGE_CHANGETAB:
                        //Cambiar la pestaña activa
                        mHandler.removeMessages(HANDLER_MESSAGE_CHANGETAB);
                        tabLayout.post(new Carrusel());

                        Snackbar.make(viewPager, "Handler 1", Snackbar.LENGTH_SHORT).show();
                        break;
                    case HANLDER_MESSAGE_CHANGE_TEXT:
                        mHandler.removeMessages(HANLDER_MESSAGE_CHANGE_TEXT);

                        int pos = tabLayout.getSelectedTabPosition();
                        mFragmentList.get(pos).actualize(messageTabs[pos].getNextMessage());

                        //Send the message to change the text
                        Message msgObj = mHandler.obtainMessage();
                        msgObj.what=MainActivity.HANLDER_MESSAGE_CHANGE_TEXT;
                        mHandler.sendMessageDelayed(msgObj,MAXTEXT_WAIT);

                        Snackbar.make(viewPager, "Handler message "+HANLDER_MESSAGE_CHANGE_TEXT, Snackbar.LENGTH_SHORT).show();
                        break;
                    case HANLDER_MESSAGE_PREV_TEXT:
                        mHandler.removeMessages(HANLDER_MESSAGE_PREV_TEXT);

                        int posP = tabLayout.getSelectedTabPosition();
                        mFragmentList.get(posP).actualize(messageTabs[posP].getPrevMessage());

                        Snackbar.make(viewPager, "Handler message "+HANLDER_MESSAGE_PREV_TEXT, Snackbar.LENGTH_SHORT).show();
                        break;
                    case HANLDER_MESSAGE_NEXT_TEXT:
                        mHandler.removeMessages(HANLDER_MESSAGE_NEXT_TEXT);

                        int posN = tabLayout.getSelectedTabPosition();
                        mFragmentList.get(posN).actualize(messageTabs[posN].getNextMessage());

                        Snackbar.make(viewPager, "Handler message "+HANLDER_MESSAGE_NEXT_TEXT, Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        Snackbar.make(viewPager, "Handler default", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }


        };

        setupTabControl();

        //Send the message to change the text
        Message msgObj = mHandler.obtainMessage();
        msgObj.what=MainActivity.HANLDER_MESSAGE_CHANGE_TEXT;
        mHandler.sendMessageDelayed(msgObj,MAXTEXT_WAIT);

        //MiniFAB salud Responde
        miniFAB_SR = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_llamar_SR);
        miniFAB_SR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 902505060));
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
                AlertDialog dialogo_sintomas = createSintomasDialog();
                //dialogo_sintomas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
                dialogo_sintomas.show();
            }
        });


        //MiniFAB llamar cuidador
        miniFAB_Cuidador = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_llamar_cuidador);
        SharedPreferences pr = getSharedPreferences("PRFS", MODE_PRIVATE);
        nom_cuidador = pr.getString("CARER_NAME", "NO HAY CUIDADOR REGISTRADO");
        if(nom_cuidador.equals("NO HAY CUIDADOR REGISTRADO")){

        }else{
            miniFAB_Cuidador.setTitle("LLamar a "+nom_cuidador);
        }
        miniFAB_Cuidador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pr = getSharedPreferences("PRFS", MODE_PRIVATE);
                nom_cuidador = pr.getString("CARER_NAME", "NO HAY CUIDADOR REGISTRADO");
                tlf_cuidador = pr.getString("CARER_PHONE", "NO HAY CUIDADOR REGISTRADO");
                if(tlf_cuidador.equals("NO HAY CUIDADOR REGISTRADO")) {
                    tlf_cuidador = "000000000";
                }
                if(nom_cuidador.equals("NO HAY CUIDADOR REGISTRADO")){

                }else{
                    miniFAB_Cuidador.setTitle("LLamar a "+nom_cuidador);
                }
                //Indicamos el telefono para llamar
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tlf_cuidador));
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
    private void setupTabControl() {
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
        adapter.addFrag(InfoFragment.newInstance(messageTabs[0].getNextMessage()), "Tab_Tratamiento");
        adapter.addFrag(InfoFragment.newInstance(messageTabs[1].getNextMessage()), "Tab_Dieta");
        adapter.addFrag(InfoFragment.newInstance(messageTabs[2].getNextMessage()), "Tab_Ejercicio");
        adapter.addFrag(InfoFragment.newInstance(messageTabs[3].getNextMessage()), "Tab_Epoc");

        viewPager.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tabLayout.getSelectedTabPosition();

        tab.select();
        tabLayout.setScrollPosition(pos,0f,true);

        viewPager.setCurrentItem(pos,true);
        tabLayout.invalidate();
        viewPager.invalidate();

        Message msgObj = mHandler.obtainMessage();
        msgObj.what=MainActivity.HANDLER_MESSAGE_CHANGETAB;
        mHandler.sendMessageDelayed(msgObj,MAXPAGE_WAIT);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //Snackbar.make(viewPager, "tab unselected", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
       // Snackbar.make(viewPager, "tab reselected", Snackbar.LENGTH_SHORT).show();
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

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
            mFragmentTitleList.get(position);
            return null;
        }
    }


    //Menú que se despliega del médico

    public void openBottomSheet(View v) {

        View view = getLayoutInflater().inflate(R.layout.bottom_dialog_menu, null);
        TextView txtCalendar = (TextView) view.findViewById(R.id.txt_calendar);
        TextView txtInforme = (TextView) view.findViewById(R.id.txt_informe);
        TextView txtLogros = (TextView) view.findViewById(R.id.txt_logros);
        TextView txtPreferencias = (TextView) view.findViewById(R.id.txt_preferencias);



        final Dialog bottomDialogMenu = new Dialog(MainActivity.this,
                R.style.MaterialDialogMenu);
        bottomDialogMenu.setContentView(view);
        bottomDialogMenu.setCancelable(true);
        bottomDialogMenu.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomDialogMenu.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialogMenu.show();



        //BOTON MENU CITAS
        txtCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo_menuCitas = createMenuCitasDialog();
                dialogo_menuCitas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 600);
                dialogo_menuCitas.show();

            }
        });


        txtInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo_InformesMedicos = createInformesDialog();
                //dialogo_sintomas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
                dialogo_InformesMedicos.show();
            }
        });

        txtLogros.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog dialogo_Ejercicios = createEjericiosDialog();
                //dialogo_sintomas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
                dialogo_Ejercicios.show();
            }
        });

        txtPreferencias.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //AlertDialog dialogo_preferencias = createPreferenciasDialog();
                //dialogo_sintomas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
                //dialogo_preferencias.show();
                startActivity(new Intent (MainActivity.this,PreferenciasActivity.class));
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
        }else{
            mes=String.valueOf(mes_ano);
        }
        //Aádir 0 delante si dia es menor que 10
        if (dia_mes < 10) {
            dia = "" + "0" + dia_mes;
        } else {
            dia = String.valueOf(dia_mes);
        }
        dia_cita=dia+"-"+mes+"-"+ano;
        /*Toast.makeText(
                this, "El dia de la cita es: " + dia + "-" + mes + "-" + ano,
                Toast.LENGTH_LONG).show();*/


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
        //var=this.obtenerDia(dia);

    }


    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");

        if (dpd != null) dpd.setOnDateSetListener(this);
        if (tpd != null) tpd.setOnTimeSetListener(this);

        Message msgObj = mHandler.obtainMessage();
        msgObj.what=1;
        mHandler.sendMessageDelayed(msgObj,MAXPAGE_WAIT);
    }


    @Override
    public void onTimeSet(RadialPickerLayout v, int hora_dia, int min, int sec) {
        String h="";
        String m="";
        //Añadir 0 delante si hora es menor que 10
        if(hora_dia<10){
            h="0"+hora_dia;
        }else{
            h=String.valueOf(hora_dia);
        }
        //Añadir 0 delante si minuto es menor que 10
        if(min<10){
            m="0"+min;
        }else{
            m=String.valueOf(min);
        }
        hora_cita=h+":"+m;
       /* Toast.makeText(
                this, "La hora de la cita es: " + h + ":" + m, Toast.LENGTH_LONG).show();*/

        //Almacenar dia y hora en un fichero
        //ALMACENAMIENTO EN FICHERO
        FileOutputStream fos = null;
        try {
            fos = MainActivity.this.openFileOutput("registro_citas",MODE_PRIVATE);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        OutputStreamWriter os = new OutputStreamWriter(fos);

        BufferedWriter bw = new BufferedWriter(os);

        try {
            bw.write(dia_cita+" | "+hora_cita);
            bw.newLine();
            Toast.makeText(MainActivity.this,"Cita Almacenada Correctamente",Toast.LENGTH_SHORT).show();
            bw.close();
            os.close();
            fos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }


    /**
     * Crea un diálogo personalizado para Control de Sintomas
     *
     * @return Diálogo
     */
    public AlertDialog createSintomasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.sintomas_dialog, null);

        builder.setView(v);

        Button aceptar = (Button) v.findViewById(R.id.btn_aceptar);
        Button cancelar = (Button) v.findViewById(R.id.btn_cancelar);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        //Barra de Color de Esputo
        SeekBar seekBarColorEsputo = (SeekBar)v.findViewById(R.id.seekBar_estado_esputo);
        seekBarColorEsputo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_esputo_estado_inicial=progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[0]=seekBarProgress;
                valor_esputo_estado_inicial=seekBarProgress;
            }


        });



        //Barra de estado de paciente
        SeekBar seekBarEstadoPaciente = (SeekBar)v.findViewById(R.id.seekBar_estado_paciente);
        seekBarEstadoPaciente.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_pacientes_estado_inicial=progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[1]=seekBarProgress;
                valor_pacientes_estado_inicial=seekBarProgress;
            }
        });


        //Barra de estado Fatiga
        SeekBar seekBarEstadoFatiga = (SeekBar)v.findViewById(R.id.seekBar_estado_fatiga);
        seekBarEstadoFatiga.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_fatiga_estado_inicial=progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[2]=seekBarProgress;
                valor_fatiga_estado_inicial=seekBarProgress;
            }
        });

        final ToggleImageButton toggleFiebre = (ToggleImageButton)v.findViewById(R.id.tb_fiebre);

        aceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                        String fecha_actual = sdf.format(new Date());
                        if(valor_esputo_estado_inicial==1000){
                            estados_seekbar[0]=1;
                        }
                        if(valor_pacientes_estado_inicial==1000){
                            estados_seekbar[1]=2;
                        }
                        if(valor_fatiga_estado_inicial==1000){
                            estados_seekbar[2]=3;
                        }
                        int chek;
                        if(toggleFiebre.isChecked()){
                            estados_seekbar[3]=1;
                        }else{
                            estados_seekbar[3]=0;
                        }


                        /////Datos a almacenar en el fichero
                        //Fecha Actual, en la que realiza el control de sintomas
                        //0--> Estado esputo
                        //1--> Nivel de Ejercicio
                        //2--> Nivel de Fatiga
                        //3--> Fiebre (Si o No)


                        //ALMACENAMIENTO EN FICHERO
                        FileOutputStream fos = null;
                        try {
                            fos = MainActivity.this.openFileOutput("registro_sintomas",MODE_PRIVATE);
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }

                        OutputStreamWriter os = new OutputStreamWriter(fos);

                        BufferedWriter bw = new BufferedWriter(os);

                            try {
                                bw.write(fecha_actual+"\t"+estados_seekbar[0]+"\t"+estados_seekbar[1]+"\t"+estados_seekbar[2]+"\t"+estados_seekbar[3]);
                                bw.newLine();
                                Toast.makeText(MainActivity.this,"Registro almacenado correctamente",Toast.LENGTH_SHORT).show();
                                bw.close();
                                os.close();
                                fos.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        dialog.dismiss();
                    }
                }
        );

        cancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }
        );


        return dialog;
    }



    public class Carrusel implements Runnable {
        @Override
        public void run() {
            int pos = tabLayout.getSelectedTabPosition();
            int ntabs= tabLayout.getTabCount();
            int newPos = (pos+1) % ntabs;
            mFragmentList.get(newPos).actualize(messageTabs[newPos].getNextMessage());
            TabLayout.Tab tab = tabLayout.getTabAt(newPos);
            tab.select();
            tabLayout.setScrollPosition(newPos,0f,true);
            tabLayout.invalidate();
            viewPager.setCurrentItem(newPos,true);
            viewPager.invalidate();


        }
    }






    /**
     * Crea Dialogo Perzonalizado para los informes Medicos del paciente
     */

    public AlertDialog createInformesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.informes_medicos_dialog, null);

        builder.setView(v);

        Button aceptar = (Button) v.findViewById(R.id.btn_aceptar_Informes);

        TableLayout tabladatos=(TableLayout) v.findViewById(R.id.tabla_informes);

        TableRow.LayoutParams layoutFila=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutFecha=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutEsputo=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutNivelEjercicio=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutFatiga=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutFiebre=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow fila;
        TextView txtFecha;
        TextView txtEsputo;
        TextView txtNIvelEjercicio;
        TextView txtFatiga;
        TextView txtFiebre;

        //Reseteamos la tabla al entrar
        tabladatos.removeAllViews();

        //Formateo Cabecera
        String cabeceras[] = { "Fecha", "Esputo", "Tipo Ej.", "Fatiga", "Fiebre" };
        TableRow cabecera = new TableRow(this);
        cabecera.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        tabladatos.addView(cabecera);
        // Textos de la cabecera
        for (int i = 0; i < 5; i++)
        {
            TextView columna = new TextView(this);
            columna.setLayoutParams(new TableRow.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            columna.setText(cabeceras[i]);
            columna.setTextColor(Color.parseColor("#4FC3F7"));
            columna.setGravity(Gravity.CENTER_HORIZONTAL);
            columna.setTypeface(null, Typeface.BOLD_ITALIC);
            columna.setPadding(5, 5, 5, 5);
            cabecera.addView(columna);
        }
        // Línea que separa la cabecera de los datos
        TableRow separador_cabecera = new TableRow(this);
        separador_cabecera.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        FrameLayout linea_cabecera = new FrameLayout(this);
        TableRow.LayoutParams linea_cabecera_params =
                new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, 2);
        linea_cabecera_params.span = 6;
        linea_cabecera.setBackgroundColor(Color.parseColor("#FFFFFF"));
        separador_cabecera.addView(linea_cabecera, linea_cabecera_params);
        tabladatos.addView(separador_cabecera);



        try {

            FileInputStream fin = MainActivity.this.openFileInput("registro_sintomas");

            InputStreamReader is = new InputStreamReader(fin);

            BufferedReader b = new BufferedReader(is);
            String line = "";
            do {
                line = b.readLine();
                if (line != null) {


                    fila=new TableRow(MainActivity.this);
                    fila.setLayoutParams(layoutFila);

                    txtFecha=new TextView(this);
                    txtEsputo=new TextView(this);
                    txtNIvelEjercicio=new TextView(this);
                    txtFatiga=new TextView(this);
                    txtFiebre=new TextView(this);

                    txtFecha.setText(line.subSequence(0,14));
                    txtFecha.setGravity(Gravity.CENTER);
                    txtFecha.setPadding(0, 0, 5, 0);
                    txtFecha.setTextSize(12);
                    txtFecha.setTypeface(null, Typeface.ITALIC);
                    txtFecha.setLayoutParams(layoutFecha);

                    //Transformar codigo numerico del control esputo
                    String esputo="";
                    if(line.charAt(15)=='0'){
                        esputo="Blanco";
                    }else if(line.charAt(15)=='1'){
                        esputo="Amarillo";
                    }else if(line.charAt(15)=='2'){
                        esputo="Verde";
                    }

                    txtEsputo.setText(esputo);
                    txtEsputo.setGravity(Gravity.CENTER);
                    txtEsputo.setPadding(0, 0, 5, 0);
                    txtEsputo.setTextSize(12);
                    txtEsputo.setTypeface(null, Typeface.ITALIC);
                    txtEsputo.setLayoutParams(layoutEsputo);


                    String tipo_actividad="";

                    if(line.charAt(17)=='0'){
                        tipo_actividad="Nada";
                    }else if(line.charAt(17)=='1'){
                        tipo_actividad="Ligera";
                    }else if(line.charAt(17)=='2'){
                        tipo_actividad="Medio";
                    }else if(line.charAt(17)=='3'){
                        tipo_actividad="Alta";
                    }else if(line.charAt(17)=='4'){
                        tipo_actividad="Intensa";
                    }
                    txtNIvelEjercicio.setText(tipo_actividad);
                    txtNIvelEjercicio.setGravity(Gravity.CENTER);
                    txtNIvelEjercicio.setPadding(0, 0, 5, 0);
                    txtNIvelEjercicio.setTextSize(12);
                    txtNIvelEjercicio.setTypeface(null, Typeface.ITALIC);
                    txtNIvelEjercicio.setLayoutParams(layoutNivelEjercicio);


                    String fatiga="";

                    if(line.charAt(19)=='0'){
                        fatiga="Nada";
                    }else if(line.charAt(19)=='1'){
                        fatiga="Leve";
                    }else if(line.charAt(19)=='2'){
                        fatiga="Ligera";
                    }else if(line.charAt(19)=='3'){
                        fatiga="Media";
                    }else if(line.charAt(19)=='4'){
                        fatiga="Alta";
                    }else if(line.charAt(19)=='5'){
                        fatiga="Severa";
                    }else if(line.charAt(19)=='6'){
                        fatiga="Exacer.";
                    }

                    txtFatiga.setText(fatiga);
                    txtFatiga.setGravity(Gravity.CENTER);
                    txtFatiga.setPadding(0, 0, 5, 0);
                    txtFatiga.setTextSize(12);
                    txtFatiga.setTypeface(null, Typeface.ITALIC);
                    txtFatiga.setLayoutParams(layoutFatiga);

                    String fiebre="";
                    if(line.charAt(21)=='0'){
                        fiebre="No";
                    }else if(line.charAt(21)=='1'){
                        fiebre="Si";
                    }

                    txtFiebre.setText(fiebre);
                    txtFiebre.setGravity(Gravity.CENTER);
                    txtFiebre.setPadding(0, 0, 5, 0);
                    txtFiebre.setTextSize(12);
                    txtFiebre.setTypeface(null, Typeface.ITALIC);
                    txtFiebre.setLayoutParams(layoutFiebre);

                    fila.addView(txtFecha);
                    fila.addView(txtEsputo);
                    fila.addView(txtNIvelEjercicio);
                    fila.addView(txtFatiga);
                    fila.addView(txtFiebre);


                    tabladatos.addView(fila);

                    // Línea que separa cada fila de datos
                    TableRow separador_filas = new TableRow(this);
                    separador_filas.setLayoutParams(new TableLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    FrameLayout linea = new FrameLayout(this);
                    TableRow.LayoutParams linea_totales_params =
                            new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, 2);
                    linea_totales_params.span = 6;
                    linea.setBackgroundColor(Color.parseColor("#4FC3F7"));
                    linea.setPadding(0,5,0,0);
                    separador_filas.addView(linea, linea_totales_params);
                    tabladatos.addView(separador_filas);

                }
            } while (line != null);
            b.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }




        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);


       aceptar.setOnClickListener(
               new View.OnClickListener(){
                   @Override
                   public void onClick(View v){
                        dialog.dismiss();
                   }
               }
       );



        return dialog;
    }





    /**
     * Crea Dialogo Perzonalizado para seleccionar el tipo de ejercicio
     */

    public AlertDialog createEjericiosDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.ejercicios_fisicos_dialog, null);

        builder.setView(v);

        Button iniciar = (Button) v.findViewById(R.id.btn_iniciar_ejercicios);
        Button pausar = (Button) v.findViewById(R.id.btn_pausar_ejercicios);
        Button cancelar = (Button) v.findViewById(R.id.btn_cancelar_ejercicios);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);


        iniciar.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                    }
                }
        );


        pausar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }

        );

        cancelar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }

        );


        return dialog;
    }





    /**
     * Crea Dialogo Perzonalizado para seleccionar el tipo de ejercicio
     */

    public AlertDialog createMenuCitasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.citas_menu_dialog, null);

        builder.setView(v);

        Button agregar_cita = (Button) v.findViewById(R.id.btn_agregar_cita);
        Button mostrar_citas = (Button) v.findViewById(R.id.btn_mostrar_citas);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);


        agregar_cita.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
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

                        dialog.dismiss();
                    }
                }
        );

        mostrar_citas.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialogo_mostrar_citas = createmostrarCitasDialog();
                        //dialogo_menuCitas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 600);
                        dialogo_mostrar_citas.show();
                    }
                }

        );



        return dialog;
    }





    /**
     * Crea Dialogo Perzonalizado para los informes Medicos del paciente
     */

    public AlertDialog createmostrarCitasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();

        View v = inflater.inflate(R.layout.mostrar_citas_dialog, null);

        builder.setView(v);

        Button aceptar = (Button) v.findViewById(R.id.btn_aceptar_citas);

        TableLayout tablacitas=(TableLayout) v.findViewById(R.id.tabla_citas);

        TableRow.LayoutParams layoutFila=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams layoutFecha_completa=new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        TableRow fila;
        TextView txtFecha_completa;


        //Reseteamos la tabla al entrar
        tablacitas.removeAllViews();

        //Formateo Cabecera
        String cabeceras[] = { "Fecha y Hora" };
        TableRow cabecera = new TableRow(this);
        cabecera.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        tablacitas.addView(cabecera);
        // Textos de la cabecera
        for (int i = 0; i < 1; i++)
        {
            TextView columna = new TextView(this);
            columna.setLayoutParams(new TableRow.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            columna.setText(cabeceras[i]);
            columna.setTextColor(Color.parseColor("#4FC3F7"));
            columna.setGravity(Gravity.CENTER_HORIZONTAL);
            columna.setTypeface(null, Typeface.BOLD_ITALIC);
            columna.setPadding(5, 5, 5, 5);
            cabecera.addView(columna);
        }
        // Línea que separa la cabecera de los datos
        TableRow separador_cabecera = new TableRow(this);
        separador_cabecera.setLayoutParams(new TableLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        FrameLayout linea_cabecera = new FrameLayout(this);
        TableRow.LayoutParams linea_cabecera_params =
                new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, 2);
        linea_cabecera_params.span = 6;
        linea_cabecera.setBackgroundColor(Color.parseColor("#FFFFFF"));
        separador_cabecera.addView(linea_cabecera, linea_cabecera_params);
        tablacitas.addView(separador_cabecera);



        try {

            FileInputStream fin = MainActivity.this.openFileInput("registro_citas");

            InputStreamReader is = new InputStreamReader(fin);

            BufferedReader b = new BufferedReader(is);
            String line = "";
            do {
                line = b.readLine();
                if (line != null) {


                    fila=new TableRow(MainActivity.this);
                    fila.setLayoutParams(layoutFila);

                    txtFecha_completa=new TextView(this);


                    txtFecha_completa.setText(line);
                    txtFecha_completa.setGravity(Gravity.CENTER);
                    txtFecha_completa.setPadding(0, 0, 5, 0);
                    txtFecha_completa.setTextSize(12);
                    txtFecha_completa.setTypeface(null, Typeface.ITALIC);
                    txtFecha_completa.setLayoutParams(layoutFecha_completa);


                    fila.addView(txtFecha_completa);


                    tablacitas.addView(fila);

                    // Línea que separa cada fila de datos
                    TableRow separador_filas = new TableRow(this);
                    separador_filas.setLayoutParams(new TableLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    FrameLayout linea = new FrameLayout(this);
                    TableRow.LayoutParams linea_totales_params =
                            new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, 2);
                    linea_totales_params.span = 6;
                    linea.setBackgroundColor(Color.parseColor("#4FC3F7"));
                    linea.setPadding(0,5,0,0);
                    separador_filas.addView(linea, linea_totales_params);
                    tablacitas.addView(separador_filas);

                }
            } while (line != null);
            b.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }




        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);


        aceptar.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        dialog.dismiss();
                    }
                }
        );



        return dialog;
    }

















}


