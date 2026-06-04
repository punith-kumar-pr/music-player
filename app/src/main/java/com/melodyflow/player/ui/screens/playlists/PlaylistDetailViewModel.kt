package com.melodyflow.player.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodyflow.player.data.local.db.entity.PlaylistEntity
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailUiState(
    val playlist: PlaylistEntity? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    val favouriteIds: StateFlow<Set<Long>> = repository.getFavouriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Get playlist info
                repository.getPlaylistsWithSongIds().collect { playlists ->
                    val playlistWithSongs = playlists.find { it.playlist.playlistId == playlistId }
                    if (playlistWithSongs != null) {
                        val songIds = playlistWithSongs.songIds
                        val allSongs = repository.getAllSongs()
                        val playlistSongs = allSongs.filter { it.id in songIds }
                        
                        _uiState.update { 
                            it.copy(
                                playlist = playlistWithSongs.playlist,
                                songs = playlistSongs,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun toggleFavourite(songId: Long) {
        viewModelScope.launch {
            repository.toggleFavourite(songId)
        }
    }
}
