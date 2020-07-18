package com.maplerr.tasbihdigitalandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("action", 0);
        Toast.makeText(context, String.valueOf(action), Toast.LENGTH_SHORT).show();

        //action++;


    }

}
