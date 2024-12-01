package com.example.adm_test;

import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.os.Bundle;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import org.json.JSONObject;
import org.json.JSONException;

import com.amazon.device.messaging.ADMMessageReceiver;
import com.amazon.device.messaging.ADMMessageHandlerBase;

public class AdmMessageReceiver extends ADMMessageReceiver {
    public AdmMessageReceiver() {
        super(AdmMessageHandler.class);
    }

    public static class AdmMessageHandler extends ADMMessageHandlerBase {
        private static final String NOTIFICATION_DATA_KEY = "notification_data";
        private static final String CHANNEL_ID = "adm_test_channel";
        
        public AdmMessageHandler() {
            super(AdmMessageHandler.class.getName());
        }

        @Override
        protected void onMessage(Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                saveNotificationData(getApplicationContext(), extras);
                createNotification(getApplicationContext(), extras);
            }
        }

        @Override
        protected void onRegistrationError(String string) {
            // Manejar error de registro
        }

        @Override
        protected void onRegistered(String registrationId) {
            // Guardar el registration ID si es necesario
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("adm_notifications", Context.MODE_PRIVATE);
            prefs.edit().putString("registration_id", registrationId).apply();
        }

        @Override
        protected void onUnregistered(String registrationId) {
            // Limpiar el registration ID guardado
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("adm_notifications", Context.MODE_PRIVATE);
            prefs.edit().remove("registration_id").apply();
        }

        private void saveNotificationData(Context context, Bundle data) {
            try {
                JSONObject jsonData = new JSONObject();
                for (String key : data.keySet()) {
                    jsonData.put(key, String.valueOf(data.get(key)));
                }
                
                SharedPreferences prefs = context.getSharedPreferences("adm_notifications", Context.MODE_PRIVATE);
                prefs.edit()
                    .putString(NOTIFICATION_DATA_KEY, jsonData.toString())
                    .apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void createNotification(Context context, Bundle extras) {
            NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Crear canal de notificación para Android 8.0 y superior
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ADM Test Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }
            
            // Intent para abrir la app
            Intent launchIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            launchIntent.putExtra("from_notification", true);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(extras.getString("title", "Nueva notificación"))
                .setContentText(extras.getString("message", ""))
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent);

            notificationManager.notify(0, builder.build());
        }
    }
}
