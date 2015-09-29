package com.example.tyson.electricbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ScreenSlidePageFragment extends Fragment
        implements View.OnClickListener {
    public static final String ARG_OBJECT = "object";
    SupportMapFragment fragment;
    GoogleMap googleMap;
    double latitude = 0.0f;
    double longitude = 0.0f;
    Stations stations;
    CheckBox favCheckBox;
    SQLDatabaseAdapter sqlDataBaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sqlDataBaseAdapter = new SQLDatabaseAdapter(getActivity());
        View rootView = inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        Bundle args = getArguments();
        stations = args.getParcelable(ARG_OBJECT);
        favCheckBox = ((CheckBox) rootView.findViewById(R.id.favorite));
        if (stations.isFavorite) {
            favCheckBox.setChecked(true);
        }

        ((TextView) rootView.findViewById(R.id.stationName)).setText(
                stations.stationName);
        ((TextView) rootView.findViewById(R.id.address)).setText(
                stations.address);
        ((TextView) rootView.findViewById(R.id.townStateZIP)).setText(
                stations.town + ", " + stations.state +
                        " " + stations.postalCode);
        ((TextView) rootView.findViewById(R.id.distance)).setText(
                stations.distance + " Mi");
        ((TextView) rootView.findViewById(R.id.telephone)).setText(
                stations.telephone.replace("(", "").replace(")", "")
                        .replace(" ", "-"));
        ((TextView) rootView.findViewById(R.id.email)).setText(
                stations.email);
        ((TextView) rootView.findViewById(R.id.url)).setText(
                stations.relatedUrl.replace("http://", ""));
        ((TextView) rootView.findViewById(R.id.usage)).setText(
                stations.usageTitle);
        ((TextView) rootView.findViewById(R.id.operator)).setText(
                stations.operatorTitle);

        LinearLayout callLinLayout = (LinearLayout) rootView.findViewById(R.id.call);
        if (!stations.telephone.equals("")) {
            callLinLayout.setOnClickListener(this);
        }

        LinearLayout emailLinLayout = (LinearLayout) rootView.findViewById(R.id.emailLinLayout);
        if (!stations.email.equals("")) {
            emailLinLayout.setOnClickListener(this);
        }

        favCheckBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            updateFavDB(true);
                        } else {
                            updateFavDB(false);
                        }
                    }
                }
        );

        try {
            latitude = Double.parseDouble(stations.latitude);
            longitude = Double.parseDouble(stations.longitude);
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }

        ArrayList<ConnectionsObject> mConnections = stations.arrayListConnection;
        TableLayout tl = (TableLayout) rootView.findViewById(R.id.tableLayout);
        //ArrayList<View> cxnRows = new ArrayList<>();
        for (int i = 0; i < mConnections.size(); i++) {
            View tr = inflater.inflate(R.layout.table_row, null, false);
            ((TextView) tr.findViewById(R.id.quantity))
                    .setText(mConnections.get(i).connectionQty);
            ((TextView) tr.findViewById(R.id.connType))
                    .setText(mConnections.get(i).connectionTitle);
            ((TextView) tr.findViewById(R.id.connLvlTitle))
                    .setText(mConnections.get(i).connectionLevelId);
            ((TextView) tr.findViewById(R.id.amps))
                    .setText(mConnections.get(i).amps);
            ((TextView) tr.findViewById(R.id.volt))
                    .setText(mConnections.get(i).voltage);
            ((TextView) tr.findViewById(R.id.powerkw))
                    .setText(mConnections.get(i).powerKw);
            tl.addView(tr);
        }
        return rootView;
    }

    private void updateFavDB(boolean updateVal) {
        try {
            if (updateVal) {
                long dataId = sqlDataBaseAdapter.insertStationData(stations.stationName, stations.address,
                        stations.distance, stations.town, stations.state, stations.postalCode,
                        stations.telephone, stations.email, stations.relatedUrl, stations.operatorTitle,
                        stations.latitude, stations.longitude, stations.stationId, stations.usageId,
                        stations.usageTitle, 1);
                if (dataId > 0)
                    Log.d("Database", "Fav Row Inserted id = " + stations.stationId);

                int count = 1;
                for (ConnectionsObject temp : stations.arrayListConnection) {
                    dataId = sqlDataBaseAdapter.insertConnectionData(temp.connectionTypeID,
                            temp.connectionTitle, temp.levelId, temp.connectionLevelId,
                            temp.connectionLevelTitle, temp.amps, temp.voltage, temp.powerKw
                            , temp.parentId, temp.connectionQty, 1);
                    if (dataId > 0) {
                        Log.d("Database", count + " CXN Row Inserted");
                        count++;
                    }
                }
            } else {
                stations.isFavorite = false;
                int deleteCount = sqlDataBaseAdapter.deleteFavStation(stations.stationId);
                Log.d("Database", "Fav Row Deleted id = " + stations.stationId);
                Log.d("Database", "Fav Row Deleted count = " + deleteCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.maps);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.maps, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap == null) {
            googleMap = fragment.getMap();
            googleMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            googleMap.moveCamera(update);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.call:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + stations.telephone));
                break;
            case R.id.emailLinLayout:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                String[] to = {stations.email};
                intent.putExtra(Intent.EXTRA_EMAIL, to);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello From USER");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello, Please assist");
                intent.setType("message/rfc822");
                break;
        }
        if (intent != null)
            startActivity(intent);
    }
}