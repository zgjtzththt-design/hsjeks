package com.melody

// Forced recompile
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.melody.ui.*
import com.melody.ui.theme.MelodyTheme

class MainActivity : ComponentActivity() {
  private val musicViewModel: MusicViewModel by viewModels()
  private val themeViewModel: ThemeViewModel by viewModels()

  override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
      when (keyCode) {
          android.view.KeyEvent.KEYCODE_VOLUME_UP -> {
              musicViewModel.increaseVolume()
              return true
          }
          android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> {
              musicViewModel.decreaseVolume()
              return true
          }
      }
      return super.onKeyDown(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
      when (keyCode) {
          android.view.KeyEvent.KEYCODE_VOLUME_UP,
          android.view.KeyEvent.KEYCODE_VOLUME_DOWN -> return true
      }
      return super.onKeyUp(keyCode, event)
  }

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
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
          } else {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
          }
        }

        val launcher = rememberLauncherForActivityResult(
          ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
          val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
          } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
          }
          hasPermission = result[requiredPermission] ?: false
        }

        LaunchedEffect(Unit) {
          // Check if permission is already granted before launching request flow
          val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
          } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
          }
          val alreadyGranted = checkSelfPermission(requiredPermission) == android.content.pm.PackageManager.PERMISSION_GRANTED
          if (alreadyGranted) {
            hasPermission = true
          } else {
            launcher.launch(permissions.toTypedArray())
          }
        }

        if (hasPermission) {
          var hasSeenWelcome by remember { 
              mutableStateOf(
                  getSharedPreferences("melody_prefs", android.content.Context.MODE_PRIVATE)
                      .getBoolean("has_seen_welcome", false)
              ) 
          }

          if (hasSeenWelcome) {
            MainScreen(
              musicViewModel = musicViewModel,
              themeViewModel = themeViewModel,
              onNavigateToSettings = { /* Already handled by selectedTab in MainScreen */ }
            )
          } else {
            WelcomeScreen(
                onFinishOnboarding = {
                    getSharedPreferences("melody_prefs", android.content.Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("has_seen_welcome", true)
                        .apply()
                    hasSeenWelcome = true
                }
            )
          }
        } else {
            // Unify design with a gorgeous Material 3 request/fallback screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Security,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "الأذونات مطلوبة",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "يحتاج تطبيق Melody إلى أذونات الوصول للذاكرة لتتمكن من تصفح وتشغيل ملفات الموسيقى الصوتية الخاصة بك على هذا الجهاز. يرجى منح الإذن للمتابعة والاستمتاع بالموسيقى.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    val useLiquidButtons by themeViewModel.useLiquidButtons.collectAsState()
                    MelodyButton(
                        onClick = {
                            launcher.launch(permissions.toTypedArray())
                        },
                        useLiquid = useLiquidButtons,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "منح الأذونات",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
      }
    }
  }
}

