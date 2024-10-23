package com.monacoprime.primepets.services;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.monacoprime.primepets.BaseAppCompatActivity;
import com.monacoprime.primepets.MainActivity;
import com.monacoprime.primepets.entities.User;
import com.monacoprime.primepets.eventbus.MessageEB;

import de.greenrobot.event.EventBus;
import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    public static final String TAG = "LOG";
    private GoogleApiClient mGoogleApiClient;
    private JobParameters mJobParameters;
    private MessageEB mMessageEB;
    private LocationRequest mLocationRequest;
    int REQUEST_LOCATION = 2;
    private MainActivity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        mMessageEB = new MessageEB();
        mMessageEB.setClassName( MainActivity.class.getName() );
        mMessageEB.setAction("get_activty_instance");

        EventBus.getDefault().post( mMessageEB );

        Log.i("onCreate", "onCreate JobSchedulerService");
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "JobSchedulerService.onStartJob()");
        mJobParameters = params;
        callConnection();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "JobSchedulerService.onStopJob()");
        return true;
    }


    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        if (activity != null) {
//            Toast.makeText(activity, "startLocationUpdate", Toast.LENGTH_SHORT).show();
            initLocationRequest();
            if (ActivityCompat.checkSelfPermission(this.activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(this.activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                // permission has been granted, continue as usual
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // LISTENERS
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "JobSchedulerService.onConnected()");
        if (activity != null) {
//            Toast.makeText(activity, "onConnected", Toast.LENGTH_SHORT).show();
            Location location = null;

            if (ActivityCompat.checkSelfPermission(this.activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Check Permissions Now
                ActivityCompat.requestPermissions(this.activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                // permission has been granted, continue as usual
                location =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }


            if (location != null) {
                Log.i(TAG, "if(location != null)");
                startLocationUpdate();
            } else {
                Log.i(TAG, "if(location == null)");
                this.jobFinished(mJobParameters, true);
            }
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "JobSchedulerService.onLocationChanged()");
        if (activity != null) {
            mMessageEB = new MessageEB();
            mMessageEB.setUser(new User(1));
            mMessageEB.setLocation(location);
            mMessageEB.setClassName(MainActivity.class.getName());
            mMessageEB.setAction("update_position");

            EventBus.getDefault().post(mMessageEB);

            BaseAppCompatActivity.saveInSharedPreferences(this, BaseAppCompatActivity.LATITUDE_KEY, String.valueOf(location.getLatitude()));
            BaseAppCompatActivity.saveInSharedPreferences(this, BaseAppCompatActivity.LONGITUDE_KEY, String.valueOf(location.getLongitude()));
            BaseAppCompatActivity.saveInSharedPreferences(this, BaseAppCompatActivity.ALTITUDE_KEY, String.valueOf(location.getAltitude()));
        } else {
            stopLocationUpdate();
            JobSchedulerService.this.stopSelf();
        }
    }

    public void onEvent(MessageEB m) {
        Log.i("oEvent", "onEvent EventBus");
        if (m.getClassName().equalsIgnoreCase(JobSchedulerService.class.getName())) {
            this.activity = (MainActivity) m.getObject();
        }
    }

}
