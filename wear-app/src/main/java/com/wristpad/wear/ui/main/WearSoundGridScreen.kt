package com.wristpad.wear.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wristpad.wear.R
import com.wristpad.wear.service.AudioService
import com.wristpad.phone.database.SoundEntity
import com.wristpad.phone.database.SoundDatabase
import kotlinx.coroutines.flow.State
import kotlinx.coroutines.flowMutableStateOf
import kotlinx.coroutines.launch

@Composable
fun WearSoundGridScreen() {
    var scrollState by rememberScrollState()
    var currentVolume by remember { mutableStateOf(1.0f) }

    // Load sounds from phone via Data Layer or local cache
    val database = SoundDatabase.getInstance(requireContext())
    val soundDao = database.soundDao()

    var sounds by mutableStateOf<List<SoundEntity>>(emptyList())
    lifecycleScope.launch {
        sounds = soundDao.getAllSounds().collectFirstOrNull() ?: emptyList()
    }

    // Calculate grid columns based on circular screen width
    val maxColumns = 4
    val maxRows = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connection status indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            verticalAlignment = Alignment.Start,
            horizontalArrangement = Arrangement.Start
        ) {
            Circle(
                modifier = Modifier.size(12.dp),
                color = if (isPhoneConnected) Color.Green else Color.Red,
                contentDescription = "Connection status"
            )
            Text(
                text = if (isPhoneConnected) "Connected" else "Disconnected",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Title
        Text(
            text = "Sound Library",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Grid of sound buttons
        if (sounds.isEmpty()) {
            Text(
                text = "No sounds\non phone",
                style = MaterialTheme.typography.body1,
                textAlign = Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            GridPager(
                modifier = Modifier.fillMaxSize(),
                itemCount = sounds.size,
                maxColumns = maxColumns,
                scrollState = scrollState
            ) { index ->
                val sound = sounds[index]
                SoundButton(
                    sound = sound,
                    volume = currentVolume,
                    onPlay = { playSound(sound) },
                    onStop = { stopSound() }
                )
            }
        }

        // Bottom control bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            OutlinedButton(
                onClick = { stopSound() }
            ) {
                Text("Stop")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = currentVolume,
                onValueChange = { currentVolume = it },
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@Composable
fun SoundButton(
    sound: SoundEntity,
    volume: Float,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    // Card with rounded corners for circular screen
    var isPressed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColor(
            if (isPressed) Color(0xFF673AB7) else Color(0xFF1E1E2F)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable {
                isPressed = true
                onPlay()
                // Delay to prevent quick re-press
                delay(200)
                isPressed = false
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = sound.emoji,
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = sound.title,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Simple grid pager composition
@Composable
fun GridPager(
    modifier: Modifier = Modifier,
    itemCount: Int,
    maxColumns: Int,
    scrollState: ScrollState,
    content: (Int) -> Unit
) {
    var pagerState by rememberPagerState(
        pageCount = (itemCount.toFloat / maxColumns).ceil().toInt(),
        orientation = Orientation.Horizontal
    )

    PageIndicator(
        modifier = Modifier.align(Alignment.BottomCenter),
        pagerState = pagerState
    )

    androidx.compose.foundation.pager.Pager(
        modifier = modifier
            .height(120.dp)
            .verticalScroll(scrollState),
        state = pagerState,
        pageSnap = PageSnap.ALWAYS,
        content = content
    )
}

// Connection state
val isPhoneConnected by remember {
    derivedStateOf {
        // Check Bluetooth connection state
        // In production, would check via Wear Data Layer
        true // Placeholder - assume connected for now
    }
}