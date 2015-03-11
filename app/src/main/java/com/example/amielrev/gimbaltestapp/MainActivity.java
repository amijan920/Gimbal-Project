package com.example.amielrev.gimbaltestapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends ActionBarActivity {
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private BeaconEventListener beaconSightingListener;
    private BeaconManager beaconManager;
    private Activity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        setContentView(R.layout.activity_main);
        Gimbal.setApiKey(this.getApplication(), "4f4aa1cf-4c62-44e8-b963-9018c8c2fd3e");

        placeEventListener = new PlaceEventListener() {
            @Override
            public void onVisitStart(Visit visit) {
                // This will be invoked when a place is entered. Example below shows a simple log upon enter
                String notification = "Enter: " + visit.getPlace().getName() + ", at: " + new Date(visit.getArrivalTimeInMillis());
                Log.i("Info:", notification);
                Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(_this)
                                .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                                .setContentTitle("Places")
                                .setContentText(notification);
//// Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(_this, MainActivity.class);
//
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(_this);
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(0, mBuilder.build());
            }

            @Override
            public void onVisitEnd(Visit visit) {
                // This will be invoked when a place is exited. Example below shows a simple log upon exit
                String notification = "Exit: " + visit.getPlace().getName() + ", at: " + new Date(visit.getDepartureTimeInMillis());
                Log.i("Info:", notification);
                Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);


        communicationListener = new CommunicationListener() {
            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Visit visit) {
                for (Communication comm : communications) {
                    String notification = "Place Communication: " + visit.getPlace().getName() + ", message: " + comm.getTitle() + " id: " + comm.getIdentifier();
                    Log.i("INFO", notification);
                    Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }

            @Override
            public Collection<Communication> presentNotificationForCommunications(Collection<Communication> communications, Push push) {
                for (Communication comm : communications) {
                    String notification = "Received a Push Communication with message: " + comm.getTitle();
                    Log.i("INFO", notification);
                    Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
                }
                //allow Gimbal to show the notification for all communications
                return communications;
            }

            @Override
            public void onNotificationClicked(List communications) {
                Log.i("INFO", "Notification was clicked on");
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);

        beaconSightingListener = new BeaconEventListener() {
            @Override
            public void onBeaconSighting(BeaconSighting sighting) {
                String notification = sighting.toString();
                Log.i("INFO", notification);
                Toast.makeText(_this, notification, Toast.LENGTH_SHORT).show();
            }
        };
        beaconManager = new BeaconManager();
        beaconManager.addListener(beaconSightingListener);

        PlaceManager.getInstance().startMonitoring();
        CommunicationManager.getInstance().startReceivingCommunications();
        beaconManager.startListening();


        Intent intent = getIntent();

        ArrayList<String> files = GetFiles("/storage/sdcard0/Download");

        for(int i = 0; i< files.size(); i++) {
            Uri uri = Uri.fromFile(new File("/storage/sdcard0/Download/"+ files.get(i)));
                    //intent.getData();
            String scheme = uri.getScheme();

            if(ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                try {
                    InputStream attachment = getContentResolver().openInputStream(uri);
                    handleZipInput(attachment);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                String path = uri.getEncodedPath();
                try {
                    FileInputStream fis = new FileInputStream(path);
                    handleZipInput(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        Log.i("Files: ", MyFiles.toString());
        return MyFiles;
    }

    private void handleZipInput(InputStream in) {
        try {
            ZipInputStream zis = new ZipInputStream(in);
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
                String filename = entry.getName();
                Log.i("zip: ", filename);
                if(filename.equals("pass.json")) {
                    StringBuilder s = new StringBuilder();
                    int read = 0;
                    byte[] buffer = new byte[1024];
                    while((read = zis.read(buffer, 0, 1024)) >= 0)
                        s.append(new String(buffer, 0, read));
                    Log.i("Pass: ", s.toString());
                    JSONObject pass = new JSONObject(s.toString());
//                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
