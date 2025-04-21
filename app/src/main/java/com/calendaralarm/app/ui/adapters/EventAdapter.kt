package com.calendaralarm.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.calendaralarm.app.R
import com.calendaralarm.app.data.models.CalendarEvent

/**
 * Adapter for displaying calendar events in a RecyclerView
 */
class EventAdapter : ListAdapter<CalendarEvent, EventAdapter.EventViewHolder>(EventDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }
    
    /**
     * ViewHolder for calendar events
     */
    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.eventTitle)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.eventDateTime)
        private val locationTextView: TextView = itemView.findViewById(R.id.eventLocation)
        private val notificationStatusTextView: TextView = itemView.findViewById(R.id.notificationStatus)
        
        /**
         * Bind event data to the view
         */
        fun bind(event: CalendarEvent) {
            titleTextView.text = event.title
            dateTimeTextView.text = event.getFormattedTimeString()
            
            // Set location if available
            if (event.location.isNullOrEmpty()) {
                locationTextView.visibility = View.GONE
            } else {
                locationTextView.visibility = View.VISIBLE
                locationTextView.text = event.location
            }
            
            // Show notification status
            notificationStatusTextView.visibility = if (event.notificationsScheduled) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
    
    /**
     * DiffUtil callback for efficient RecyclerView updates
     */
    class EventDiffCallback : DiffUtil.ItemCallback<CalendarEvent>() {
        override fun areItemsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CalendarEvent, newItem: CalendarEvent): Boolean {
            return oldItem == newItem
        }
    }
}
