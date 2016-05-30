package com.epsl.peritos;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.epsl.peritos.peritos.R;

import java.util.HashMap;


public class Fragment_Name extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {




    StructureParametersBBDD.Treatment treatment;
    CustomViewPager vp;
    EditText nombre;
    EditText apellido1;
    EditText apellido2;
    Button bt;

    public static Fragment_Name newInstance() {


        Bundle args = new Bundle();
        //   args.putSerializable("dataUser", dataUser);
        Fragment_Name fragment = new Fragment_Name();
        // fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        bt = (Button) getActivity().findViewById(R.id.button);
        bt.setVisibility(View.VISIBLE);
        super.onResume();
        System.out.println(" public void onResume() ");



    }
    @Override
    public void onPause() {
        super.onResume();
        System.out.println(" public void onPause() ");


    }
    @Override
    public void onStop() {
        super.onResume();
        System.out.println(" public void onStop() ");


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        bt = (Button) getActivity().findViewById(R.id.button);
        bt.setVisibility(View.VISIBLE);

        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        // dataUser = (Data_User) bundle.getSerializable("dataUser");
        context = getActivity();
    }


    Context context;
    SliderLayout mDemoSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bt = (Button) getActivity().findViewById(R.id.button);
        bt.setVisibility(View.VISIBLE);
        bt.setEnabled(false);
        vp = (CustomViewPager) getActivity().findViewById(R.id.myViewPager);
        Bundle bundle = getArguments();
        View rootView = inflater.inflate(R.layout.name_layout, container, false);

        // dataUser = (Data_User) bundle.getSerializable("dataUser");
        context = getActivity().getApplicationContext();

        boolean isEmpty = true;
        nombre = (EditText) rootView.findViewById(R.id.nombre);
        apellido1 = (EditText) rootView.findViewById(R.id.apellido1);
        apellido2 = (EditText) rootView.findViewById(R.id.apellido2);

        mDemoSlider = (SliderLayout)rootView.findViewById(R.id.slider);

        nombre.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkData();
            }
        });

        apellido1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkData();
            }
        });

        apellido2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                checkData();
            }
        });




        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Banner1",R.drawable.banner1);
        file_maps.put("Banner2",R.drawable.banner2);


        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity().getApplicationContext());
            // initialize a SliderLayout
            textSliderView


                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);



            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.CubeIn);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);


        return rootView;
    }




    public  void checkData(){

        if(nombre.getText().toString().isEmpty() == false  && apellido1.getText().toString().isEmpty() == false && apellido2.getText().toString().isEmpty() == false  ){


            bt.setEnabled(true);


        }



    }

    public StructureParametersBBDD.Treatment getTreatment() {
        return treatment;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}

