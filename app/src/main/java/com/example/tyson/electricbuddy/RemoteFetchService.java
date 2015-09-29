package com.example.tyson.electricbuddy;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class RemoteFetchService extends Service implements DownloadTasker.UIUpdater{

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static ArrayList<Stations> stations;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        processResult();
        return super.onStartCommand(intent, flags, startId);
    }

    private void processResult() {
        DownloadTasker myTasker = new DownloadTasker(Constants.SERVER_URL, this, this, 40.646960
                , -73.904569, true, false);
        myTasker.execute();
    }

    @Override
    public void updateProgressTo(int progress) {

    }

    @Override
    public void onDestroy() {
        Log.d("SERVICE", "ended");
        super.onDestroy();
    }

    @Override
    public void updateUI(ArrayList<Stations> stationNamesArray) {
        stations = stationNamesArray;
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(MyWidgetProvider.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        sendBroadcast(widgetUpdateIntent);
        this.stopSelf();
    }
}
