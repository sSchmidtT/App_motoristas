package com.grupoib3.schmidt.app_motorista.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.IRemoteJobService;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.View.MainActivity;
import com.grupoib3.schmidt.app_motorista.View.WebActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
            if(remoteMessage.getData().containsKey("web")){
                sendWebNotification(remoteMessage);
            } else if (remoteMessage.getData().containsKey("cad_filial")) {
                cadFilial(remoteMessage);
            } else {
                sendDataNotification(remoteMessage);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean notificar = sharedPref.getBoolean("notifications_new_message", false);

        if(notificar){

            boolean notificate_vibrate = sharedPref.getBoolean("notifications_new_message_vibrate", true);
            String ring = sharedPref.getString("notifications_new_message_ringtone", "");

            String channelId = getString(R.string.default_notification_channel_id);

            Uri sound = Uri.parse(ring);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                @SuppressLint("ResourceAsColor") NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                .setContentTitle(message)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setColor(R.color.colorPrimary);

                NotificationChannel channel = new NotificationChannel(channelId,
                        "Mpark Motorista",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(messageBody);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.setShowBadge(true);
                if(!ring.equals("")){
                    channel.setSound(sound, null);
                }else
                    channel.setSound(null, null);
                if(notificate_vibrate){
                    channel.setVibrationPattern(new long[] { 0, 1000, 500, 1000});
                    channel.enableVibration(true);
                }else
                    channel.enableVibration(false);


                Uri teste = channel.getSound();
                Log.d("sound", teste.toString());
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

            }else{
                @SuppressLint("ResourceAsColor")
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                .setContentTitle(message)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setSound(sound)
                                .setContentIntent(pendingIntent)
                                .setColor(R.color.colorPrimary)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC);
                if(!ring.equals(""))
                    notificationBuilder.setSound(sound);
                if(notificate_vibrate)
                    notificationBuilder.setVibrate(new long[] { 0, 1000, 500, 1000});

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

    }

    private void cadFilial(RemoteMessage remoteMessage){
        try{
            BancoController bc = new BancoController(getBaseContext());
            ContentValues filial = new ContentValues();
            filial.put(CriaBanco.COD_FILIAL, remoteMessage.getData().get("cod_filial"));
            filial.put(CriaBanco.NOME_FILIAL, remoteMessage.getData().get("nome_filial"));
            filial.put(CriaBanco.LOCAL_FILIAL, remoteMessage.getData().get("local_filial"));
            filial.put(CriaBanco.URL_FILIAL, remoteMessage.getData().get("url_filial"));

            boolean insert = bc.InsereFilial(filial);

            if(insert){
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean notificar = sharedPref.getBoolean("notifications_new_message", false);

                if (notificar) {

                    boolean notificate_vibrate = sharedPref.getBoolean("notifications_new_message_vibrate", true);
                    String ring = sharedPref.getString("notifications_new_message_ringtone", "");

                    String channelId = getString(R.string.default_notification_channel_id);

                    Uri sound = Uri.parse(ring);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        @SuppressLint("ResourceAsColor") NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(this, channelId)
                                        .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                        .setContentTitle("Filial adicionada")
                                        .setContentText("A filial " + remoteMessage.getData().get("nome_filial") + " de " + remoteMessage.getData().get("local_filial") + " está disponível para consulta")
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setColor(R.color.colorPrimary);

                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Mpark Motorista",
                                NotificationManager.IMPORTANCE_HIGH);
                        channel.setDescription("A filial " + remoteMessage.getData().get("nome_filial") + " de " + remoteMessage.getData().get("local_filial") + " está disponível para consulta");
                        channel.enableLights(true);
                        channel.setLightColor(Color.BLUE);
                        channel.setShowBadge(true);
                        if(!ring.equals("")){
                            channel.setSound(sound, null);
                        }else
                            channel.setSound(null, null);
                        if(notificate_vibrate){
                            channel.setVibrationPattern(new long[] { 0, 1000, 500, 1000});
                            channel.enableVibration(true);
                        }else
                            channel.enableVibration(false);


                        Uri teste = channel.getSound();
                        Log.d("sound", teste.toString());
                        notificationManager.createNotificationChannel(channel);
                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

                    }else{
                        @SuppressLint("ResourceAsColor")
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(this, channelId)
                                        .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                        .setContentTitle("Filial inserida")
                                        .setContentText("A filial " + remoteMessage.getData().get("nome_filial") + " de " + remoteMessage.getData().get("local_filial") + " está disponível para consulta")
                                        .setAutoCancel(true)
                                        .setSound(sound)
                                        .setContentIntent(pendingIntent)
                                        .setColor(R.color.colorPrimary)
                                        .setPriority(Notification.PRIORITY_HIGH)
                                        .setVisibility(Notification.VISIBILITY_PUBLIC);
                        if(!ring.equals(""))
                            notificationBuilder.setSound(sound);
                        if(notificate_vibrate)
                            notificationBuilder.setVibrate(new long[] { 0, 1000, 500, 1000});

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void sendWebNotification(RemoteMessage remoteMessage){
        try{
            String url = remoteMessage.getData().get("web");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", url);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean notificar = sharedPref.getBoolean("notifications_new_message", true);

            if (notificar) {

                boolean notificate_vibrate = sharedPref.getBoolean("notifications_new_message_vibrate", true);
                String ring = sharedPref.getString("notifications_new_message_ringtone", "");

                String channelId = getString(R.string.default_notification_channel_id);

                Uri sound = Uri.parse(ring);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    @SuppressLint("ResourceAsColor") NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setColor(R.color.colorPrimary);

                    NotificationChannel channel = new NotificationChannel(channelId,
                            "Mpark Motorista",
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(body);
                    channel.enableLights(true);
                    channel.setLightColor(Color.BLUE);
                    channel.setShowBadge(true);
                    if(!ring.equals("")){
                        channel.setSound(sound, null);
                    }else
                        channel.setSound(null, null);
                    if(notificate_vibrate){
                        channel.setVibrationPattern(new long[] { 0, 1000, 500, 1000});
                        channel.enableVibration(true);
                    }else
                        channel.enableVibration(false);


                    Uri teste = channel.getSound();
                    Log.d("sound", teste.toString());
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

                }else{
                    @SuppressLint("ResourceAsColor")
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setSound(sound)
                                    .setContentIntent(pendingIntent)
                                    .setColor(R.color.colorPrimary)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC);
                    if(!ring.equals(""))
                        notificationBuilder.setSound(sound);
                    if(notificate_vibrate)
                        notificationBuilder.setVibrate(new long[] { 0, 1000, 500, 1000});

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                }
            }
        }catch (Exception ex){
            throw ex;
        }
    }

    private void sendDataNotification(RemoteMessage remoteMessage) {
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean notificar = sharedPref.getBoolean("notifications_new_message", false);

        if (notificar) {

            boolean notificate_vibrate = sharedPref.getBoolean("notifications_new_message_vibrate", true);
            String ring = sharedPref.getString("notifications_new_message_ringtone", "");

            String channelId = getString(R.string.default_notification_channel_id);

            Uri sound = Uri.parse(ring);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                @SuppressLint("ResourceAsColor") NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setColor(R.color.colorPrimary);

                NotificationChannel channel = new NotificationChannel(channelId,
                        "Mpark Motorista",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(body);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.setShowBadge(true);
                if(!ring.equals("")){
                    channel.setSound(sound, null);
                }else
                    channel.setSound(null, null);
                if(notificate_vibrate){
                    channel.setVibrationPattern(new long[] { 0, 1000, 500, 1000});
                    channel.enableVibration(true);
                }else
                    channel.enableVibration(false);


                Uri teste = channel.getSound();
                Log.d("sound", teste.toString());
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

            }else{
                @SuppressLint("ResourceAsColor")
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setSound(sound)
                                .setContentIntent(pendingIntent)
                                .setColor(R.color.colorPrimary)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC);
                if(!ring.equals(""))
                    notificationBuilder.setSound(sound);
                if(notificate_vibrate)
                    notificationBuilder.setVibrate(new long[] { 0, 1000, 500, 1000});

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }
    }
}
