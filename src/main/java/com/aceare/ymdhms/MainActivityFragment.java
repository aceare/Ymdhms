package com.aceare.ymdhms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    ViewHolder mViewHolder = null;
    AlarmManager mAlarmManager = null;
    PendingIntent mPendingIntent = null;
    private static MainActivityFragment mFragmentInstance;

    public MainActivityFragment() {
    }

    private final String VIEW_HOLDER = "VIEW_HOLDER";
    private class ViewHolder {
        public final Chronometer chrono;
        public final TextView hms;
        public final TextView dayofweek;
        public final TextView dmy;

        ViewHolder(View view) {
            chrono      = (Chronometer) view.findViewById(R.id.chrono);
            hms         = (TextView) view.findViewById(R.id.hms);
            dayofweek   = (TextView) view.findViewById(R.id.dayofweek);
            dmy         = (TextView) view.findViewById(R.id.dmy);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
Log.v(LOG_TAG, "onCreate()" + " mFragmentInstance==" + mFragmentInstance);
        mFragmentInstance = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
Log.v(LOG_TAG, "onDestroy()" + " mFragmentInstance==" + mFragmentInstance);
        mFragmentInstance = null;
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
Log.v(LOG_TAG, "onCreateView()" + " mFragmentInstance==" + mFragmentInstance);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mViewHolder = new ViewHolder(rootView); // TODO: when to free/destroy?
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(LOG_TAG, "onDestroyView()" + " mFragmentInstance==" + mFragmentInstance);
        super.onDestroyView();
//        mViewHolder = null;
/** After onDestroyView(), pause/resume are called without calling onCreateView()!!
    onDestroyView() is also called on getFragmentManager() detach/attach without calling onCreateView()!!
 */
    }

    public void updateView(String dayofweek) {
Log.v(LOG_TAG, dayofweek + " mViewHolder==" + mViewHolder);
        if (null != mViewHolder) {
            mViewHolder.dayofweek.setText(dayofweek);
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }


    public static class AlarmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            long currentTime = System.currentTimeMillis();
            String dayofweek = "T " + currentTime;
            Log.v(LOG_TAG, dayofweek);
Log.v(LOG_TAG, dayofweek + " mFragmentInstance==" + mFragmentInstance);
            if (null != mFragmentInstance) {
                mFragmentInstance.updateView(dayofweek);
            }
        }
    }

    @Override
    public void onResume() {
        Context context = getActivity();
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0); //PendingIntent.FLAG_ONE_SHOT);
        // Set the AlarmManager based on locale clock.
//tmptmp        mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000, mPendingIntent);
Log.v(LOG_TAG, "onResume()" + " mFragmentInstance==" + mFragmentInstance + " mAlarmManager==" + mAlarmManager + " mPendingIntent==" + mPendingIntent);

/*
        mViewHolder.chrono.setBase(
                        (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000) +
                        (calendar.get(Calendar.MINUTE) * 60 * 1000) +
                        (calendar.get(Calendar.SECOND) * 1000) +
                        calendar.get(Calendar.MILLISECOND));
*/
//        mViewHolder.chrono.setBase(System.currentTimeMillis() - SystemClock.elapsedRealtime());
//        mViewHolder.chrono.setBase(-(60*60*1000));

        long elapsedTime = SystemClock.elapsedRealtime();
        Calendar calendar = Calendar.getInstance();
        long currTimeInMilli = (calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000) +
                (calendar.get(Calendar.MINUTE) * 60 * 1000) +
                (calendar.get(Calendar.SECOND) * 1000) +
                calendar.get(Calendar.MILLISECOND);
        // The value set as setBase is *subtracted* from the chronoBase and that time is shown by Chronometer.
        // Thus, setBase(SystemClock.elapsedRealtime()) sets the clock to 00:00.
        // elapsedTime = 3:00                       Clock (currTime)
        //   setBase(elapsedTime) -> 3:00 - 03:00 = 00:00
        //   setBase(01:00)       -> 3:00 - 01:00 = 02:00
        //   setBase(-01:00)      -> 3:00 + 01:00 = 04:00
        //  baseToSet = elapsedTime - currTime
        mViewHolder.chrono.setBase(elapsedTime - currTimeInMilli);
        mViewHolder.chrono.start();

        super.onResume();
    }

    @Override
    public void onPause() {
Log.v(LOG_TAG, "onPause()" + " mFragmentInstance==" + mFragmentInstance + " mAlarmManager==" + mAlarmManager + " mPendingIntent==" + mPendingIntent);
        mViewHolder.chrono.stop();
        if ((null != mAlarmManager) && (null != mPendingIntent)) {
            mAlarmManager.cancel(mPendingIntent);
            mAlarmManager = null;
            mPendingIntent = null;
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart()" + " mFragmentInstance==" + mFragmentInstance);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.v(LOG_TAG, "onStop()" + " mFragmentInstance==" + mFragmentInstance);
        super.onStop();
    }

}

