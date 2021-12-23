package net.stockovin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import static net.stockovin.ProfileActivity.NOTIFICATION_CHANNEL_ID;


public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NB_BOT = "nb_bot";

    public void onReceive(Context context, Intent intent) {

        long [] swPattern = new long[] { 0, 500, 110, 500, 110, 450, 110, 200, 110,
                170, 40, 450, 110, 200, 110, 170, 40, 500 };

        int nb_bot = intent.getIntExtra(NB_BOT, 0);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Alerte seuil minimum atteint");
        builder.setContentText("Il vous reste "+ nb_bot + " bouteilles");
        builder.setSmallIcon(R.drawable.icon_bottle);
        builder.setLights(Color.RED, 1000, 1000);
        builder.setAutoCancel(true);
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        builder.setVibrate(swPattern);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("YOUR_PACKAGE_NAME");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "YOUR_PACKAGE_NAME",
                    "YOUR_APP_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(context,ProfileActivity.class);
        PendingIntent conPendingIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(conPendingIntent);

        notificationManager.notify(id, builder.build());

    }
}

