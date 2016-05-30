package com.epsl.peritos.peritos.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.epsl.peritos.CustomViewPager;
import com.epsl.peritos.Fragment_Hour;
import com.epsl.peritos.Fragment_Name;
import com.epsl.peritos.Fragment_Qr;
import com.epsl.peritos.StructureParametersBBDD;
import com.epsl.peritos.communications.CommunicationAsynctask;
import com.epsl.peritos.peritos.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    android.support.v4.app.FragmentManager fm;

    List<Fragment> fragments;
    Button btn;
    StructureParametersBBDD.Treatment treatment;
    CustomViewPager viewPager;
    SampleFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act_layout);

//        Intent i = new Intent(this,MyserviceTwo.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.setFlags(Intent.FLAG_FROM_BACKGROUND);
//        i.setAction(Constants.INSTALLAPP);
//        startService(i);
//
//        Intent in = new Intent(this,MainActivity.class);
//        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        in.setFlags(Intent.FLAG_FROM_BACKGROUND);
//        startActivity(in);


        //Código para obtener el GCM_ID y registrarse en el servidor en GAE
        //En caso de que no haya conexión, decirle al usuario que active el WiFi y vuelva a inicar la app cuando tenga conexión
        final SharedPreferences prefs = getSharedPreferences("PRFS", Context.MODE_PRIVATE);
        String registrationId = prefs.getString("REG_ID", "");
        if (registrationId.isEmpty()) {
            new GcmRegistrationAsyncTask(LoginActivity.this).execute();
        }

        viewPager = (CustomViewPager) findViewById(R.id.myViewPager);
        adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(Fragment_Name.newInstance());


        viewPager.setAdapter(adapter);

        viewPager.setPagingEnabled(false);


        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                System.out.println(viewPager.getCurrentItem());


                adapter.addFrag(Fragment_Qr.newInstance());
                adapter.addFrag(Fragment_Hour.newInstance());
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                System.out.println("tumtumpak");
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {

                switch (position){

                    case 0:



                        break;

                    case 1:


                        break;

                    case 2:
                        Fragment_Qr f = (Fragment_Qr ) fragments.get(1);


                        Fragment_Hour h = (Fragment_Hour ) fragments.get(2);
                        h.setListTreatment(f.getListTreatment());
                        break;



                }




                // Check if this is the page you want.
            }
        });

    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {



        private Context context;
        private int position;

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<Fragment>();

        }

        public void addFrag(Fragment fragment) {
            fragments.add(fragment);

        }

        public void deleteFrags() {
            fragments.clear();

        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


    }



    //********************************************************************************************//
    //******** Clase asíncrona para la obtención del GCM_ID y el posterior registro en GAE *******//
    //********************************************************************************************//
    class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
        private GoogleCloudMessaging gcm;
        private Context context;
        public GcmRegistrationAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String SENDER_ID = "108686228916";
            String msg = "";

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                SharedPreferences prefs = getSharedPreferences("PRFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                //Obtain Google GCM ID and save it
                String regId = gcm.register(SENDER_ID);
                editor.putString("REG_ID", regId);
                editor.apply();

                //Get actual date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String fecha = sdf.format(new Date());

                //Mensaje que se enviará al servidor -> GCM_REG_ID@FECHA_ACTUAL
                msg = regId+"%"+fecha;

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            new CommunicationAsynctask(context).execute("REG",msg);
        }
    }
}
