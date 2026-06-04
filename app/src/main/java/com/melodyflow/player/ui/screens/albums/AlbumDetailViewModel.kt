package com.melodyflow.player.ui.screens.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodyflow.player.data.model.Album
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlbumDetailUiState(
    val album: Album? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetailUiState())
    val uiState: StateFlow<AlbumDetailUiState> = _uiState.asStateFlow()

    val favouriteIds: StateFlow<Set<Long>> = repository.getFavouriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun loadAlbum(albumId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val allSongs = repository.getAllSongs()
                val albumSongs = allSongs.filter { it.albumId == albumId }.sortedBy { it.title }
                
                if (albumSongs.isNotEmpty()) {
                    val firstSong = albumSongs.first()
                    val album = Album(
                        id = albumId,
                        name = firstSong.album,
                        artist = firstSong.artist,
                        albumArtUri = firstSong.albumArtUri,
                        songCount = albumSongs.size
                    )
                    
                    _uiState.update { 
                        it.copy(
                            album = album,
                            songs = albumSongs,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
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
