package com.epsl.peritos.peritos.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.epsl.peritos.CustomViewPager;
import com.epsl.peritos.Fragment_Hour;

import com.epsl.peritos.peritos.R;
import com.epsl.peritos.sintomas_registro.StructureParametersBBDD;

import java.util.ArrayList;
import java.util.List;


public class OnlyTimePickerActivity extends AppCompatActivity {

    FragmentManager fm;



    List<Fragment> fragments;
    Button btn;
    StructureParametersBBDD.Treatment treatment;
    CustomViewPager viewPager;
    SampleFragmentPagerAdapter adapter;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences pr = getSharedPreferences("PRFS", MODE_PRIVATE);
        boolean comprobar_tratamiento = pr.getBoolean("NEW_HOUR", false);
        if(comprobar_tratamiento==true){

            Intent i = new Intent(OnlyTimePickerActivity.this,PreferenciasActivity.class);
            startActivity(i);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act_layout);





        viewPager = (CustomViewPager) findViewById(R.id.myViewPager);
        adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(Fragment_Hour.newInstance());


        viewPager.setAdapter(adapter);

        viewPager.setPagingEnabled(false);


        viewPager.setCurrentItem(viewPager.getCurrentItem());



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




}
