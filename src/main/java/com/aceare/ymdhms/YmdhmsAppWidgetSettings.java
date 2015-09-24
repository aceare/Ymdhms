package com.aceare.ymdhms;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Created by shreekant on 9/22/2015.
 */
public class YmdhmsAppWidgetSettings extends Activity {
    private static final String LOG_TAG = YmdhmsAppWidgetSettings.class.getSimpleName();
    private static final String SHARED_PREFS_NAME = YmdhmsAppWidgetSettings.class.getName();
//            = "com.aceare.ymdhms.YmdhmsAppWidgetSettings";

    public YmdhmsAppWidgetSettings() {
        super();
        Log.v(LOG_TAG, "YmdhmsAppWidgetSettings()");
    }

/*
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.v(LOG_TAG, "YmdhmsAppWidgetSettings.onReceive action=" + action);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.v(LOG_TAG, "appWidgetId=" + appWidgetId);

//        if (action.equals(WIDGET_CLICK_ACTION)) {
//        }
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "YmdhmsAppWidgetSettings: onCreate()");
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.ymdhms_app_widget_settings);

        // Bind the action for the save button.
        findViewById(R.id.save_button).setOnClickListener(mOnClickListener);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            // Apply current settings read from preferences:
            ((RadioGroup)findViewById(R.id.hms_opt_radiogroup)).check(readPrefHmsOptId(this, appWidgetId));
            ((RadioGroup)findViewById(R.id.ymd_opt_radiogroup)).check(readPrefYmdOptId(this, appWidgetId));
        } else { // If they gave us an intent without the widget id, just bail.
            finish();
        }

        Log.v(LOG_TAG, "YmdhmsAppWidgetSettings: onCreate() finished. mAppWidgetId=" + appWidgetId + ", context=" + this);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = YmdhmsAppWidgetSettings.this;

            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            int appWidgetId=0;
            if (extras != null) {
                appWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
Log.v(LOG_TAG, "onClick: appWidgetId=" + appWidgetId + ", this=" + context);

            // When the button is clicked, save options in our prefs
            savePrefHmsOptId(context, appWidgetId,
                    ((RadioGroup)findViewById(R.id.hms_opt_radiogroup)).getCheckedRadioButtonId());
            savePrefYmdOptId(context, appWidgetId,
                    ((RadioGroup)findViewById(R.id.ymd_opt_radiogroup)).getCheckedRadioButtonId());

            // update widget:
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            YmdhmsAppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Log.v(LOG_TAG, "YmdhmsAppWidgetSettings: onNewIntent() mAppWidgetId=" + appWidgetId + ", context=" + this);
    }

    static final String getPrefWidgetResourceKey(Context context, int appWidgetId, int resourceId) {
        return "Widget" + appWidgetId + context.getString(resourceId);
    }

    // HmsOpt: return value read from preference settings, if available; otherwise use default value
    static int readPrefHmsOptId(Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        int hmsOptId = sharedPref.getInt(getPrefWidgetResourceKey(context, appWidgetId, R.string.hms_opt_key),
                R.id.hms_opt_12hr); // default selection
        Log.v(LOG_TAG, "readPrefHmsOptId: appWidgetId=" + appWidgetId + ", hmsOptId=" + hmsOptId);
        return hmsOptId;
    }

    static void savePrefHmsOptId(Context context, int appWidgetId, int hmsOptId) {
        Log.v(LOG_TAG, "savePrefHmsOptId: appWidgetId=" + appWidgetId + ", hmsOptId=" + hmsOptId);
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, 0).edit();
        sharedPref.putInt(getPrefWidgetResourceKey(context, appWidgetId, R.string.hms_opt_key),
                hmsOptId)
                .commit();
    }

    // YmdOpt: return value read from preference settings, if available; otherwise use default value
    static int readPrefYmdOptId(Context context, int appWidgetId) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        int ymdOptId = sharedPref.getInt(getPrefWidgetResourceKey(context, appWidgetId, R.string.ymd_opt_key),
                R.id.ymd_opt_dash_mmm); // default selection
        Log.v(LOG_TAG, "readPrefYmdOptId: appWidgetId=" + appWidgetId + ", ymdOptId=" + ymdOptId);
        return ymdOptId;
    }

    static void savePrefYmdOptId(Context context, int appWidgetId, int ymdOptId) {
        Log.v(LOG_TAG, "savePrefYmdOptId: appWidgetId=" + appWidgetId + ", ymdOptId=" + ymdOptId);
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, 0).edit();
        sharedPref.putInt(getPrefWidgetResourceKey(context, appWidgetId, R.string.ymd_opt_key),
                ymdOptId)
                .commit();
    }
}

/*
    public void onYmdOptRadioButtonClicked(View view) {
        Log.v(LOG_TAG, "onYmdOptRadioButtonClicked:");
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked)
            Log.v(LOG_TAG, "Checked ID: " + view.getId());
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.ymd_opt_dmy:
                if (checked)
                    Log.v(LOG_TAG, getString(R.string.ymd_opt_dmy));
                    break;
            case R.id.ymd_opt_mdy:
                if (checked)
                    Log.v(LOG_TAG, getString(R.string.ymd_opt_mdy));
                    break;
            case R.id.ymd_opt_ymd:
                if (checked)
                    Log.v(LOG_TAG, getString(R.string.ymd_opt_ymd));
                break;
        }
    }
*/
