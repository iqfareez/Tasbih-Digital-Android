package com.maplerr.tasbihdigitalandroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.maplerr.tasbihdigitalandroid.App.CHANNEL_ID_1;

public class NotifService extends Service {

    private final String ACTION_STOP_SERVICE = "stopHideNotifs";

    @Override
    public void onCreate() { //only trigger once
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            stopSelf();
        }

        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent,0);

        Intent stopSelf = new Intent(this, NotifService.class);
        stopSelf.setAction(ACTION_STOP_SERVICE);
        PendingIntent pStopSelf = PendingIntent.getService(this,
                0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);



        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setContentText("Example service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_notifs_icon)
                .addAction(R.drawable.ic_notifs_icon, "HIDE",pStopSelf)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;

        //TODO: Add action for counting
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
