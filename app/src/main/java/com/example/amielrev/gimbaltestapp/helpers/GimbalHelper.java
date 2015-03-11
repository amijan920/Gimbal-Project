package com.example.amielrev.gimbaltestapp.helpers;

import android.app.Activity;
import android.util.Log;

import com.example.amielrev.gimbaltestapp.listeners.GimbalBeaconListener;
import com.example.amielrev.gimbaltestapp.listeners.GimbalPlaceListener;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.Place;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.Communication;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

/**
 * Created by AmielRev on 3/8/2015.
 */
public class GimbalHelper {
    private Activity mApplication;
    private boolean mIsInitialized;
    private boolean mIsProximityServiceRunning;
    private boolean mHasOptions;
    private PlaceEventListener mGimbalPlaceListener;
    private BeaconEventListener mGimbalBeaconListener;
    private PlaceManager mPlaceManager;
    private BeaconManager mBeaconManager;
    private boolean mIsGimbalServiceRunning;

    private static volatile GimbalHelper mInstance;

    private GimbalHelper() {
        mApplication = null;
        mIsInitialized = false;
        mIsGimbalServiceRunning = false;
        mPlaceManager = null;
        mGimbalPlaceListener = null;
        mGimbalBeaconListener = null;
    }

    public static GimbalHelper getInstance() {
        if (mInstance == null) {
            synchronized (GimbalHelper.class) {
                if (mInstance == null) {
                    mInstance = new GimbalHelper();
                }
            }
        }

        return mInstance;
    }

    public boolean getIsGimbalServiceRunning() {
        return mIsGimbalServiceRunning;
    }

    public PlaceManager getPlaceManager() {
        if (mPlaceManager == null) {
            mPlaceManager = PlaceManager.getInstance();
        }

        return mPlaceManager;
    }

    public BeaconManager getBeaconManager() {
        if (mBeaconManager == null) {
            mBeaconManager = new BeaconManager();
        }

        return mBeaconManager;
    }

    public Activity getApplication() {
        return mApplication;
    }

    public void setApplication(Activity application) {
        mApplication = application;
    }

    public void setIsGimbalServiceRunning(boolean isGimbalServiceRunning) {
        mIsGimbalServiceRunning = isGimbalServiceRunning;
    }

    public void startGimbalService() {
        if (!getIsGimbalServiceRunning()) {
            Log.d("Gimbal Helper", "Starting the Gimbal Service.");

            if (mApplication == null) {
                throw new IllegalArgumentException("mApplication cannot be null. Did you set the application reference using setApplication()?");
            }

            if (!mIsInitialized) {
                initialize();

                if (mGimbalPlaceListener == null) {
                    mGimbalPlaceListener = new GimbalPlaceListener();
                }

                if (mGimbalBeaconListener == null) {
                    mGimbalBeaconListener = new GimbalBeaconListener();
                }

                getPlaceManager().addListener(mGimbalPlaceListener);
                getPlaceManager().startMonitoring();
                getBeaconManager().addListener(mGimbalBeaconListener);
                getBeaconManager().startListening();
            }
        } else {
            Log.d("Gimbal Helper", "Cannot start service as it's already running.");
        }
    }

    public void stopGimbalService() {
        if (mIsGimbalServiceRunning) {
            Log.d("Gimbal Helper", "Stopping the Gimbal Service.");
            mGimbalPlaceListener = null;
            getPlaceManager().stopMonitoring();
            getPlaceManager().removeListener(mGimbalPlaceListener);
            mGimbalBeaconListener = null;
            getBeaconManager().stopListening();
            getBeaconManager().removeListener(mGimbalBeaconListener);
            mPlaceManager = null;
            mBeaconManager = null;
        } else {
            Log.d("Gimbal Helper", "Cannot stop service as it isn't currently running.");
        }
    }

    private void initialize() {
        if (mApplication == null) {
            throw new IllegalArgumentException("mApplication cannot be null. Did you properly set the application reference?");
        } else if (!mIsInitialized) {
            Gimbal.setApiKey(mApplication.getApplication(), "4f4aa1cf-4c62-44e8-b963-9018c8c2fd3e");
            mIsInitialized = true;
        } else {
            Log.d("Gimbal Helper", "Cannot initialize as Gimbal Service is already running.");
        }
    }
}