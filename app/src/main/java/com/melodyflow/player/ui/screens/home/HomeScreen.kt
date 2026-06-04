package com.melodyflow.player.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.melodyflow.player.R
import com.melodyflow.player.ui.components.MiniPlayer
import com.melodyflow.player.ui.components.PermissionHandler
import com.melodyflow.player.ui.navigation.Screen
import com.melodyflow.player.ui.screens.albums.AlbumsTab
import com.melodyflow.player.ui.screens.artists.ArtistsTab
import com.melodyflow.player.ui.screens.favourites.FavouritesTab
import com.melodyflow.player.ui.screens.playlists.PlaylistsTab
import com.melodyflow.player.ui.screens.songs.SongsTab
import com.melodyflow.player.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    playerViewModel: PlayerViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    PermissionHandler {
        val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
        val favouriteIds by homeViewModel.favouriteIds.collectAsStateWithLifecycle()
        val playlists by homeViewModel.playlistsWithSongs.collectAsStateWithLifecycle()
        
        val playerState by playerViewModel.uiState.collectAsStateWithLifecycle()
        
        var selectedTabIndex by remember { mutableStateOf(0) }
        var isSearchExpanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        if (isSearchExpanded) {
                            TextField(
                                value = uiState.searchQuery,
                                onValueChange = { homeViewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search songs, albums, artists...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        } else {
                            Text(stringResource(id = R.string.app_name))
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            isSearchExpanded = !isSearchExpanded 
                            if (!isSearchExpanded) {
                                homeViewModel.updateSearchQuery("")
                            }
                        }) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    MiniPlayer(
                        currentSong = playerState.currentSong,
                        isPlaying = playerState.isPlaying,
                        progress = playerState.progress,
                        onPlayPauseClick = {
                            if (playerState.isPlaying) playerViewModel.pause() else playerViewModel.play()
                        },
                        onNextClick = { playerViewModel.next() },
                        onClick = { navController.navigate(Screen.NowPlaying.route) }
                    )
                    
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Rounded.MusicNote, contentDescription = "Songs") },
                            label = { Text("Songs") },
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Rounded.Album, contentDescription = "Albums") },
                            label = { Text("Albums") },
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Rounded.Person, contentDescription = "Artists") },
                            label = { Text("Artists") },
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Rounded.QueueMusic, contentDescription = "Playlists") },
                            label = { Text("Playlists") },
                            selected = selectedTabIndex == 3,
                            onClick = { selectedTabIndex = 3 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Rounded.Favorite, contentDescription = "Favourites") },
                            label = { Text("Favourites") },
                            selected = selectedTabIndex == 4,
                            onClick = { selectedTabIndex = 4 }
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (selectedTabIndex) {
                        0 -> SongsTab(
                            songs = uiState.songs,
                            favouriteIds = favouriteIds,
                            currentSongId = playerState.currentSong?.id,
                            onSongClick = { songs, index -> playerViewModel.setQueue(songs, index) },
                            onFavouriteClick = { homeViewModel.toggleFavourite(it) },
                            onAddToPlaylistClick = { /* Handle add to playlist */ }
                        )
                        1 -> AlbumsTab(
                            albums = uiState.albums,
                            onAlbumClick = { navController.navigate(Screen.AlbumDetail.createRoute(it)) }
                        )
                        2 -> ArtistsTab(
                            artists = uiState.artists,
                            onArtistClick = { navController.navigate(Screen.ArtistDetail.createRoute(it)) }
                        )
                        3 -> PlaylistsTab(
                            playlists = playlists,
                            onCreatePlaylist = { homeViewModel.createPlaylist(it) {} },
                            onPlaylistClick = { navController.navigate(Screen.PlaylistDetail.createRoute(it)) },
                            onDeletePlaylist = { homeViewModel.deletePlaylist(it) }
                        )
                        4 -> FavouritesTab(
                            songs = uiState.songs.filter { favouriteIds.contains(it.id) },
                            currentSongId = playerState.currentSong?.id,
                            onSongClick = { songs, index -> playerViewModel.setQueue(songs, index) },
                            onFavouriteClick = { homeViewModel.toggleFavourite(it) }
                        )
                    }
                }
            }
        }
    }
}
