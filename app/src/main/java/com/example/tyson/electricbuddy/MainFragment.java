package com.example.tyson.electricbuddy;

import android.animation.Animator;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        DownloadTasker.UIUpdater,
        View.OnClickListener{

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location lastLocation;
    boolean networkConnected = false;
    ProgressPieView progressBar;
    EditText editText;
    Button button;
    private RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar = (ProgressPieView) view.findViewById(R.id.progressbar);
        progressBar.setTextColor(getResources().getColor(R.color.holo_orange_light));
        progressBar.setBackgroundColor(getResources().getColor(R.color.holo_red_dark));
        progressBar.setProgressColor(getResources().getColor(R.color.holo_green_dark));
        progressBar.setStrokeColor(getResources().getColor(R.color.holo_blue_dark));
        progressBar.setProgressFillType(1);
        progressBar.setVisibility(View.GONE);

        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(this);
        editText = (EditText) view.findViewById(R.id.editText);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (googleServicesAvailable()) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public boolean googleServicesAvailable() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, getActivity(), 0);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null)
            Toast.makeText(getActivity(), "Cant get Current location", Toast.LENGTH_LONG).show();
    }

    public void findLocation() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        progressBar.setProgress(0);
        progressBar.setText("0");

        if (networkInfo != null && networkInfo.isConnected())
            networkConnected = true;
        else {
            networkConnected = false;
            Toast.makeText(getActivity(), "Network not connected - Retrieving Results from Last Search",
                    Toast.LENGTH_LONG).show();
        }
        if (lastLocation != null) {
            double latitude = lastLocation.getLatitude();
            double longitude = lastLocation.getLongitude();
            DownloadTasker myTasker = new DownloadTasker(Constants.SERVER_URL, this, getActivity(), latitude
                    , longitude, networkConnected, false);
            myTasker.execute();

            rv.setVisibility(View.GONE);
            int cx = progressBar.getWidth() / 2;
            int cy = progressBar.getHeight() / 2;
            int finalRadius = Math.max(progressBar.getWidth(), progressBar.getHeight());
            if(android.os.Build.VERSION.SDK_INT > 20) {
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(progressBar, cx, cy, 0, finalRadius);
                progressBar.setVisibility(View.VISIBLE);
                anim.start();
            }else{
                progressBar.setProgressFillType(View.VISIBLE);
            }

            if (networkConnected)
                getGeoAddress(latitude, longitude);
        }
    }

    public void getGeoAddress(double latitude, double longitude) {
        String cityName = "";
        String streetAddNumber = "";
        String street = "";
        String state = "";

        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null) {
            if (addresses.size() > 0) {
                streetAddNumber = addresses.get(0).getSubThoroughfare();
                street = addresses.get(0).getThoroughfare();
                cityName = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
            }
            editText.setText(streetAddNumber + " " + street + ", " + cityName + ", " + state);
        }
    }

    @Override
    public void updateProgressTo(int progress) {
        progressBar.setProgress(progress);
        progressBar.setText("" + progress);
    }


    @Override
    public void updateUI(final ArrayList<Stations> stationNames) {
        ///***********old code ****//////
        /*listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        ExampleListAdapter myAdapter = new ExampleListAdapter(getActivity(), stationNames);
        listView.setAdapter(myAdapter);
        listView.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ScreenSlidePagerActivity.class);
                intent.putParcelableArrayListExtra("theObject", stationNames);
                intent.putExtra("position", position);
                if(android.os.Build.VERSION.SDK_INT > 20) {
                    getActivity().getWindow().setExitTransition(new Fade());
                    startActivity(intent, ActivityOptions
                            .makeSceneTransitionAnimation(getActivity()).toBundle());
                }else
                    startActivity(intent);
            }
        });*/

        rv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        RVAdapter adapter = new RVAdapter(stationNames, getActivity());
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                findLocation();
                break;
            default:
                break;
        }
    }
}
