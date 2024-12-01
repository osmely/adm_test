import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'adm_test_method_channel.dart';

abstract class AdmTestPlatform extends PlatformInterface {
  /// Constructs a AdmTestPlatform.
  AdmTestPlatform() : super(token: _token);

  static final Object _token = Object();

  static AdmTestPlatform _instance = MethodChannelAdmTest();

  /// The default instance of [AdmTestPlatform] to use.
  ///
  /// Defaults to [MethodChannelAdmTest].
  static AdmTestPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AdmTestPlatform] when
  /// they register themselves.
  static set instance(AdmTestPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
