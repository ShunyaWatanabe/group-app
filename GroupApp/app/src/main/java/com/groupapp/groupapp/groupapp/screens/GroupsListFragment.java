package com.groupapp.groupapp.groupapp.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
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
import com.groupapp.groupapp.groupapp.clickListeners.RecyclerItemClickListener;
import com.groupapp.groupapp.groupapp.groups.GroupAdapter;
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

    public static ArrayList<Group> groupsList;
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
        Log.e(TAG, err.getMessage());
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

        getGroups();
        ivProfile.bringToFront();

        // use a linear layout manager
//        RecyclerView.LayoutManager rvGroupsLayoutManager = new LinearLayoutManager(getContext());
        //rvGroups.setLayoutManager(rvGroupsLayoutManager);

        // get string array of group names
//        ArrayList<String> groupNamesList = new ArrayList<>();
//        for (Group g: groupsList){
//            groupNamesList.add(g.getName());
//        }

        // specify an adapter (see also next example)
//        RecyclerView.Adapter rvGroupsAdapter = new GroupListAdapter(groupNamesList.toArray(new String[groupsList.size()]));
//        rvGroups.setAdapter(rvGroupsAdapter);
//        rvGroups.addOnItemTouchListener(
//                new RecyclerItemClickListener(getContext(), rvGroups ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                        // do whatever
//                        replaceFragment("ChatPageFragment");
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                        System.out.println("group clicked long");
//                    }
//                })
//        );

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

        switch (fragmentString) {
            case "CreateGroupFragment": {
                CreateGroupFragment fragment = new CreateGroupFragment();
                fragment.setArguments(bundle);
                ft.replace(R.id.fragmentFrame, fragment, CreateGroupFragment.TAG);
                ft.commit();
                break;
            }
            case "UserInfoFragment": {
                UserInfoFragment fragment = new UserInfoFragment();
                fragment.setArguments(bundle);
                ft.replace(R.id.fragmentFrame, fragment, UserInfoFragment.TAG);
                ft.commit();
                break;
            }
            case "ChatPageFragment": {
                ChatPageFragment fragment = new ChatPageFragment();
                fragment.setArguments(bundle);
                ft.replace(R.id.fragmentFrame, fragment, ChatPageFragment.TAG);
                ft.commit();
                break;
            }
        }

        Log.e("Stack count", getActivity().getSupportFragmentManager().getBackStackEntryCount() + "");
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
        groupsList = new ArrayList<>();
        Group group = new Group();
        group.setName("Software Engineering");
        groupsList.add(group);
        group = new Group();
        group.setName("Computer Networks");
        groupsList.add(group);
        group = new Group();
        group.setName("Operating Systems");
        groupsList.add(group);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvGroups.getContext(),
//                mLayoutManager.getOrientation());
//
//        rvGroups.addItemDecoration(dividerItemDecoration);



        mLayoutManager = new LinearLayoutManager(getActivity());//, LinearLayoutManager.VERTICAL, true);
        rvGroups.setLayoutManager(mLayoutManager);
        rvGroups.addItemDecoration(new VerticalSpaceItemDecoration(10));

        GroupAdapter adapter = new GroupAdapter(groupsList, getContext(),getActivity());
        rvGroups.setAdapter(adapter);
//        mSubscriptions.add(NetworkUtil.getRetrofit(Constants.getAccessToken(getActivity()), Constants.getRefreshToken(getActivity()), Constants.getEmail(getActivity())).getEvents(Constants.loggedUser.getEmail())
//                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse, this::handleError));

        if(groupsList==null){
            progressText.setText(getResources().getString(R.string.nothing_found));
            pbHeaderProgress.setVisibility(View.GONE);
        }else{
            headerProgress.setVisibility(View.GONE);
            pbHeaderProgress.setVisibility(View.GONE);
        }
        swipeContainer.setRefreshing(false);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            //If we don't want the last space
//            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
//                outRect.bottom = verticalSpaceHeight;
//            }
            outRect.bottom = verticalSpaceHeight;
        }
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