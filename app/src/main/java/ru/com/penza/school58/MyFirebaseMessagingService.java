package ru.com.penza.school58;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.DatabaseCallback;
import ru.com.penza.school58.datamodel.LocalCacheManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService  implements DatabaseCallback {
    private static final String TAG = "MyFirebaseMsgService";
    String body;
    String title;



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        body = data.get("body");
        title = data.get("title");
        int id = Integer.parseInt(data.get("id"));
        LocalCacheManager.getInstance(getApplicationContext()).findCardbyId(this,id);

    }



    private void sendCustomNotification(String title, String body, Card card) {


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.textView, body);
        remoteViews.setTextViewText(R.id.textViewTitle, title);
        remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_dinner)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        Notification notification;
        builder.setColor(card.getColor());
        builder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setStyle(new Notification.DecoratedCustomViewStyle())
                    .setCustomContentView(remoteViews)
                    .setCustomBigContentView(remoteViews);
            notification = builder.build();
        }
        else {
            notification= builder.build();
            notification.bigContentView = remoteViews;

        }


        if (card.getImageURL()!=null && !card.getImageURL().isEmpty()) {
            Picasso.with(getApplicationContext())
                    .load(new File(card.getImageURL()))
                    .transform(new CropCircleTransformation())
                    .into(remoteViews, R.id.imageView, card.getId(), notification);
        }
        else {
            Picasso.with(getApplicationContext())
                    .load(R.drawable.student)
                    .transform(new CropCircleTransformation())
                    .into(remoteViews, R.id.imageView, card.getId(), notification);
        }

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(m, notification);
    }


    @Override
    public void onCardsLoaded(List<Card> cards) {

    }

    @Override
    public void onCardDeleted() {

    }

    @Override
    public void onCardAdded() {

    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onCardUpdated() {

    }

    @Override
    public void onCardLoaded(Card card) {
        sendCustomNotification(title,body, card);

    }
}
