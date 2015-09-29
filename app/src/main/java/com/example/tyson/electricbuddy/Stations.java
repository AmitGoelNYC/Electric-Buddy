package com.example.tyson.electricbuddy;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Stations implements Parcelable {

    String stationName;
    String distance;
    String address;
    String town;
    String state;
    String postalCode;
    String telephone;
    String email;
    String relatedUrl;
    String operatorTitle;
    String latitude;
    String longitude;
    String usageId;
    String usageTitle;
    String stationId;
    boolean isFavorite;

    Context context;
    JSONObject jsonObject;
    ArrayList<ConnectionsObject> arrayListConnection;
    SQLDatabaseAdapter sqlDataBaseAdapter;

    public Stations(JSONObject jsonObject, Context context) {
        this.jsonObject = jsonObject;
        this.sqlDataBaseAdapter = new SQLDatabaseAdapter(context);
        this.context = context;
    }

    public Stations(String title, String distance, String address,
                    String town, String state, String postalCode,
                    String telephone, String email, String relatedUrl,
                    String operatorTitle, String latitude, String longitude,
                    String usageId, String usageTitle, String StationId,
                    boolean isFavorite,
                    ArrayList<ConnectionsObject> arrayListConnection) {
        this.stationName = title;
        this.distance = distance;
        this.address = address;
        this.town = town;
        this.state = state;
        this.postalCode = postalCode;
        this.telephone = telephone;
        this.email = email;
        this.relatedUrl = relatedUrl;
        this.operatorTitle = operatorTitle;
        this.latitude = latitude;
        this.longitude = longitude;
        this.usageId = usageId;
        this.usageTitle = usageTitle;
        this.stationId = StationId;
        this.arrayListConnection = arrayListConnection;
        this.isFavorite = isFavorite;
    }

    protected Stations(Parcel in) {
        stationName = in.readString();
        distance = in.readString();
        address = in.readString();
        town = in.readString();
        state = in.readString();
        postalCode = in.readString();
        telephone = in.readString();
        email = in.readString();
        relatedUrl = in.readString();
        operatorTitle = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        usageId = in.readString();
        usageTitle = in.readString();
        stationId = in.readString();
        isFavorite = in.readByte() != 0x00;
        arrayListConnection = new ArrayList<>();
        if (in.readByte() == 0x01) {
            arrayListConnection = new ArrayList<>();
            in.readList(arrayListConnection, ConnectionsObject.class.getClassLoader());
        } else {
            arrayListConnection = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stationName);
        dest.writeString(distance);
        dest.writeString(address);
        dest.writeString(town);
        dest.writeString(state);
        dest.writeString(postalCode);
        dest.writeString(telephone);
        dest.writeString(email);
        dest.writeString(relatedUrl);
        dest.writeString(operatorTitle);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(usageId);
        dest.writeString(usageTitle);
        dest.writeString(stationId);
        dest.writeByte((byte) (isFavorite ? 0x01 : 0x00));
        if (arrayListConnection == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(arrayListConnection);
        }
    }

    public static final Parcelable.Creator<Stations> CREATOR = new Parcelable.Creator<Stations>() {
        @Override
        public Stations createFromParcel(Parcel in) {
            return new Stations(in);
        }

        @Override
        public Stations[] newArray(int size) {
            return new Stations[size];
        }
    };

    public void parseJson() {
        try {
            arrayListConnection = new ArrayList<>();
            stationId = jsonObject.getString("ID");
            isFavorite = sqlDataBaseAdapter.findFavorite(stationId);
            JSONObject operatorInfo = jsonObject.optJSONObject("OperatorInfo");
            JSONObject addressInfo = jsonObject.optJSONObject("AddressInfo");
            JSONArray jsonConnectionArray = jsonObject.getJSONArray("Connections");
            JSONObject usageInfo = jsonObject.optJSONObject("UsageType");

            for (int j = 0; j < jsonConnectionArray.length(); j++) {
                ConnectionsObject connectionsObject =
                        new ConnectionsObject(jsonConnectionArray.optJSONObject(j)
                                , stationId, context);
                connectionsObject.parseJson(jsonConnectionArray.getJSONObject(j));
                arrayListConnection.add(connectionsObject);
            }

            stationName = addressInfo.getString("Title").replace("'", "");
            address = addressInfo.getString("AddressLine1").replace("'", "");
            distance = addressInfo.getString("Distance").substring(0, 4);
            town = addressInfo.getString("Town");
            state = addressInfo.getString("StateOrProvince");
            postalCode = addressInfo.getString("Postcode");
            telephone = addressInfo.getString("ContactTelephone1").trim()
                    .replace("(","")
                    .replace(")","")
                    .replace(" ","-");
            relatedUrl = addressInfo.getString("RelatedURL");
            latitude = addressInfo.getString("Latitude");
            longitude = addressInfo.getString("Longitude");

            if (operatorInfo != null) {
                operatorTitle = operatorInfo.getString("Title");
                email = operatorInfo.getString("ContactEmail")
                        .replace("null", "---");
                if (email.equals("")) {
                    email = "---";
                }
            }
            if (usageInfo != null) {
                usageId = usageInfo.getString("ID");
                usageTitle = usageInfo.getString("Title");
            }

            long dataId = sqlDataBaseAdapter.insertStationData(stationName, address
                    , distance, town, state , postalCode, telephone, email, relatedUrl
                    , operatorTitle, latitude, longitude, stationId , usageId, usageTitle, 0);
            if (dataId > 0)
                Log.d("Database", "Station " + stationId + " Inserted");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}