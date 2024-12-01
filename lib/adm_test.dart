import 'dart:async';
import 'package:flutter/services.dart';
import 'dart:convert';

class AdmTest {
  static const MethodChannel _channel = MethodChannel('adm_test');
  static StreamController<Map<String, dynamic>>? _notificationController;

  /// Stream que emite los datos de la notificación cuando el usuario la toca
  static Stream<Map<String, dynamic>> get onNotificationClicked {
    _notificationController ??= StreamController<Map<String, dynamic>>.broadcast();
    
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'onNotificationClicked') {
        try {
          final Map<String, dynamic> data = 
            json.decode(call.arguments as String) as Map<String, dynamic>;
          _notificationController?.add(data);
        } catch (e) {
          print('Error parsing notification data: $e');
        }
      }
    });

    return _notificationController!.stream;
  }

  /// Obtiene la versión de la plataforma
  static Future<String?> getPlatformVersion() async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
