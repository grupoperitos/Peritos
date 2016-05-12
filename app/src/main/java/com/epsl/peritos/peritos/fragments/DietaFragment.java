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

import org.w3c.dom.Text;

/**
 * Created by noni_ on 02/05/2016.
 */
public class DietaFragment extends Fragment {

    private int messageId = 0;
    private TextView mMessage=null;

    private TextView mTitle=null;

    public DietaFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dieta, container, false);

        mMessage = (TextView) view.findViewById(R.id.txt_message);
        mTitle = (TextView) view.findViewById(R.id.txt_title);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    public void showMessage()
    {
        if (MainActivity.messageList != null) {
            InformationMessage message= MainActivity.messageList.getMessageById(messageId);
            mMessage.setText(message.getMessage());
            messageId++;
        }

    }
}
