package com.aceare.ymdhms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;


/**
 * Implementation of App Widget functionality.
 */
public class YmdhmsAppWidget extends AppWidgetProvider {

    private static final String LOG_TAG = YmdhmsAppWidget.class.getSimpleName();

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
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5*1000, createAlarmPendingIntent(context));
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

            //ComponentName thisAppWidget = new ComponentName(context.getPackageName(), YmdhmsAppWidget.class.getSimpleName());
            ComponentName thisAppWidget = new ComponentName(context, YmdhmsAppWidget.class);
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

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ymdhms_app_widget);

        Calendar calendar = Calendar.getInstance();
        long elapsedTime = SystemClock.elapsedRealtime();
        long curr24HrTimeInMilli = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000) +
                (calendar.get(Calendar.MINUTE) * 60 * 1000) +
                (calendar.get(Calendar.SECOND) * 1000) +
                calendar.get(Calendar.MILLISECOND);
        /**
            The value set as setBase is *subtracted* from the chronoBase and that time is shown by Chronometer.
            Thus, setBase(SystemClock.elapsedRealtime()) sets the clock to 00:00.
            elapsedTime = 3:00                         Clock (currTime)
                setBase(elapsedTime) -> 3:00 - 03:00 = 00:00
                setBase(01:00)       -> 3:00 - 01:00 = 02:00
                setBase(-01:00)      -> 3:00 + 01:00 = 04:00
            baseToSet = elapsedTime - currTime
        */
        views.setChronometer(R.id.chrono, (elapsedTime - curr24HrTimeInMilli), null, true);
        long currentTimeInMilli = System.currentTimeMillis();
        views.setTextViewText(R.id.dayofweek, Utility.formatDayName(currentTimeInMilli));
        views.setTextViewText(R.id.dmy, Utility.formatDate(currentTimeInMilli));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
}

