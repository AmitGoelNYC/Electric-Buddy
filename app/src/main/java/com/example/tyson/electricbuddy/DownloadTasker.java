package com.example.tyson.electricbuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadTasker extends AsyncTask<Void, Integer, Void> {

    private String url;
    private ArrayList stationNamesArray;
    private boolean networkConnected;
    private boolean fetchFavsFromDB;

    UIUpdater uiUpdater;
    Context context;
    SQLDatabaseAdapter sqlDataBaseAdapter;
    Stations stations;

    public DownloadTasker(String url, UIUpdater context, Context contexts, double lat, double lon
            , boolean connected, boolean fetchfavs) {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(contexts);
        boolean distanceUnits = SP.getBoolean("pref_sync", false);

        this.url = url + "latitude=" + lat + "&longitude=" + lon;
        if(!distanceUnits)
            this.url += "&distanceunit=KM";

        this.uiUpdater = context;
        this.context = contexts;
        this.networkConnected = connected;
        this.fetchFavsFromDB = fetchfavs;
    }

    @Override
    protected Void doInBackground(Void... params) {
        stationNamesArray = new ArrayList();
        sqlDataBaseAdapter = new SQLDatabaseAdapter(context);
        if (networkConnected) {
            fetchJson();
        } else if (fetchFavsFromDB) {
            fetchFromDb("FavStations");
        } else {
            fetchFromDb("Stations");
        }
        return null;
    }

    private void fetchJson() {
        String json;
        try {
            sqlDataBaseAdapter.deleteAllStations();
            sqlDataBaseAdapter.deleteAllConnections();

            StringBuilder builder = new StringBuilder();
            URL theUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(theUrl
                    .openConnection().getInputStream()));
            while ((json = reader.readLine()) != null) {
                builder.append(json);
            }
            json = builder.toString();

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                stations = new Stations(jsonArray.optJSONObject(i), context);
                stations.parseJson();
                double val = ((i + 1.0) / jsonArray.length()) * 100.0;
                int x = (int) val;
                publishProgress(x);
                stationNamesArray.add(stations);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchFromDb(String table) {
        try {
            stationNamesArray = sqlDataBaseAdapter.getData(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        uiUpdater.updateProgressTo(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        uiUpdater.updateUI(stationNamesArray);
    }

    interface UIUpdater {
        void updateProgressTo(int progress);
        void updateUI(ArrayList<Stations> stationNamesArray);
    }
}
