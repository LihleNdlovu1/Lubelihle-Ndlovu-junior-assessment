package com.PersonaPulse.personapulse

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.PersonaPulse.personapulse.navigation.NavGraph
import com.PersonaPulse.personapulse.notification.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Composable
fun PersonaPulseApp(context: android.content.Context) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavGraph(context = context)
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, schedule daily summary
            notificationManager.scheduleDailySummary()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Permission already granted, schedule daily summary
                notificationManager.scheduleDailySummary()
            }
        } else {
            // For older versions, just schedule daily summary
            notificationManager.scheduleDailySummary()
        }
        
        setContent { PersonaPulseApp(this) }
    }
}