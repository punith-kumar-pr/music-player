package com.melodyflow.player.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val dynamicColor by viewModel.dynamicColor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ListItem(
                headlineContent = { Text("Dark Theme") },
                supportingContent = { Text("Enable dark mode") },
                leadingContent = {
                    Icon(Icons.Rounded.DarkMode, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            )
            
            Divider()
            
            ListItem(
                headlineContent = { Text("Dynamic Color") },
                supportingContent = { Text("Use wallpaper colors (Android 12+)") },
                leadingContent = {
                    Icon(Icons.Rounded.ColorLens, contentDescription = null)
                },
                trailingContent = {
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = { viewModel.toggleDynamicColor(it) }
                    )
                }
            )
            
            Divider()
            
            ListItem(
                headlineContent = { Text("About MelodyFlow") },
                supportingContent = { Text("Version 1.0.0") },
                leadingContent = {
                    Icon(Icons.Rounded.Info, contentDescription = null)
                }
            )
        }
    }
}
