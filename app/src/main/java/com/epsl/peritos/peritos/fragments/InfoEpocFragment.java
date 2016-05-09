package com.epsl.peritos.peritos.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epsl.peritos.peritos.R;

/**
 * Created by noni_ on 02/05/2016.
 */
public class InfoEpocFragment extends Fragment {

    public InfoEpocFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_info_epoc, container, false);
    }
}

