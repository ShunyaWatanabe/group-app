package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
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
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.RoomListener;
import com.groupapp.groupapp.groupapp.adapters.MessageAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatPageFragment extends Fragment implements RoomListener{
    private OnFragmentInteractionListener mListener;
    public static final String TAG = ChatPageFragment.class.getSimpleName();

    private Group thisGroup;

    private CompositeSubscription mSubscriptions;

    private Button[] buttonList = new Button[12];

    public ChatPageFragment(){

    }

    private String channelID = "dCqA04nH0FCzZoFN";
    private String roomName = "observable-room";

    private Scaledrone scaledrone;

    private MessageAdapter messageAdapter;

    private Socket mSocket;
    {
        Log.i(TAG,"creating socket");
        try {
            IO.Options opts = new IO.Options();
            opts.timeout = 30000;
            opts.reconnection = true;
            opts.reconnectionAttempts = 10;
            opts.reconnectionDelay = 1000;
            opts.forceNew = true;
            //mSocket = IO.socket("https://group-app-android.herokuapp.com");
            mSocket = IO.socket(Constants.API_URL);
            //mSocket = IO.socket("https://localhost");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.print("created socket!");
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

        // for scale drone
//        String message = editText.getText().toString();
//        if (message.length() > 0) {
//            scaledrone.publish("observable-room", message);
//            editText.getText().clear();
//        }

        // for socket.io
        String io_message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(io_message)) {
            return;
        }

        editText.setText("");
        attemptSend(io_message);
    }

    @OnClick(R.id.b_add_member)
    public void addMember(){
        replaceFragment("JoinGroupFragment");
    }

    @OnClick(R.id.b_show_members)
    public void showMembers(){
        replaceFragment("MemberFragment");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        String id = getArguments().getString("groupID");

        Log.i(TAG,"oncreate: "+id);
        //Downloading gorupData
        getGroup(id);


        MemberData data = new MemberData(getRandomName(), getRandomColor());


        scaledrone = new Scaledrone(channelID, data);

        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, ChatPageFragment.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });

        //mSocket.on("new message", onNewMessage);
        //mSocket.connect();

        mSocket.connect();
        mSocket.on("join group", onJoined);
        mSocket.on("send message", onMessageReceive);
        attemptJoinRoom();
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
        Log.i(TAG,"Group downloaded");
        thisGroup = group;
        groupName.setText(thisGroup.getName());
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

        // add previous messages
        /*for (Message m : thisGroup.getConversation()){
            MemberData md = new MemberData(getRandomName(), getRandomColor());
            MessageContent mc = new MessageContent(m.getText(), md, false);
            messageAdapter.add(mc);
        }*/

        System.out.print("onCreateView");

        return view;
    }

    private void replaceFragment(String fragmentString){
        Bundle bundle = new Bundle();

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
        mSocket.off("new message", onNewMessage);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // Successfully connected to Scaledrone room
    @Override
    public void onOpen(Room room) {
        System.out.println("Connected to room");
    }

    // Connecting to Scaledrone room failed
    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    // Received a message from Scaledrone room
    @Override
    public void onMessage(Room room, final JsonNode json, final Member member) {
        // TODO
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        final ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.print("try in onMessage function ");
            // member.clientData is a MemberData object, let's parse it as such
            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
            // if the clientID of the message sender is the same as our's it was sent by us
            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
            // since the message body is a simple string in our case we can use json.asText() to parse it as such
            // if it was instead an object we could use a similar pattern to data parsing
            final MessageContent message = new MessageContent(json.asText(), data, belongsToCurrentUser);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};
        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    private void addMessage(String username, String message) {
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Message m;
                    try {
                        m = (Message) data.get("message");
                    } catch (JSONException e) {
                        return;
                    }

                    MessageContent mc;
                    MemberData md;
                    // add the message to view
                    md = new MemberData(m.getSender().getName(), getRandomColor());
                    mc = new MessageContent(m.getText(), md, true);
                    messageAdapter.add(mc);
                }
            });
        }
    };

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private Emitter.Listener onJoined = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.e("Response", "in joined");
        }
    };

    private Emitter.Listener onMessageReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            Log.e("Response", "message received");
            JSONObject data = (JSONObject) args[0];
            Message m;
            try {
                m = (Message) data.get("message");
            } catch (JSONException e) {
                return;
            }

            MessageContent mc;
            MemberData md;
            // add the message to view
            md = new MemberData(m.getSender().getName(), getRandomColor());
            mc = new MessageContent(m.getText(), md, true);
            messageAdapter.add(mc);
            /*
            JSONObject data = (JSONObject) args[0];
            String message;
            try {
                message = data.getString("message");
            } catch (JSONException e) {
                return;
            }

            if(message != null) {
                messageList.add(new Message(etMessage.getText().toString(), date.getTime(), email));
                MessageAdapter adapter = new MessageAdapter(getActivity().getBaseContext(), R.layout.item_message_sender, messageList, null);
                lvMessages.setAdapter(adapter);
            }

            Log.e("Response", message);
            */
//                }
//            });
        }
    };

    private void attemptJoinRoom() {
        /*if (thisGroup == null) {
            Log.i(TAG,"group is null");
            return;
        }*/
        JSONObject object = new JSONObject();
        try {
            object.put("group", getArguments().getString("groupID"));
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
