package com.groupapp.groupapp.groupapp.adapters;

import android.content.Context;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.List;

public class NumbersAdapter extends ArrayAdapter{

    public NumbersAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        return 12;
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
        Button button;
        if (convertView == null) {
            button = new Button(super.getContext());
            if (position == 9) button.setText("");
            else if (position == 10) button.setText("0");
            else if (position == 11) button.setText("<");
            else button.setText(String.valueOf(position+1));
            //button.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
            //button.setPadding(8, 8, 8, 8);
        } else {
            button = (Button) convertView;
        }

        return button;
    }
}
