package com.melodyflow.player.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithSongIds(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "playlistId",
        entity = PlaylistSongCrossRef::class
    )
    val songRefs: List<PlaylistSongCrossRef>
) {
    val songIds: List<Long> get() = songRefs.map { it.songId }
}
