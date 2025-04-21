package com.calendaralarm.app.utils

object Constants {
    // Google Sign-In
    const val RC_SIGN_IN = 9001
    const val GOOGLE_CALENDAR_API_SCOPE = "https://www.googleapis.com/auth/calendar.readonly"
    
    // Permissions
    const val RC_PERMISSIONS = 9002
    
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "calendar_events_channel"
    const val EVENT_NOTIFICATION_ID_PREFIX = 1000
    const val EVENT_NOTIFICATION_10MIN_SUFFIX = 1
    const val EVENT_NOTIFICATION_NOW_SUFFIX = 2
    
    // WorkManager
    const val SYNC_WORK_NAME = "calendar_sync_work"
    const val EVENT_WORK_NAME_PREFIX = "event_notification_"
    const val EVENT_ID_KEY = "event_id"
    const val EVENT_TITLE_KEY = "event_title"
    const val EVENT_START_TIME_KEY = "event_start_time"
    const val EVENT_LOCATION_KEY = "event_location"
    const val NOTIFICATION_TYPE_KEY = "notification_type"
    const val NOTIFICATION_TYPE_10MIN = "10_min_before"
    const val NOTIFICATION_TYPE_NOW = "event_time"
    
    // Preferences
    const val PREFS_NAME = "calendar_alarm_prefs"
    const val PREF_LAST_SYNC = "last_sync_time"
    const val PREF_SELECTED_CALENDARS = "selected_calendars"
    const val PREF_SYNC_FREQUENCY = "sync_frequency"
    const val PREF_NOTIFICATION_SOUND = "notification_sound"
    const val PREF_VIBRATION_PATTERN = "vibration_pattern"
    
    // Sync frequencies (in minutes)
    const val SYNC_FREQUENCY_15MIN = 15
    const val SYNC_FREQUENCY_30MIN = 30
    const val SYNC_FREQUENCY_1HOUR = 60
    const val SYNC_FREQUENCY_3HOURS = 180
    const val SYNC_FREQUENCY_6HOURS = 360
    
    // Calendar query
    const val CALENDAR_PROJECTION_ID_INDEX = 0
    const val CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX = 1
    const val CALENDAR_PROJECTION_DISPLAY_NAME_INDEX = 2
    const val CALENDAR_PROJECTION_OWNER_ACCOUNT_INDEX = 3
    const val CALENDAR_PROJECTION_COLOR_INDEX = 4
    
    // Event query
    const val EVENT_PROJECTION_ID_INDEX = 0
    const val EVENT_PROJECTION_TITLE_INDEX = 1
    const val EVENT_PROJECTION_START_INDEX = 2
    const val EVENT_PROJECTION_END_INDEX = 3
    const val EVENT_PROJECTION_LOCATION_INDEX = 4
    
    // Time constants
    const val TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L
    const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    const val SEVEN_DAYS_IN_MILLIS = 7 * ONE_DAY_IN_MILLIS
}
