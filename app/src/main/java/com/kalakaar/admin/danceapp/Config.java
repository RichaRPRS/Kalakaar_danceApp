package com.kalakaar.admin.danceapp;

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID2 = 102;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 1001;
    public static final int NOTIFICATION_ID_BIG_IMAGE2 = 1003;

    public static final String SHARED_PREF = "ah_firebase";
}
