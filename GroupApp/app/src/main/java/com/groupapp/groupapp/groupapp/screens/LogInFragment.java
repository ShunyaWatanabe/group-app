package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LogInFragment extends Fragment {
    public static final String TAG = LogInFragment.class.getSimpleName();

    private CompositeSubscription mSubscriptions;

    @BindView(R.id.b_start)
    Button bStart;
    @BindView(R.id.tv_username)
    EditText tvUsername;

    @OnClick(R.id.b_start)
    public void startApp(){
//                mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()), Constants.getRefreshToken(getActivity()), Constants.getName(getActivity())).getGroups(Constants.loggedUser.getName())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse, this::handleError));
        replaceFragment();
    }

    private void handleResponse(Response response){
        Log.e(TAG, "Login succeeded!: " + response.toString());
        showSnackBarMessage("WELCOME USER");
        Constants.saveTokens(getActivity(), response.getToken(), response.getRefreshToken(), response.getMessage());

        loadProfile(response.getToken(),response.getRefreshToken(),response.getMessage());
    }

    private void loadProfile(String mToken, String mRefreshToken, String mName) {

        Log.e("RELOGIN", "token: " + mToken + ", name: " + mName + ", refreshToken: " + mRefreshToken);

//        mSubscriptions.add(NetworkUtil.getRetrofit(mToken, mRefreshToken, Constants.getEmail(getActivity())).getProfile(mEmail)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseProfile,this::handleError));
    }

    private void handleResponseProfile(User user) {
//        Constants.loggedUser = user;

//        if(user.getToken()!=null){
//            Constants.saveAccessToken(getActivity(), user.getToken());
//        }

//        progress.dismiss();

        replaceFragment();
    }

    private void handleError(Throwable error) {

        Log.e(TAG, "Login error!: " + error.getMessage());

//        progress.dismiss();

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private OnFragmentInteractionListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void replaceFragment(){
        Bundle bundle = new Bundle();
//        bundle.putParcelable("userData",user);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("LogInFragment");

        GroupsListFragment fragment = new GroupsListFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, GroupsListFragment.TAG);

        ft.commit();

    }

    private void showSnackBarMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }


}
