package com.wristpad.phone.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wristpad.phone.R
import com.wristpad.phone.database.SoundDatabase
import com.wristpad.phone.service.AudioService

class PhoneMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WristPadTheme {
                SoundLibraryHostScreen()
            }
        }
    }
}

@Composable
fun SoundLibraryHostScreen() {
    val context = LocalContext.current
    val database = SoundDatabase.getInstance(context)
    val soundDao = database.soundDao()

    var sounds by remember { mutableStateOf<List<SoundEntity>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<String>("") }

    // Load sounds on composition
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel { }
    // In production, would use proper ViewModel with repository

    lifecycleScope.launch {
        sounds = soundDao.getAllSounds().collectFirstOrNull() ?: emptyList()
    }

    MaterialTheme {
        Scaffold(
            drawerColumn = {
                // Navigation drawer - minimal for phone version
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Start
                ) {
                    Text("WristPad", style = MaterialTheme.typography.h6)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = { /* Navigate to categories */ }) {
                        Text("Library")
                    }
                    OutlinedButton(onClick = { /* Navigate to settings */ }) {
                        Text("Settings")
                    }
                }
            }
        ) {
            // Top bar
            TopAppBar(
                title = { Text("WristPad Library") },
                actions = {
                    OutlinedButton(onClick = { /* Import action */ }) {
                        Text("Import")
                    }
                }
            ) {
                // Main content
                SoundLibraryScreen(
                    onSoundSelected = { sound ->
                        // Start playback when sound is selected
                        val intent = android.content.Intent(context, AudioService::class.java)
                        intent.action = "com.wristpad.phone.service.PLAY_SOUND"
                        intent.putExtra("sound_id", sound.id)
                        intent.putExtra("sound_title", sound.title)
                        context.startForegroundService(intent)
                    },
                    onCategorySelected = { category ->
                        selectedCategory = category
                        // Filter sounds by category
                    }
                )
            }
        }
    }
}