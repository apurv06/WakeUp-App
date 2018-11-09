package com.example.apurv.todo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.FloatMath;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class CheckUser extends Service implements SensorEventListener, AutoCloseable, LocationListener {

    private SensorManager sensorMan;
    private static Sensor accelerometer;
    Intent intent;
    private float[] mGravity;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    boolean flag1 = false, flag2 = false, flag3 = false;
    Long time;
    double latitude, longitude, Ch1, Ch2;
    final Handler handler = new Handler();
    // GPSTracker class
    LocationManager gps;
    Location locationA;
    Location locationB;
    Calendar c;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public CheckUser() {


    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //  Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMan.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
            c=Calendar.getInstance();
        mAccelLast = SensorManager.GRAVITY_EARTH;
        time = System.currentTimeMillis();
        c.setTimeInMillis(time);
        Log.d("time",c.getTime()+"");
        startForeground(1, new Notification());
        this.intent = intent;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        gps = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, (float) MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
        theFilter.addAction(Intent.ACTION_USER_PRESENT);

        BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON)  )
                    if( myKM.inKeyguardRestrictedInputMode())
                    {
                        Log.d("tag1" , "LOCKED");
                    } else
                    {
                        flag1=true;

                    }

            }
        };

        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);


        return START_STICKY;
    }

    private void check() throws Exception {
       // Toast.makeText(this, "Checking", Toast.LENGTH_SHORT).show();
        if((flag1&&flag2)||(flag1&&flag3)||(flag2&&flag3)){
            NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("mychannelid2", "GM Channel", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);

            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "mychannelid2");
            builder.setContentTitle("Rise And Shine");
            builder.setContentText("Have a Lovely Day");
            builder.setSmallIcon(R.drawable.ic_launcher);
            Intent intent1 = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, intent1, 0);
            builder.setContentIntent(pendingIntent);
            Notification notification = builder.build();
            manager.notify(1, notification);
            try {
                close();
            }catch (Exception e){}


        }
        else
        {
            Long diff=System.currentTimeMillis()-time;
            if((diff)>600000)
            {
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                time=System.currentTimeMillis()+120000;
                c.setTimeInMillis(time);
                Log.d("time",c.getTime()+"");
               Toast.makeText(this, "New Alarm Set"+time,Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(this, AlarmReceiver.class);
                myIntent.putExtra("title", "Please Wake Up");
                myIntent.putExtra("description", intent.getStringExtra("I can do this forever"));
                myIntent.putExtra("time",time);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) 2, myIntent, 0);
                manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                close();
            }

        }

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

            mGravity = event.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
            Log.d("time",(System.currentTimeMillis()-time)+"");
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if(mAccel > 2){
                    flag2=true;

            }
            else {


            }
        try {
            check();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        sensorMan.unregisterListener(this,accelerometer);
        super.onDestroy();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void close() throws Exception {
        Log.d("todo", "closing");
        sensorMan.unregisterListener(this, accelerometer);
        gps.removeUpdates(this);
        this.stopSelf();

    }

    @Override
    public void onLocationChanged(Location location) {
        flag3=true;

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
