package com.groupapp.groupapp.groupapp.screens;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.groupapp.groupapp.groupapp.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    //Retrofit
    private CompositeSubscription mSubscriptions;
    String opponents="";
    String tempName;

    ArrayList<ConnectingUser> joiningUsers = new ArrayList<ConnectingUser>();
    JoiningUserAdapter adapter;

    @BindView(R.id.gv_joining_users)
    GridView gvJoiningUsers;
    @BindView(R.id.b_createGroupAntechamber)
    Button bCreateGroup;

    @OnClick(R.id.b_createGroupAntechamber)
    public void createGroup(){
        //new Group();
        //joiningUsers has an array of name, endpoint (not needed), and privateKey. We need to put all these users in a group.
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
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
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
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
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
                    tempName = info.getEndpointName();
                    joiningUsers.add(new ConnectingUser("kupa1",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa2",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa3",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa4",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa5",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa6",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa7",endpointId));
                    joiningUsers.add(new ConnectingUser("kupa8",endpointId));
                    joiningUsers.add(new ConnectingUser(tempName,endpointId));

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    Log.e(TAG,"NO ENDPOINT FOUND");
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
                    sendPaylod(endpointId);
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");
                        Log.i(TAG,"Endpoint id "+endpointId);

                        //Checking for more users than one (better display)
//                        joiningUsers.add(new ConnectingUser("kupa1",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa2",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa3",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa4",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa5",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa6",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa7",endpointId));
//                        joiningUsers.add(new ConnectingUser("kupa8",endpointId));
//                        joiningUsers.add(new ConnectingUser(tempName,endpointId));
//
//                        adapter.notifyDataSetChanged();


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
                    //ZNAJDZ USERA I DODAJ MU PRIVATE KEY PO ENDPOINT
                    addKey( new String(payload.asBytes(),UTF_8),findEndpoint(endpointId));
                    Log.i(TAG,"USER KEY "+findEndpoint(endpointId).toString());
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
        Log.i(TAG,"In findAndDelete");
        for(ConnectingUser user: joiningUsers){
            if(user.getEndpoint().equals(endpoint)){
                Log.i(TAG,"user found!");
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

    private void sendPaylod(String otherEndpoint){
        connectionsClient.sendPayload(
                otherEndpoint, Payload.fromBytes(Constants.loggedUser.getPrivate_key().getBytes(UTF_8)));

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
        mListener = null;
    }

    public void replaceFragment(){
        connectionsClient.stopAdvertising();
        connectionsClient.stopDiscovery();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
