package com.ladopoulos.rart;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

public class CheckRecentRun extends Service {
    SharedPreferences myPrefs;
    private final static String TAG = "CheckRecentPlay";
    private static Long MILLISECS_PER_DAY = 86400000L;
    private static Long MILLISECS_PER_MIN = 60000L;

    //private static long delay = MILLISECS_PER_MIN;   // 1 minutes (for testing)
    private static long delay = MILLISECS_PER_DAY * 3;   // 3 days

    @Override
    public void onCreate() {
        super.onCreate();

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

        // Are notifications enabled?
        if (myPrefs.getBoolean("enabled", true)) {
            // Is it time for a notification?
            if (myPrefs.getLong("lastRun", Long.MAX_VALUE) < System.currentTimeMillis() - delay){
                sendNotification();
            }

        }
        // Set an alarm for the next time this service should run:
        setAlarm();

        stopSelf();
    }

    public void setAlarm() {

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, CheckRecentRun.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PendingIntent pi = PendingIntent.getForegroundService(this, 0, serviceIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + delay, pi);

        } else {
            PendingIntent pi = PendingIntent.getService(this, 0, serviceIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + delay, pi);
            } else {
                am.setExact(AlarmManager.RTC, System.currentTimeMillis() + delay, pi);
            }
        }



    }

    public void sendNotification() {
        int unicode = 0x1F62D;
        String emoji = getEmojiByUnicode(unicode);

        NotificationCompat.Builder noti = new NotificationCompat.Builder(this, "notify_001");
        Intent ii = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        noti.setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("We Missed You! " + emoji)
                .setContentText("Please visit our app more regularly")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.painting)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setTicker("We Missed You! Please visit our app more regularly")
                .setWhen(System.currentTimeMillis())
                .build();

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            noti.setChannelId(channelId);
        }

        mNotificationManager.notify(0, noti.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
