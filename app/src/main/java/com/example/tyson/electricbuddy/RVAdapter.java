package com.example.tyson.electricbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.StationViewHolder> {
    ArrayList<Stations> stations;
    Context context;

    public static class StationViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        CardView cv;
        TextView stationName;
        TextView stationDistance;
        ImageView stationPhoto;
        public IMyViewHolderClicks mListener;


        StationViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);
            mListener = listener;
            cv = (CardView) itemView.findViewById(R.id.cv);
            stationName = (TextView) itemView.findViewById(R.id.person_name);
            stationDistance = (TextView) itemView.findViewById(R.id.person_age);
            stationPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPotato(v, getAdapterPosition());
        }

        public interface IMyViewHolderClicks {
            void onPotato(View caller, int pos);
        }
    }

    RVAdapter(ArrayList<Stations> stations, Context context) {
        this.stations = stations;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        final RVAdapter.StationViewHolder stationViewHolder =
                new StationViewHolder(v, new RVAdapter.StationViewHolder.IMyViewHolderClicks() {
                    public void onPotato(View caller, int pos) {
                        Intent intent = new Intent(context, ScreenSlidePagerActivity.class);
                        intent.putParcelableArrayListExtra("theObject", stations);
                        intent.putExtra("position", pos);
                        /*if(android.os.Build.VERSION.SDK_INT > 20) {
                            context.getWindow().setExitTransition(new Fade());
                            context.startActivity(intent, ActivityOptions
                                    .makeSceneTransitionAnimation(context).toBundle());
                        }else*/
                            context.startActivity(intent);
                    }
                });
        return stationViewHolder;
    }

    @Override
    public void onBindViewHolder(StationViewHolder stationViewHolder, int i) {
        stationViewHolder.stationName.setText(stations.get(i).stationName);

        String distance;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        boolean distanceUnits = SP.getBoolean("pref_sync", false);
        if(distanceUnits){
            distance = stations.get(i).distance + " Miles";
        }else{
            distance = stations.get(i).distance + " Km";
        }


        stationViewHolder.stationDistance.setText(distance);
        stationViewHolder.stationPhoto.setImageResource(R.drawable.download);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}