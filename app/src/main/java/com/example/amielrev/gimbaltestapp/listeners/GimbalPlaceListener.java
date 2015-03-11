package com.example.amielrev.gimbaltestapp.listeners;

import android.util.Log;
import android.widget.Toast;

import com.gimbal.android.Place;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.Visit;

import java.sql.Date;

/**
 * Created by AmielRev on 3/8/2015.
 * copied from danieleagle
 */
public class GimbalPlaceListener extends PlaceEventListener {
    @Override
    public void onVisitStart(Visit visit) {
        // This will be invoked when a place is entered. Example below shows a simple log upon enter
        String notification = "Enter: " + visit.getPlace().getName() + ", at: " + new Date(visit.getArrivalTimeInMillis());
        Log.i("Info:", notification);
//        Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVisitEnd(Visit visit) {
        // This will be invoked when a place is exited. Example below shows a simple log upon exit
        String notification = "Exit: " + visit.getPlace().getName() + ", at: " + new Date(visit.getDepartureTimeInMillis());
        Log.i("Info:", notification);
//        Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
    }
}