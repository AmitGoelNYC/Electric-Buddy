package com.example.tyson.electricbuddy;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLDatabaseAdapter {

    SQLHelper helper;

    public SQLDatabaseAdapter(Context context) {
        helper = new SQLHelper(context);
    }

    public long insertStationData(String stationName, String address, String distance, String town, String state
            , String postalCode, String telephone, String email, String relatedUrl, String operatorTitle
            , String latitude, String longitude, String id, String usageId, String usageTitle, int table) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.STATION_NAME, stationName);
        contentValues.put(SQLHelper.ADDRESS, address);
        contentValues.put(SQLHelper.DISTANCE, distance);
        contentValues.put(SQLHelper.TOWN, town);
        contentValues.put(SQLHelper.STATE, state);
        contentValues.put(SQLHelper.POSTAL_CODE, postalCode);
        contentValues.put(SQLHelper.TELEPHONE, telephone);
        contentValues.put(SQLHelper.EMAIL, email);
        contentValues.put(SQLHelper.URL, relatedUrl);
        contentValues.put(SQLHelper.OPERATOR, operatorTitle);
        contentValues.put(SQLHelper.LATITUDE, latitude);
        contentValues.put(SQLHelper.LONGITUDE, longitude);
        contentValues.put(SQLHelper.ID, id);
        contentValues.put(SQLHelper.USAGE_ID, usageId);
        contentValues.put(SQLHelper.USAGE_TITLE, usageTitle);

        long returnId;
        if (table == 0) {
            returnId = db.insert(SQLHelper.STATION_TABLE_NAME, null, contentValues);
        } else {
            returnId = db.insert(SQLHelper.FAVSTATION_TABLE_NAME, null, contentValues);
        }
        return returnId;
    }

    public long insertConnectionData(String ConnectionTypeID, String connectionTitle
            , String levelId, String connectionLevelId, String connectionLevelTitle, String amps
            , String voltage, String powerKw, String id, String connectionQty, int table) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.CONNECTION_TYPE_ID, ConnectionTypeID);
        contentValues.put(SQLHelper.CONNECTION_TITLE, connectionTitle);
        contentValues.put(SQLHelper.LEVEL_ID, levelId);
        contentValues.put(SQLHelper.CONNECTION_LEVEL_ID, connectionLevelId);
        contentValues.put(SQLHelper.CONNECTION_LEVEL_TITLE, connectionLevelTitle);
        contentValues.put(SQLHelper.AMPS, amps);
        contentValues.put(SQLHelper.VOLTAGE, voltage);
        contentValues.put(SQLHelper.POWER_KW, powerKw);
        contentValues.put(SQLHelper.PARENT_ID, id);
        contentValues.put(SQLHelper.CONNECTION_QTY, connectionQty);

        long returnId;
        if (table == 0) {
            returnId = db.insert(SQLHelper.CONNECTION_TABLE_NAME, null, contentValues);
        } else {
            returnId = db.insert(SQLHelper.FAVCONNECTION_TABLE_NAME, null, contentValues);
        }
        return returnId;

    }

    public int deleteAllStations() {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(SQLHelper.STATION_TABLE_NAME, null, null);
    }

    public int deleteAllConnections() {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(SQLHelper.CONNECTION_TABLE_NAME, null, null);
    }

    public boolean findFavorite(String refId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean foundFavorite = false;
        String[] columns = {SQLHelper.STATION_NAME};
        String selection = SQLHelper.ID + " = ?";
        String[] args = {refId};
        Cursor cursor = db.query(SQLHelper.FAVSTATION_TABLE_NAME, columns, selection,
                args, null, null, null);
        if (cursor.getCount() > 0) {
            foundFavorite = true;
        }
        cursor.close();
        return foundFavorite;
    }

    public int deleteFavStation(String stationId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {stationId};
        int count = db.delete(SQLHelper.FAVSTATION_TABLE_NAME, SQLHelper.ID + " =? ", whereArgs);
        count += db.delete(SQLHelper.FAVCONNECTION_TABLE_NAME, SQLHelper.PARENT_ID + " =? ", whereArgs);
        return count;
    }

    public ArrayList<Stations> getData(String table) {
        String stationsTable;
        String connectionsTable;
        if(table.equals(SQLHelper.STATION_TABLE_NAME)){
            stationsTable = SQLHelper.STATION_TABLE_NAME;
            connectionsTable = SQLHelper.CONNECTION_TABLE_NAME;
        }else{
            stationsTable = SQLHelper.FAVSTATION_TABLE_NAME;
            connectionsTable = SQLHelper.FAVCONNECTION_TABLE_NAME;
        }
        ArrayList<Stations> stationsList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] stationColumns = {SQLHelper.STATION_NAME, SQLHelper.ADDRESS, SQLHelper.DISTANCE,
                SQLHelper.TOWN, SQLHelper.STATE, SQLHelper.POSTAL_CODE,
                SQLHelper.TELEPHONE, SQLHelper.EMAIL, SQLHelper.URL,
                SQLHelper.OPERATOR, SQLHelper.LATITUDE, SQLHelper.LONGITUDE,
                SQLHelper.ID, SQLHelper.USAGE_ID, SQLHelper.USAGE_TITLE
        };

        String[] cxnColumns = {SQLHelper.CONNECTION_TYPE_ID, SQLHelper.CONNECTION_TITLE,
                SQLHelper.LEVEL_ID, SQLHelper.CONNECTION_LEVEL_ID, SQLHelper.CONNECTION_LEVEL_TITLE,
                SQLHelper.AMPS, SQLHelper.VOLTAGE, SQLHelper.POWER_KW, SQLHelper.PARENT_ID,
                SQLHelper.CONNECTION_QTY
        };
        String selection = SQLHelper.PARENT_ID + " = ?";

        Cursor cursor = db.query(stationsTable, stationColumns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            ArrayList<ConnectionsObject> connectionsList = new ArrayList<>();
            String name = cursor.getString(cursor.getColumnIndex(SQLHelper.STATION_NAME));
            String address = cursor.getString(cursor.getColumnIndex(SQLHelper.ADDRESS));
            String distance = cursor.getString(cursor.getColumnIndex(SQLHelper.DISTANCE));
            String town = cursor.getString(cursor.getColumnIndex(SQLHelper.TOWN));
            String state = cursor.getString(cursor.getColumnIndex(SQLHelper.STATE));
            String postalCode = cursor.getString(cursor.getColumnIndex(SQLHelper.POSTAL_CODE));
            String telephone = cursor.getString(cursor.getColumnIndex(SQLHelper.TELEPHONE));
            String email = cursor.getString(cursor.getColumnIndex(SQLHelper.EMAIL));
            String url = cursor.getString(cursor.getColumnIndex(SQLHelper.URL));
            String operator = cursor.getString(cursor.getColumnIndex(SQLHelper.OPERATOR));
            String latitude = cursor.getString(cursor.getColumnIndex(SQLHelper.LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(SQLHelper.LONGITUDE));
            String stationId = cursor.getString(cursor.getColumnIndex(SQLHelper.ID));
            String usageId = cursor.getString(cursor.getColumnIndex(SQLHelper.USAGE_ID));
            String usageTitle = cursor.getString(cursor.getColumnIndex(SQLHelper.USAGE_TITLE));
            boolean isFavorite;

            String[] args = {stationId};
            Cursor cxnCursor = db.query(connectionsTable, cxnColumns, selection, args, null, null, null);
            while (cxnCursor.moveToNext()) {
                String ConnectionTypeID = cxnCursor.getString(
                        cxnCursor.getColumnIndex(SQLHelper.CONNECTION_TYPE_ID));
                String connectionTitle = cxnCursor.getString(
                        cxnCursor.getColumnIndex(SQLHelper.CONNECTION_TITLE));
                String levelId = cxnCursor.getString(
                        cxnCursor.getColumnIndex(SQLHelper.LEVEL_ID));
                String connectionLevelId = cxnCursor.getString(
                        cxnCursor.getColumnIndex(SQLHelper.CONNECTION_LEVEL_ID));
                String connectionLevelTitle = cxnCursor.getString(
                        cxnCursor.getColumnIndex(SQLHelper.CONNECTION_LEVEL_TITLE));
                String amps = cxnCursor.getString(cxnCursor.getColumnIndex(SQLHelper.AMPS));
                String voltage = cxnCursor.getString(cxnCursor.getColumnIndex(SQLHelper.VOLTAGE));
                String powerKw = cxnCursor.getString(cxnCursor.getColumnIndex(SQLHelper.POWER_KW));
                String parentId = cxnCursor.getString(cxnCursor.getColumnIndex(SQLHelper.PARENT_ID));
                String connectionQty = cxnCursor.getString(cxnCursor.getColumnIndex(SQLHelper.CONNECTION_QTY));
                ConnectionsObject connectionsObject = new ConnectionsObject(
                        ConnectionTypeID, connectionTitle, levelId,
                        connectionLevelId, connectionLevelTitle,
                        amps, voltage, powerKw, parentId, connectionQty);
                connectionsList.add(connectionsObject);
            }
            cxnCursor.close();
            isFavorite = findFavorite(stationId);
            Stations stations = new Stations(name, distance, address, town, state, postalCode,
                    telephone, email, url, operator, latitude, longitude, usageId, usageTitle, stationId,
                    isFavorite, connectionsList);
            stationsList.add(stations);
        }
        cursor.close();
        return stationsList;

    }

   /* //   Reading/Retrieving
    public String getData() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {SQLHelper.UID, SQLHelper.NAME, SQLHelper.ADDRESS};

        Cursor cursor = db.query(SQLHelper.TABLE_NAME, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();

        while(cursor.moveToNext()) {
            int indexId = cursor.getColumnIndex(SQLHelper.UID); //0
            int indexName = cursor.getColumnIndex(SQLHelper.NAME); //1
            int indexAddress = cursor.getColumnIndex(SQLHelper.ADDRESS); //2
            int cid = cursor.getInt(indexId); //1
            String name = cursor.getString(indexName);
            String address = cursor.getString(indexAddress);
            buffer.append(cid+":"+name+"-"+address+"\n");

        }
        return  buffer.toString();

    }

    public String getAddress(String name){
        // select (name, address) from Users WHERE name = 'Jane'
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {SQLHelper.NAME, SQLHelper.ADDRESS};
        String[] args = {name};
        Cursor cursor = db.query(SQLHelper.TABLE_NAME, columns, SQLHelper.NAME+" = ?", args, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()) {
//           int index1 = cursor.getColumnIndex(SQLHelper.NAME);
            int index2 = cursor.getColumnIndex(SQLHelper.ADDRESS);
//           String personName = cursor.getString(index1);
            String address = cursor.getString(index2);
            buffer.append(name+"-"+address+"\n");
        }
        return  buffer.toString();
    }

    public String getUserId(String name, String address){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {SQLHelper.UID};
        String[] selectionArgs = {name, address};
        Cursor cursor = db.query(SQLHelper.TABLE_NAME, columns, SQLHelper.NAME+" =? AND "+SQLHelper.ADDRESS +" =?", selectionArgs, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while(cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(SQLHelper.UID);
            int cid = cursor.getInt(index1);
            buffer.append(cid+"\n");
        }
        return  buffer.toString();
    }
    //Update
    public int updateName(String oldName, String newName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.NAME, newName);
        String[] whereArgs = {oldName};
        int count = db.update(SQLHelper.TABLE_NAME, contentValues, SQLHelper.NAME+" =? ", whereArgs);
        return count;

    }

    public int updateAddress(String userName, String newAddress) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLHelper.ADDRESS, newAddress);
        String[] whereArgs = {userName};
        int count = db.update(SQLHelper.TABLE_NAME, contentValues, SQLHelper.NAME+" =? ", whereArgs);
        return count;

    }

    public int deleteName(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] whereArgs = {name};
        int count = db.delete(SQLHelper.TABLE_NAME, SQLHelper.NAME+" =? ",whereArgs);
        return count;
    }*/


    static class SQLHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "MyDB";
        private static final String STATION_TABLE_NAME = "Stations";
        private static final String FAVSTATION_TABLE_NAME = "FavStations";
        private static final String CONNECTION_TABLE_NAME = "Connections";
        private static final String FAVCONNECTION_TABLE_NAME = "FavConnections";
        private static final int DATABASE_VERSION = 1;

        /***********
         * Station Name Fields
         ************/
        private static final String STATION_NAME = "StationName";
        private static final String ADDRESS = "Address";
        private static final String DISTANCE = "Distance";
        private static final String TOWN = "Town";
        private static final String STATE = "State";
        private static final String POSTAL_CODE = "PostalCode";
        private static final String TELEPHONE = "Telephone";
        private static final String EMAIL = "Email";
        private static final String URL = "RelatedUrl";
        private static final String OPERATOR = "OperatorTitle";
        private static final String LATITUDE = "Latitude";
        private static final String LONGITUDE = "Longitude";
        private static final String ID = "StationId";
        private static final String USAGE_ID = "UsageId";
        private static final String USAGE_TITLE = "UsageTitle";

        /***********
         * Connection Name Fields
         ************/
        private static final String CONNECTION_TYPE_ID = "ConnectionTypeID";
        private static final String CONNECTION_TITLE = "ConnectionTitle";
        private static final String LEVEL_ID = "LevelId";
        private static final String CONNECTION_LEVEL_ID = "ConnectionLevelId";
        private static final String CONNECTION_LEVEL_TITLE = "ConnectionLevelTitle";
        private static final String AMPS = "Amps";
        private static final String VOLTAGE = "Voltage";
        private static final String POWER_KW = "PowerKw";
        private static final String PARENT_ID = "ParentId";
        private static final String CONNECTION_QTY = "ConnectionQty";

        private static final String CREATE_STATION_TABLE = "CREATE TABLE " + STATION_TABLE_NAME + " ("
                + STATION_NAME + " TEXT, " + ADDRESS + " TEXT, " + DISTANCE + " TEXT, "
                + TOWN + " TEXT, " + STATE + " TEXT, " + POSTAL_CODE + " TEXT, "
                + TELEPHONE + " TEXT, " + EMAIL + " TEXT, " + URL + " TEXT, "
                + OPERATOR + " TEXT, " + LATITUDE + " TEXT, " + LONGITUDE + " TEXT, "
                + ID + " TEXT, " + USAGE_ID + " TEXT, " + USAGE_TITLE + " TEXT);";

        private static final String CREATE_FAVSTATION_TABLE = "CREATE TABLE " + FAVSTATION_TABLE_NAME + " ("
                + STATION_NAME + " TEXT, " + ADDRESS + " TEXT, " + DISTANCE + " TEXT, "
                + TOWN + " TEXT, " + STATE + " TEXT, " + POSTAL_CODE + " TEXT, "
                + TELEPHONE + " TEXT, " + EMAIL + " TEXT, " + URL + " TEXT, "
                + OPERATOR + " TEXT, " + LATITUDE + " TEXT, " + LONGITUDE + " TEXT, "
                + ID + " TEXT, " + USAGE_ID + " TEXT, " + USAGE_TITLE + " TEXT);";

        private static final String CREATE_CONNECTION_TABLE = "CREATE TABLE " + CONNECTION_TABLE_NAME + " ("
                + CONNECTION_TYPE_ID + " TEXT, " + CONNECTION_TITLE + " TEXT, " + LEVEL_ID + " TEXT, "
                + CONNECTION_LEVEL_ID + " TEXT, " + CONNECTION_LEVEL_TITLE + " TEXT, " + AMPS + " TEXT, "
                + VOLTAGE + " TEXT, " + POWER_KW + " TEXT, " + PARENT_ID + " TEXT, "
                + CONNECTION_QTY + " TEXT);";

        private static final String CREATE_FAVCONNECTION_TABLE = "CREATE TABLE " + FAVCONNECTION_TABLE_NAME + " ("
                + CONNECTION_TYPE_ID + " TEXT, " + CONNECTION_TITLE + " TEXT, " + LEVEL_ID + " TEXT, "
                + CONNECTION_LEVEL_ID + " TEXT, " + CONNECTION_LEVEL_TITLE + " TEXT, " + AMPS + " TEXT, "
                + VOLTAGE + " TEXT, " + POWER_KW + " TEXT, " + PARENT_ID + " TEXT, "
                + CONNECTION_QTY + " TEXT);";
        //        private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
        /*private static final String ALTER_TABLE = "ALTER TABLE " + STATION_TABLE_NAME + " ADD COLUMN "
                + PHONE + " int DEFAULT 0";*/

        Context context;

        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STATION_TABLE);
            db.execSQL(CREATE_FAVSTATION_TABLE);
            db.execSQL(CREATE_CONNECTION_TABLE);
            db.execSQL(CREATE_FAVCONNECTION_TABLE);
            Log.d("DB", "Created Databses");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL(ALTER_TABLE);
            Toast.makeText(context, "onUpgrade Called", Toast.LENGTH_LONG).show();
        }
    }
}
