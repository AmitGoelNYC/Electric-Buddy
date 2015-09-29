package com.example.tyson.electricbuddy;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MyWidgetProviderConfigureActivity extends Activity implements View.OnClickListener {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public MyWidgetProviderConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.my_widget_provider_configure);
        findViewById(R.id.add_button).setOnClickListener(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    /*View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            *//*final Context context = MyWidgetProviderConfigureActivity.this;

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MyWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);*//*

            // Make sure we pass back the original appWidgetId

        }
    };*/

    @Override
    public void onClick(View v) {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);

        Intent serviceIntent = new Intent(this, RemoteFetchService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        startService(serviceIntent);

        finish();
    }
}

