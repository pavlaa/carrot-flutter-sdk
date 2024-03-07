package io.carrotquest.carrotquest_sdk

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.google.firebase.messaging.RemoteMessage
import io.carrotquest_sdk.android.Carrot
import io.carrotquest_sdk.android.Carrot.Callback
import io.carrotquest_sdk.android.core.main.ThemeSdk
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import org.json.JSONObject

/** CarrotquestSdkPlugin */
class CarrotquestSdkPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private var context: Context? = null
    private var activity: Activity? = null

    private var pluginInitted = false

    private var appId: String? = null
    private var apiKey: String? = null
    private var isLightTheme: Boolean? = null


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "carrotquest_sdk")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (call.method == "setup") {
            if (pluginInitted) {
                result.error("Plugin is already initialized.", null, null)
                return
            }

            setup(call, result)
            return
        }

        if(call.method == "auth") {
            auth(call, result)
            return
        }

        if(call.method == "logOut") {
            logOut(call, result)
            return
        }

        if (call.method == "sendToken") {
            sendToken(call, result)
            return
        }

        if (call.method == "sendFirebasePushNotification") {
            sendFirebasePushNotification(call, result)
            return
        }

        if (call.method == "openChat") {
            openChat(call, result)
            return
        }

        if(call.method == "setUserProperty") {
            setUserProperty(call, result)
            return
        }

        if(call.method == "trackEvent") {
            trackEvent(call, result)
            return
        }

        if(call.method == "getUnreadConversationsCount") {
            getUnreadConversationsCount(call, result)
            return
        }

        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
            return
        }

        if(call.method == "pushNotificationsUnsubscribe") {
            pushNotificationsUnsubscribe(result)
            return
        }

        if(call.method == "pushCampaignsUnsubscribe") {
            pushCampaignsUnsubscribe(result)
            return
        }

        result.notImplemented()
    }

    // override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    //   channel.setMethodCallHandler(null)
    // }

    private fun checkPluginInitiated(@NonNull result: MethodChannel.Result): Boolean {
        if (!pluginInitted) {
            result.error(
                "The plugin hasn't been initialized yet. Do Carrot.io.carrotquest.carrotquest_sdk.setup(...) first .",
                null,
                null
            )
            return false
        }
        return true
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        pluginInitted = false
        context = null
    }

    override fun onDetachedFromActivity() {
        pluginInitted = false
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    //=====
    private fun setup(
        @NonNull call: MethodCall,
        @NonNull result: MethodChannel.Result,
    ) {

        apiKey = call.argument<String?>("api_key")
        appId = call.argument<String?>("app_id")
        isLightTheme = call.argument<Boolean?>("is_light_theme")
        if (apiKey == null || appId == null) {
            result.error("An error has occurred, the apiKey or appId is null.", null, null)
            return
        }

        val con = context
        if (con != null) {
            Carrot.setup(con, apiKey!!, appId!!, object : Callback<Boolean> {
                override fun onResponse(resultSetup: Boolean?) {
                    pluginInitted = (resultSetup == true)
                   
                    try {
                        if(resultSetup == true) {
                            Carrot.setNotificationIcon(R.drawable.ic_cqsdk_notification)
                            Carrot.setUnreadConversationsCallback(object : Callback<List<String>>{
                                override fun onResponse(unreadConversationsIds: List<String>?) {
                                    channel.invokeMethod("unreadConversationsCount", unreadConversationsIds?.size ?: 0)
                                }

                                override fun onFailure(t: Throwable?) {
                                    
                                }
                            })
                            if (isLightTheme == true) {
                                Carrot.setTheme(ThemeSdk.DARK)
                            } else {
                                Carrot.setTheme(ThemeSdk.LIGHT)
                            }
                            result.success(null)
                        } else {
                            result.error("Setup is failed", null, null)
                        }
                    } catch (e: java.lang.Exception) {
                        //result.error("Setup is failed", null, null)
                    }
                }

                override fun onFailure(t: Throwable?) {
                    pluginInitted = false
                    result.error("Setup is failed: " + t.toString(), null, null)
                }
            })
        } else {
            result.error("Context is null", null, null)
        }
    }

    private fun auth(@NonNull call: MethodCall,
        @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }

        val userId = call.argument<String?>("user_id")
        val userAuthKey = call.argument<String?>("user_auth_key")
        if (userId == null || userAuthKey == null) {
            result.error("An error has occurred, the userId or userAuthKey is null.", null, null)
            return
        }

        Carrot.auth(userId, userAuthKey, object : Callback<Boolean> {
            override fun onResponse(resultAuth: Boolean?) {
                if(resultAuth == true) {
                    result.success(null)
                } else {
                    result.error("Auth is failed", null, null)
                }
            }

            override fun onFailure(t: Throwable?) {
                result.error("Auth is failed: " + t.toString(), null, null)
            }
        })
    }

    private fun logOut(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }

        if (apiKey == null || appId == null) {
            result.error("An error has occurred, the apiKey or appId is null.", null, null)
            return
        }

        Carrot.deInit(object : Callback<Boolean>{
            override fun onResponse(resDeInit: Boolean) {
                if(!resDeInit) {
                    result.error("deInit is failed", null, null)
                    return
                }

                val con = context
                if (con != null) {
                    Carrot.setup(con, apiKey!!, appId!!, object : Callback<Boolean> {
                        override fun onResponse(resultSetup: Boolean?) {
                            pluginInitted = (resultSetup == true)

                            try {
                                if(resultSetup == true) {
                                    result.success(null)
                                } else {
                                    result.error("Setup is failed", null, null)
                                }
                            } catch (e: java.lang.Exception) {
                                //result.error("Setup is failed", null, null)
                            }
                        }

                        override fun onFailure(t: Throwable?) {
                            pluginInitted = false
                            result.error("Setup is failed: " + t.toString(), null, null)
                        }
                    })
                } else {
                    result.error("Context is null", null, null)
                }
            }

            override fun onFailure(t: Throwable) {
                result.error(t.localizedMessage, null, null)
            }
        })
    }

    private fun sendToken(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }

        val token = call.argument<String?>("token")
        Carrot.sendFcmToken(token)

        result.success(null)
    }

    private fun sendFirebasePushNotification(
        @NonNull call: MethodCall,
        @NonNull result: MethodChannel.Result
    ) {
        val bundle = Bundle()
        val data: Map<String, Any> =
            call.argument<Map<String, Any>>("data") ?: HashMap<String, Any>()
            
        //Bundle().put
        val message = RemoteMessage(bundle)
        bundle.putString("id", data["id"]?.toString() ?: "")
        bundle.putString("title", data["title"]?.toString() ?: "")
        bundle.putString("body", data["body"]?.toString() ?: "")
        bundle.putString("conversation_id", data["conversation_id"]?.toString() ?: "")
        bundle.putString("conversation_type", data["conversation_type"]?.toString() ?: "")
        bundle.putString("sent_via", data["sent_via"]?.toString() ?: "")
        bundle.putString("body_json", data["body_json"]?.toString() ?: "")
//        bundle.putBoolean(
//            "is_carrot",
//            java.lang.Boolean.parseBoolean(data["is_carrot"]?.toString() ?: "False")
//        )
        bundle.putString("is_carrot", data["is_carrot"]?.toString() ?: "")
        bundle.putString("direction", data["direction"]?.toString() ?: "")

        Carrot.sendFirebasePushNotification(message, if(activity == null) context else activity)

        result.success(null)
    }


    private fun openChat(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }
        try {
            if (activity != null) {
                Carrot.openChat(activity)
                result.success(null)
            } else {
                result.error("Activity in null", null, null)
            }
        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }

    private fun setUserProperty(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }
        try {
            val key = call.argument<String?>("key")
            val value = call.argument<String?>("value")
            if(key == null || value == null) {
                result.error("Key or value is empty", null, null)
                return
            }

            Carrot.setUserProperty(key, value.toString())
            result.success(null)
        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }

    private fun trackEvent(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }
        try {
            val event = call.argument<String?>("event")
            if(event == null) {
                result.error("Event is empty", null, null)
                return
            }

            val paramsStr = call.argument<String?>("params")
            if(paramsStr == null || paramsStr.isEmpty()) {
                Carrot.trackEvent(event)
                result.success(null)
            } else {
                Carrot.trackEvent(event, paramsStr)
                result.success(null)
            }

        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }

    private fun getUnreadConversationsCount(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        if (!checkPluginInitiated(result)) {
            return
        }
        try {
            val count =  Carrot.getUnreadConversations().size
            result.success(count)
        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }

    private fun pushNotificationsUnsubscribe(@NonNull result: MethodChannel.Result) {
        try {
            Carrot.pushNotificationsUnsubscribe()
            result.success(null)
        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }

    private fun pushCampaignsUnsubscribe(@NonNull result: MethodChannel.Result) {
        try {
            Carrot.pushCampaignsUnsubscribe()
            result.success(null)
        } catch (e: Exception) {
            result.error(e.localizedMessage, null, null)
        }
    }
}