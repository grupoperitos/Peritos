package com.epsl.peritos.peritos.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;

/**
 * Created by Juan Carlos on 12/05/2016.
 */
public class InfoFragment extends Fragment {

    public static final String INFO_TYPE = "infotype";

    private String mTitle = "";
    private TextView mMessageView = null;
    private TextView mTitleView = null;

    public static InfoFragment newInstance(String title) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(INFO_TYPE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTitle = this.getArguments().getString(INFO_TYPE);

        View view = inflater.inflate(R.layout.fragment_dieta, container, false);

        mMessageView = (TextView) view.findViewById(R.id.txt_message);
        mTitleView = (TextView) view.findViewById(R.id.txt_title);
        showTitle(mTitle);
        showMessage(mTitle);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    protected void showTitle(String message) {
        mTitleView.setText(message);
        mTitleView.invalidate();
    }

    protected void showMessage(String message) {
        mMessageView.setText(message);
        mMessageView.invalidate();
    }


    public void actualize(InformationMessage message) {
        if (message != null) {
            showMessage(message.getMessage());
            showTitle(message.getTitle());

        }

    }
}
