package com.example.apurv.todo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MyService extends Service {

    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        startForeground(1,new Notification());
        myIntent.putExtra("title", intent.getStringExtra("title"));
        myIntent.putExtra("description",intent.getStringExtra("description"));
        myIntent.putExtra("time",intent.getLongExtra("time",0));
        myIntent.putExtra("code",intent.getLongExtra("code",0));
        myIntent.putExtra("repeat",intent.getStringExtra("repeat"));
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyService.this,(int) intent.getLongExtra("code",0), myIntent, 0);
        if (intent.getStringExtra("repeat").contentEquals("true")) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, intent.getLongExtra("time",0),86400000, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, intent.getLongExtra("time",0), pendingIntent);
        }
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
