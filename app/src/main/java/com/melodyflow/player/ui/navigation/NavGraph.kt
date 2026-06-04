package com.melodyflow.player.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.melodyflow.player.ui.screens.albums.AlbumDetailScreen
import com.melodyflow.player.ui.screens.artists.ArtistDetailScreen
import com.melodyflow.player.ui.screens.home.HomeScreen
import com.melodyflow.player.ui.screens.nowplaying.NowPlayingScreen
import com.melodyflow.player.ui.screens.playlists.PlaylistDetailScreen
import com.melodyflow.player.ui.screens.settings.SettingsScreen
import com.melodyflow.player.ui.viewmodel.PlayerViewModel

@Composable
fun MelodyFlowNavGraph(
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    
    // Connect to service when graph is created
    val isConnected by playerViewModel.isConnected.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
        
        composable(Screen.NowPlaying.route) {
            NowPlayingScreen(
                navController = navController,
                playerViewModel = playerViewModel
            )
        }
        
        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
            AlbumDetailScreen(
                navController = navController,
                playerViewModel = playerViewModel,
                albumId = albumId
            )
        }
        
        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistName") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistName = backStackEntry.arguments?.getString("artistName") ?: return@composable
            ArtistDetailScreen(
                navController = navController,
                playerViewModel = playerViewModel,
                artistName = artistName
            )
        }
        
        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
            PlaylistDetailScreen(
                navController = navController,
                playerViewModel = playerViewModel,
                playlistId = playlistId
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
