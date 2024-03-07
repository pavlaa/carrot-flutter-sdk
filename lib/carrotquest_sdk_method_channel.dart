import 'dart:async';
import 'dart:convert';

import 'package:carrotquest_sdk/user_property/user_property.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'carrotquest_sdk_platform_interface.dart';

/// An implementation of [CarrotquestSdkPlatform] that uses method channels.
class MethodChannelCarrotquestSdk extends CarrotquestSdkPlatform {
  final StreamController<int> _unreadConversationsCountStreamController = StreamController<int>();

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('carrotquest_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> setup(String appId, String apiKey, bool isLightTheme, String? appGroup) async {
    await methodChannel.invokeMethod<String>(
      'setup',
      {
        'api_key': apiKey,
        'app_id': appId,
        'is_light_theme': isLightTheme,
        'app_group': appGroup,
      },
    );
    return;
  }

  @override
  Future<void> auth(userId, userAuthKey) async {
    await methodChannel
        .invokeMethod<String>('auth', {'user_id': userId, 'user_auth_key': userAuthKey});
    return;
  }

  @override
  Future<void> logOut() async {
    await methodChannel.invokeMethod<String>('logOut');
    return;
  }

  @override
  Future<void> sendFcmToken(String token) async {
    await methodChannel.invokeMethod<String>('sendToken', {'token': token});
    return;
  }

  @override
  Future<void> sendFirebasePushNotification(Map<String, dynamic> message) async {
    await methodChannel.invokeMethod<String>('sendFirebasePushNotification', {'data': message});
    return;
  }

  @override
  Future<void> openChat() async {
    await methodChannel.invokeMethod<String>('openChat');
    return;
  }

  @override
  Future<void> setUserProperty(UserProperty property) async {
    await methodChannel
        .invokeMethod<String>('setUserProperty', {'key': property.name, 'value': property.value});
    return;
  }

  @override
  Future<void> trackEvent(String event, {Map<String, String>? params}) async {
    if (params == null) {
      await methodChannel.invokeMethod<String>('trackEvent', {'event': event});
    } else {
      String paramsStr = json.encode(params);
      await methodChannel.invokeMethod<String>('trackEvent', {'event': event, 'params': paramsStr});
    }

    return;
  }

  @override
  Future<int> getUnreadConversationsCount() async {
    int count = await methodChannel.invokeMethod('getUnreadConversationsCount');
    return count;
  }

  @override
  Stream<int> getUnreadConversationsCountStream() {
    const MethodChannel("carrotquest_sdk").setMethodCallHandler((call) async {
      if (call.method == "unreadConversationsCount") {
        try {
          int count = int.parse(call.arguments.toString());
          _unreadConversationsCountStreamController.add(count);
        } catch (e) {
          _unreadConversationsCountStreamController.addError(e);
        }
      }
    });
    return _unreadConversationsCountStreamController.stream.asBroadcastStream();
  }

  @override
  Future<void> pushNotificationsUnsubscribe() async {
    return methodChannel
        .invokeMethod<String>('pushNotificationsUnsubscribe')
        .then((value) => value);
  }

  @override
  Future<void> pushCampaignsUnsubscribe() async {
    return methodChannel.invokeMethod<String>('pushCampaignsUnsubscribe').then((value) => value);
  }
}
