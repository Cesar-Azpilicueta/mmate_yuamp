package com.flash21.yuamp_android;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Joseph on 2016-10-11.
 */
@SuppressLint("InvalidWakeLockTag")
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

        }

        if (remoteMessage.getNotification() != null) {

        }

        if(remoteMessage.getData().get("title") != null){
            sendNotification(remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("body"), remoteMessage.getData().get("board_id"),
                    remoteMessage.getData().get("board_no"));
        } else {
            sendNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),"","");
        }
    }

    private void sendNotification(String title, String body, String board_id, String board_no) {
        //Intent intent = new Intent(this, SplashActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        if(board_id.equals("notice")){
            intent.putExtra("moveUrl", PageInfo.BOARD_VIEW_PAGE+"?brd_no="+board_no);
            //Log.e("moveUrlNotice", intent.getStringExtra("moveUrl"));
        }else if (board_id.equals("event")){
            intent.putExtra("moveUrl", PageInfo.EVENT_BOARD_VIEW_PAGE+"?brd_no="+board_no);
            //Log.e("moveUrlHongbo", intent.getStringExtra("moveUrl"));
        }else if (board_id.equals("hongbo")){
            intent.putExtra("moveUrl", PageInfo.HONGBO_BOARD_VIEW_PAGE+"?brd_no="+board_no);
            //Log.e("moveUrlHongbo", intent.getStringExtra("moveUrl"));
        }else {
            intent.putExtra("moveUrl", PageInfo.BOARD_VIEW_PAGE);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("알림이 도착했습니다.")
                .setContentTitle(title)   // 앱이 켜져있는 상태에서 받는 알림 메세지
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setCategory(Notification.CATEGORY_MESSAGE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // 앱이 종료되어있을때 화면을 깨운다.
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG"); //WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        wakelock.acquire(5000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelName = getString(R.string.default_notification_channel_name);

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
