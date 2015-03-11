package com.example.amielrev.gimbaltestapp.listeners;

import android.util.Log;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconSighting;

/**
 * Created by AmielRev on 3/8/2015.
 * copied from danieleagle
 */
public class GimbalBeaconListener extends BeaconEventListener {
    @Override
    public void onBeaconSighting(BeaconSighting sighting) {
        Log.d("Gimbal Beacon Listener", "Beacon " + sighting.getBeacon().getName() + " with a signal strength of " + sighting.getRSSI() + " has been sighted.");
    }
}