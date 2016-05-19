package com.epsl.peritos.peritos.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.peritos.R;

/**
 * Created by Juan Carlos on 12/05/2016.
 */
public class InfoFragment extends Fragment {

    public static final String INFO_TYPE = "infotype";
    public static final String INFO_TITLE = "infottitle";
    public static final String INFO_CAPTION = "infocaption";
    public static final String INFO_MESSAGE = "infomessage";
    public static final String INFO_DETAIL = "infodetail";

    public static final String TAG_DETAILS = "details";
    private static final long MAIN_MESSAGE_TIME = 5000;

    private String mTitle = "";
    private String mMessage = "";
    private String mCaption = "";
    private String mDetail = "";
    int mType = -1;

    private TextView mMessageView = null;
    private TextView mTitleView = null;

    public static InfoFragment newInstance(int type, String title, String caption, String message, String detail) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt(INFO_TYPE, type);
        args.putString(INFO_TITLE, title);
        args.putString(INFO_CAPTION, caption);
        args.putString(INFO_MESSAGE, message);
        args.putString(INFO_DETAIL, detail);
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
        mType = getArguments().getInt(INFO_TYPE);
        mTitle = getArguments().getString(INFO_TITLE);
        mCaption = getArguments().getString(INFO_CAPTION);
        mMessage = getArguments().getString(INFO_MESSAGE);
        mDetail = getArguments().getString(INFO_DETAIL);

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        mMessageView = (TextView) view.findViewById(R.id.txt_message);
        mTitleView = (TextView) view.findViewById(R.id.txt_title);



        showTitle(mTitle);
        showMessage(mCaption);

        mMessageView.postDelayed(new FlashMessage(mMessageView,mMessage),MAIN_MESSAGE_TIME);

        mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetailsDialog();
                //Llamar al método de la interfaz con la actividad para que añada los puntos de logro
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Se guarda el estado que permite actualizar el contenido del fragmento
        outState.putInt(INFO_TYPE, mType);
        outState.putString(INFO_TITLE, mTitle);
        outState.putString(INFO_CAPTION, mCaption);
        outState.putString(INFO_MESSAGE, mMessage);
        outState.putString(INFO_DETAIL, mDetail);

    }

    void showDetailsDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        DialogFragment newFragment = DetailsFragment.newInstance(mType, mTitle, mDetail);
        newFragment.show(fm, TAG_DETAILS);
    }


    public class FlashMessage implements Runnable {

        String mMessage1 = "";

        TextView mFlashTextView = null;

        FlashMessage(TextView text, String message) {
            mFlashTextView = text;
            mMessage1 = message;
        }

        @Override
        public void run() {
            mFlashTextView.setText(mMessage1);
            mFlashTextView.postInvalidate();
            if (mMessage1.equals(mCaption))
                mMessage1 = mMessage;
            else
                mMessage1 = mCaption;
            mFlashTextView.postDelayed(new FlashMessage(mFlashTextView,mMessage1),MAIN_MESSAGE_TIME);

        }
    }



}
