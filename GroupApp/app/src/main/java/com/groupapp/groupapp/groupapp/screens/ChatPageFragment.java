package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.groupapp.groupapp.groupapp.MainActivity;
import android.app.Activity;
import com.groupapp.groupapp.groupapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.MemberData;
import com.groupapp.groupapp.groupapp.model.Message;
import com.groupapp.groupapp.groupapp.model.MessageContent;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;
import com.groupapp.groupapp.groupapp.adapters.MessageAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;


import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class ChatPageFragment extends Fragment{
    private OnFragmentInteractionListener mListener;
    public static final String TAG = ChatPageFragment.class.getSimpleName();

    private Group thisGroup;

    private String id = "";

    private CompositeSubscription mSubscriptions;

    public ChatPageFragment(){

    }

    private MessageAdapter messageAdapter;

    private Socket mSocket;
    {
        Log.i(TAG, "creating socket");
        try {
            mSocket = IO.socket("https://group-app-android.herokuapp.com");
            //mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.print("something happened\n");
        }
        Log.i(TAG, "created socket!");
    }

    @BindView(R.id.group_name)
    TextView groupName;

    @BindView(R.id.b_add_member)
    Button bAddMember;

    @BindView(R.id.b_show_members)
    Button bShowMembers;

    @BindView(R.id.b_send)
    ImageButton bSend;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.messages_view)
    ListView messagesView;

    @OnClick(R.id.b_send)
    public void sendMessage(){
        Log.i(TAG,"send button is clicked");

        // for socket.io
        String io_message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(io_message)) {
            return;
        }
        attemptSend(io_message);
        thisGroup.getConversation().add(io_message);
//        loadMessages();

//        MemberData md = new MemberData("Anonymous", getWhite());
//        MessageContent  mc = new MessageContent(io_message, md, false);
//        messageAdapter.add(mc);

        editText.setText("");
    }

    @OnClick(R.id.b_add_member)
    public void addMember(){
        replaceFragment("JoinGroupFragment");
        bAddMember.setEnabled(false);
    }

    @OnClick(R.id.b_show_members)
    public void showMembers(){
        replaceFragment("MemberFragment");
        bShowMembers.setEnabled(false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        if (getArguments()!=null) {
            id = getArguments().getString("groupID");
        }
        Log.i(TAG,"oncreate: "+id);
        Log.e(TAG,"Chat id "+id);
        //Downloading gorupData
        getGroup(id);

        initialize_socket_io();
    }

    private void getGroup(String id){
        mSubscriptions.add(NetworkUtil.getRetrofit( Constants.getAccessToken(getActivity()),
                Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).getSingleGroupFromServer(id)
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetGroup, this::handleErrorGetGroup));
    }

    private void handleResponseGetGroup(Group group) {
        Log.e(TAG,"Group downloaded");
        Log.i(TAG,group.toString());
        thisGroup = group;
        groupName.setText(thisGroup.getName());
        bShowMembers.setEnabled(true);
        bAddMember.setEnabled(true);

//        Log.e(TAG, "IN ON ATTACH");
        SharedPreferences.Editor editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        editor.putString("id", thisGroup.getId());
        editor.apply();

        loadMessages();

    }

    private void loadMessages(){
        for (String message : thisGroup.getConversation()){
            MemberData md = new MemberData("Anonymous", getWhite());
            MessageContent mc = new MessageContent(message, md, false);
            messageAdapter.add(mc);
        }
    }

    private void handleErrorGetGroup(Throwable throwable) {
        Log.e(TAG,"Error downloading the group");
        throwable.printStackTrace();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);
        ButterKnife.bind(this,view);
        messageAdapter = new MessageAdapter(getContext());
        messagesView.setAdapter(messageAdapter);
        bShowMembers.setEnabled(false);
        bAddMember.setEnabled(false);
        return view;
    }

    private void replaceFragment(String fragmentString){
        Bundle bundle = new Bundle();
        bundle.putString("groupID",thisGroup.getId());

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("ChatPageFragment");


        if (fragmentString.equals("MemberFragment")) {
            MemberFragment fragment = new MemberFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragmentFrame, fragment, MemberFragment.TAG);

            ft.commit();
        } else if (fragmentString.equals("JoinGroupFragment")){
            JoinGroupFragment fragment = new JoinGroupFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragmentFrame, fragment, JoinGroupFragment.TAG);

            ft.commit();
        }

        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
        mSocket.disconnect();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        SharedPreferences.Editor editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        editor.remove("id");
        editor.apply();

        Log.e(TAG, "IN ON DETACH");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "RESUMING");
        SharedPreferences prefs = getActivity().getPreferences(MODE_PRIVATE);
        String restoredId = prefs.getString("id", null);
        if (restoredId != null)
        {
            getGroup(restoredId);
        }
    }


    private String getWhite() {
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append("F");
        }
        return sb.toString().substring(0, 7);
    }

    private void addMessage(String username, String message) {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void initialize_socket_io() {
        mSocket.connect();
        mSocket.on("join group", onJoined);
        mSocket.on("send message", onMessageReceive);
        attemptJoinRoom();
    }

    private Emitter.Listener onJoined = args -> Log.e("Response", "joined");

    private Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Response", "message received");
            JSONObject data = (JSONObject) args[0];
            String m;
            try {
                m = (String) data.get("message");
            } catch (JSONException e) {
                Log.e("Receiving message", "Json exception happened");
                return;
            }

            MessageContent mc;
            MemberData md;
            // add the message to view
            thisGroup.getConversation().add(m);

            md = new MemberData("Anonymous", getWhite());
            mc = new MessageContent(m, md, false);
            messageAdapter.add(mc);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private void attemptJoinRoom() {
        /*if (thisGroup == null) {
            Log.i(TAG,"group is null");
            return;
        }*/
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", getArguments().getString("groupID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("join group", object);
        Log.i(TAG,"joined group");
    }

    private void attemptSend(String message) {
        Log.i(TAG,"attempt send");
        if (TextUtils.isEmpty(message)) {
            return;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", getArguments().getString("groupID"));
            object.put("message", message);
            object.put("sender", Constants.loggedUser.getPrivate_key());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("send message", object);
    }
}
