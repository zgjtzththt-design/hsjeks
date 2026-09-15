package com.melody.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * Palette configurations for different Liquid Glass styles
 */
data class LiquidGlassThemePalette(
    val id: String,
    val name: String,
    val nameAr: String,
    val orbColors: List<Color>,
    val glassTint: Color,
    val highlightColor: Color,
    val isDarkAesthetic: Boolean
)

val LiquidGlassPresets = listOf(
    LiquidGlassThemePalette(
        id = "aurora",
        name = "Neon Aurora",
        nameAr = "أورورا نيون",
        orbColors = listOf(
            Color(0xFF8A2387),
            Color(0xFFE94057),
            Color(0xFFF27121),
            Color(0xFF00F260)
        ),
        glassTint = Color(0xFF1E1B4B),
        highlightColor = Color(0xFF38BDF8),
        isDarkAesthetic = true
    ),
    LiquidGlassThemePalette(
        id = "cosmic",
        name = "Cosmic Space",
        nameAr = "فضاء كوني",
        orbColors = listOf(
            Color(0xFF1F1C2C),
            Color(0xFF928DAB),
            Color(0xFF3A6073),
            Color(0xFF16222A)
        ),
        glassTint = Color(0xFF111827),
        highlightColor = Color(0xFF94A3B8),
        isDarkAesthetic = true
    ),
    LiquidGlassThemePalette(
        id = "ocean",
        name = "Deep Ocean",
        nameAr = "محيط عميق",
        orbColors = listOf(
            Color(0xFF00C6FF),
            Color(0xFF0072FF),
            Color(0xFF0052D4),
            Color(0xFF4364F7)
        ),
        glassTint = Color(0xFF0F172A),
        highlightColor = Color(0xFF38BDF8),
        isDarkAesthetic = true
    ),
    LiquidGlassThemePalette(
        id = "sunset",
        name = "Warm Sunset",
        nameAr = "غروب دافئ",
        orbColors = listOf(
            Color(0xFFFF512F),
            Color(0xFFDD2476),
            Color(0xFFFF8008),
            Color(0xFFFFC837)
        ),
        glassTint = Color(0xFF2A0845),
        highlightColor = Color(0xFFFFD1DC),
        isDarkAesthetic = true
    ),
    LiquidGlassThemePalette(
        id = "emerald",
        name = "Crystal Emerald",
        nameAr = "زمردي كريستالي",
        orbColors = listOf(
            Color(0xFF0BA360),
            Color(0xFF3CBA92),
            Color(0xFF30E8BF),
            Color(0xFFFF8235)
        ),
        glassTint = Color(0xFF064E3B),
        highlightColor = Color(0xFFA7F3D0),
        isDarkAesthetic = true
    ),
    LiquidGlassThemePalette(
        id = "frosted",
        name = "Pure Frosted",
        nameAr = "زجاج نقي",
        orbColors = listOf(
            Color(0xFF93C5FD),
            Color(0xFFC4B5FD),
            Color(0xFFFDE68A),
            Color(0xFFBAE6FD)
        ),
        glassTint = Color(0xFFFFFFFF),
        highlightColor = Color(0xFFFFFFFF),
        isDarkAesthetic = false
    )
)

fun getGlassPreset(id: String): LiquidGlassThemePalette {
    return LiquidGlassPresets.find { it.id == id } ?: LiquidGlassPresets[0]
}

/**
 * Dynamic Animated Liquid Mesh Background with glowing ambient orbs
 */
@Composable
fun LiquidMeshBackground(
    modifier: Modifier = Modifier,
    presetId: String = "aurora",
    dominantMusicColor: Color = Color.Transparent,
    audioAmplitude: Float = 0f,
    isPlaying: Boolean = false,
    enableOrbs: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val preset = remember(presetId) { getGlassPreset(presetId) }
    val isDark = isSystemInDarkTheme() || preset.isDarkAesthetic

    val infiniteTransition = rememberInfiniteTransition(label = "LiquidOrbs")

    val t1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbPhase1"
    )

    val t2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbPhase2"
    )

    val t3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbPhase3"
    )

    val pulseScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f + (audioAmplitude * 0.35f).coerceIn(0f, 0.45f) else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "MusicPulse"
    )

    val baseBackgroundColor by animateColorAsState(
        targetValue = if (isDark) {
            Color(0xFF0B0E14)
        } else {
            Color(0xFFF4F6FB)
        },
        animationSpec = tween(600),
        label = "MeshBaseColor"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBackgroundColor)
    ) {
        if (enableOrbs) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Dynamic Orb 1 (Top-Left fluid)
                val orb1X = width * (0.25f + 0.18f * cos(t1))
                val orb1Y = height * (0.25f + 0.15f * sin(t1 * 0.8f))
                val orb1Radius = (width * 0.65f) * pulseScale
                val orb1Color = if (dominantMusicColor.isSpecified && dominantMusicColor != Color.Transparent) {
                    dominantMusicColor
                } else {
                    preset.orbColors.getOrElse(0) { Color(0xFF673AB7) }
                }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            orb1Color.copy(alpha = if (isDark) 0.55f else 0.35f),
                            orb1Color.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(orb1X, orb1Y),
                        radius = orb1Radius
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                )

                // Dynamic Orb 2 (Right Center fluid)
                val orb2X = width * (0.75f + 0.20f * sin(t2))
                val orb2Y = height * (0.50f + 0.18f * cos(t2 * 0.9f))
                val orb2Radius = (width * 0.60f) * pulseScale
                val orb2Color = preset.orbColors.getOrElse(1) { Color(0xFFE91E63) }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            orb2Color.copy(alpha = if (isDark) 0.50f else 0.30f),
                            orb2Color.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(orb2X, orb2Y),
                        radius = orb2Radius
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                )

                // Dynamic Orb 3 (Bottom Center / Pulsing Accent)
                val orb3X = width * (0.45f + 0.25f * sin(t3 * 0.7f))
                val orb3Y = height * (0.85f + 0.12f * cos(t3))
                val orb3Radius = (width * 0.55f) * pulseScale
                val orb3Color = preset.orbColors.getOrElse(2) { Color(0xFF00F2FE) }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            orb3Color.copy(alpha = if (isDark) 0.45f else 0.30f),
                            orb3Color.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(orb3X, orb3Y),
                        radius = orb3Radius
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                )

                // Ambient floating shimmer orb
                val orb4X = width * (0.8f + 0.15f * sin(t1 * 1.2f))
                val orb4Y = height * (0.15f + 0.1f * cos(t2 * 1.1f))
                val orb4Radius = (width * 0.40f) * pulseScale
                val orb4Color = preset.orbColors.getOrElse(3) { Color(0xFFE94057) }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            orb4Color.copy(alpha = if (isDark) 0.40f else 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(orb4X, orb4Y),
                        radius = orb4Radius
                    ),
                    center = Offset(orb4X, orb4Y),
                    radius = orb4Radius
                )
            }
        }

        // Host app content on top of glowing fluid mesh
        content()
    }
}

/**
 * Modifier that applies custom liquid glassmorphism (frosted glass, refraction, specular border, blur)
 */
fun Modifier.liquidGlassEffect(
    blurRadius: Float = 28f,
    opacity: Float = 0.70f,
    tint: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(24.dp),
    hasSpecularShine: Boolean = true,
    isDark: Boolean = true,
    borderWidth: Dp = 1.2.dp,
    highlightColor: Color = Color.White
): Modifier = this.then(
    Modifier
        .graphicsLayer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0f) {
                renderEffect = RenderEffect.createBlurEffect(
                    blurRadius,
                    blurRadius,
                    Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            }
        }
        .drawWithCache {
            onDrawWithContent {
                val effectiveTint = if (tint.isSpecified && tint != Color.Transparent) tint
                else if (isDark) Color(0xFF1E2433)
                else Color(0xFFFFFFFF)

                // 1. Refractive Frosted Gradient Body
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            effectiveTint.copy(alpha = (opacity * (if (isDark) 0.55f else 0.75f)).coerceIn(0f, 1f)),
                            effectiveTint.copy(alpha = (opacity * (if (isDark) 0.35f else 0.50f)).coerceIn(0f, 1f))
                        )
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )

                // 2. Secondary Chromatic Highlight Sheen
                if (hasSpecularShine) {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                highlightColor.copy(alpha = if (isDark) 0.18f else 0.35f),
                                Color.Transparent,
                                effectiveTint.copy(alpha = if (isDark) 0.12f else 0.20f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width * 0.8f, size.height * 0.8f)
                        ),
                        cornerRadius = CornerRadius(24.dp.toPx())
                    )
                }

                // 3. Draw Inner Content
                drawContent()

                // 4. Specular Bevel & Light Rim (Simulating 3D curved glass refraction border)
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            highlightColor.copy(alpha = if (isDark) 0.55f else 0.85f),
                            highlightColor.copy(alpha = if (isDark) 0.15f else 0.30f),
                            Color.Transparent,
                            highlightColor.copy(alpha = if (isDark) 0.20f else 0.40f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    style = Stroke(width = borderWidth.toPx())
                )
            }
        }
        .clip(shape)
)

/**
 * Reusable Liquid Glass Card Surface with customizable glass reflections and spring response
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = Color.Unspecified,
    tintColor: Color = Color.Unspecified,
    opacity: Float = 0.68f,
    blurRadius: Dp = 24.dp,
    shineAlpha: Float = 0.35f,
    borderWidth: Dp = 1.2.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val effectiveTint = if (tintColor.isSpecified && tintColor != Color.Transparent) tintColor else tint

    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "GlassCardPress"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = ripple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        onClick = onClick
                    )
                } else Modifier
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        (if (effectiveTint.isSpecified) effectiveTint else (if (isDark) Color(0xFF1E2230) else Color.White))
                            .copy(alpha = if (isDark) opacity * 0.70f else opacity * 0.85f),
                        (if (effectiveTint.isSpecified) effectiveTint else (if (isDark) Color(0xFF121622) else Color(0xFFF9FAFB)))
                            .copy(alpha = if (isDark) opacity * 0.45f else opacity * 0.60f)
                    )
                ),
                shape = shape
            )
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (if (isDark) 0.40f else 0.75f) * (shineAlpha / 0.35f).coerceIn(0f, 1.5f)),
                        Color.White.copy(alpha = (if (isDark) 0.10f else 0.25f) * (shineAlpha / 0.35f).coerceIn(0f, 1.5f)),
                        Color.Transparent,
                        Color.White.copy(alpha = (if (isDark) 0.20f else 0.45f) * (shineAlpha / 0.35f).coerceIn(0f, 1.5f))
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(800f, 800f)
                ),
                shape = shape
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Liquid Glass Interactive Action Button
 */
@Composable
fun LiquidGlassActionBtn(
    text: String? = null,
    icon: ImageVector? = null,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    isEnabled: Boolean = true,
    shape: Shape = RoundedCornerShape(24.dp)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = isSystemInDarkTheme()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "GlassActionBtnScale"
    )

    val effectiveTint = if (selected) {
        MaterialTheme.colorScheme.primary
    } else if (tint.isSpecified && tint != Color.Transparent) {
        tint
    } else {
        if (isDark) Color(0xFF262C3A) else Color(0xFFF1F5F9)
    }

    val effectiveContentColor = if (selected) {
        Color.White
    } else if (contentColor.isSpecified && contentColor != Color.Transparent) {
        contentColor
    } else {
        if (isDark) Color.White else Color(0xFF1E293B)
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                enabled = isEnabled,
                onClick = onClick
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = if (selected) {
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        listOf(
                            effectiveTint.copy(alpha = if (isDark) 0.65f else 0.85f),
                            effectiveTint.copy(alpha = if (isDark) 0.40f else 0.65f)
                        )
                    }
                ),
                shape = shape
            )
            .border(
                width = 1.3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (selected) 0.80f else 0.60f),
                        Color.White.copy(alpha = 0.20f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.35f)
                    )
                ),
                shape = shape
            )
            .heightIn(min = 44.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = effectiveContentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            if (text != null) {
                Text(
                    text = text,
                    color = effectiveContentColor,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )
            }
        }
    }
}

/**
 * Liquid Glass Circular or Shaped Icon Button
 */
@Composable
fun LiquidGlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    containerTint: Color = Color.Unspecified,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    content: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDark = isSystemInDarkTheme()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "GlassIconBtnScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = tint.copy(alpha = 0.25f)),
                onClick = onClick
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        (if (containerTint.isSpecified) containerTint else (if (isDark) Color(0xFF242A38) else Color.White))
                            .copy(alpha = if (isDark) 0.55f else 0.80f),
                        (if (containerTint.isSpecified) containerTint else (if (isDark) Color(0xFF141924) else Color(0xFFE5E7EB)))
                            .copy(alpha = if (isDark) 0.35f else 0.55f)
                    )
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.50f else 0.80f),
                        Color.White.copy(alpha = 0.10f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.30f)
                    )
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

/**
 * Data item representing navigation tabs
 */
data class LiquidNavTabItem(
    val id: Int,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

/**
 * Liquid Glass Floating Navigation Bar with morphing squishy pill indicator
 */
@Composable
fun LiquidGlassNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val tabShape = RoundedCornerShape(32.dp)

    val tabs = listOf(
        LiquidNavTabItem(0, "Library", Icons.Rounded.LibraryMusic, Icons.Outlined.LibraryMusic),
        LiquidNavTabItem(1, "Folders", Icons.Rounded.Folder, Icons.Outlined.Folder),
        LiquidNavTabItem(2, "Settings", Icons.Rounded.Settings, Icons.Outlined.Settings)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 16.dp, top = 6.dp)
            .height(68.dp)
            .clip(tabShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        (if (isDark) Color(0xFF1C2232) else Color(0xFFFFFFFF)).copy(alpha = if (isDark) 0.70f else 0.85f),
                        (if (isDark) Color(0xFF101420) else Color(0xFFF3F4F6)).copy(alpha = if (isDark) 0.50f else 0.65f)
                    )
                ),
                shape = tabShape
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isDark) 0.50f else 0.85f),
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.35f)
                    )
                ),
                shape = tabShape
            )
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val barWidth = maxWidth
            val tabCount = tabs.size
            val tabWidth = barWidth / tabCount

            val targetFraction by remember(selectedTab) {
                derivedStateOf { selectedTab.toFloat() }
            }
            val animatedFraction by animateFloatAsState(
                targetValue = targetFraction,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "LiquidTabPill"
            )

            val diff = abs(targetFraction - animatedFraction)
            val scaleXFactor = 1f + (diff * 0.25f).coerceIn(0f, 0.35f)
            val scaleYFactor = 1f - (diff * 0.10f).coerceIn(0f, 0.18f)

            // Liquid morphing active pill
            Box(
                modifier = Modifier
                    .offset(
                        x = tabWidth * animatedFraction + (tabWidth * (1f - scaleXFactor) / 2f)
                    )
                    .width(tabWidth * scaleXFactor)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 10.dp)
                    .graphicsLayer {
                        scaleY = scaleYFactor
                    }
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.70f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tabItem ->
                    val isSelected = selectedTab == tabItem.id

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTabSelected(tabItem.id)
                            }
                            .testTag("nav_tab_${tabItem.label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            val iconScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.18f else 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "NavIconScale"
                            )

                            Icon(
                                imageVector = if (isSelected) tabItem.activeIcon else tabItem.inactiveIcon,
                                contentDescription = tabItem.label,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f),
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )

                            AnimatedVisibility(
                                visible = isSelected,
                                enter = expandHorizontally(
                                    expandFrom = Alignment.Start,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(animationSpec = tween(150)),
                                exit = shrinkHorizontally(
                                    shrinkTowards = Alignment.Start,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut(animationSpec = tween(150))
                            ) {
                                Text(
                                    text = tabItem.label,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Liquid Sound Visualizer wave bars
 */
@Composable
fun LiquidAudioWaveVisualizer(
    amplitude: Float,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")

    val phases = List(barCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 350 + (index * 47) % 300,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "WaveBar$index"
        )
    }

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        phases.forEachIndexed { index, animPhase ->
            val normalizedAmp = if (isPlaying) (amplitude * 2.5f).coerceIn(0.15f, 1f) else 0.1f
            val barHeightFraction = (animPhase.value * normalizedAmp).coerceIn(0.1f, 1f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(barHeightFraction)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                tint,
                                tint.copy(alpha = 0.4f)
                            )
                        )
                    )
            )
        }
    }
}
