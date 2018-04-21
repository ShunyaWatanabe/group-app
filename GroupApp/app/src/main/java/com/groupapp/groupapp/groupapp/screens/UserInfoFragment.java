package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemClick;
import butterknife.OnTouch;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class UserInfoFragment extends Fragment {
    public static final String TAG = UserInfoFragment.class.getSimpleName();


    private CompositeSubscription mSubscriptions;
//    private boolean changed = true;

    @BindView(R.id.et_user_name)
    TextView etUserName;
    @BindView(R.id.tv_user_key)
    TextView tvUserKey;


    @BindView(R.id.rl_userinfo)
    RelativeLayout root;


    public UserInfoFragment(){
        // constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mSubscriptions = new CompositeSubscription();





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this,view);

        etUserName.setText(Constants.loggedUser.getName());
        tvUserKey.setText(Constants.loggedUser.getPrivate_key());
        etUserName.setEnabled(false);

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


    //THIS DOES NOT WORK!!!!!!!!!!!!
    @OnClick(R.id.et_user_name)
    public void enableChangeName(){
        etUserName.setEnabled(true);
        Log.e(TAG, "click edit text");
    }


    @OnClick(R.id.rl_userinfo)
    public void saveChange(){
        Log.e(TAG, "click backgrond");


        etUserName.setEnabled(false);
        String newName = etUserName.getText().toString();

        if (!newName.equals(Constants.loggedUser.getName())){
            String[] temp ={newName,Constants.loggedUser.getPrivate_key()};

            mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()),
                    Constants.getRefreshToken(getActivity()),
                    Constants.getName(getActivity())).changeUserName(temp)
                    .observeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponseChangeName, this::handleErrorRegister));

            }



    }

    private void handleResponseChangeName(Response response){

        Constants.loggedUser.setName(response.getMessage());

        Log.e(TAG, "Name change succeeded!: " + response.toString());

    }

    private void handleErrorRegister(Throwable error){
        Log.e(TAG, "Name change error!: " + error.getMessage());
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
