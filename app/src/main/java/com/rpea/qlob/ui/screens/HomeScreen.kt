package com.rpea.qlob.ui.screens

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rpea.qlob.models.PrayerData
import com.rpea.qlob.models.SurahItem

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val surahs by viewModel.surahs.collectAsState()
    val currentSurah by viewModel.currentPlayingSurah.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                currentSurah?.let {
                    PlayerBar(
                        surah = it,
                        isPlaying = isPlaying,
                        onPlayPause = { viewModel.togglePlayPause() }
                    )
                }
                FooterRights()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Background image based on current surah or default
            val bgUrl = when (currentSurah?.type) {
                1 -> "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Masjid_al-Haram_panorama.jpg/1200px-Masjid_al-Haram_panorama.jpg"
                2 -> "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Al-Masjid_al-Nabawi%2C_Medina%2C_Saudi_Arabia.jpg/1200px-Al-Masjid_al-Nabawi%2C_Medina%2C_Saudi_Arabia.jpg"
                else -> null
            }

            if (bgUrl != null) {
                AsyncImage(
                    model = bgUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.3f
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                    text = "ربيع القلوب",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp)
                )

                prayerTimes?.let {
                    PrayerTimesCard(it)
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(surahs) { surah ->
                            SurahItemRow(
                                surah = surah,
                                isPlaying = currentSurah?.id == surah.id && isPlaying,
                                onPlay = { viewModel.playSurah(surah) },
                                onDownload = { viewModel.downloadSurah(surah, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerTimesCard(prayerData: PrayerData) {
    GlassCard {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                "مواقيت الصلاة في جازان",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PrayerItem("الفجر", prayerData.timings.Fajr)
                PrayerItem("الظهر", prayerData.timings.Dhuhr)
                PrayerItem("العصر", prayerData.timings.Asr)
                PrayerItem("المغرب", prayerData.timings.Maghrib)
                PrayerItem("العشاء", prayerData.timings.Isha)
            }
        }
    }
}

@Composable
fun PrayerItem(name: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Text(time, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SurahItemRow(surah: SurahItem, isPlaying: Boolean, onPlay: () -> Unit, onDownload: (Context) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val elevation by animateFloatAsState(if (isFocused) 12f else 2f)
    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .shadow(elevation.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onPlay() }
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (surah.type == 1) "مكية" else "مدنية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            if (surah.isDownloaded) {
                Text("محملة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            } else {
                IconButton(onClick = { onDownload(context) }) {
                    Icon(Icons.Default.Download, contentDescription = "Download")
                }
            }
        }
    }
}

@Composable
fun PlayerBar(surah: SurahItem, isPlaying: Boolean, onPlayPause: () -> Unit) {
    GlassCard(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(surah.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("يتم التشغيل الآن", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun FooterRights() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "جميع الحقوق محفوظة © يحيى مسودي",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        content()
    }
}
