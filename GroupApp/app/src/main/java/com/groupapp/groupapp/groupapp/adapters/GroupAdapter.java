package com.groupapp.groupapp.groupapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.R;
import com.groupapp.groupapp.groupapp.model.Group;
import com.groupapp.groupapp.groupapp.screens.ChatPageFragment;

import java.util.ArrayList;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static String TAG = GroupAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<Group> myGroups;
    private FragmentActivity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvName;
        public ImageView ivGroupImage;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.iv_group_name);
            ivGroupImage = view.findViewById(R.id.iv_group_image);
        }
    }

    public Group getElement(int position){
        return myGroups.get(position);
    }

    public GroupAdapter(ArrayList<Group> groups, Context mContext, FragmentActivity fa) {
        myGroups = groups;
        context = mContext;
        activity = fa;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View groupView = inflater.inflate(R.layout.item_group, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(groupView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Group thisGroup = myGroups.get(position);
        holder.tvName.setText(thisGroup.getName());

        Log.e(TAG,"MyGroups: " + myGroups.toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print(thisGroup.getId());
                Log.e(TAG,"POSITION OF CLICKED GROUP " + position);
                Log.e(TAG,"ID OF CLICKED GROUP " + thisGroup.getId());
                Log.e(TAG,"NAME OF CLICKED GROUP " + thisGroup.getName());
                replaceFragment(thisGroup.getId());
            }
        });

    }

    private void replaceFragment(String groupID) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
//        b.putParcelable("event",event);
        //b.putInt("position",position);
        bundle.putString("groupID", groupID);

        ft.addToBackStack("GroupsListFragment");


        ChatPageFragment fragment = new ChatPageFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentFrame, fragment, ChatPageFragment.TAG);

        ft.commit();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myGroups.size();
    }
}
