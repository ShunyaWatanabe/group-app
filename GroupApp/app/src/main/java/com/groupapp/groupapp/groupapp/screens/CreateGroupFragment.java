package com.groupapp.groupapp.groupapp.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.groupapp.groupapp.groupapp.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends Fragment {
    public String[] keyboardArray =  {"1","2","3","4","5","6","7","8","9"," ","0","<-"};

    @BindView(R.id.b_joinGroup)
    Button join;

    @BindView(R.id.tv_inputDigit)
    TextView inputDigit;

    @BindView(R.id.gv_keyboard)
    GridView keyboard;

    @OnClick(R.id.b_joinGroup)
    public void joinGroup(){
        throw new UnsupportedOperationException();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //i need to add an adapter and listener to the keyboard

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

}

