package com.melodyflow.player.ui.screens.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melodyflow.player.data.local.db.entity.PlaylistWithSongIds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlaylistsTab(
    playlists: List<PlaylistWithSongIds>,
    onCreatePlaylist: (String) -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onDeletePlaylist: (Long) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (playlists.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No playlists found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(playlists, key = { it.playlist.playlistId }) { playlistWithSongs ->
                    val playlist = playlistWithSongs.playlist
                    ListItem(
                        headlineContent = { Text(playlist.name) },
                        supportingContent = { 
                            val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(playlist.createdAt))
                            Text("${playlistWithSongs.songRefs.size} songs • Created $date") 
                        },
                        leadingContent = {
                            Icon(Icons.Rounded.QueueMusic, contentDescription = null)
                        },
                        trailingContent = {
                            IconButton(onClick = { onDeletePlaylist(playlist.playlistId) }) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                            }
                        },
                        modifier = Modifier.clickable { onPlaylistClick(playlist.playlistId) }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 72.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Create Playlist")
        }

        if (showCreateDialog) {
            CreatePlaylistDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    onCreatePlaylist(name)
                    showCreateDialog = false
                }
            )
        }
    }
}
