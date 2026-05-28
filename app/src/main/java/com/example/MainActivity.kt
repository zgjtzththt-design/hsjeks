package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.ui.*
import com.example.ui.theme.MelodyTheme

class MainActivity : ComponentActivity() {
  private val musicViewModel: MusicViewModel by viewModels()
  private val themeViewModel: ThemeViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val dynamicColor by themeViewModel.useDynamicColor.collectAsState()
      val primaryColor by themeViewModel.primaryColor.collectAsState()

      MelodyTheme(dynamicColor = dynamicColor, customColor = primaryColor) {
        var hasPermission by remember { mutableStateOf(false) }
        val permissions = mutableListOf<String>().apply {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_AUDIO)
            add(Manifest.permission.POST_NOTIFICATIONS)
          } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
          }
        }

        val launcher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
          hasPermission = result.values.all { it }
        }

        LaunchedEffect(Unit) {
          launcher.launch(permissions.toTypedArray())
        }

        if (hasPermission) {
          MainScreen(
            musicViewModel = musicViewModel,
            themeViewModel = themeViewModel,
            onNavigateToSettings = { /* Already handled by selectedTab in MainScreen */ }
          )
        } else {
            // Simple placeholder for permission denied or not yet asked
        }
      }
    }
  }
}

