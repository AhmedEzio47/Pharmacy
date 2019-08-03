package nabil.ahmed.pharmacy.Helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import nabil.ahmed.pharmacy.MainActivity;

public class NotificationHelper {
    public static String CHANNEL_ID = "MAIN_CHANNEL";

    public static PendingIntent handlingClicks(Context context){
        Intent ordersIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                ordersIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        return pendingIntent;
    }

    public static void displayNotification(Context context, String title, String body){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                //.setSmallIcon(R.drawable.launcher_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(handlingClicks(context))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = (int) System.currentTimeMillis();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }


}
