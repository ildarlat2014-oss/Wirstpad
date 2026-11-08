package com.wristpad.phone.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BitmapImage
import androidx.compose.ui.graphics.ImageVector
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wristpad.phone.database.SoundDao
import com.wristpad.phone.database.SoundDatabase
import com.wristpad.phone.service.AudioService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flowMutableStateOf
import kotlinx.coroutines.flowStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

@Composable
fun SoundLibraryScreen(
    context: androidx.context.Context,
    private val onSoundSelected: (SoundEntity) -> Unit,
    private val onCategorySelected: (String) -> Unit
) {
    var scrollState by rememberScrollState()
    var selectedCategory by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }

    // Get sounds from Room database
    val database = SoundDatabase.getInstance(context)
    val soundDao = database.soundDao()

    // Load sounds as StateFlow
    var allSounds by mutableStateOf<List<SoundEntity>>(emptyList())
    lifecycleScope.launch {
        allSounds = soundDao.getAllSounds().collectFirstOrNull() ?: emptyList()
    }

    // Get unique categories
    var categories by mutableStateOf<List<String>>(emptyList())
    lifecycleScope.launch {
        categories = soundDao.getAllCategories().collectFirstOrNull() ?: emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WristPad Library",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { /* Add sound action */ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add sound")
                    Text("Add")
                }
            }
        }

        // Categories filter
        if (categories.isNotEmpty()) {
            ScrollableColumn(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Start
            ) {
                ForEach(categories, ifEmpty = {}) { category ->
                    FilledButton(
                        onClick = { selectedCategory = category; onCategorySelected(category) },
                        selected = selectedCategory == category
                    ) {
                        Text(category)
                    }
                }
            }
        }

        // Sounds list
        if (allSounds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No sounds yet. Add some sounds to get started.",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = rememberScrollState(scrollState)
            ) {
                items(allSounds) { sound ->
                    SoundCard(
                        sound = sound,
                        onClick = { onSoundSelected(sound) },
                        isSelected = selectedCategory.isNotEmpty() && sound.category == selectedCategory,
                        filterCategory = selectedCategory
                    )
                }
            }
        }
    }
}

@Composable
fun SoundCard(
    sound: SoundEntity,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    filterCategory: String? = null
) {
    // Apply category filter
    if (filterCategory != null && sound.category != filterCategory) {
        return
    }

    var isToggled by remember { mutableStateOf(isSelected) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = if (isToggled) 4.dp else 1.dp,
        colors = CardDefaults.cardColor(if (isToggled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
        shadow = CardDefaults.shadow(if (isToggled) Color(0xFFBB86FC) else Color(0x00000000))
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Emoji and title
                Column(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = sound.emoji,
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = sound.title,
                        style = MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    if (sound.isFavorite) {
                        Icon(
                            Icons.Default.Star,
                            color = MaterialTheme.colorScheme.secondary,
                            contentDescription = "Favorite"
                        )
                    }
                }

                // Volume slider hint
                OutlinedTextField(
                    value = "${sound volume.toInt()}%",
                    readOnly = true,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}