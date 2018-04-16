package com.groupapp.groupapp.groupapp.screens;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.groupapp.groupapp.groupapp.R;
import android.widget.ArrayAdapter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateGroupFragment extends Fragment {

    public static final String TAG = CreateGroupFragment.class.getSimpleName();

    private Button[] buttonList = new Button[12];

    ArrayAdapter<Button> arrayAdapter;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private OnFragmentInteractionListener mListener;

    public CreateGroupFragment(){

    }

    @BindView(R.id.b_joinGroup)
    Button join;

    @BindView(R.id.tv_inputDigit)
    TextView inputDigit;

    @BindView(R.id.gv_keyboard)
    GridView keyboard;

    @OnClick(R.id.b_joinGroup)
    public void joinGroup(){

        // move to group chat fragment
        replaceFragment();
        //throw new UnsupportedOperationException();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void adapterToKeyboard(){
        for (int i = 0; i < 12; i++) {
            Button button = new Button(getContext());
            button.setTextSize(15);
            if (i == 9) button.setText("");
            else if (i == 10) button.setText("0");
            else if (i == 11) button.setText("<");
            else button.setText(String.valueOf(i+1));
            buttonList[i] = button;
        }
        //i think there is something wrong with the code here with arrayadapter on buttons, but I don't know how to fix it.
        arrayAdapter = new ArrayAdapter<>(getContext(),
              android.R.layout.simple_list_item_1,buttonList);

        keyboard.setNumColumns(3);
        keyboard.setAdapter(arrayAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        ButterKnife.bind(this,view);


        //I want to set an adapter to keyboard here
        adapterToKeyboard();

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

    private void replaceFragment(){
        Bundle bundle = new Bundle();
//        bundle.putParcelable("userData",user);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("CreateGroupFragment");

        ChatPageFragment fragment = new ChatPageFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, ChatPageFragment.TAG);

        ft.commit();

    }

}

