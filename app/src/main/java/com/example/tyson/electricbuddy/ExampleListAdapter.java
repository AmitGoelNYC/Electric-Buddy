package com.example.tyson.electricbuddy;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ExampleListAdapter extends ArrayAdapter {

    private Context context;

    public ExampleListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;
    }

    private class ViewHolder{
        TextView titleText;
        TextView distanceText;
        ImageView imageView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Stations item = (Stations)getItem(position);
        View viewToUse;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewToUse = mInflater.inflate(R.layout.example_list_item, null);
            holder = new ViewHolder();
            holder.titleText = (TextView)viewToUse.findViewById(R.id.textView);
            holder.distanceText = (TextView) viewToUse.findViewById(R.id.textView2);
            holder.imageView =   (ImageView)viewToUse.findViewById(R.id.imageView);
            viewToUse.setTag(holder);
        } else {
            viewToUse = convertView;
            holder = (ViewHolder) viewToUse.getTag();
        }

        holder.titleText.setText(item.stationName);
        holder.distanceText.setText(item.distance + " Miles");
        holder.imageView.setImageResource(R.drawable.download);

        return viewToUse;
    }
}