package com.groupapp.groupapp.groupapp.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Button;
import butterknife.ButterKnife;
import com.groupapp.groupapp.groupapp.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    public static final String TAG = MemberFragment.class.getSimpleName();

    @BindView(R.id.b_leaveGroup)
    Button bLeaveGroup;
    @BindView(R.id.gv_members)
    GridView gvMembers;

    @OnClick(R.id.b_leaveGroup)
    public void leaveGroup(){
        throw new UnsupportedOperationException();
        //here i need to delete this person from the group
    }

    //for the gridview I also need to link it to the profiles of people in the group


    @Override
    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

}

