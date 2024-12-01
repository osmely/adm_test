import 'package:flutter_test/flutter_test.dart';
import 'package:adm_test/adm_test.dart';
import 'package:adm_test/adm_test_platform_interface.dart';
import 'package:adm_test/adm_test_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockAdmTestPlatform
    with MockPlatformInterfaceMixin
    implements AdmTestPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final AdmTestPlatform initialPlatform = AdmTestPlatform.instance;

  test('$MethodChannelAdmTest is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelAdmTest>());
  });

  test('getPlatformVersion', () async {
    AdmTest admTestPlugin = AdmTest();
    MockAdmTestPlatform fakePlatform = MockAdmTestPlatform();
    AdmTestPlatform.instance = fakePlatform;

    expect(await admTestPlugin.getPlatformVersion(), '42');
  });
}
