package nabil.ahmed.pharmacy.Services;

import com.google.firebase.messaging.RemoteMessage;

import nabil.ahmed.pharmacy.Helpers.NotificationHelper;

public class NotificationService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() != null){

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            String dataPlaceHolder = "dataPlaceHolder";

            NotificationHelper.displayNotification(getApplicationContext(), title, body);

        }
    }

}
