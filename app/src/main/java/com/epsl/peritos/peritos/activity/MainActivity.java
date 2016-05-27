package com.epsl.peritos.peritos.activity;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pools;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.epsl.peritos.info.InformationManager;
import com.epsl.peritos.info.MessageList;
import com.epsl.peritos.info.MessageTypes;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.fragments.InfoFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private Handler mHandler=null;//Handled to manage tab iteration
    public static final String CARRUSEL = "carrusel";

    //Control del tiempo que está cada tab activo
    public final long MAXPAGE_WAIT = 5000;//5 segundos
    private long tabchangeInstant = 0L;
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
    int valor_esputo_estado_inicial = 1000;
    int valor_pacientes_estado_inicial = 1000;
    int valor_fatiga_estado_inicial = 1000;

    //Mensajes
    public static MessageList messageList = null;
    public static MessageList tratamientoList = null;
    public static MessageList dietaList = null;
    public static MessageList ejercicioList = null;
    public static MessageList epocList = null;


    static final int DIALOG_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageList = InformationManager.loadInformation(this);
        if (messageList != null) {
            tratamientoList = messageList.getMessagesByType(MessageTypes.INFO_TRATAMIENTO);
            dietaList = messageList.getMessagesByType(MessageTypes.INFO_DIETA);
            ejercicioList = messageList.getMessagesByType(MessageTypes.INFO_EJERCICIO);
            epocList = messageList.getMessagesByType(MessageTypes.INFO_EPOC);
        } else {
            Toast.makeText(this, "Error al cargar el fichero de recursos", Toast.LENGTH_LONG).show();
            finish();
        }


        setContentView(R.layout.activity_main);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(this);
        setupTabIcons();


        //MiniFAB salud Responde
        miniFAB_SR = (FloatingActionButton) findViewById(R.id.fab_llamar_SR);
        miniFAB_SR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //902505060
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
        miniFAB_Sintomas = (FloatingActionButton) findViewById(R.id.fab_control_sintomas);
        miniFAB_Sintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialogo_sintomas = createSintomasDialog();
                //dialogo_sintomas.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, 1000);
                dialogo_sintomas.show();
            }
        });


        //MiniFAB llamar cuidador
        miniFAB_Cuidador = (FloatingActionButton) findViewById(R.id.fab_llamar_cuidador);
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

                Snackbar.make(view, getString(R.string.interfaz_cuidador) + "OBTENER NOMBRE DE AGENDA", Snackbar.LENGTH_LONG)
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        //tabLayout.post(new Carrusel());

        mHandler = new Handler(Looper.getMainLooper()) {
            /*
                     * handleMessage() defines the operations to perform when
                     * the Handler receives a new Message to process.
                     */
            @Override
            public void handleMessage(Message inputMessage) {

                switch(inputMessage.what){
                    case 1:
                        tabLayout.post(new Carrusel());
                        mHandler.removeMessages(1);
                        Snackbar.make(viewPager, "Handler 1", Snackbar.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Snackbar.make(viewPager, "Handler 2", Snackbar.LENGTH_SHORT).show();
                        break;
                    default:
                        Snackbar.make(viewPager, "Handler default", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }


        };

        Message msgObj = mHandler.obtainMessage();
        msgObj.what=1;
        mHandler.sendMessageDelayed(msgObj,MAXPAGE_WAIT);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(InfoFragment.newInstance(tratamientoList.getNextMessage()), "Tab_Tratamiento");
        adapter.addFrag(InfoFragment.newInstance(dietaList.getNextMessage()), "Tab_Dieta");
        adapter.addFrag(InfoFragment.newInstance(ejercicioList.getNextMessage()), "Tab_Ejercicio");
        adapter.addFrag(InfoFragment.newInstance(epocList.getNextMessage()), "Tab_Epoc");


        viewPager.setAdapter(adapter);


        tabchangeInstant = System.currentTimeMillis();
    }

    @Override
    public void onStart() {
        super.onStart();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.epsl.peritos.peritos.activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.epsl.peritos.peritos.activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        Message msgObj = mHandler.obtainMessage();
        msgObj.what=1;
        mHandler.sendMessageDelayed(msgObj,MAXPAGE_WAIT);

        //Snackbar.make(viewPager, "tab tocada 1", Snackbar.LENGTH_SHORT).show();


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

        Message msgObj = mHandler.obtainMessage();
        msgObj.what=1;
        mHandler.sendMessageDelayed(msgObj,MAXPAGE_WAIT);
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

        public void addFrag(InfoFragment fragment, String title) {
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

    //obtener el dia seleccionado y demás de dentro del metodo onDataSet
    String var = "";

    private String obtenerDia(String hola) {
        return hola;
    }


    @Override
    public void onDateSet(DatePickerDialog v, int ano, int mes_ano, int dia_mes) {
        //POR DEFECTO EN JAVA LOS MESES VAN DE 0 A 11, POR ESO SUMAMOS 1, PARA MOSTRARLO BIEN
        mes_ano = mes_ano + 1;
        String mes = "";
        String dia = "";
        //Añadir 0 delante si mes es menor que 10
        if (mes_ano < 10) {
            mes = "0" + mes_ano;
        } else {
            mes = String.valueOf(mes_ano);
        }
        //Aádir 0 delante si dia es menor que 10
        if (dia_mes < 10) {
            dia = "" + "0" + dia_mes;
        } else {
            dia = String.valueOf(dia_mes);
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
        var = this.obtenerDia(dia);

    }


    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");

        if (dpd != null) dpd.setOnDateSetListener(this);
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }


    @Override
    public void onTimeSet(RadialPickerLayout v, int hora_dia, int min, int sec) {
        String h = "";
        String m = "";
        //Añadir 0 delante si hora es menor que 10
        if (hora_dia < 10) {
            h = "0" + hora_dia;
        } else {
            h = String.valueOf(hora_dia);
        }
        //Añadir 0 delante si minuto es menor que 10
        if (min < 10) {
            m = "0" + min;
        } else {
            m = String.valueOf(min);
        }
        Toast.makeText(
                this, "La hora de la cita es: " + h + ":" + m, Toast.LENGTH_LONG).show();
    }


    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de login
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


        //Array de 4 numeros, que va a almacenar el estado de cada una de las 3
        //seekbars, asi como el de la fiebre para luego almacenarlos en el fichero de texto.
        final int[] estados_seekbar = new int[4];


        //Barra de Color de Esputo
        SeekBar seekBarColorEsputo = (SeekBar) v.findViewById(R.id.seekBar_estado_esputo);
        seekBarColorEsputo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_esputo_estado_inicial = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[0] = seekBarProgress;
                valor_esputo_estado_inicial = seekBarProgress;
            }


        });


        //Barra de estado de paciente
        SeekBar seekBarEstadoPaciente = (SeekBar) v.findViewById(R.id.seekBar_estado_paciente);
        seekBarEstadoPaciente.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_pacientes_estado_inicial = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[1] = seekBarProgress;
                valor_pacientes_estado_inicial = seekBarProgress;
            }
        });


        //Barra de estado Fatiga
        SeekBar seekBarEstadoFatiga = (SeekBar) v.findViewById(R.id.seekBar_estado_fatiga);
        seekBarEstadoFatiga.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress;
                valor_fatiga_estado_inicial = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                estados_seekbar[2] = seekBarProgress;
                valor_fatiga_estado_inicial = seekBarProgress;
            }
        });

        final ToggleImageButton toggleFiebre = (ToggleImageButton) v.findViewById(R.id.tb_fiebre);

        aceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String fecha_actual = sdf.format(new Date());
                        if (valor_esputo_estado_inicial == 1000) {
                            estados_seekbar[0] = 1;
                        }
                        if (valor_pacientes_estado_inicial == 1000) {
                            estados_seekbar[1] = 3;
                        }
                        if (valor_fatiga_estado_inicial == 1000) {
                            estados_seekbar[2] = 3;
                        }
                        int chek;
                        if (toggleFiebre.isChecked()) {
                            estados_seekbar[3] = 1;
                        } else {
                            estados_seekbar[3] = 0;
                        }

                        Toast.makeText(MainActivity.this, "La fecha actual es: " + fecha_actual + "Estado" +
                                "Esputo: " + estados_seekbar[0] + " Estado Paciente: " + estados_seekbar[1] + "" +
                                "Fatiga: " + estados_seekbar[2] + "Fiebre: " + estados_seekbar[3], Toast.LENGTH_SHORT).show();

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
            TabLayout.Tab tab = tabLayout.getTabAt(newPos);

            tab.select();
            tabLayout.setScrollPosition(newPos,0f,true);
            tabLayout.invalidate();
            viewPager.setCurrentItem(newPos,true);
            viewPager.invalidate();


        }
    }
}


