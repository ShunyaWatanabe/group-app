package com.groupapp.groupapp.groupapp.screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.groupapp.groupapp.groupapp.adapters.NumbersAdapter;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.lang.StringBuilder;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends Fragment {

    public static final String TAG = CreateGroupFragment.class.getSimpleName();

    private CompositeSubscription mSubscriptions;

    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private Button[] buttonList = new Button[12];
    private ArrayList<String> numList = new ArrayList<>(12);
    private String code = "----";

    ArrayAdapter<Button> arrayAdapter;
    NumbersAdapter numbersAdapter;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment(){

    }

    @BindView(R.id.b_joinGroup)
    Button join;

    @BindView(R.id.tv_inputDigit_1)
    TextView inputDigit1;

    @BindView(R.id.tv_inputDigit_2)
    TextView inputDigit2;

    @BindView(R.id.tv_inputDigit_3)
    TextView inputDigit3;

    @BindView(R.id.tv_inputDigit_4)
    TextView inputDigit4;

    @BindView(R.id.gv_keyboard)
    GridView keyboard;


    //here should not immediately create group and send response to server, should go to ant room
    @OnClick(R.id.b_joinGroup)
    public void joinGroup(){


        if (!code.contains("-")){
            //send code to server
            mSubscriptions.add(NetworkUtil.getRetrofit( Constants.getAccessToken(getActivity()),
                    Constants.getRefreshToken(getActivity()),
                    Constants.getName(getActivity())).joinGroup(Constants.loggedUser)
                    //code
                    .observeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponseJoin, this::handleErrorJoin));


            code = "----";
            replaceFragment();

        }

    }

    ///change the respons and error
    private void handleResponseJoin(Response response){
        Log.e(TAG, "Join group succeeded!: " + response.toString());
    }

    private void handleErrorJoin(Throwable error){
        Log.e(TAG, "Join group error!: " + error.getMessage());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubscriptions = new CompositeSubscription();
    }

    private void setAdapterToKeyboard(){
        for (int i = 0; i < 12; i++) {
            Button button = new Button(getContext());
            button.setTextSize(15);
            if (i == 9) button.setText("");
            else if (i == 10) button.setText("0");
            else if (i == 11) button.setText("<");
            else button.setText(String.valueOf(i+1));
            buttonList[i] = button;
            numList.add(Integer.toString(i));
        }

        numbersAdapter = new NumbersAdapter(getActivity());

        keyboard.setNumColumns(3);
        keyboard.setAdapter(numbersAdapter);
        keyboard.setOnItemClickListener((parent, v, position, id) -> {
            Log.e("button clicked", "position: " + position);
            System.out.print("button clicked");


            String num = Integer.toString(position+1);
            if (num.equals("11")) num = "0";

            switch (num) {
                case "10":
                    // do nothing
                    break;
                case "12":
                    int i = code.indexOf("-");
                    switch (i){
                        case 1:code = "----";break;
                        case 2:code = code.substring(0,1)+"---";break;
                        case 3:code = code.substring(0,2)+"--";break;
                        case -1:code = code.substring(0,3)+"-";break;
                        default: break;
                    }

                    break;
                default:
                    if (code.indexOf("-")!=-1) {
                        code = code.replaceFirst("-", num);
                    }
                    break;

            }

            inputDigit1.setText(Character.toString(code.charAt(0)));
            inputDigit2.setText(Character.toString(code.charAt(1)));
            inputDigit3.setText(Character.toString(code.charAt(2)));
            inputDigit4.setText(Character.toString(code.charAt(3)));

        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        ButterKnife.bind(this,view);

        setAdapterToKeyboard();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!hasPermissions(getActivity(), REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void replaceFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("code",code);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("CreateGroupFragment");


        AntechamberFragment fragment = new AntechamberFragment();
        //ChatPageFragment fragment = new ChatPageFragment(); //test

        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, AntechamberFragment.TAG);

        ft.commit();
        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");

    }

}

