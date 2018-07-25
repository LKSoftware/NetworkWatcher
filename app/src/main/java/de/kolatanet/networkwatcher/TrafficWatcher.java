package de.kolatanet.networkwatcher;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;

public class TrafficWatcher extends IntentService {

    private static long RX_INIT;

    private static long TX_INIT;

    private static long DATA = 0;

    private static long THRESHOLD = 15000;

    private static NotificationManager notificationManager;


    private CountDownTimer timer = new CountDownTimer(30000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) {
            checkUsage();
        }

        @Override
        public void onFinish() {
            DATA = 0;
            setInitialValues();
            timer.start();
        }
    };


    public TrafficWatcher() {
        super("TrafficWatcher");
    }

    public static void bindManager(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

    }

    public static void setTHRESHOLD(long threshold) {
        THRESHOLD = threshold;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.start();
        setInitialValues();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setInitialValues() {
        RX_INIT = TrafficStats.getMobileRxBytes();
        TX_INIT = TrafficStats.getMobileTxBytes();
    }

    private long getDifference() {
        long result = TrafficStats.getMobileRxBytes() - RX_INIT;
        result += TrafficStats.getMobileTxBytes() - TX_INIT;
        return result / 1000;
    }

    private void checkUsage() {

        long currentUsage = DATA + getDifference();
        if (currentUsage > THRESHOLD) {
            makeNotify();
        }
    }

    private void makeNotify() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Warnung zur Mobilen Daten")
                .setContentText("Ihr Mobil-Datentraffic ist gerade hoch!")
                .setPriority(NotificationCompat.PRIORITY_HIGH).setWhen(System.currentTimeMillis()).setDefaults(NotificationCompat.DEFAULT_SOUND);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Datennutzung",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, mBuilder.build());

    }
}
