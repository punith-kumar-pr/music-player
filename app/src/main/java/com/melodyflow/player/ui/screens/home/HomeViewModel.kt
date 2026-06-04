package com.melodyflow.player.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melodyflow.player.data.local.db.entity.PlaylistEntity
import com.melodyflow.player.data.local.db.entity.PlaylistWithSongIds
import com.melodyflow.player.data.model.Album
import com.melodyflow.player.data.model.Artist
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    TITLE, ARTIST, DATE_ADDED, DURATION
}

data class HomeUiState(
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.TITLE
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val favouriteIds: StateFlow<Set<Long>> = repository.getFavouriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
        
    val playlists: StateFlow<List<PlaylistEntity>> = repository.getPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val playlistsWithSongs: StateFlow<List<PlaylistWithSongIds>> = repository.getPlaylistsWithSongIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var allSongsCache: List<Song> = emptyList()
    private var allAlbumsCache: List<Album> = emptyList()
    private var allArtistsCache: List<Artist> = emptyList()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val songs = repository.getAllSongs()
                allSongsCache = songs
                
                // Group to create albums
                allAlbumsCache = songs.groupBy { it.albumId }.map { (albumId, albumSongs) ->
                    val firstSong = albumSongs.first()
                    Album(
                        id = albumId,
                        name = firstSong.album,
                        artist = firstSong.artist,
                        albumArtUri = firstSong.albumArtUri,
                        songCount = albumSongs.size
                    )
                }.sortedBy { it.name }
                
                // Group to create artists
                allArtistsCache = songs.groupBy { it.artist }.map { (artistName, artistSongs) ->
                    Artist(
                        name = artistName,
                        songCount = artistSongs.size,
                        albumCount = artistSongs.distinctBy { it.albumId }.size
                    )
                }.sortedBy { it.name }
                
                applyFiltersAndSort()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndSort()
    }
    
    fun updateSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()
        
        var filteredSongs = allSongsCache
        var filteredAlbums = allAlbumsCache
        var filteredArtists = allArtistsCache
        
        if (query.isNotEmpty()) {
            filteredSongs = allSongsCache.filter { 
                it.title.lowercase().contains(query) || it.artist.lowercase().contains(query) 
            }
            filteredAlbums = allAlbumsCache.filter { 
                it.name.lowercase().contains(query) || it.artist.lowercase().contains(query) 
            }
            filteredArtists = allArtistsCache.filter { 
                it.name.lowercase().contains(query) 
            }
        }
        
        filteredSongs = when (state.sortOrder) {
            SortOrder.TITLE -> filteredSongs.sortedBy { it.title }
            SortOrder.ARTIST -> filteredSongs.sortedBy { it.artist }
            SortOrder.DATE_ADDED -> filteredSongs.sortedByDescending { it.dateAdded }
            SortOrder.DURATION -> filteredSongs.sortedByDescending { it.duration }
        }
        
        _uiState.update { 
            it.copy(
                songs = filteredSongs,
                albums = filteredAlbums,
                artists = filteredArtists
            )
        }
    }
    
    fun toggleFavourite(songId: Long) {
        viewModelScope.launch {
            repository.toggleFavourite(songId)
        }
    }
    
    fun createPlaylist(name: String, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createPlaylist(name)
            onSuccess(id)
        }
    }
    
    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            repository.addSongToPlaylist(playlistId, songId)
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }
}
