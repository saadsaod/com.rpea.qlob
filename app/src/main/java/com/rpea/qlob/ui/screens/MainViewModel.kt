package com.rpea.qlob.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.rpea.qlob.api.NetworkModule
import com.rpea.qlob.models.PrayerData
import com.rpea.qlob.models.Reciter
import com.rpea.qlob.models.SurahItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _reciters = MutableStateFlow<List<Reciter>>(emptyList())
    val reciters: StateFlow<List<Reciter>> = _reciters.asStateFlow()

    private val _selectedReciter = MutableStateFlow<Reciter?>(null)
    val selectedReciter: StateFlow<Reciter?> = _selectedReciter.asStateFlow()

    private val _surahs = MutableStateFlow<List<SurahItem>>(emptyList())
    val surahs: StateFlow<List<SurahItem>> = _surahs.asStateFlow()

    private val _prayerTimes = MutableStateFlow<PrayerData?>(null)
    val prayerTimes: StateFlow<PrayerData?> = _prayerTimes.asStateFlow()

    private val _currentPlayingSurah = MutableStateFlow<SurahItem?>(null)
    val currentPlayingSurah: StateFlow<SurahItem?> = _currentPlayingSurah.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val player: ExoPlayer = ExoPlayer.Builder(application).build().apply {
        addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch prayer times for Jazan
                val prayerRes = NetworkModule.aladhanApi.getPrayerTimes()
                _prayerTimes.value = prayerRes.data

                // Fetch Quran data
                val recitersRes = NetworkModule.quranApi.getReciters()
                val suwarRes = NetworkModule.quranApi.getSuwar()

                _reciters.value = recitersRes.reciters
                
                // Select first reciter by default, e.g., Abdulbasit or anyone
                val firstReciter = recitersRes.reciters.firstOrNull()
                if (firstReciter != null) {
                    selectReciter(firstReciter, suwarRes.suwar)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectReciter(reciter: Reciter, allSuwar: List<com.rpea.qlob.models.Surah> = emptyList()) {
        _selectedReciter.value = reciter
        viewModelScope.launch {
            try {
                val suwar = if (allSuwar.isEmpty()) {
                    NetworkModule.quranApi.getSuwar().suwar
                } else {
                    allSuwar
                }
                
                val moshaf = reciter.moshaf.firstOrNull() ?: return@launch
                val surahList = moshaf.surahList.split(",").map { it.toInt() }
                
                val items = suwar.filter { surahList.contains(it.id) }.map { surah ->
                    val server = moshaf.server
                    val formattedId = DecimalFormat("000").format(surah.id)
                    val url = "$server$formattedId.mp3"
                    
                    // Check if file exists locally
                    val file = File(getApplication<Application>().filesDir, "surah_${reciter.id}_${surah.id}.mp3")
                    
                    SurahItem(
                        id = surah.id,
                        name = surah.name,
                        type = surah.makkia, // 1 Makki, 2 Madani
                        serverUrl = url,
                        isDownloaded = file.exists(),
                        localPath = if (file.exists()) file.absolutePath else null
                    )
                }
                _surahs.value = items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playSurah(surah: SurahItem) {
        _currentPlayingSurah.value = surah
        val uri = surah.localPath ?: surah.serverUrl
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun downloadSurah(surah: SurahItem, context: Context) {
        val reciter = _selectedReciter.value ?: return
        viewModelScope.launch {
            val file = com.rpea.qlob.api.Downloader.downloadFile(context, surah.serverUrl, reciter.id, surah.id)
            if (file != null) {
                // Update surah list
                val updated = _surahs.value.map {
                    if (it.id == surah.id) it.copy(isDownloaded = true, localPath = file.absolutePath) else it
                }
                _surahs.value = updated
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
