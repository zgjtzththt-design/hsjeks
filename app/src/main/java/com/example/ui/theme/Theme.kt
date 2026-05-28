package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
  )

@Composable
fun MelodyTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  customColor: androidx.compose.ui.graphics.Color? = null,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      customColor != null -> {
          // Generate a full palette based on the primary color for a real Material You feel
          if (darkTheme) {
              darkColorScheme(
                  primary = customColor,
                  onPrimary = customColor.luminance().let { if (it > 0.5f) Color.Black else Color.White },
                  primaryContainer = customColor.copy(alpha = 0.3f),
                  surface = customColor.copy(alpha = 0.12f).compositeOver(Color.Black),
                  background = customColor.copy(alpha = 0.08f).compositeOver(Color.Black),
                  surfaceVariant = customColor.copy(alpha = 0.15f).compositeOver(Color.Black)
              )
          } else {
              lightColorScheme(
                  primary = customColor,
                  onPrimary = customColor.luminance().let { if (it > 0.5f) Color.Black else Color.White },
                  primaryContainer = customColor.copy(alpha = 0.15f),
                  surface = customColor.copy(alpha = 0.08f).compositeOver(Color.White),
                  background = customColor.copy(alpha = 0.05f).compositeOver(Color.White),
                  surfaceVariant = customColor.copy(alpha = 0.12f).compositeOver(Color.White)
              )
          }
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
