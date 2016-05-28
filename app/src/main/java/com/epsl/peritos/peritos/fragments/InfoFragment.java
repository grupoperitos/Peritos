package com.epsl.peritos.peritos.fragments;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
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
    private static final long MAIN_MESSAGE_TIME = 10000;

    private String mTitle = "";
    private String mMessage = "";
    private String mCaption = "";
    private String mDetail = "";
    int mType = -1;

    private TextView mMessageView = null;
    private TextView mCaptionView = null;
    private TextView mTitleView = null;

    private ScrollView mScrollCaption = null;
    private ScrollView mScrollMessage = null;

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

    public static InfoFragment newInstance(InformationMessage message) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt(INFO_TYPE, message.getType());
        args.putString(INFO_TITLE, message.getTitle());
        args.putString(INFO_CAPTION, message.getCaption());
        args.putString(INFO_MESSAGE, message.getCommentary());
        args.putString(INFO_DETAIL, message.getDetail());
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
        mCaptionView = (TextView) view.findViewById(R.id.txt_caption);
        mTitleView = (TextView) view.findViewById(R.id.txt_title);

        mScrollCaption = (ScrollView) view.findViewById(R.id.scroll_caption);
        mScrollMessage = (ScrollView) view.findViewById(R.id.scroll_message);


        showTitle(mTitle);
        showCaption(mCaption);
        showMessage(mMessage);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog details = createDetailsDialog();
                details.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                details.show();
                //Llamar al método de la interfaz con la actividad para que añada los puntos de logro
            }
        });
        mCaptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog details = createDetailsDialog();
                details.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                details.show();
                //Llamar al método de la interfaz con la actividad para que añada los puntos de logro
            }
        });

        mCaptionView.postDelayed(new FlashMessage(mCaptionView, mMessageView, mMessage), MAIN_MESSAGE_TIME*2);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public AlertDialog createDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_details, null);

        builder.setView(v);

        Button aceptar = (Button) v.findViewById(R.id.btn_aceptar);

        WebView web = (WebView) v.findViewById(R.id.text_details);
        web.loadData(mDetail, "text/html", null);

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        aceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                }
        );


        return dialog;
    }

    public synchronized void actualizeMessage(int type, String title, String caption, String message, String detail) {
        mType = type;
        mTitle = title;
        mCaption = caption;
        mMessage = message;
        mDetail = detail;
    }

    public String getTitle() {
        return mTitle;
    }

    protected void showTitle(String message) {
        mTitleView.setText(message);
        mTitleView.invalidate();
    }

    protected void showMessage(String message) {
        mMessageView.setText(message);
        mMessageView.invalidate();
    }

    protected void showCaption(String message) {
        mCaptionView.setText(message);
        mCaptionView.invalidate();
    }

    public void actualize(InformationMessage message) {
        if (message != null) {
            showCaption(message.getCaption());
            showMessage(message.getCommentary());
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


        private TextView mCaptionTextView = null;
        private TextView mMessageTextView = null;
        private String mMessage1 = "";
        protected Context mContext = null;

        FlashMessage(/*Context context,*/ TextView captionview, TextView messageview, String message) {
            //mContext = context;
            mCaptionTextView = captionview;
            mMessageTextView = messageview;
            mMessage1 = message;
        }

        @Override
        public void run() {

            if (mMessage1.equals(mCaption)) {
                mMessageTextView.setText(mMessage);
                mMessageTextView.postInvalidate();

                final AnimatorSet set1 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                        R.animator.fade_out);
                final AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                        R.animator.fade_in);

                set1.setTarget(mScrollCaption);
                set1.start();
                set2.setTarget(mScrollMessage);
                set1.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        set2.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }


                });


                mMessage1 = mMessage;
            } else {
                mCaptionTextView.setText(mCaption);
                mCaptionTextView.postInvalidate();
                final AnimatorSet set1 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                        R.animator.fade_out);
                final AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),
                        R.animator.fade_in);
                set1.setTarget(mScrollMessage);
                set1.start();
                set2.setTarget(mScrollCaption);
                set1.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        set2.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }


                });

                mMessage1 = mCaption;

            }

            mCaptionTextView.postDelayed(new FlashMessage(mCaptionTextView, mMessageTextView, mMessage1), MAIN_MESSAGE_TIME);

        }
    }


}
