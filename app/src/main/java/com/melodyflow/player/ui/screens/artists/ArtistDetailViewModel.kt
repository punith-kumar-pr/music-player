package com.melodyflow.player.ui.screens.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodyflow.player.data.model.Artist
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailUiState(
    val artist: Artist? = null,
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    val favouriteIds: StateFlow<Set<Long>> = repository.getFavouriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun loadArtist(artistName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val allSongs = repository.getAllSongs()
                val artistSongs = allSongs.filter { it.artist == artistName }.sortedBy { it.title }
                
                if (artistSongs.isNotEmpty()) {
                    val artist = Artist(
                        name = artistName,
                        songCount = artistSongs.size,
                        albumCount = artistSongs.distinctBy { it.albumId }.size
                    )
                    
                    _uiState.update { 
                        it.copy(
                            artist = artist,
                            songs = artistSongs,
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
