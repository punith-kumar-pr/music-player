package com.melodyflow.player.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object NowPlaying : Screen("now_playing")
    object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    object ArtistDetail : Screen("artist/{artistName}") {
        fun createRoute(artistName: String) = "artist/${Uri.encode(artistName)}"
    }
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
    object Settings : Screen("settings")
}
