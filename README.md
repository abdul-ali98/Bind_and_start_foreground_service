# Android : Bind and start foreground service

This repository is talking about services using Android Studio

# Content
-  start service
- bind existing service
- foreground service
- Chronometer in services
- LocationListener in services
- interface in services
- callbacks for an interface in the services

# Details

In this project I created a service class to bind it to the MainActivity

when you bind  a service to an activity, you have to unbind it when the Activity is stopped or destroyed
It prefers to unbind it when the activity is stopped.

Note that : when unbind the service that the the service is being off and onDestroy in the service was called.
So, What about keeping the service running in the background even the activity is destroyed ? 
in this case you have to start the service then bind it, and this what the project is covered :)
In the MainActivity we start the activity then bind it in the onStart() method, but we checked if the service is 
being running or not if its not running we call startService(intent) passing the intent, 
# Very important notes 
Actually it doesn't matter if we didn't check if the service is running or not because 
multiple calls to startService() do not nest (though they do result in multiple corresponding calls to onStartCommand()),
so no matter how many times it is started a service will be stopped once stopService() or stopSelf() is called

Also, if we started the service then bound it , Once neither of these situations hold, the service's onDestroy() method 
is called and the service is effectively terminated. 
All cleanup (stopping threads, unregistering receivers) should be complete upon returning from onDestroy().
So, Keep in mind if you called stopService(intent), actually the service won't stop untill you call unbind(inten) too.
# Foreground Service
the most powerful of using foreground service its priority like the foregeound activity.
basiclly, the system considers it to be something the user is actively aware of and thus not a candidate
for killing when low on memory. (It is still theoretically possible for the service to be killed under extreme
memory pressure from the current foreground application,but in practice this should not be a concern.)
# Chronometer and Location Listener 
In the service I implemented the interface LocationListener and Started a chronometer
Actually I did that to show you how to set callbacks in the activity based on any event happen in the sevice like
changing the location using the interface we created in the service class.
Also I explained how to keep the chronmeter running in the background and get its value when the activity starts.
# For more details 
Feel free to contact me.  happy coding  :) . 
Please check the documentation
https://developer.android.com/reference/android/app/Service.html


