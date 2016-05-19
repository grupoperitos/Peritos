package com.epsl.peritos.peritos.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.epsl.peritos.peritos.R;

/**
 * Created by Juan Carlos on 19/05/2016.
 */


public class DetailsFragment extends android.support.v4.app.DialogFragment {
    public static long MAX_WAIT_TIME = 20000;

    public static final String INFO_TYPE = "infotype";
    public static final String INFO_TITLE = "infottitle";
    public static final String INFO_DETAIL = "infodetail";


    private String mTitle = "";
    private String mDetail = "";
    private int mType = -1;



    /**
     * Create a new instance of DetailsFragmente
     */
    static DetailsFragment newInstance(int type,String title, String details) {
        DetailsFragment f = new DetailsFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(INFO_TYPE, type);
        args.putString(INFO_TITLE, title);
        args.putString(INFO_DETAIL, details);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mType = getArguments().getInt(INFO_TYPE);
        mTitle = getArguments().getString(INFO_TYPE);
        mDetail = getArguments().getString(INFO_DETAIL);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;

        setStyle(style,theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_details, container, false);
        getDialog().setTitle(mTitle);

        WebView web = (WebView) view.findViewById(R.id.text_details);
        web.loadData(mDetail, "text/html", null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(MAX_WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        }
        ).start();

        Button volver = (Button)view.findViewById(R.id.button_back);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        return view;
    }
}