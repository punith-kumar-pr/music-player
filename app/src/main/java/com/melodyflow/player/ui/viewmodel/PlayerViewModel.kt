package com.melodyflow.player.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.repository.MusicRepository
import com.melodyflow.player.service.PlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val queue: List<Song> = emptyList()
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository
) : ViewModel() {

    private var mediaController: MediaController? = null
    
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var progressJob: Job? = null
    private var allSongs: List<Song> = emptyList()

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            _isConnected.value = true
            setupPlayerListener()
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        val player = mediaController ?: return
        
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateCurrentSong(mediaItem)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    updateProgress()
                }
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                _uiState.update { it.copy(shuffleEnabled = shuffleModeEnabled) }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                _uiState.update { it.copy(repeatMode = repeatMode) }
            }
        })
        
        // Initial state sync
        updateCurrentSong(player.currentMediaItem)
        _uiState.update { 
            it.copy(
                isPlaying = player.isPlaying,
                shuffleEnabled = player.shuffleModeEnabled,
                repeatMode = player.repeatMode
            )
        }
        if (player.isPlaying) startProgressTracking()
    }

    private fun updateCurrentSong(mediaItem: MediaItem?) {
        val songId = mediaItem?.mediaId?.toLongOrNull()
        val currentSong = allSongs.find { it.id == songId } ?: uiState.value.queue.find { it.id == songId }
        
        _uiState.update { it.copy(currentSong = currentSong) }
        updateProgress()
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                updateProgress()
                delay(200)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun updateProgress() {
        val player = mediaController ?: return
        val pos = player.currentPosition.coerceAtLeast(0)
        val dur = player.duration.coerceAtLeast(1)
        
        _uiState.update {
            it.copy(
                currentPosition = pos,
                duration = if (dur > 0) dur else it.duration,
                progress = if (dur > 0) pos.toFloat() / dur.toFloat() else 0f
            )
        }
    }

    fun setQueue(songs: List<Song>, startIndex: Int) {
        val player = mediaController ?: return
        allSongs = songs
        
        _uiState.update { it.copy(queue = songs) }
        
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.contentUri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setArtworkUri(song.albumArtUri)
                        .build()
                )
                .build()
        }
        
        player.setMediaItems(mediaItems)
        player.seekToDefaultPosition(startIndex)
        player.prepare()
        player.play()
    }

    fun play() = mediaController?.play()
    fun pause() = mediaController?.pause()
    fun next() = mediaController?.seekToNextMediaItem()
    fun previous() = mediaController?.seekToPreviousMediaItem()
    fun seekTo(positionMs: Long) = mediaController?.seekTo(positionMs)
    
    fun toggleShuffle() {
        val player = mediaController ?: return
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }
    
    fun cycleRepeatMode() {
        val player = mediaController ?: return
        val newMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        player.repeatMode = newMode
    }

    fun toggleFavourite(songId: Long) {
        viewModelScope.launch {
            musicRepository.toggleFavourite(songId)
        }
    }

    override fun onCleared() {
        stopProgressTracking()
        mediaController?.release()
        super.onCleared()
    }
}
