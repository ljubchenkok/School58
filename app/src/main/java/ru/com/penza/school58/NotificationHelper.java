package ru.com.penza.school58;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.io.FileDescriptor;
import java.io.IOException;

import ru.com.penza.school58.datamodel.Card;


public class NotificationHelper {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    public NotificationHelper(Context context) {
        mContext = context;
    }

    /**
     * Create and push the notification
     */
    public void createNotification(String title, String message, Card card)
    {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.student);
        if (card.getImageURL()!=null && !card.getImageURL().isEmpty()) {
            largeIcon = getBitmapfromUri(Uri.parse("file:///" + card.getImageURL()));
        }
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_stat_local_dining)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setLargeIcon(largeIcon)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    private Bitmap getBitmapfromUri(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    mContext.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return  getCircleBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }
    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height){
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        }else{
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width)/2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }
}



