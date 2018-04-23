package com.groupapp.groupapp.groupapp.screens;


import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Button;
import butterknife.ButterKnife;

import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.adapters.MemberAdapter;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemberFragment extends Fragment {

    public static final String TAG = MemberFragment.class.getSimpleName();

    private CompositeSubscription mSubscriptions;

    private ArrayList<User> members = new ArrayList<>();
    private String group_ID;

    @BindView(R.id.b_leaveGroup)
    Button bLeaveGroup;
    @BindView(R.id.gv_members)
    GridView gvMembers;


    @OnClick(R.id.b_leaveGroup)
    public void leaveGroup(){

        String[] groupid_private_key = {Constants.loggedUser.getPrivate_key(),group_ID};

        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()),
        Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).leaveGroup(groupid_private_key)
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLeaveGroup, this::handleErrorLeaveGroup));

    }

    private void handleResponseLeaveGroup(Response response){
        Log.e(TAG,"leave group succeed");

        //todo client user side remove group
        //response.getId(); //this is the group ID
        //todo go back to chat list fragment
        getFragmentManager().popBackStack("GroupsListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");
    }

    private void handleErrorLeaveGroup(Throwable err){
        Log.e(TAG,"leave group fails");
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();


        //todo populate it with actual values of the current group
        members.add(new User("a"));
        group_ID = "fake group ID";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_member, container, false);
        ButterKnife.bind(this,view);

        gvMembers.setAdapter(new MemberAdapter(getContext(), members));
        gvMembers.setColumnWidth(1);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private LogInFragment.OnFragmentInteractionListener mListener;

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

