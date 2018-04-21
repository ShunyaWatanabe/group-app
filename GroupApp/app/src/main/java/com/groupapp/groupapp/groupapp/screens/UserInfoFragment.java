package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserInfoFragment extends Fragment {
    public static final String TAG = UserInfoFragment.class.getSimpleName();

    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_key)
    TextView tvUserKey;

    public UserInfoFragment(){
        // constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this,view);

        tvUserName.setText(Constants.loggedUser.getName());
        tvUserKey.setText(Constants.loggedUser.getPrivate_key());
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

    private void replaceFragment(){
        Bundle bundle = new Bundle();
//        bundle.putParcelable("userData",user);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("UserInfoFragment");

        GroupsListFragment fragment = new GroupsListFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, GroupsListFragment.TAG);

        ft.commit();

    }
}
