package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinGroupFragment extends Fragment {
    public static final String TAG = JoinGroupFragment.class.getSimpleName();

    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_code_info)
    TextView tvCodeInfo;

    public JoinGroupFragment(){
        // constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
}
