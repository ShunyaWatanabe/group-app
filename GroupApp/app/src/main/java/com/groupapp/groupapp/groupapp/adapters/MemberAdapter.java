package com.groupapp.groupapp.groupapp.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.drm.DrmStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;


import com.groupapp.groupapp.groupapp.model.Group;

public class MemberAdapter extends BaseAdapter {
    Context context;
    Group group;

    public MemberAdapter(Context c, Group g) {
        context = c;
        group = g;
    }

    @Override
    public int getCount() {
        return group.getMembers().size();
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
            textView.setText(group.getMembers().get(position).getName());
            textView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        } else {
            textView = (TextView) convertView;
        }

        return textView;
    }
}
