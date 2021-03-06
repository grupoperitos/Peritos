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
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
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

import com.epsl.peritos.achievements.AchievementManager;
import com.epsl.peritos.info.InformationMessage;
import com.epsl.peritos.peritos.R;
import com.epsl.peritos.peritos.activity.MainActivity;

import java.io.File;
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
    public static final String INFO_CAPSULE = "infocapsule";
    public static final String INFO_INHALADOR = "ininh";
    public static final String INFO_ACHIEVEMENT = "achv";

    public static final String TAG_DETAILS = "details";


    private String mTitle = "";
    private String mMessage = "";
    private String mCaption = "";
    private String mDetail = "";
    private String mURL = "";
    private int    mAchivPoints = 0;
    int mType = -1;

    private View mFragmentView = null;
    private TextView mMessageView = null;
    private TextView mCaptionView = null;
    private TextView mTitleView = null;
    private ImageView mPlayVideo = null;
    private ImageView mPrevMessage = null;
    private ImageView mNextMessage = null;
    private ImageView mCapsule = null;
    private ImageView mInhalador = null;
    ImageView capsula;
    private ScrollView mScrollCaption = null;
    private ScrollView mScrollMessage = null;

    private Handler mHandler = null;//Handled to manage tab iteration

    private boolean mShowCapsule=false;
    private boolean mShowInhalador=false;

    private long enqueue;
    private DownloadManager mDM;
    private BroadcastReceiver mReceiver = null;

    private final int REQUEST_CODE_ASK_PERMISSIONS_WRITEEXTERNAL = 123;

    private MainActivity mActivity=null;


    public static InfoFragment newInstance(int type, String title, String caption, String message, String detail, String url,int points) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putInt(INFO_TYPE, type);
        args.putString(INFO_TITLE, title);
        args.putString(INFO_CAPTION, caption);
        args.putString(INFO_MESSAGE, message);
        args.putString(INFO_DETAIL, detail);
        args.putString(INFO_URL, url);
        args.putInt(INFO_ACHIEVEMENT,points);

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
        args.putInt(INFO_ACHIEVEMENT,message.getAchievement());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(INFO_TYPE);
            mTitle = getArguments().getString(INFO_TITLE);
            mCaption = getArguments().getString(INFO_CAPTION);
            mMessage = getArguments().getString(INFO_MESSAGE);
            mDetail = getArguments().getString(INFO_DETAIL);
            mURL = getArguments().getString(INFO_URL);
            mAchivPoints = getArguments().getInt(INFO_ACHIEVEMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(INFO_TYPE);
            mTitle = savedInstanceState.getString(INFO_TITLE);
            mCaption = savedInstanceState.getString(INFO_CAPTION);
            mMessage = savedInstanceState.getString(INFO_MESSAGE);
            mDetail = savedInstanceState.getString(INFO_DETAIL);
            mURL = savedInstanceState.getString(INFO_URL);
            mShowCapsule = savedInstanceState.getBoolean(INFO_CAPSULE);
            mShowInhalador = savedInstanceState.getBoolean(INFO_INHALADOR);
            mAchivPoints = savedInstanceState.getInt(INFO_ACHIEVEMENT);

        }
        mFragmentView = inflater.inflate(R.layout.fragment_info, container, false);

        mMessageView = (TextView) mFragmentView.findViewById(R.id.txt_message);
        mCaptionView = (TextView) mFragmentView.findViewById(R.id.txt_caption);
        mTitleView = (TextView) mFragmentView.findViewById(R.id.txt_title);
        mPlayVideo = (ImageView) mFragmentView.findViewById(R.id.info_play);
        mPrevMessage = (ImageView) mFragmentView.findViewById(R.id.info_rewind);
        mNextMessage = (ImageView) mFragmentView.findViewById(R.id.info_fforward);
        mCapsule = (ImageView)mFragmentView.findViewById(R.id.info_capsule);
        mInhalador = (ImageView)mFragmentView.findViewById(R.id.info_inhalador);



        mScrollCaption = (ScrollView) mFragmentView.findViewById(R.id.scroll_caption);
        mScrollMessage = (ScrollView) mFragmentView.findViewById(R.id.scroll_message);
        showTitle(mTitle);
        showCaption(mCaption);
        showMessage(mMessage);

        actualizeInterfaceButtons();

        return mFragmentView;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void actualizeInterfaceButtons() {
        if (mPlayVideo != null)
            if (isExternalStorageWritable() && !mURL.isEmpty() && !mURL.equals("-")) {
                mPlayVideo.setVisibility(View.VISIBLE);
            } else {
                mPlayVideo.setVisibility(View.INVISIBLE);

            }
        if(mCapsule!=null) {
            if (mShowCapsule)
                mCapsule.setVisibility(View.VISIBLE);
            else
                mCapsule.setVisibility(View.INVISIBLE);
        }

        if(mInhalador!=null) {
            if (mShowInhalador)
                mInhalador.setVisibility(View.VISIBLE);
            else
                mInhalador.setVisibility(View.INVISIBLE);
        }
    }

    /**
     *
     * @param show true to show
     */
    public void showCapsule(boolean show)
    {
        mShowCapsule=show;
        actualizeInterfaceButtons();

    }

    /**
     *
     * @param show true to show
     */
    public void showInhalador(boolean show)
    {
        mShowInhalador=show;
        actualizeInterfaceButtons();

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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
                startActivity(intent);

                //Iniciar el carrusel
                Message msgObj1 = mActivity.mHandler.obtainMessage();
                msgObj1.what=MainActivity.HANLDER_MESSAGE_STARTCARRUSEL;
                mActivity.mHandler.sendMessage(msgObj1);

                //Control de logros
                Message msgObj2 = mActivity.mHandler.obtainMessage();
                msgObj2.what=MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS;
                Bundle b = new Bundle();
                b.putInt(MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS_DATA, AchievementManager.ACHIEVE_VIDEO);
                msgObj2.setData(b);
                mActivity.mHandler.sendMessage(msgObj2);

            } else {//The file does not exists, mainly due to a first run
                //TODO: Parar el carrusel para cuando se recibe el video para que se reproduzca
                mDM = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(mURL));
                request.setTitle(getString(R.string.app_name) + " " + file.getPath());
                request.setDestinationUri(Uri.fromFile(file));
                enqueue = mDM.enqueue(request);
                Snackbar.make(mFragmentView, getString(R.string.info_fragment_descarga), Snackbar.LENGTH_LONG).show();

                //Parar el carrusel
                Message msgObj = mActivity.mHandler.obtainMessage();
                msgObj.what=MainActivity.HANLDER_MESSAGE_STOPCARRUSEL;
                mActivity.mHandler.sendMessageAtFrontOfQueue(msgObj);
            }

        } catch (MalformedURLException e) {
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

        mActivity = (MainActivity)getActivity();

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    if (mDM != null) {
                        Cursor c = mDM.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {
                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                                startVideo();


                            }
                        }
                        getActivity().unregisterReceiver(mReceiver);
                    }
                }

            }
        };

        getActivity().registerReceiver(mReceiver, new IntentFilter(
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


        mMessageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog details = createDetailsDialog();
                //details.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                details.show();
                //Control de logros
                Message msgObj = mActivity.mHandler.obtainMessage();
                msgObj.what=MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS;
                Bundle b = new Bundle();
                b.putInt(MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS_DATA, mAchivPoints);
                msgObj.setData(b);
                mActivity.mHandler.sendMessage(msgObj);
            }
        });
        mCaptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog details = createDetailsDialog();
                details.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                details.show();

                //Control de logros
                Message msgObj = mActivity.mHandler.obtainMessage();
                msgObj.what=MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS;
                Bundle b = new Bundle();
                b.putInt(MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS_DATA, mAchivPoints);
                msgObj.setData(b);
                mActivity.mHandler.sendMessage(msgObj);
            }
        });

        mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog details = createDetailsDialog();
                details.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 600);
                details.show();

                //Control de logros
                Message msgObj = mActivity.mHandler.obtainMessage();
                msgObj.what=MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS;
                Bundle b = new Bundle();
                b.putInt(MainActivity.HANLDER_MESSAGE_ACHIEVEMENT_POINTS_DATA, mAchivPoints);
                msgObj.setData(b);
                mActivity.mHandler.sendMessage(msgObj);
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
                    case MainActivity.HANLDER_MESSAGE_CAPTION:
                        //Cambiar la pestaña activa
                        mHandler.removeMessages(MainActivity.HANLDER_MESSAGE_CAPTION);


                        if(getActivity()!=null){
                            final AnimatorSet set1 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                                    R.animator.fade_out);
                            final AnimatorSet set2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
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
                            Message msgObj = mHandler.obtainMessage();
                            msgObj.what = MainActivity.HANLDER_MESSAGE_COMMENTARY;
                            mHandler.sendMessageDelayed(msgObj, MainActivity.MAIN_MESSAGE_TIME);
                        }



                        break;
                    case MainActivity.HANLDER_MESSAGE_COMMENTARY:
                        //Cambiar la pestaña activa
                        mHandler.removeMessages(MainActivity.HANLDER_MESSAGE_COMMENTARY);

                        if(getActivity()!=null){
                            final AnimatorSet set_c1 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                                    R.animator.fade_out);
                            final AnimatorSet set_c2 = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(),
                                    R.animator.fade_in);
                            set_c1.setTarget(mScrollMessage);
                            set_c1.start();
                            set_c2.setTarget(mScrollCaption);
                            set_c1.addListener(new Animator.AnimatorListener() {

                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    set_c2.start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });

                            Message msgObj2 = mHandler.obtainMessage();
                            msgObj2.what = MainActivity.HANLDER_MESSAGE_CAPTION;
                            mHandler.sendMessageDelayed(msgObj2, MainActivity.MAIN_MESSAGE_TIME);
                        }

                        break;

                    default:

                        break;
                }
            }
        };

        mPrevMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msgObj = mHandler.obtainMessage();
                msgObj.what = MainActivity.HANLDER_MESSAGE_PREV_TEXT;
                mActivity.mHandler.sendMessage(msgObj);
            }
        });

        mNextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msgObj = mHandler.obtainMessage();
                msgObj.what = MainActivity.HANLDER_MESSAGE_NEXT_TEXT;
                mActivity.mHandler.sendMessage(msgObj);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();


    }


    @Override
    public void onResume() {
        super.onResume();

        actualizeInterfaceButtons();

        Message msgObj = mHandler.obtainMessage();
        msgObj.what = MainActivity.HANLDER_MESSAGE_COMMENTARY;
        mHandler.sendMessageDelayed(msgObj, MainActivity.MAIN_MESSAGE_TIME);

    }

    public AlertDialog createDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_details,null);

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
        if (mTitleView != null) {
            mTitleView.setText(message);
            mTitleView.invalidate();
        }
    }

    protected void showMessage(String message) {
        if (mCaptionView != null) {
            mMessageView.setText(message);
            mMessageView.invalidate();
        }
    }

    protected void showCaption(String message) {
        if (mMessageView != null) {
            mCaptionView.setText(message);
            mCaptionView.invalidate();
        }
    }

    public synchronized void actualize(InformationMessage message) {
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
            actualizeInterfaceButtons();
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
        outState.putBoolean(INFO_CAPSULE,mShowCapsule);
        outState.putBoolean(INFO_INHALADOR,mShowInhalador);
        outState.putInt(INFO_ACHIEVEMENT,mAchivPoints);

    }

    void showDetailsDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogFragment newFragment = DetailsFragment.newInstance(mType, mTitle, mDetail);
        newFragment.show(fm, TAG_DETAILS);
    }


}
