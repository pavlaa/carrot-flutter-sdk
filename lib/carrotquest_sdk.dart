import 'package:carrotquest_sdk/user_property/user_property.dart';

import 'carrotquest_sdk_platform_interface.dart';

class Carrot {
  static Future<String?> getWebVersion() {
    return CarrotquestSdkPlatform.instance.getWebVersion();
  }

  /// Setup SDK
  static Future<void> setup(String appId, String apiKey, {String? appGroup}) {
    return CarrotquestSdkPlatform.instance.setup(appId, apiKey, appGroup);
  }

  /// Authentification user
  static Future<void> auth(userId, userAuthKey) {
    return CarrotquestSdkPlatform.instance.auth(userId, userAuthKey);
  }

  /// Deinitialisation SDK
  /// Only for Android
  // Future<void> deInit() {
  //   // TODO
  //   return Future.value();
  // }

  /// Deinitialisation SDK
  static Future<void> logOut() {
    return CarrotquestSdkPlatform.instance.logOut();
  }

  /// Set user property
  Future<void> setUserProperty(UserProperty property) {
    return CarrotquestSdkPlatform.instance.setUserProperty(property);
  }

  /// Track event
  static Future<void> trackEvent(String event, {Map<String, String>? params}) {
    return CarrotquestSdkPlatform.instance.trackEvent(event, params: params);
  }

  /// Open chat
  static Future<void> openChat() {
    return CarrotquestSdkPlatform.instance.openChat();
  }

  /// Get count unread conversations
  static Future<int> getUnreadConversationsCount() {
    return CarrotquestSdkPlatform.instance.getUnreadConversationsCount();
  }

  /// Get count unread conversations stream
  static Stream<int> getUnreadConversationsCountStream() {
    return CarrotquestSdkPlatform.instance.getUnreadConversationsCountStream();
  }

  static Future<void> sendFcmToken(String token) {
    return CarrotquestSdkPlatform.instance.sendFcmToken(token);
  }

  static Future<void> sendFirebasePushNotification(
      Map<String, dynamic> message) {
    return CarrotquestSdkPlatform.instance
        .sendFirebasePushNotification(message);
  }

  static bool isCarrotQuestPush(Map<String, dynamic> message) {
    return message['is_carrot'] != null;
  }
}
