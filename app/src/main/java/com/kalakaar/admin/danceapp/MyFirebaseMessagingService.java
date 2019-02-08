package com.kalakaar.admin.danceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (pref.contains("role")) {
            DataFields.role=pref.getString("role", "");
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                handleNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().toString());
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            String activename= data.getString("activity");

            JSONObject payload = data.getJSONObject("payload");

            /*Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/


            /*if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {*/
                //Log.e(TAG, "app is in background: " );
                // app is in background, show the notification in notification tray
            Intent resultIntent;
            if (activename.equals("PostDetailsActivity")&&DataFields.role.equals(DataFields.user))
            {
                String heading= data.getString("heading");
                String description= data.getString("description");
                String contacts= data.getString("contact");
                String venue= data.getString("venue");
                String categoryt= data.getString("category");
                String subcategory= data.getString("subcategory");
                String cityt= data.getString("city");
                String statet= data.getString("state");
                String country= data.getString("country");
                String proname= data.getString("proname");
                String provid= data.getString("provid");
                String eventid= data.getString("eventid");
                String category_id= data.getString("category_id");
                resultIntent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                resultIntent.putExtra("message", message);
                resultIntent.putExtra("heading",heading);
                resultIntent.putExtra("description",description);
                resultIntent.putExtra("contact",contacts);
                resultIntent.putExtra("venue",venue);
                resultIntent.putExtra("category",categoryt);
                resultIntent.putExtra("subcategory",subcategory);
                resultIntent.putExtra("city",cityt);
                resultIntent.putExtra("state",statet);
                resultIntent.putExtra("country",country);
                resultIntent.putExtra("proname",proname);
                resultIntent.putExtra("image",imageUrl);
                resultIntent.putExtra("provid",provid);
                resultIntent.putExtra("eventid",eventid);
                resultIntent.putExtra("category_id",category_id);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    Log.e(TAG, "image not present: " );
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    Log.e(TAG, "image present: " );
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }else if (activename.equals("admindetail")&&DataFields.role.equals(DataFields.admin)){
                resultIntent = new Intent(getApplicationContext(), AdminpostActivity.class);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    Log.e(TAG, "image not present: " );
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    Log.e(TAG, "image present: " );
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
            else if (activename.equals("Postadmin")&&DataFields.role.equals(DataFields.post)){
                resultIntent = new Intent(getApplicationContext(), PostsdetailsActivity.class);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    Log.e(TAG, "image not present: " );
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    Log.e(TAG, "image present: " );
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }else if (activename.equals("AppliedApplicationActivity")&&DataFields.role.equals(DataFields.post)){
                resultIntent = new Intent(getApplicationContext(), AppliedApplicationActivity.class);
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    Log.e(TAG, "image not present: " );
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    Log.e(TAG, "image present: " );
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
            else {
                if (DataFields.role.equals(DataFields.post)&&pref.contains("user_name1")) {
                    resultIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        Log.e(TAG, "image not present: ");
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    } else {
                        // image is present, show notification with image
                        Log.e(TAG, "image present: ");
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
            }

            //}
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
