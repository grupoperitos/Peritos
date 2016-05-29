package com.epsl.peritos.peritos.fragments;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.peritos.R;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Juan Carlos on 12/05/2016.
 */
public class InfoFragment extends Fragment {

    public static final String INFO_TYPE = "infotype";
    public static final String INFO_TITLE = "infottitle";
    public static final String INFO_CAPTION = "infocaption";
    public static final String INFO_MESSAGE = "infomessage";
    public static final String INFO_DETAIL = "infodetail";
    public static final String INFO_URL = "infourl";

    public static final String TAG_DETAILS = "details";
    private static final long MAIN_MESSAGE_TIME = 5000;

    private String mTitle = "";
    private String mMessage = "";
    private String mCaption = "";
    private String mDetail = "";
    private String mURL = "";
    int mType = -1;

    private View mFragmentView = null;
    private TextView mMessageView = null;
    private TextView mCaptionView = null;
    private TextView mTitleView = null;
    private ImageView mPlayVideo = null;

    private ScrollView mScrollCaption = null;
    private ScrollView mScrollMessage = null;

    private Handler mHandler = null;//Handled to manage tab iteration
    public static final int HANLDER_MESSAGE_CAPTION = 2;
    public static final int HANLDER_MESSAGE_COMMENTARY = 3;

    private long enqueue;
    private DownloadManager mDM;
    private MediaPlayer mMediaPlayer = null;

    private final int REQUEST_CODE_ASK_PERMISSIONS_WRITEEXTERNAL = 123;


    public static InfoFragment newInstance(int type, String title, String caption, String message, String detail, String url) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt(INFO_TYPE, type);
        args.putString(INFO_TITLE, title);
        args.putString(INFO_CAPTION, caption);
        args.putString(INFO_MESSAGE, message);
        args.putString(INFO_DETAIL, detail);
        args.putString(INFO_URL, url);
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
        args.putString(INFO_URL, message.getURL());
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
        mURL = getArguments().getString(INFO_URL);

        mFragmentView = inflater.inflate(R.layout.fragment_info, container, false);

        mMessageView = (TextView) mFragmentView.findViewById(R.id.txt_message);
        mCaptionView = (TextView) mFragmentView.findViewById(R.id.txt_caption);
        mTitleView = (TextView) mFragmentView.findViewById(R.id.txt_title);
        mPlayVideo = (ImageView) mFragmentView.findViewById(R.id.info_play);

        mScrollCaption = (ScrollView) mFragmentView.findViewById(R.id.scroll_caption);
        mScrollMessage = (ScrollView) mFragmentView.findViewById(R.id.scroll_message);

        showHideVideoButton();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = mDM.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {


                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            Snackbar.make(mFragmentView, "Descargado " + uriString, Snackbar.LENGTH_SHORT).show();
                            startVideo();
                        }
                    }
                }
            }
        };

        getActivity().registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        mPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mURL.equals("-")) {
                    int hasWriteContactsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(getString(R.string.info_fragment_permiso),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(getActivity(),
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS_WRITEEXTERNAL);
                                        }
                                    });
                            return;
                        }
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_ASK_PERMISSIONS_WRITEEXTERNAL);
                        return;
                    } else {
                        startVideo();
                    }
                }
            }
        });
        showTitle(mTitle);
        showCaption(mCaption);
        showMessage(mMessage);


        return mFragmentView;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void showHideVideoButton() {
        if (isExternalStorageWritable() && !mURL.isEmpty() && mURL.equals("-")) {
            mPlayVideo.setVisibility(View.VISIBLE);
        } else {
            mPlayVideo.setVisibility(View.INVISIBLE);
        }
    }


    protected void startVideo() {
        try {
            URL url = new URL(mURL);
            String path = url.getFile();
            String fileName = Uri.decode(path);
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName);

            if (file.exists()) {//The file was already created
                String videoUrl = "file://" + file.getPath(); // your URL here
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(file.getPath());
                mMediaPlayer.prepare(); // might take long! (for buffering, etc)
                mMediaPlayer.start();
            } else {//The file does not exists, mainly due to a first run
                mDM = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(mURL));


//                            //Restrict the types of networks over which this download may proceed.
//                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//                            //Set whether this download may proceed over a roaming connection.
//                            request.setAllowedOverRoaming(false);
//                            //Set the title of this download, to be displayed in notifications (if enabled).
                request.setTitle(getString(R.string.app_name) + " " + file.getPath());
//                            //Set a description of this download, to be displayed in notifications (if enabled)
//                            request.setDescription("Android Data download using DownloadManager.");
//                            //Set the local destination for the downloaded file to a path within the application's external files directory
//                            //request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"CountryList.json");
//
                request.setDestinationUri(Uri.fromFile(file));
                enqueue = mDM.enqueue(request);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_WRITEEXTERNAL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startVideo();
                } else {
                    // Permission Denied
                    Snackbar.make(mFragmentView, getString(R.string.info_fragment_permisoDenegado), Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.ok), okListener)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create()
                .show();
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

        mHandler = new Handler(Looper.getMainLooper()) {
            /*
                     * handleMessage() defines the operations to perform when
                     * the Handler receives a new Message to process.
                     */
            @Override
            public void handleMessage(Message inputMessage) {

                switch (inputMessage.what) {
                    case HANLDER_MESSAGE_CAPTION:
                        //Cambiar la pestaña activa
                        mHandler.removeMessages(HANLDER_MESSAGE_CAPTION);
                        mCaptionView.post(new FlashMessage(mCaptionView, mMessageView, mCaption));

                        Message msgObj = mHandler.obtainMessage();
                        msgObj.what = HANLDER_MESSAGE_COMMENTARY;
                        mHandler.sendMessageDelayed(msgObj, MAIN_MESSAGE_TIME);
                        break;
                    case HANLDER_MESSAGE_COMMENTARY:
                        //Cambiar la pestaña activa
                        mHandler.removeMessages(HANLDER_MESSAGE_COMMENTARY);
                        mCaptionView.post(new FlashMessage(mCaptionView, mMessageView, mMessage));

                        Message msgObj2 = mHandler.obtainMessage();
                        msgObj2.what = HANLDER_MESSAGE_CAPTION;
                        mHandler.sendMessageDelayed(msgObj2, MAIN_MESSAGE_TIME);
                        break;

                    default:

                        break;
                }
            }


        };


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        showHideVideoButton();
        Message msgObj = mHandler.obtainMessage();
        msgObj.what = HANLDER_MESSAGE_COMMENTARY;
        mHandler.sendMessageDelayed(msgObj, MAIN_MESSAGE_TIME);

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
            mType = message.getType();
            mTitle = message.getTitle();
            mCaption = message.getCaption();
            mMessage = message.getCommentary();
            mDetail = message.getDetail();
            mURL = message.getURL();
            showCaption(mCaption);
            showMessage(mMessage);
            showTitle(mTitle);

            if (mURL.equals("-"))
                mPlayVideo.setVisibility(View.INVISIBLE);
            else
                mPlayVideo.setVisibility(View.VISIBLE);


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
        outState.putString(INFO_URL, mURL);

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

        //mCaptionTextView.postDelayed(new FlashMessage(mCaptionTextView, mMessageTextView, mMessage1), MAIN_MESSAGE_TIME);

    }
}


}
