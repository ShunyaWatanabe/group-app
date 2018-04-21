package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.utils.Constants;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

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
    private static final String TAG = AntechamberFragment.class.getSimpleName();

    // Our handle to Nearby Connections
    private ConnectionsClient connectionsClient;
    //Retrofit
    private CompositeSubscription mSubscriptions;

    public AntechamberFragment() {
        // Required empty public constructor
    }

    public static AntechamberFragment newInstance(String param1, String param2) {
        AntechamberFragment fragment = new AntechamberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions= new CompositeSubscription();
        connectionsClient = Nearby.getConnectionsClient(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_antechamber, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
                getActivity().getPackageName(), endpointDiscoveryCallback, new DiscoveryOptions(Constants.STRATEGY));
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
                Constants.loggedUser.getName(), getActivity().getPackageName(), connectionLifecycleCallback, new AdvertisingOptions(STRATEGY));
    }

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(Constants.loggedUser.getName(), endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    opponentName = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        opponentEndpointId = endpointId;
                        setOpponentName(opponentName);
                        setStatusText(getString(R.string.status_connected));
                        setButtonState(true);
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                    resetGame();
                }
            };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
