package com.calendaralarm.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.calendaralarm.app.data.repository.CalendarRepository
import com.calendaralarm.app.databinding.ActivityMainBinding
import com.calendaralarm.app.ui.adapters.EventAdapter
import com.calendaralarm.app.utils.Constants
import com.calendaralarm.app.utils.PermissionUtils
import com.calendaralarm.app.workers.CalendarSyncWorker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private val TAG = "MainActivity"
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var repository: CalendarRepository
    private lateinit var eventAdapter: EventAdapter
    
    private var isAuthenticated = false
    private var hasPermissions = false
    
    // Permission request launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        
        if (allGranted) {
            hasPermissions = true
            Toast.makeText(this, R.string.permissions_granted, Toast.LENGTH_SHORT).show()
            syncCalendarEvents()
        } else {
            hasPermissions = false
            Toast.makeText(this, R.string.permissions_denied, Toast.LENGTH_LONG).show()
            
            if (PermissionUtils.shouldShowRationale(this)) {
                showPermissionRationaleDialog()
            } else {
                // User has permanently denied permissions, direct them to settings
                showPermissionSettingsDialog()
            }
        }
        
        updateUI()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up toolbar
        setSupportActionBar(binding.mainContent.toolbar)
        
        // Initialize repository
        repository = CalendarRepository(this)
        
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(Constants.GOOGLE_CALENDAR_API_SCOPE))
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        // Set up RecyclerView
        eventAdapter = EventAdapter()
        binding.mainContent.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = eventAdapter
        }
        
        // Set up SwipeRefreshLayout
        binding.mainContent.swipeRefresh.setOnRefreshListener {
            syncCalendarEvents()
        }
        
        // Set up sign-in button
        binding.signInButton.setOnClickListener {
            signIn()
        }
        
        // Set up settings button
        binding.mainContent.settingsButton.setOnClickListener {
            // TODO: Open settings activity
            Toast.makeText(this, "Settings not implemented yet", Toast.LENGTH_SHORT).show()
        }
        
        // Check authentication and permissions
        checkAuthAndPermissions()
    }
    
    override fun onStart() {
        super.onStart()
        
        // Check if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        isAuthenticated = account != null
        
        // Check if we have the required permissions
        hasPermissions = PermissionUtils.hasRequiredPermissions(this)
        
        updateUI()
        
        // If authenticated and has permissions, sync calendar events
        if (isAuthenticated && hasPermissions) {
            syncCalendarEvents()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                syncCalendarEvents()
                true
            }
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent()
        if (requestCode == Constants.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                handleSignInSuccess(account)
            } catch (e: ApiException) {
                Log.e(TAG, "Sign-in failed: ${e.statusCode}", e)
                Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show()
                isAuthenticated = false
                updateUI()
            }
        }
    }
    
    /**
     * Check authentication and permissions status
     */
    private fun checkAuthAndPermissions() {
        // Check if the user is already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        isAuthenticated = account != null
        
        // Check if we have the required permissions
        hasPermissions = PermissionUtils.hasRequiredPermissions(this)
        
        updateUI()
        
        // Request permissions if needed
        if (isAuthenticated && !hasPermissions) {
            requestPermissions()
        }
    }
    
    /**
     * Update the UI based on authentication and permission status
     */
    private fun updateUI() {
        if (isAuthenticated) {
            // Show main content if authenticated and has permissions
            if (hasPermissions) {
                binding.authContainer.visibility = View.GONE
                binding.mainContent.root.visibility = View.VISIBLE
            } else {
                // Show auth container with permission message
                binding.authContainer.visibility = View.VISIBLE
                binding.mainContent.root.visibility = View.GONE
                binding.authMessage.setText(R.string.permissions_required)
                binding.signInButton.visibility = View.GONE
            }
        } else {
            // Show auth container with sign-in button
            binding.authContainer.visibility = View.VISIBLE
            binding.mainContent.root.visibility = View.GONE
            binding.authMessage.setText(R.string.auth_required)
            binding.signInButton.visibility = View.VISIBLE
        }
    }
    
    /**
     * Start Google Sign-In flow
     */
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN)
    }
    
    /**
     * Handle successful sign-in
     */
    private fun handleSignInSuccess(account: GoogleSignInAccount) {
        Log.d(TAG, "Sign-in successful: ${account.email}")
        Toast.makeText(this, R.string.auth_success, Toast.LENGTH_SHORT).show()
        
        isAuthenticated = true
        updateUI()
        
        // Check permissions after successful sign-in
        if (!hasPermissions) {
            requestPermissions()
        } else {
            syncCalendarEvents()
        }
    }
    
    /**
     * Sign out from Google
     */
    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            isAuthenticated = false
            updateUI()
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Request required permissions
     */
    private fun requestPermissions() {
        PermissionUtils.requestPermissions(permissionLauncher)
    }
    
    /**
     * Show dialog explaining why permissions are needed
     */
    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage("Calendar and notification permissions are required for this app to function properly. Please grant these permissions to continue.")
            .setPositiveButton("Grant Permissions") { _, _ ->
                requestPermissions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Show dialog directing user to app settings to grant permissions
     */
    private fun showPermissionSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Permissions Required")
            .setMessage("Calendar and notification permissions are required but have been permanently denied. Please enable them in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                PermissionUtils.openAppSettings(this)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Sync calendar events and update UI
     */
    private fun syncCalendarEvents() {
        if (!isAuthenticated || !hasPermissions) {
            return
        }
        
        binding.mainContent.syncStatusText.visibility = View.VISIBLE
        binding.mainContent.swipeRefresh.isRefreshing = true
        
        // Launch coroutine to fetch events
        lifecycleScope.launch {
            try {
                // Get selected calendar IDs (for now, get all calendars)
                val calendars = repository.getCalendarAccounts()
                val calendarIds = calendars.map { it.id }
                
                // Fetch events from device calendar
                val events = repository.getUpcomingEvents(calendarIds)
                
                // Fetch events from Google Calendar if signed in
                val googleAccount = repository.getSignedInAccount()
                if (googleAccount != null) {
                    try {
                        val googleEvents = repository.getGoogleCalendarEvents(googleAccount)
                        events.addAll(googleEvents)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching Google Calendar events", e)
                    }
                }
                
                // Sort events by start time
                val sortedEvents = events.sortedBy { it.startTime }
                
                // Update UI
                eventAdapter.submitList(sortedEvents)
                
                // Show empty state if no events
                binding.mainContent.noEventsText.visibility = if (sortedEvents.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // Schedule notifications for events
                CalendarSyncWorker.scheduleImmediateSync(this@MainActivity)
                
                Toast.makeText(this@MainActivity, R.string.sync_complete, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing calendar events", e)
                Toast.makeText(this@MainActivity, R.string.sync_failed, Toast.LENGTH_SHORT).show()
            } finally {
                binding.mainContent.syncStatusText.visibility = View.GONE
                binding.mainContent.swipeRefresh.isRefreshing = false
            }
        }
    }
}
