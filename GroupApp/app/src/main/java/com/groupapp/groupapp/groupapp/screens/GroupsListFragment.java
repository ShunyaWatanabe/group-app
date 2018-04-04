package com.groupapp.groupapp.groupapp.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.model.GroupsList;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        rvGroups = view.findViewById(R.id.list);
//        ButterKnife.bind(this,view);
//
//        svEvent = (android.support.v7.widget.SearchView)view.findViewById(R.id.search_view);

        getGroups();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    private void getGroups() {
        //Change latter to loggeduser.getEmail()
        //for now not sure if it already download these data

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

//    @Override
//    public void onBackPressed() {
//        if (searchView.isSearchOpen()) {
//            searchView.closeSearch();
//        } else {
//            super.onBackPressed();
//        }
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