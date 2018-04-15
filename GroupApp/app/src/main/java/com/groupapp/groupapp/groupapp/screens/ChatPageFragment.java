package com.groupapp.groupapp.groupapp.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatPageFragment extends Fragment {
    public static final String TAG = ChatPageFragment.class.getSimpleName();

    private Button[] buttonList = new Button[12];

    public ChatPageFragment(){

    }

    @BindView(R.id.group_name)
    TextView groupName;

    @BindView(R.id.b_add_member)
    Button bAddMember;

    @BindView(R.id.b_show_members)
    Button bShowMembers;

    @BindView(R.id.b_send)
    Button bSend;

    @BindView(R.id.chat_message_input)
    EditText chatMessageInput;

    @BindView(R.id.chat_messages)
    ListView chatMessages;

    @OnClick(R.id.b_send)
    public void sendMessage(){
        //send message
        throw new UnsupportedOperationException();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);
        ButterKnife.bind(this,view);
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

    }
}
