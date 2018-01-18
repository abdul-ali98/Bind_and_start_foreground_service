package com.abdelrahman.boundandstartforegroundserviceexample;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

/**
 * Created by abdalrahman on 1/18/2018.
 * abdalrahman2587@gmail.com
 * Egypt
 */

public class MyService extends Service implements LocationListener {

    private final IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    private LocationManager locationManager;
    private ServiceCallbacks serviceCallbacks;

    // this ID for the notification to start foreground service
    // we could update the notification usung this ID
    private static final int NOTIFICATION_ID =5;

    // we will use this final in debugging logs to understand the service life cycle
    private static final String SERVICE_LIFE_CYCLE = "life cycle for service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(SERVICE_LIFE_CYCLE, "onBind in MyService class called");

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        // return true means that when the client bind again to the service it will call onRebind
        // return false means that when the client bind again to the service it will call onBind
        Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(SERVICE_LIFE_CYCLE, "onCreate in MyService class called");
        mChronometer = new Chronometer(this);
        mChronometer.start();
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(SERVICE_LIFE_CYCLE, "onStartCommand in MyService class called");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        // START_NOT_STICKY
        //If the system kills the service after onStartCommand() returns, do not recreate the service
        // unless there are pending intents to deliver. This is the safest option to avoid running your service
        // when not necessary and when your application can simply restart any unfinished jobs.
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SERVICE_LIFE_CYCLE, "onDestroy in MyService class called");

        // unregister the chronometer
        mChronometer.stop();
        mChronometer = null;

        // unregister the locationManager
        locationManager.removeUpdates(this);
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    }

    //will be used by the activity to set the chronometer in the activity
    public long getChronometerBase(){return mChronometer.getBase();}


    public void setForegroundMode() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new
                Notification.Builder(this)
                .setContentTitle("Enter your Title here")
                .setContentText("set your content here")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                        .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    public class MyBinder extends Binder {
        MyService getService() {
            Log.d(SERVICE_LIFE_CYCLE,"MyBinder method");
            return MyService.this;
        }
    }
    public interface ServiceCallbacks {
        // you could create as many methods here to keep the activity trigger when any event in the service happen
        void doSomething(Location location);
    }

    // this method to bind the interface we created to the activity
    public void setCallbacks(ServiceCallbacks callbacks) {serviceCallbacks = callbacks;}


    @Override
    public void onLocationChanged(Location location) {
        // call the method doSomething(Location location) in the activity passing the current location
        serviceCallbacks.doSomething(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // you could handle any action you want onStatusChanged
    }

    @Override
    public void onProviderEnabled(String s) {
        // you could handle any action you want onProviderEnabled
    }

    @Override
    public void onProviderDisabled(String s) {
        // you could handle any action you want  onProviderDisabled
    }
}
