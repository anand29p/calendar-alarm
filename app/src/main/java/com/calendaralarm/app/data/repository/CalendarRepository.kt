package com.calendaralarm.app.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import com.calendaralarm.app.data.models.CalendarAccount
import com.calendaralarm.app.data.models.CalendarEvent
import com.calendaralarm.app.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.Date

class CalendarRepository(private val context: Context) {
    
    private val TAG = "CalendarRepository"
    
    // Calendar projection arrays
    private val CALENDAR_PROJECTION = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.OWNER_ACCOUNT,
        CalendarContract.Calendars.CALENDAR_COLOR
    )
    
    private val EVENT_PROJECTION = arrayOf(
        CalendarContract.Events._ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.EVENT_LOCATION
    )
    
    /**
     * Get the currently signed-in Google account
     */
    fun getSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    /**
     * Check if the user has granted calendar read permission
     */
    fun hasCalendarPermission(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.READ_CALENDAR) == 
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get all calendar accounts on the device
     */
    suspend fun getCalendarAccounts(): List<CalendarAccount> = withContext(Dispatchers.IO) {
        val accounts = mutableListOf<CalendarAccount>()
        
        if (!hasCalendarPermission()) {
            Log.e(TAG, "Calendar permission not granted")
            return@withContext accounts
        }
        
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                CALENDAR_PROJECTION,
                null,
                null,
                null
            )
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(Constants.CALENDAR_PROJECTION_ID_INDEX)
                    val accountName = cursor.getString(Constants.CALENDAR_PROJECTION_ACCOUNT_NAME_INDEX)
                    val displayName = cursor.getString(Constants.CALENDAR_PROJECTION_DISPLAY_NAME_INDEX)
                    val ownerAccount = cursor.getString(Constants.CALENDAR_PROJECTION_OWNER_ACCOUNT_INDEX)
                    val color = cursor.getInt(Constants.CALENDAR_PROJECTION_COLOR_INDEX)
                    
                    accounts.add(
                        CalendarAccount(
                            id = id,
                            accountName = accountName,
                            displayName = displayName,
                            ownerName = ownerAccount,
                            color = color
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting calendar accounts", e)
        } finally {
            cursor?.close()
        }
        
        return@withContext accounts
    }
    
    /**
     * Get upcoming events from the device calendar
     */
    suspend fun getUpcomingEvents(calendarIds: List<String>): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<CalendarEvent>()
        
        if (!hasCalendarPermission() || calendarIds.isEmpty()) {
            Log.e(TAG, "Calendar permission not granted or no calendars selected")
            return@withContext events
        }
        
        val now = Date().time
        val endTime = now + Constants.SEVEN_DAYS_IN_MILLIS
        
        // Build selection for specified calendar IDs
        val selectionBuilder = StringBuilder()
        val selectionArgs = mutableListOf<String>()
        
        selectionBuilder.append("(${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?)")
        selectionArgs.add(now.toString())
        selectionArgs.add(endTime.toString())
        
        if (calendarIds.isNotEmpty()) {
            selectionBuilder.append(" AND (")
            calendarIds.forEachIndexed { index, id ->
                if (index > 0) selectionBuilder.append(" OR ")
                selectionBuilder.append("${CalendarContract.Events.CALENDAR_ID} = ?")
                selectionArgs.add(id)
            }
            selectionBuilder.append(")")
        }
        
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                EVENT_PROJECTION,
                selectionBuilder.toString(),
                selectionArgs.toTypedArray(),
                "${CalendarContract.Events.DTSTART} ASC"
            )
            
            if (cursor != null && cursor.moveToFirst()) {
                val calendarMap = getCalendarMap()
                
                do {
                    val id = cursor.getString(Constants.EVENT_PROJECTION_ID_INDEX)
                    val title = cursor.getString(Constants.EVENT_PROJECTION_TITLE_INDEX)
                    val startTime = Date(cursor.getLong(Constants.EVENT_PROJECTION_START_INDEX))
                    val endTime = Date(cursor.getLong(Constants.EVENT_PROJECTION_END_INDEX))
                    val location = cursor.getString(Constants.EVENT_PROJECTION_LOCATION_INDEX)
                    
                    // Get calendar ID for this event
                    val eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id.toLong())
                    val calendarId = getCalendarIdForEvent(eventUri)
                    
                    if (calendarId != null && calendarMap.containsKey(calendarId)) {
                        val calendar = calendarMap[calendarId]!!
                        
                        events.add(
                            CalendarEvent(
                                id = id,
                                title = title ?: "Untitled Event",
                                startTime = startTime,
                                endTime = endTime,
                                location = location,
                                calendarId = calendarId,
                                calendarName = calendar.displayName
                            )
                        )
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting upcoming events", e)
        } finally {
            cursor?.close()
        }
        
        return@withContext events
    }
    
    /**
     * Get calendar ID for a specific event
     */
    private fun getCalendarIdForEvent(eventUri: Uri): String? {
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                eventUri,
                arrayOf(CalendarContract.Events.CALENDAR_ID),
                null,
                null,
                null
            )
            
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting calendar ID for event", e)
        } finally {
            cursor?.close()
        }
        
        return null
    }
    
    /**
     * Get a map of calendar IDs to CalendarAccount objects
     */
    private suspend fun getCalendarMap(): Map<String, CalendarAccount> {
        val accounts = getCalendarAccounts()
        val map = mutableMapOf<String, CalendarAccount>()
        
        for (account in accounts) {
            map[account.id] = account
        }
        
        return map
    }
    
    /**
     * Get events from Google Calendar API
     */
    suspend fun getGoogleCalendarEvents(account: GoogleSignInAccount): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<CalendarEvent>()
        
        try {
            val credential = GoogleAccountCredential.usingOAuth2(
                context,
                Collections.singleton(CalendarScopes.CALENDAR_READONLY)
            )
            credential.selectedAccount = account.account
            
            val service = Calendar.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory(),
                credential
            )
                .setApplicationName("Calendar Alarm")
                .build()
            
            val now = DateTime(System.currentTimeMillis())
            val oneWeekFromNow = DateTime(System.currentTimeMillis() + Constants.SEVEN_DAYS_IN_MILLIS)
            
            val eventsList = service.events().list("primary")
                .setTimeMin(now)
                .setTimeMax(oneWeekFromNow)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
            
            for (event in eventsList.items) {
                if (event.start.dateTime != null) {
                    val startTime = Date(event.start.dateTime.value)
                    val endTime = Date(event.end.dateTime.value)
                    
                    events.add(
                        CalendarEvent(
                            id = event.id,
                            title = event.summary ?: "Untitled Event",
                            startTime = startTime,
                            endTime = endTime,
                            location = event.location,
                            calendarId = "primary",
                            calendarName = "Google Calendar"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Google Calendar events", e)
        }
        
        return@withContext events
    }
}
