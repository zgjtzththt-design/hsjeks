package com.melody.ui.theme

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
  customFontPath: String? = null,
  fontSizeScale: Float = 1.0f,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val customFontFamily = androidx.compose.runtime.remember(customFontPath) {
    if (customFontPath != null) {
      try {
        val file = java.io.File(customFontPath)
        if (file.exists()) {
          androidx.compose.ui.text.font.FontFamily(
            androidx.compose.ui.text.font.Font(file)
          )
        } else {
          androidx.compose.ui.text.font.FontFamily.Default
        }
      } catch (e: Exception) {
        androidx.compose.ui.text.font.FontFamily.Default
      }
    } else {
      androidx.compose.ui.text.font.FontFamily.Default
    }
  }

  val customTypography = androidx.compose.runtime.remember(customFontFamily, fontSizeScale) {
    androidx.compose.material3.Typography(
        displayLarge = Typography.displayLarge.copy(fontFamily = customFontFamily, fontSize = Typography.displayLarge.fontSize * fontSizeScale),
        displayMedium = Typography.displayMedium.copy(fontFamily = customFontFamily, fontSize = Typography.displayMedium.fontSize * fontSizeScale),
        displaySmall = Typography.displaySmall.copy(fontFamily = customFontFamily, fontSize = Typography.displaySmall.fontSize * fontSizeScale),
        headlineLarge = Typography.headlineLarge.copy(fontFamily = customFontFamily, fontSize = Typography.headlineLarge.fontSize * fontSizeScale),
        headlineMedium = Typography.headlineMedium.copy(fontFamily = customFontFamily, fontSize = Typography.headlineMedium.fontSize * fontSizeScale),
        headlineSmall = Typography.headlineSmall.copy(fontFamily = customFontFamily, fontSize = Typography.headlineSmall.fontSize * fontSizeScale),
        titleLarge = Typography.titleLarge.copy(fontFamily = customFontFamily, fontSize = Typography.titleLarge.fontSize * fontSizeScale),
        titleMedium = Typography.titleMedium.copy(fontFamily = customFontFamily, fontSize = Typography.titleMedium.fontSize * fontSizeScale),
        titleSmall = Typography.titleSmall.copy(fontFamily = customFontFamily, fontSize = Typography.titleSmall.fontSize * fontSizeScale),
        bodyLarge = Typography.bodyLarge.copy(fontFamily = customFontFamily, fontSize = Typography.bodyLarge.fontSize * fontSizeScale),
        bodyMedium = Typography.bodyMedium.copy(fontFamily = customFontFamily, fontSize = Typography.bodyMedium.fontSize * fontSizeScale),
        bodySmall = Typography.bodySmall.copy(fontFamily = customFontFamily, fontSize = Typography.bodySmall.fontSize * fontSizeScale),
        labelLarge = Typography.labelLarge.copy(fontFamily = customFontFamily, fontSize = Typography.labelLarge.fontSize * fontSizeScale),
        labelMedium = Typography.labelMedium.copy(fontFamily = customFontFamily, fontSize = Typography.labelMedium.fontSize * fontSizeScale),
        labelSmall = Typography.labelSmall.copy(fontFamily = customFontFamily, fontSize = Typography.labelSmall.fontSize * fontSizeScale)
    )
  }

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

  MaterialTheme(colorScheme = colorScheme, typography = customTypography, content = content)
}
