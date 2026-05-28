package com.example.ui

import android.app.Application
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _useDynamicColor = MutableStateFlow(prefs.getBoolean("dynamic_color", true))
    val useDynamicColor: StateFlow<Boolean> = _useDynamicColor

    private val _primaryColor = MutableStateFlow(Color(prefs.getInt("primary_color", Color.Blue.toArgb())))
    val primaryColor: StateFlow<Color> = _primaryColor

    private val _useCompactLayout = MutableStateFlow(prefs.getBoolean("compact_layout", false))
    val useCompactLayout: StateFlow<Boolean> = _useCompactLayout

    private val _isGlassEffectEnabled = MutableStateFlow(prefs.getBoolean("glass_effect", false))
    val isGlassEffectEnabled: StateFlow<Boolean> = _isGlassEffectEnabled

    private val _albumArtShape = MutableStateFlow(prefs.getInt("album_art_shape", 0))
    val albumArtShape: StateFlow<Int> = _albumArtShape

    fun setDynamicColor(enabled: Boolean) {
        _useDynamicColor.value = enabled
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
    }

    fun setPrimaryColor(color: Color) {
        _primaryColor.value = color
        prefs.edit().putInt("primary_color", color.toArgb()).apply()
    }

    fun setCompactLayout(enabled: Boolean) {
        _useCompactLayout.value = enabled
        prefs.edit().putBoolean("compact_layout", enabled).apply()
    }

    fun setGlassEffectEnabled(enabled: Boolean) {
        _isGlassEffectEnabled.value = enabled
        prefs.edit().putBoolean("glass_effect", enabled).apply()
    }

    fun setAlbumArtShape(index: Int) {
        _albumArtShape.value = index
        prefs.edit().putInt("album_art_shape", index).apply()
    }
}
