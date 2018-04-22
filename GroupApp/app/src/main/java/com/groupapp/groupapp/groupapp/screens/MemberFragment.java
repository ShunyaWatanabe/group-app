package com.groupapp.groupapp.groupapp.screens;


import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Button;
import butterknife.ButterKnife;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.adapters.MemberAdapter;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    public static final String TAG = MemberFragment.class.getSimpleName();
    private Group group = new Group();

    @BindView(R.id.b_leaveGroup)
    Button bLeaveGroup;
    @BindView(R.id.gv_members)
    GridView gvMembers;


    @OnClick(R.id.b_leaveGroup)
    public void leaveGroup(){
        // TO DO database transaction to erase this user from the group
        //here i need to delete this person from the group
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("GroupsListFragment", 0);
    }

    //for the gridview I also need to link it to the profiles of people in the group


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // create dummy group TODO get group from server
        group.setName("Software Engineering");
        ArrayList<User> tmpUsers = new ArrayList<>();
        tmpUsers.add(new User("shunya"));
        tmpUsers.add(new User("wendi"));
        tmpUsers.add(new User("tomasz"));
        group.setMembers(tmpUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member, container, false);
        ButterKnife.bind(this,view);

        gvMembers.setAdapter(new MemberAdapter(getContext(), group));
        gvMembers.setColumnWidth(1);
        return view;
    }

}

