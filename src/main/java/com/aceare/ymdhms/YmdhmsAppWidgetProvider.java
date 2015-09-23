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

import java.text.DateFormat;


/**
 * Implementation of App Widget functionality.
 */
public class YmdhmsAppWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = YmdhmsAppWidgetProvider.class.getSimpleName();
    private static final String WIDGET_CLICK_ACTION = "com.aceare.ymdhms.WIDGET_CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
Log.v(LOG_TAG, "onReceive action=" + action);
final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
    AppWidgetManager.INVALID_APPWIDGET_ID);
Log.v(LOG_TAG, "appWidgetId=" + appWidgetId);

        if (action.equals(WIDGET_CLICK_ACTION)) {
            Intent configIntent = new Intent(context, YmdhmsAppWidgetConfigure.class)
                    .putExtras(intent.getExtras())                  // pass parameters as-is
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       /// TODO: check??
            context.startActivity(configIntent);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
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
//        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000, createAlarmPendingIntent(context));
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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
Log.v(LOG_TAG, "updateAppWidget(): appWidgetId=" + appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ymdhms_app_widget);

Bundle extras = new Bundle();
extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

/*
// WORKS:
// Bind the click intent on the whole widget
final Intent clickIntent = new Intent(context, YmdhmsAppWidgetProvider.class);
clickIntent.setAction(YmdhmsAppWidgetProvider.WIDGET_CLICK_ACTION)
        .putExtras(extras);
final PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0,
        clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
views.setOnClickPendingIntent(R.id.widget, clickPendingIntent);
*/


// WORKS:
// Bind the click intent on the whole widget
final Intent clickIntent = new Intent(context, YmdhmsAppWidgetConfigure.class)
        .putExtras(extras)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // in case activity is started outside of the context of an existing activity
final PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0,
        clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
views.setOnClickPendingIntent(R.id.widget, clickPendingIntent);


        long currentTimeInMilli = System.currentTimeMillis();
        views.setTextViewText(R.id.hms, Utility.formatTime(currentTimeInMilli, DateFormat.DEFAULT));
        views.setTextViewText(R.id.dayofweek, Utility.formatDayName(currentTimeInMilli));
        views.setTextViewText(R.id.dmy, Utility.formatDate(currentTimeInMilli, DateFormat.DEFAULT));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

/*
Intent configIntent = new Intent(context, YmdhmsAppWidgetConfigure.class)
        .putExtras(extras)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       /// TODO: check??
context.startActivity(configIntent);
*/
/*
        Intent settingsIntent = new Intent(context, YmdhmsAppWidgetConfigure.class);
        Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        settingsIntent.putExtras(extras);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(settingsIntent);
//        PendingIntent settingsPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
        stackBuilder.startActivities();
*/
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
