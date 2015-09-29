package com.example.tyson.electricbuddy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
    public static final String DATA_FETCHED ="com.example.tyson.electricbuddy.DATA_FETCHED";
    public static final String UPDATE_WIDGET = "com.example.tyson.electricbuddy.UPDATE_WIDGET";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent serviceIntent = new Intent(context, RemoteFetchService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            context.startService(serviceIntent);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            cancelAlarmManager(context, appWidgetId);
        }
    }

    private void cancelAlarmManager(Context context, int appWidgetId) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentUpdate = new Intent(context, MyWidgetProvider.class);
        intentUpdate.setAction(UPDATE_WIDGET);
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context,
                0,
                intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntentAlarm);
        Log.d("cancelAlarmManager", "Cancelled Alarm. Action = " + UPDATE_WIDGET);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context,intent);
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        Log.d("Received", "broadcast");
        if (intent.getAction().equals(DATA_FETCHED)) {
            RemoteViews remoteViews = updateAppWidget(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)){
            onEnabled(context);
            Log.d("enabled","ENABLED");
            Intent intentUpdate = new Intent(context, MyWidgetProvider.class);
            intentUpdate.setAction(UPDATE_WIDGET);

            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(context,
                    0,
                    intentUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            //If you want one global AlarmManager for all instances, put this alarmManger as
            //static and create it only the first time.
            //Then pass in the Intent all the ids and do not put the Uri.
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (60 * 1000),
                    (60 * 1000),
                    pendingIntentAlarm);
        }else if(intent.getAction().equals(UPDATE_WIDGET)){
            int[] appWidgets = {appWidgetId};
            Log.d("FROM ALARM", UPDATE_WIDGET);
            onUpdate(context, appWidgetManager, appWidgets);
        }
    }

    private RemoteViews updateAppWidget(Context context, int appWidgetId) {
        Log.d("Updating", "appwidgets");
        //CharSequence widgetText = MyWidgetProviderConfigureActivity.loadTitlePref(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget_provider);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent intent = new Intent(context, ListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        views.setRemoteAdapter(R.id.list, intent);
        views.setEmptyView(R.id.list, R.id.appwidget_text);

        return views;
    }
}

