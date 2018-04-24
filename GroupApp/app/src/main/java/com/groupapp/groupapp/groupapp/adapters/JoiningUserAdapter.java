package com.groupapp.groupapp.groupapp.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groupapp.groupapp.groupapp.model.ConnectingUser;
import com.groupapp.groupapp.groupapp.model.User;


import java.util.ArrayList;

/**
 * Created by Tomek on 2018-04-24.
 */

public class JoiningUserAdapter extends BaseAdapter {
    Context context;
    ArrayList<ConnectingUser> joiningUsers;


    public JoiningUserAdapter(Context c, ArrayList<ConnectingUser> members) {
        this.context = c;
        this.joiningUsers = members;
    }

    @Override
    public int getCount() {
        return joiningUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setText(joiningUsers.get(position).getName());
            textView.setLayoutParams(new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        } else {
            textView = (TextView) convertView;
        }

        return textView;
    }
}
