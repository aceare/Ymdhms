package com.aceare.ymdhms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
Log.v(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
Log.v(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
Log.v(LOG_TAG, "onCreateView()");
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(LOG_TAG, "onDestroyView()");
        super.onDestroyView();
//        mViewHolder = null;
/** After onDestroyView(), pause/resume are called without calling onCreateView()!!
    onDestroyView() is also called on getFragmentManager() detach/attach without calling onCreateView()!!
 */
    }

    @Override
    public void onResume() {
Log.v(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
Log.v(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStart() {
        Log.v(LOG_TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.v(LOG_TAG, "onStop()");
        super.onStop();
    }
}
