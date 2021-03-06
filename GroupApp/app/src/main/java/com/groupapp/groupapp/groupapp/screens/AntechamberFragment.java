package com.groupapp.groupapp.groupapp.screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.adapters.JoiningUserAdapter;
import com.groupapp.groupapp.groupapp.model.ConnectingUser;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AntechamberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AntechamberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AntechamberFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String TAG = AntechamberFragment.class.getSimpleName();


    // Our handle to Nearby Connections
    private ConnectionsClient connectionsClient;
    private String code;
    private String tempGroupID;
    //Retrofit
    private CompositeSubscription mSubscriptions;
    String opponents="";
    private final String CREATED = "created";

    ArrayList<ConnectingUser> joiningUsers = new ArrayList<ConnectingUser>();
    JoiningUserAdapter adapter;

    @BindView(R.id.gv_joining_users)
    GridView gvJoiningUsers;
    @BindView(R.id.b_createGroupAntechamber)
    Button bCreateGroup;

    @OnClick(R.id.b_createGroupAntechamber)
    public void createGroup(){
        //joiningUsers has an array of name, endpoint (not needed), and privateKey. We need to put all these users in a group.
        //send code to server
        //add the host
        bCreateGroup.setEnabled(false);
        joiningUsers.add(new ConnectingUser(Constants.loggedUser.getName(),"host"));
        joiningUsers.get(joiningUsers.size()-1).setKey(Constants.loggedUser.get_id());
        Log.e(TAG,"JOINING USERS ARE: "+joiningUsers.toString());

        mSubscriptions.add(NetworkUtil.getRetrofit( Constants.getAccessToken(getActivity()),
                Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).newGroupFromAntechamber(joiningUsers)
                //code
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCreate, this::handleErrorCreate));
    }

    ///change the respons and error
    private void handleResponseCreate(Response response){
        Log.e(TAG, "Create group succeeded!: " + response.toString());
        Log.e(TAG, "GroupID: " + response.getId());
        Log.e(TAG, "GroupID: " + response.getMessage());
        sendToAllConnected(response.getId());

        replaceFragment(response.getId());


    }

    private void handleErrorCreate(Throwable error){
        Log.e(TAG, "Create group error!: " + error.getMessage());
        bCreateGroup.setEnabled(true);
    }


    public AntechamberFragment() {
        // Required empty public constructor
    }

    public static AntechamberFragment newInstance() {
        AntechamberFragment fragment = new AntechamberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions= new CompositeSubscription();
        code=getArguments().getString("code");
        connectionsClient = Nearby.getConnectionsClient(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_antechamber, container, false);
        ButterKnife.bind(this,view);
        Log.e(TAG,"is connectionsClient initliaized");
        Log.e(TAG,connectionsClient.toString());

        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //joiningUsers.add(new ConnectingUser(Constants.loggedUser.getName(),"host"));//czemu
        //joiningUsers.get(0).setKey(Constants.loggedUser.getPrivate_key());

        adapter = new JoiningUserAdapter(getContext(),joiningUsers);
        gvJoiningUsers.setAdapter(adapter);

        startAdvertising();
        startDiscovery();
        return view;
    }

    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        Log.e(TAG,"Start Discovery...");
        Log.e("OPPONENTS",opponents);
        connectionsClient.startDiscovery(
                code, endpointDiscoveryCallback, new DiscoveryOptions(Constants.STRATEGY))
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,"Unable to discover");
                                e.printStackTrace();
                            }
                        });
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        Log.e(TAG,"Start Advertising...");

        connectionsClient.startAdvertising(
                Constants.loggedUser.getName(), code, connectionLifecycleCallback, new AdvertisingOptions(Constants.STRATEGY))
         .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Unable to advertise");
                        e.printStackTrace();
                    }
                });
    }

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    info.getEndpointName();
                    connectionsClient.requestConnection(Constants.loggedUser.getName(), endpointId, connectionLifecycleCallback);
//                    startDiscovery();


                }

                @Override
                public void onEndpointLost(String endpointId) {
                    Log.e(TAG,"Endpoint lost!");
                    deleteByEndpoint(findEndpoint(endpointId));
                }

            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    Log.e(TAG,"OPPONENT NAME "+connectionInfo.getEndpointName());
//                    tempName = connectionInfo.getEndpointName();

                    joiningUsers.add(new ConnectingUser(connectionInfo.getEndpointName(),endpointId));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");
                        Log.i(TAG,"Endpoint id "+endpointId);

                        Log.e(TAG,"Send payload");
                        sendPayloadKey(endpointId);

                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                    deleteByEndpoint(findEndpoint(endpointId));
                }
            };

    // Callbacks for receiving payloads
    //ADD SENDING MY PRIVATE KEY
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    Log.i(TAG,"payloadReceived");
                    //ZNAJDZ USERA I DODAJ MU PRIVATE KEY PO ENDPOINT4
                    String response = new String(payload.asBytes(),UTF_8);
                    Log.i(TAG,"Response1 is "+response);
//                    Log.i(TAG,"Response2 is "+response.substring(0,7));
//                    Log.i(TAG,"Response3 is "+response.substring(8));
                    if(response.substring(0,7).equals(CREATED)){
//                        Log.i(TAG,"In created");
//                        Log.i(TAG,"Id of event"+response.substring(8));
                        //group created, moving to another screen
                        replaceFragment(response.substring(8));
                    }else {
                        //received a key
                        Log.i(TAG,"Received a key!");
                        addKey(new String(payload.asBytes(), UTF_8), findEndpoint(endpointId));
                        Log.i(TAG, "USER KEY " + findEndpoint(endpointId));
                    }
                    //opponentChoice = GameChoice.valueOf(new String(payload.asBytes(), UTF_8));
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    Log.i(TAG,"payloadReceivedUpdate");
                }
            };

    private void addKey(String key, ConnectingUser user){
        user.setKey(key);
    }

    private ConnectingUser findEndpoint(String endpoint){
        Log.i(TAG,"In find");
        for(ConnectingUser user: joiningUsers){
            if(user.getEndpoint().equals(endpoint)){
                return user;
            }
        }
        return null;
    }

    private void deleteByEndpoint(ConnectingUser user){
        //                Log.i(TAG,"Users after delete " + joiningUsers.toString());
        joiningUsers.remove(user);
        adapter.notifyDataSetChanged();
//                Log.i(TAG,"Users after delete " + joiningUsers.toString());
        return;
    }

    private void sendPayloadKey(String otherEndpoint){
        Log.e(TAG,"Sending payload key");
        connectionsClient.sendPayload(
                otherEndpoint, Payload.fromBytes(Constants.loggedUser.get_id().getBytes(UTF_8)));

    }

    private void sendPayloadNotification(String otherEndpoint, String groupID){
        //Sending a payload to all connected user that the gorup has been created. moving them to the group in on receive
        String temp = CREATED+"_"+groupID;
        connectionsClient.sendPayload(
                otherEndpoint, Payload.fromBytes(temp.getBytes(UTF_8)));

    }

    private void sendToAllConnected(String groupID){
        for(ConnectingUser user:joiningUsers){
            sendPayloadNotification(user.getEndpoint(), groupID);
        }
    }

//    public void render(){
//
//        for(ConnectingUser joiner : joiningUsers){
//            final Button b = new Button(getContext());
//            b.setText(joiner.getName());
//            gvJoiningUsers.addView(b);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();
        joiningUsers.clear();
        mListener = null;
    }

    public void replaceFragment(String groupID){
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();

        //We send both groupID and private key, otherwise we won't verify the user
        String[] groupID_private_key ={groupID,Constants.loggedUser.getPrivate_key()};
        tempGroupID=groupID;

        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()),
                Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).addGroupMembership(groupID_private_key)
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdateMembership, this::handleErrorUpdateMembership));

    }

    private void handleErrorUpdateMembership(Throwable throwable) {
        throwable.printStackTrace();
    }

    private void handleResponseUpdateMembership(Response response) {
        Log.i(TAG,response.toString());
        Bundle bundle = new Bundle();
        bundle.putString("groupID",tempGroupID);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack(TAG);

        ChatPageFragment fragment = new ChatPageFragment(); //test
        fragment.setArguments(bundle);

        ft.replace(R.id.fragmentFrame, fragment, AntechamberFragment.TAG);

        ft.commit();
        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
