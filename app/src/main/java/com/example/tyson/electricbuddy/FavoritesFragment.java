package com.example.tyson.electricbuddy;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.filippudak.ProgressPieView.ProgressPieView;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements DownloadTasker.UIUpdater {

    private RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        ((ProgressPieView) view.findViewById(R.id.progressbar)).setVisibility(View.GONE);
        ((Button)view.findViewById(R.id.button)).setVisibility(View.GONE);
        ((EditText) view.findViewById(R.id.editText)).setVisibility(View.GONE);

        DownloadTasker myTasker = new DownloadTasker("", this, getActivity(), 0
                , 0, false, true);
        myTasker.execute();
        return view;
    }

    @Override
    public void updateProgressTo(int progress) {
    }


    @Override
    public void updateUI(final ArrayList<Stations> stationNames) {
        RVAdapter adapter = new RVAdapter(stationNames, getActivity());
        rv.setAdapter(adapter);
    }

}
