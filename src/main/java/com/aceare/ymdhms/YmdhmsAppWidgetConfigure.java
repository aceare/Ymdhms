package com.aceare.ymdhms;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by shreekant on 9/22/2015.
 */
public class YmdhmsAppWidgetConfigure extends Activity {
    private static final String LOG_TAG = YmdhmsAppWidgetConfigure.class.getSimpleName();

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public YmdhmsAppWidgetConfigure() {
        super();
Log.v(LOG_TAG, "YmdhmsAppWidgetConfigure()");
    }

/*
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.v(LOG_TAG, "YmdhmsAppWidgetConfigure.onReceive action=" + action);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.v(LOG_TAG, "appWidgetId=" + appWidgetId);

//        if (action.equals(WIDGET_CLICK_ACTION)) {
//        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "YmdhmsAppWidgetConfigure: onCreate()");
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.ymdhms_app_widget_configure);

        // Bind the action for the save button.
        findViewById(R.id.save_button).setOnClickListener(mOnClickListener);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        Log.v(LOG_TAG, "YmdhmsAppWidgetConfigure: onCreate() finished. mAppWidgetId=" + mAppWidgetId);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = YmdhmsAppWidgetConfigure.this;
            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            YmdhmsAppWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
}
