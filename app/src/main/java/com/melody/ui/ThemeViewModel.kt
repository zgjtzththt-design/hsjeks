package com.melody.ui

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

    private val _albumArtShape = MutableStateFlow(prefs.getInt("album_art_shape", 0))
    val albumArtShape: StateFlow<Int> = _albumArtShape

    private val _useLiquidButtons = MutableStateFlow(prefs.getBoolean("use_liquid_buttons", false))
    val useLiquidButtons: StateFlow<Boolean> = _useLiquidButtons

    // New values for list sizing, opacity, background, and fonts
    private val _listSizing = MutableStateFlow(prefs.getFloat("list_sizing", 1.0f))
    val listSizing: StateFlow<Float> = _listSizing

    private val _listOpacity = MutableStateFlow(prefs.getFloat("list_opacity", 0.8f))
    val listOpacity: StateFlow<Float> = _listOpacity

    private val _customBackgroundPath = MutableStateFlow(prefs.getString("custom_background_path", null))
    val customBackgroundPath: StateFlow<String?> = _customBackgroundPath

    private val _customFontPath = MutableStateFlow(prefs.getString("custom_font_path", null))
    val customFontPath: StateFlow<String?> = _customFontPath

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

    fun setAlbumArtShape(index: Int) {
        _albumArtShape.value = index
        prefs.edit().putInt("album_art_shape", index).apply()
    }

    fun setUseLiquidButtons(enabled: Boolean) {
        _useLiquidButtons.value = enabled
        prefs.edit().putBoolean("use_liquid_buttons", enabled).apply()
    }

    fun setListSizing(value: Float) {
        _listSizing.value = value
        prefs.edit().putFloat("list_sizing", value).apply()
    }

    fun setListOpacity(value: Float) {
        _listOpacity.value = value
        prefs.edit().putFloat("list_opacity", value).apply()
    }

    fun importFont(uri: android.net.Uri, context: Context): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val file = java.io.File(context.filesDir, "custom_font.ttf")
            if (file.exists()) {
                file.delete()
            }
            val outputStream = java.io.FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            _customFontPath.value = file.absolutePath
            prefs.edit().putString("custom_font_path", file.absolutePath).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearCustomFont(context: Context) {
        _customFontPath.value = null
        prefs.edit().remove("custom_font_path").apply()
        try {
            val file = java.io.File(context.filesDir, "custom_font.ttf")
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun importBackground(uri: android.net.Uri, context: Context): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val file = java.io.File(context.filesDir, "custom_background.jpg")
            if (file.exists()) {
                file.delete()
            }
            val outputStream = java.io.FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            _customBackgroundPath.value = file.absolutePath
            prefs.edit().putString("custom_background_path", file.absolutePath).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearCustomBackground(context: Context) {
        _customBackgroundPath.value = null
        prefs.edit().remove("custom_background_path").apply()
        try {
            val file = java.io.File(context.filesDir, "custom_background.jpg")
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
