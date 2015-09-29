package com.example.tyson.electricbuddy;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

public class ConnectionsObject implements Parcelable {

    String connectionTypeID;
    String connectionTitle;
    String levelId;
    String connectionLevelId;
    String connectionLevelTitle;
    String amps;
    String voltage;
    String powerKw;
    String parentId;
    String connectionQty;
    JSONObject jsonObject;
    SQLDatabaseAdapter sqlDataBaseAdapter;

    public ConnectionsObject(JSONObject jsonObject, String parentId, Context context) {
        this.jsonObject = jsonObject;
        this.parentId = parentId;
        this.sqlDataBaseAdapter = new SQLDatabaseAdapter(context);
    }

    public ConnectionsObject(String connectionTypeID, String connectionTitle,
                             String levelID, String connectionLevelID,
                             String connectionLevelTitle, String amps,
                             String voltage, String powerKw, String parentId,
                             String connectionQty) {

        /*Log.d("dataGram", connectionTypeID + connectionTitle +
                 levelID+  connectionLevelID +
                 connectionLevelTitle + amps +
                 voltage +  powerKw +  parentId);*/
        this.connectionTypeID = connectionTypeID;
        this.connectionTitle = connectionTitle;
        this.levelId = levelID;
        this.connectionLevelId = connectionLevelID;
        this.connectionLevelTitle = connectionLevelTitle;
        this.amps = amps;
        this.voltage = voltage;
        this.powerKw = powerKw;
        this.parentId = parentId;
        this.connectionQty = connectionQty;
    }

    protected ConnectionsObject(Parcel in) {
        connectionTypeID = in.readString();
        connectionTitle = in.readString();
        levelId = in.readString();
        connectionLevelId = in.readString();
        connectionLevelTitle = in.readString();
        amps = in.readString();
        voltage = in.readString();
        powerKw = in.readString();
        parentId = in.readString();
        connectionQty = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(connectionTypeID);
        dest.writeString(connectionTitle);
        dest.writeString(levelId);
        dest.writeString(connectionLevelId);
        dest.writeString(connectionLevelTitle);
        dest.writeString(amps);
        dest.writeString(voltage);
        dest.writeString(powerKw);
        dest.writeString(parentId);
        dest.writeString(connectionQty);
    }

    public static final Parcelable.Creator<ConnectionsObject> CREATOR = new Parcelable.Creator<ConnectionsObject>() {
        @Override
        public ConnectionsObject createFromParcel(Parcel in) {
            return new ConnectionsObject(in);
        }

        @Override
        public ConnectionsObject[] newArray(int size) {
            return new ConnectionsObject[size];
        }
    };

    public void parseJson(JSONObject jsonObject) {
        try {
            connectionTypeID = jsonObject.getString("ConnectionTypeID");
            JSONObject connectionTypeObject = jsonObject.optJSONObject("ConnectionType");
            connectionTitle = connectionTypeObject.getString("Title");
            levelId = jsonObject.getString("LevelID");
            connectionQty = jsonObject.getString("Quantity").replace("null", "---");
            JSONObject levelTypeObject = jsonObject.optJSONObject("Level");

            if (levelTypeObject != null) {
                connectionLevelId = levelTypeObject.getString("ID");
                connectionLevelTitle = levelTypeObject.getString("Title");
            }

            amps = jsonObject.getString("Amps").replace("null", "---");
            voltage = jsonObject.getString("Voltage").replace("null", "---");
            powerKw = jsonObject.getString("PowerKW").replace("null", "---");

            long dataId = sqlDataBaseAdapter.insertConnectionData(connectionTypeID, connectionTitle
                    , levelId, connectionLevelId, connectionLevelTitle, amps, voltage, powerKw
                    , parentId, connectionQty, 0);
            if (dataId > 0)
                Log.d("Database", "CXN Row Inserted for Station " + parentId);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
