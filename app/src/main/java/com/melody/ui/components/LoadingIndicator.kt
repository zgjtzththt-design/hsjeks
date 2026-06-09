package com.melody.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "expressive_pulse")

    // Continuous time variable for wave-like deforming phases
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Fluid pulsation for scale
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scalePulse"
    )

    // Typing dots builder
    val dotCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )
    val dots = ".".repeat(dotCount)

    // Smoothly rotating morphing indicators
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val primaryContainer = MaterialTheme.colorScheme.primaryContainer

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2
                val centerY = height / 2
                
                // --- LAYER 1: Deep Background Morphing Blob (Pulsating and Counter-rotating) ---
                val rBase1 = minOf(width, height) * 0.38f * scalePulse
                val c1_bg = sin(time.toDouble()).toFloat() * 16f
                val c2_bg = cos((time * 1.3f).toDouble()).toFloat() * 14f
                val c3_bg = sin((time * 0.7f).toDouble()).toFloat() * 12f

                val pathBg = Path().apply {
                    val step = 3
                    for (angleDegrees in 0..360 step step) {
                        val angleRad = Math.toRadians(angleDegrees.toDouble()).toFloat()
                        val sampleAngle = angleRad - (time * 0.18f) // slow counter rotate

                        // Dynamic modulation of the circle boundary
                        val rMod = c1_bg * sin(2.0 * angleRad).toFloat() + 
                                   c2_bg * cos(3.0 * angleRad).toFloat() + 
                                   c3_bg * sin(5.0 * angleRad).toFloat()
                        val currRadius = rBase1 + rMod

                        val x = centerX + currRadius * cos(sampleAngle)
                        val y = centerY + currRadius * sin(sampleAngle)

                        if (angleDegrees == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }

                drawPath(
                    path = pathBg,
                    color = secondaryColor.copy(alpha = 0.22f)
                )

                // --- LAYER 2: Main Foreground Morphing Blob (Highly Vibrant and Dynamic) ---
                val rBase2 = minOf(width, height) * 0.32f * scalePulse
                val c1_fg = sin((time + 1f).toDouble()).toFloat() * 20f
                val c2_fg = cos((time * 1.5f + 2f).toDouble()).toFloat() * 16f
                val c3_fg = sin((time * 0.9f).toDouble()).toFloat() * 14f

                val pathFg = Path().apply {
                    val step = 2
                    for (angleDegrees in 0..360 step step) {
                        val angleRad = Math.toRadians(angleDegrees.toDouble()).toFloat()
                        val sampleAngle = angleRad + (time * 0.25f) // slow forward rotate

                        // Organic Star/Flower/Squircle deformation pattern
                        val rMod = c1_fg * sin(3.0 * angleRad).toFloat() + 
                                   c2_fg * cos(4.0 * angleRad).toFloat() + 
                                   c3_fg * sin(2.0 * angleRad).toFloat()
                        val currRadius = rBase2 + rMod

                        val x = centerX + currRadius * cos(sampleAngle)
                        val y = centerY + currRadius * sin(sampleAngle)

                        if (angleDegrees == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }

                drawPath(
                    path = pathFg,
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor, tertiaryColor),
                        center = Offset(centerX, centerY),
                        radius = rBase2 * 1.4f
                    )
                )
            }

            // Central icon nested inside the morphing blob with soft glowing halo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color.White.copy(alpha = 0.15f), shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = "Melody Note",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // --- Premium Nate-style Capsule Card ---
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(vertical = 16.dp, horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Pulsating music headphone icon as the status accessory
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Headphones,
                        contentDescription = "Audio Processing",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Dynamic Status labels
                Column {
                    Text(
                        text = "Melody | ميلودي",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "جاري تهيئة الاستوديو الخاص بك$dots",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
