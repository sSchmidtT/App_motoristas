package com.grupoib3.schmidt.app_motorista.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Notificacao;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.View.MainActivity;
import com.grupoib3.schmidt.app_motorista.View.NotificationActivity;
import com.grupoib3.schmidt.app_motorista.View.WebActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        BancoController bd = new BancoController(getBaseContext());
        Usuario user = new Usuario();
        try {
            user = UsuarioServices.LoginMotorista(getBaseContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss a");

        Notificacao notifi = new Notificacao();
        notifi.setData_notificacao(ft.format(dNow));
        notifi.setId_filial(user.getId_Filial());
        notifi.setId_user(user.getId());
        notifi.setMsg_notificacao(messageBody);
        notifi.setTitulo_notificacao(message);
        notifi.setStatus_notificacao(0);

        bd.InsereNotification(notifi);

        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userId", user.getId());
        bundle.putSerializable("userIdFilial", user.getId_Filial());
        intent.putExtras(bundle);
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
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
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
                        @SuppressLint({"ResourceAsColor"})
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(this, channelId)
                                        .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                        .setContentTitle("Filial inserida")
                                        .setContentText("A filial " + remoteMessage.getData().get("nome_filial") + " de " + remoteMessage.getData().get("local_filial") + " está disponível para consulta")
                                        .setAutoCancel(true)
                                        .setSound(sound)
                                        .setContentIntent(pendingIntent)
                                        .setColor(R.color.colorPrimary)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
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

            BancoController bd = new BancoController(getBaseContext());
            Usuario user = new Usuario();
            try {
                user = UsuarioServices.LoginMotorista(getBaseContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dNow = new Date( );
            SimpleDateFormat ft =
                    new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss a");

            Notificacao notifi = new Notificacao();
            notifi.setData_notificacao(ft.format(dNow));
            notifi.setId_filial(user.getId_Filial());
            notifi.setId_user(user.getId());
            notifi.setMsg_notificacao(body);
            notifi.setUrl(url);
            notifi.setTitulo_notificacao(title);
            notifi.setStatus_notificacao(0);

            bd.InsereNotification(notifi);

            //Intent intent = new Intent(this, WebActivity.class);
            //intent.putExtra("url", url);
            Intent intent = new Intent(this, NotificationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userId", user.getId());
            bundle.putSerializable("userIdFilial", user.getId_Filial());
            intent.putExtras(bundle);
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
                    @SuppressLint({"ResourceAsColor"})
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                    .setContentTitle(title)
                                    .setContentText(body)
                                    .setAutoCancel(true)
                                    .setSound(sound)
                                    .setContentIntent(pendingIntent)
                                    .setColor(R.color.colorPrimary)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
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
        String cod  = remoteMessage.getData().get("codMarc") != null ? remoteMessage.getData().get("codMarc"): "";
        BancoController bd = new BancoController(getBaseContext());
        Usuario user = new Usuario();
        try {
            user = UsuarioServices.LoginMotorista(getBaseContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(!cod.equals("")){
            getJson(bd, user, cod);
        }
        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss a");

        Notificacao notifi = new Notificacao();
        notifi.setData_notificacao(ft.format(dNow));
        notifi.setId_filial(user.getId_Filial());
        notifi.setId_user(user.getId());
        notifi.setMsg_notificacao(body);
        notifi.setTitulo_notificacao(title);
        notifi.setStatus_notificacao(0);

        bd.InsereNotification(notifi);

        Intent intent = new Intent(this, NotificationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userId", user.getId());
        bundle.putSerializable("userIdFilial", user.getId_Filial());
        intent.putExtras(bundle);
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

                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                        "Mpark Motorista",
                        NotificationManager.IMPORTANCE_MAX);
                channel.setDescription(body);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                channel.setShowBadge(true);
                if(!ring.equals("")){
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();
                    channel.setSound(sound, audioAttributes);
                }else
                    channel.setSound(null, null);
                if(notificate_vibrate){
                    channel.setVibrationPattern(new long[] { 0, 10, 5, 15});
                    channel.enableVibration(true);
                    notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                }else
                    channel.enableVibration(false);

                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

            }else{
                @SuppressLint({"ResourceAsColor"})
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_boton_notification_mpark)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setAutoCancel(true)
                                .setSound(sound)
                                .setContentIntent(pendingIntent)
                                .setColor(R.color.colorPrimary)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                if(!ring.equals(""))
                    notificationBuilder.setSound(sound);
                if(notificate_vibrate)
                    notificationBuilder.setVibrate(new long[] { 0, 10, 5, 15});

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

    }

    public void getJson(BancoController bd, Usuario user, String cCodMarc){
        class GetJson extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                HttpServices rh = new HttpServices();
                Cursor cursor = bd.carregaFilialById(user.getId_Filial());
                String url = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_ATTNOTIFI + "?cCodMarc=" + cCodMarc;
                String s = rh.getJSONFromAPI(url, "", "GET", user.getAccessToken());
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }
        }
        GetJson gj = new GetJson();
        gj.execute();
    }
}
