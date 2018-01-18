package com.abdelrahman.boundandstartforegroundserviceexample;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.abdelrahman.boundandstartforegroundserviceexample.MyService.*;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks {

    private Chronometer mChronometerActivity;
    private Button stopMyService;
    private Button getRunningService;
    private Button startAndBind;
    private TextView updateStatus;
    private boolean mBound = false;
    private MyService mMyService;
    private Button startForrgroundService;
    private static final String EVENT_INFO = "event info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // in Oncreate we defined the click listeners

        stopMyService = (Button) findViewById(R.id.stopMyService);
        getRunningService = (Button) findViewById(R.id.getRunningService);
        startAndBind = (Button) findViewById(R.id.startAndBind);
        startForrgroundService = (Button) findViewById(R.id.startForrgroundService);
        updateStatus = (TextView) findViewById(R.id.updateLastEvent);
        mChronometerActivity = (Chronometer) findViewById(R.id.chronometer);
        stopMyService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    stopService(intent);
                    unbindService(mConnection);
                    mBound = false;
                    updateStatus.setText("you stopped the service");
                }
            }
        });
        getRunningService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // call the method isMyServiceRunning() which return the service class name if exist
                Log.d(EVENT_INFO,"the Running Service is" +isMyServiceRunning(MyService.class));
                updateStatus.setText(" MyService run"+isMyServiceRunning(MyService.class));
            }
        });
        startAndBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartAndBind();
                updateStatus.setText("you started and binded the service");
            }
        });
        startForrgroundService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    mMyService.setForegroundMode();
                }
                else {
                    Log.d(EVENT_INFO,"you have to start service first then foreground it");
                    updateStatus.setText("your service was stopped," +
                            " you have to start service first then foreground it");
                }
            }
        });

        stopMyService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    mMyService.setCallbacks(null);
                    unbindService(mConnection);
                    stopService(intent);
                    mBound = false;
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) {

            // unregister the interface callbacks
            mMyService.setCallbacks(null);

            //unbind the service but it will keeps running in the background  as we  called startService()
            unbindService(mConnection);
            // set mBound false as the service is unbind now
            mBound = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStartAndBind();
    }

    private void setStartAndBind(){
        Intent intent = new Intent(this, MyService.class);
        if(!isMyServiceRunning(MyService.class)) {

            // we call startService() to keep service running in the background even you unbind it
            // note that multiple calls to startService() do not nest (though they do result in multiple
            // corresponding calls to onStartCommand()),
            // so no matter how many times it is started a service will be stopped once
            // stopService() or stopSelf() is called;
            // but in our case to stop the service we have to unbindService() and stopService()
            startService(intent);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

            // if the service is running so we just need to bind it
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            MyBinder binder = (MyBinder) service;
            mMyService = binder.getService();
            // Now the service is binded to the activity
            // you can access any method in the service
            mBound = true;
            // after we bind to the service we get the chronometer base to continue counting up

            // to keep the activity trigger to any update in our service
            mMyService.setCallbacks(MainActivity.this);

            // start the chronometer in the activity to start count from the service's chronometer was started
            mChronometerActivity.setBase(mMyService.getChronometerBase());
            mChronometerActivity.start();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // the service is now unbind so change mBoud to be false
            mBound = false;
            Log.d(EVENT_INFO,"the service is disconnected");
        }
    };

    // this method we created at the interface in MyService class to trigger events in service
    @Override
    public void doSomething(Location location) {
        updateStatus.setText("the location is changed" );
    }
}
