package com.app.pujaconnectsc.Services;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
        public final static String Channel_1_ID = "10001";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
    private void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(Channel_1_ID,"Admin", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is a notification provided for admins only");
            NotificationManager notificationManager =(NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
    }
}
