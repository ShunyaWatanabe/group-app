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
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class JoinGroupFragment extends Fragment {
    public static final String TAG = JoinGroupFragment.class.getSimpleName();

    @BindView(R.id.tv_code)
    TextView tvCode;
    @BindView(R.id.tv_code_info)
    TextView tvCodeInfo;

    private CompositeSubscription mSubscriptions;
    private String group_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo create groupID of the current group
        group_ID = "5adb5af45cc9df0004910a7b";

        mSubscriptions = new CompositeSubscription();
        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()),
                Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).getInvitationCode(group_ID)
                //i have to change here
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetInvitationCode, this::handleErrorGetInvitationCode));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);
        ButterKnife.bind(this,view);

        tvCode.setText("wait");
        return view;
    }

    private void handleResponseGetInvitationCode(Response response){
        Log.e(TAG, "Get Invitation Code succeeds");
        tvCode.setText(response.getMessage());
        tvCodeInfo.setText("Code Active for 5 minutes");

    }

    private void handleErrorGetInvitationCode(Throwable err){
        Log.e(TAG, "Get Invitation Code fails");
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

        ft.addToBackStack("JoinGroupFragment");

        GroupsListFragment fragment = new GroupsListFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, GroupsListFragment.TAG);

        ft.commit();
        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");

    }
}
