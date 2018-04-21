package com.groupapp.groupapp.groupapp.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.adapters.GroupListAdapter;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.GroupsList;
import com.groupapp.groupapp.groupapp.model.Response;
import com.groupapp.groupapp.groupapp.model.User;
import com.groupapp.groupapp.groupapp.network.NetworkUtil;
import com.groupapp.groupapp.groupapp.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GroupsListFragment extends Fragment {
    public static final String TAG = GroupsListFragment.class.getSimpleName();

    private CompositeSubscription mSubscriptions;
    private RecyclerView.LayoutManager mLayoutManager;

    android.support.v7.widget.SearchView svEvent;

    public static ArrayList<Group> groupsList = new ArrayList<>();
    private ProgressDialog progress;
    @BindView(R.id.tv_progressText)
    TextView progressText;
    @BindView(R.id.linlaHeaderProgress)
    LinearLayout headerProgress;
    @BindView(R.id.pbHeaderProgress)
    ProgressBar pbHeaderProgress;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.create_group_button)
    FloatingActionButton createGroupButton;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;

    public static RecyclerView rvGroups;

    private OnFragmentInteractionListener mListener;

    public GroupsListFragment() {
        // Required empty public constructor
    }

    public static GroupsListFragment newInstance() {
        GroupsListFragment fragment = new GroupsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSubscriptions = new CompositeSubscription();

        mSubscriptions.add(NetworkUtil.getRetrofit( Constants.getAccessToken(getActivity()),
                Constants.getRefreshToken(getActivity()),
                Constants.getName(getActivity())).getGroup(Constants.loggedUser.getPrivate_key())
                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseGetGroup, this::handleErrorGetGroup));
    }

    private void handleResponseGetGroup(Response response){
        Log.e(TAG, "Get groups complete!");
        Constants.loggedUser.setGroups(response.getGroups());
    }

    private void handleErrorGetGroup(Throwable err){
        Log.e(TAG, "Get groups fail!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.search_item, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//
//        initSearchView();
//        initSwipeRefresh();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        rvGroups = view.findViewById(R.id.list);
        ButterKnife.bind(this,view);
//
//        svEvent = (android.support.v7.widget.SearchView)view.findViewById(R.id.search_view);
        getGroups();
        ivProfile.bringToFront();



        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvGroups.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager rvGroupsLayoutManager = new LinearLayoutManager(getContext());
        rvGroups.setLayoutManager(rvGroupsLayoutManager);

        // get string array of group names
        ArrayList<String> groupNamesList = new ArrayList<>();
        for (Group g: groupsList){
            groupNamesList.add(g.getName());
        }

        // specify an adapter (see also next example)
        RecyclerView.Adapter rvGroupsAdapter = new GroupListAdapter(groupNamesList.toArray(new String[groupsList.size()]));
        rvGroups.setAdapter(rvGroupsAdapter);



        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.searching));
        progress.setCancelable(false);

        return view;
    }

    public static GroupsListFragment getInstance() {
        GroupsListFragment fragment = new GroupsListFragment();
        return fragment;
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

    @OnClick(R.id.create_group_button)
    public void createGroup(){
        Log.e(TAG, "create group button is clicked!");
        replaceFragment("CreateGroupFragment");
    }

    @OnClick(R.id.iv_profile)
    public void show_profile(View view){
        Log.e(TAG, "profile is clicked!");
        replaceFragment("UserInfoFragment");
    }

    private void replaceFragment(String fragmentString){
        Log.e(TAG, "Replace fragment to" + fragmentString);
        Bundle bundle = new Bundle();
//        bundle.putParcelable("userData",user);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        ft.addToBackStack("GroupsListFragment");

        if (fragmentString.equals("CreateGroupFragment")) {
            CreateGroupFragment fragment = new CreateGroupFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragmentFrame, fragment, CreateGroupFragment.TAG);

            ft.commit();

        } else if (fragmentString.equals("UserInfoFragment")) {
            UserInfoFragment fragment = new UserInfoFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragmentFrame, fragment, UserInfoFragment.TAG);

            ft.commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    private void getGroups() {
        //Change latter to loggeduser.getEmail()
        //for now not sure if it already download these data

        // create dummy group list
        Group group = new Group();
        group.setName("Software Engineering");
        groupsList.add(group);
        group = new Group();
        group.setName("Computer Networks");
        groupsList.add(group);
        group = new Group();
        group.setName("Operating Systems");
        groupsList.add(group);

//        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()), Constants.getRefreshToken(getActivity()), Constants.getEmail(getActivity())).getEvents(Constants.loggedUser.getEmail())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse, this::handleError));
    }

    public void initSwipeRefresh() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                Log.v("swipeContainer", "REFRESH!");
                refresh();
            }
        });
        swipeContainer.setColorSchemeResources(new int[]{17170456});
    }

    public void refresh() {
        if (groupsList == null) {
            pbHeaderProgress.setVisibility(View.VISIBLE);
            headerProgress.setVisibility(View.VISIBLE);
            progressText.setText(getResources().getString(R.string.loading_groups));
        }
        getGroups();
    }


    private void handleResponse(GroupsList groups) {
        progress.dismiss();

        Log.e(TAG, "Events!");

//        if(events.getToken() != null)
//            Constants.saveAccessToken(getActivity(), events.getToken());
//
//        eventList = events.getEvents();
//
//        EventAdapter adapter = new EventAdapter(eventList, getContext(),getActivity());
//
//        //lvEvents.setHasFixedSize(true);
//
//        // use a linear layout manager
//        lvEvents.setAdapter(adapter);
//        mLayoutManager = new LinearLayoutManager(getActivity());//, LinearLayoutManager.VERTICAL, true);
//        lvEvents.setLayoutManager(mLayoutManager);
//
//        if(eventList==null){
//            progressText.setText(getResources().getString(R.string.nothing_found));
//            pbHeaderProgress.setVisibility(View.GONE);
//        }else{
//            headerProgress.setVisibility(View.GONE);
//            pbHeaderProgress.setVisibility(View.GONE);
//        }
//        swipeContainer.setRefreshing(false);
    }

    private void handleError(Throwable error) {
        progress.dismiss();

        Log.e(TAG, "Events error!: " + error.getMessage());

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
//                Response response = gson.fromJson(errorBody, Response.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

//    private void initSearchView(){
//        svEvent.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                getSearchedEvents(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//
//        svEvent.setOnCloseListener(() -> {
//            progress.show();
//            getGroups();
//            return false;
//        });
//    }

    private void getSearchedEvents(String query) {
        progress.show();
//
//        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()), Constants.getRefreshToken(getActivity()), Constants.getEmail(getActivity())).getEventsSearch(query)
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseSearchedEvents, this::handleError));
    }

    private void handleResponseSearchedEvents(GroupsList events) {
//        progress.dismiss();
//
//        Log.e(TAG, "Events!");
//
//        if(events.getToken() != null)
//            Constants.saveAccessToken(getActivity(), events.getToken());
//
//        if(events.getEvents()==null || events.getEvents().size() == 0)
//            eventList = new ArrayList<>();
//        else
//            eventList = events.getEvents();
//
//        EventAdapter adapter = new EventAdapter(eventList, getContext(), getActivity());
//        lvEvents.setAdapter(adapter);

    }
}