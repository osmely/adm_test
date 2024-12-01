package com.example.adm_test;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class AdmTestPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Context context;
    private Activity activity;
    private static final String CHANNEL_NAME = "adm_test";
    
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        this.context = binding.getApplicationContext();
        setupChannel(binding.getBinaryMessenger());
    }

    private void setupChannel(BinaryMessenger messenger) {
        channel = new MethodChannel(messenger, CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        
        // Verificamos si la app fue abierta desde una notificación
        Intent intent = activity.getIntent();
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            checkAndSendNotificationData();
        }
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.activity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        this.activity = null;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private void checkAndSendNotificationData() {
        SharedPreferences prefs = context.getSharedPreferences("adm_notifications", Context.MODE_PRIVATE);
        String notificationData = prefs.getString("notification_data", null);
        if (notificationData != null) {
            // Enviamos los datos a Flutter
            channel.invokeMethod("onNotificationClicked", notificationData);
            // Limpiamos los datos guardados
            prefs.edit().remove("notification_data").apply();
        }
    }
}
