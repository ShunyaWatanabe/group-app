package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.groupapp.groupapp.groupapp.MainActivity;
import android.app.Activity;
import com.groupapp.groupapp.groupapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.MemberData;
import com.groupapp.groupapp.groupapp.model.MessageContent;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.RoomListener;
import com.groupapp.groupapp.groupapp.adapters.MessageAdapter;

import java.util.Random;

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
        System.out.print("send button is clicked!");
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish("observable-room", message);
            editText.getText().clear();
        }
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
        Log.e(TAG,"Chat id "+id);


        //BASED ON THAT ID OCTOVER DOWNLAOD THE GROUP DATA+
//        mSubscriptions.add(NetworkUtil.getRetrofit( Constants.getAccessToken(getActivity()),
//                Constants.getRefreshToken(getActivity()),
//                Constants.getName(getActivity())).getGroupsFromServer(Constants.loggedUser.getPrivate_key())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseGetGroup, this::handleErrorGetGroup));

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

        System.out.print("onCreateView");

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
                    System.out.print("reached here");
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
