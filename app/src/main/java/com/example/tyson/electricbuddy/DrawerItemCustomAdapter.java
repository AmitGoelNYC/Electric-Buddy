package com.example.tyson.electricbuddy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemCustomAdapter extends ArrayAdapter {

    Context mContext;
    ObjectDrawerItem data[] = null;

    public DrawerItemCustomAdapter(Context mContext, ObjectDrawerItem[] data) {
        super(mContext, android.R.layout.simple_list_item_1, data);
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(R.layout.drawer_list_item, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.rowIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textView);
        ObjectDrawerItem folder = data[position];
        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);

        return listItem;
    }

}