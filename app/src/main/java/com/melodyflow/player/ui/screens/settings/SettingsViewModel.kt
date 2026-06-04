package com.melodyflow.player.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
) : ViewModel() {

    // Using simple state flows here for brevity.
    // In a real app, this would use DataStore to persist preferences.
    
    private val _darkMode = MutableStateFlow(true)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()
    
    private val _dynamicColor = MutableStateFlow(true)
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()
    
    fun toggleDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
    }
    
    fun toggleDynamicColor(enabled: Boolean) {
        _dynamicColor.value = enabled
    }
}
