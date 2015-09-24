package com.aceare.ymdhms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;


/**
 * Implementation of App Widget functionality.
 */
public class YmdhmsAppWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = YmdhmsAppWidgetProvider.class.getSimpleName();

    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.v(LOG_TAG, "onReceive action=" + action + ", appWidgetId=" + appWidgetId);

        super.onReceive(context, intent);
    }
*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        String appWidgetIdList = "" + appWidgetIds[0];
        for (int i = 1; i < appWidgetIds.length; i++) {
            appWidgetIdList = appWidgetIdList + ", " + appWidgetIds[i];
        }
        Log.v(LOG_TAG, "onDeleted: appWidgetIds=" + appWidgetIdList);
        super.onDeleted(context, appWidgetIds);
    }

    private PendingIntent createAlarmPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT); // modify & reuse intent
        return pendingIntent;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
Log.v(LOG_TAG, "onEnabled");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//should trigger at clock 60 sec. alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60*1000, createAlarmPendingIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
Log.v(LOG_TAG, "onDisabled");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createAlarmPendingIntent(context));
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            long currentTime = System.currentTimeMillis();
            String dayofweek = "T5 " + currentTime;
Log.v(LOG_TAG, dayofweek);

            //ComponentName thisAppWidget = new ComponentName(context.getPackageName(), YmdhmsAppWidgetProvider.class.getSimpleName());
            ComponentName thisAppWidget = new ComponentName(context, YmdhmsAppWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
//Log.v(LOG_TAG, "AlarmReceiver(): ids.length=" + ids.length);
            for (int appWidgetId: ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    private static PendingIntent createSettingsActivityPendingIntent(Context context, int appWidgetId) {
        final Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        final Intent settingsActivityIntent = new Intent(context, YmdhmsAppWidgetSettings.class)
                .putExtras(extras)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // in case activity is started outside of the context of an existing activity
        // IMPORTANT: PendingIntent will be reused even if extras differ between the intents targeted for different instances of AppWidgets.
        // This creates a problem by using latest extras (appWidgetId) for ALL previous and new instances of AppWidgets!!
        // One option is to make Intent's Data unique by using toUri, effectively forcing creation of separate PendingIntents:
        //    settingsActivityIntent.setData(Uri.parse(settingsActivityIntent.toUri(Intent.URI_INTENT_SCHEME)));
        // Another better/cleaner solution is to unique requestCode for creating unique PendingIntents for each AppWidget.
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, // use unique request code to create unique PendingIntents
                settingsActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
Log.v(LOG_TAG, "updateAppWidget(): appWidgetId=" + appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ymdhms_app_widget);

        long currentTimeInMilli = System.currentTimeMillis();
        views.setTextViewText(R.id.hms, formatTime(currentTimeInMilli,
                YmdhmsAppWidgetSettings.readPrefHmsOptId(context, appWidgetId)));
        views.setTextViewText(R.id.dmy, formatDate(currentTimeInMilli,
                YmdhmsAppWidgetSettings.readPrefYmdOptId(context, appWidgetId)));

        // Would RemoteViewsService (Android 3.0) be useful to set this up only once on creation?
        final PendingIntent settingsActivityIntent = createSettingsActivityPendingIntent(context, appWidgetId);
        views.setOnClickPendingIntent(R.id.widget, settingsActivityIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    /**
     hmsOptId:               Representing
     hms_opt_12hr:       10:20 PM   (default)
     hms_opt_24hr:       22:20
     */
    static String formatTime(long dateInMillis, int ymdOptId) {
        String ymdFormat;

        switch (ymdOptId) {
            case R.id.hms_opt_24hr:
                ymdFormat = "HH:mm" ;
                break;
            default: // fall thru
            case R.id.hms_opt_12hr:
                ymdFormat = "KK:mm a" ;
                break;
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(ymdFormat);
        return timeFormat.format(dateInMillis);
    }

    /**
        ymdOptId:               Representing
            ymd_opt_dash_mmm:   Friday, 15-Aug-1947
            ymd_opt_dash_mm:    Friday, 15-08-1947
            ymd_opt_mmmm:       Friday, August 15   (default)
            ymd_opt_mdy:        Friday, 08/15/1947
    */
    static String formatDate(long dateInMillis, int ymdOptId) {
        String ymdFormat;

        switch (ymdOptId) {
            case R.id.ymd_opt_dash_mmm:
                ymdFormat = "EEEE, dd-MMM-yyyy" ;
                break;
            case R.id.ymd_opt_dash_mm:
                ymdFormat = "EEEE, dd-MM-yyyy" ;
                break;
            case R.id.ymd_opt_mdy:
                ymdFormat = "EEEE, MM/dd/yyyy" ;
                break;
            default: // fall thru
            case R.id.ymd_opt_mmmm:
                ymdFormat = "EEEE, MMMM dd" ;
                break;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(ymdFormat);
        return dateFormat.format(dateInMillis);
    }

}

/*
DateFormat
    SHORT       mm/dd/yy
    MEDIUM      Mmm dd, yyyy                            (DEFAULT)
    LONG        Mmmmm dd, yyyy
    FULL        Ddddd, Mmmmm dd, yyyy

TimeFormat
    SHORT       hh:mm AM
    MEDIUM      hh:mm:ss AM                             (DEFAULT)
    LONG        hh:mm:ss AM GMT+05:30
    FULL        hh:mm:ss AM Indian Standard Time
*/

