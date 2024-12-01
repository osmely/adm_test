import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'adm_test_platform_interface.dart';

/// An implementation of [AdmTestPlatform] that uses method channels.
class MethodChannelAdmTest extends AdmTestPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('adm_test');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
