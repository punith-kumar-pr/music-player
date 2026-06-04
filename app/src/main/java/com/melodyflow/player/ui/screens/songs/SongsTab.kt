package com.melodyflow.player.ui.screens.songs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.melodyflow.player.data.model.Song
import com.melodyflow.player.ui.components.SongItem

@Composable
fun SongsTab(
    songs: List<Song>,
    favouriteIds: Set<Long>,
    currentSongId: Long?,
    onSongClick: (List<Song>, Int) -> Unit,
    onFavouriteClick: (Long) -> Unit,
    onAddToPlaylistClick: (Long) -> Unit
) {
    if (songs.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MusicOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No songs found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                SongItem(
                    song = song,
                    isFavourite = favouriteIds.contains(song.id),
                    isPlaying = song.id == currentSongId,
                    onClick = { onSongClick(songs, index) },
                    onFavouriteClick = { onFavouriteClick(song.id) },
                    onAddToPlaylistClick = { onAddToPlaylistClick(song.id) }
                )
            }
        }
    }
}
