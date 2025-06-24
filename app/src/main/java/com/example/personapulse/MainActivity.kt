package com.example.personapulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.personapulse.navigation.NavGraph

@Composable
fun PersonaPulseApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavGraph()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PersonaPulseApp() }
    }
}