package com.melody.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.melody.data.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class FocusMode(val labelAr: String, val labelEn: String, val defaultMinutes: Int, val color: Color) {
    FOCUS("تركيز عميق", "Deep Focus", 25, Color(0xFFE91E63)),
    SHORT_BREAK("راحة قصيرة", "Short Break", 5, Color(0xFF4CAF50)),
    LONG_BREAK("راحة طويلة", "Long Break", 15, Color(0xFF2196F3))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FocusScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel,
    musicViewModel: MusicViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var selectedMode by remember { mutableStateOf(FocusMode.FOCUS) }
    
    // Customized focus run configurations in minutes
    var focusDurationMin by remember { mutableStateOf(25) }
    var shortBreakDurationMin by remember { mutableStateOf(5) }
    var longBreakDurationMin by remember { mutableStateOf(15) }

    // Timer states
    val targetMinutes = when (selectedMode) {
        FocusMode.FOCUS -> focusDurationMin
        FocusMode.SHORT_BREAK -> shortBreakDurationMin
        FocusMode.LONG_BREAK -> longBreakDurationMin
    }
    
    var timeRemainingSec by remember { mutableStateOf(targetMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var totalCompletedCycles by remember { mutableStateOf(0) }
    var showCongratulationAnim by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Sync timer timeRemainingSec whenever mode duration settings change
    LaunchedEffect(targetMinutes, selectedMode) {
        if (!isTimerRunning) {
            timeRemainingSec = targetMinutes * 60
        }
    }

    // Main Timer loop
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeRemainingSec > 0) {
                delay(1000L)
                timeRemainingSec--
            }
            if (timeRemainingSec == 0) {
                isTimerRunning = false
                if (selectedMode == FocusMode.FOCUS) {
                    totalCompletedCycles++
                    showCongratulationAnim = true
                }
                // Toggle mode automatically, like Tomato
                selectedMode = when (selectedMode) {
                    FocusMode.FOCUS -> {
                        if (totalCompletedCycles % 4 == 0) FocusMode.LONG_BREAK else FocusMode.SHORT_BREAK
                    }
                    else -> FocusMode.FOCUS
                }
                timeRemainingSec = (if (selectedMode == FocusMode.FOCUS) focusDurationMin
                else if (selectedMode == FocusMode.SHORT_BREAK) shortBreakDurationMin
                else longBreakDurationMin) * 60
            }
        }
    }

    // Congratulation fade effect
    LaunchedEffect(showCongratulationAnim) {
        if (showCongratulationAnim) {
            delay(4000)
            showCongratulationAnim = false
        }
    }

    val customBackgroundPath by themeViewModel.customBackgroundPath.collectAsState()
    val listOpacity by themeViewModel.listOpacity.collectAsState()
    val listSizing by themeViewModel.listSizing.collectAsState()
    val useCompactLayout by themeViewModel.useCompactLayout.collectAsState()

    val containerBgColor = if (customBackgroundPath != null) {
        MaterialTheme.colorScheme.surface.copy(alpha = listOpacity)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(containerBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = contentPadding.calculateBottomPadding() + 64.dp)
        ) {
            // Elegant top header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Tomato Studio | مساحة التركيز",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Inspired by Tomato Pomodoro Timer design",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                // Stars/Completed session indicators
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stars,
                        contentDescription = "Cycles",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalCompletedCycles",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Congratulation Alert Box
            AnimatedVisibility(
                visible = showCongratulationAnim,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Celebration,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "أحسنت صنعاً! اكتملت جلسة التركيز 🎉",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Great job! Your focus cycle has been logged. Take a quick break now.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GIGANTIC TOMATO COOLDOWN RING CONTAINER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background dynamic breath pulse
                val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.96f,
                    targetValue = 1.04f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseScale"
                )

                // Render dynamic breathing backdrop blob inside circle when timer is running
                if (isTimerRunning) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        selectedMode.color.copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                val totalSec = targetMinutes * 60
                val progressFraction = if (totalSec > 0) timeRemainingSec.toFloat() / totalSec.toFloat() else 0f
                val baseTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

                // Draw deep Canvas clock representation
                Canvas(modifier = Modifier.size(240.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    val diameter = size.width - strokeWidth
                    val sizeRect = Size(diameter, diameter)
                    val offsetRect = Offset(strokeWidth / 2f, strokeWidth / 2f)

                    // Track base circle
                    drawArc(
                        color = selectedMode.color.copy(alpha = 0.08f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = offsetRect,
                        size = sizeRect,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active sweep progress arc
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                selectedMode.color.copy(alpha = 0.6f),
                                selectedMode.color,
                                selectedMode.color.copy(alpha = 0.8f),
                                selectedMode.color.copy(alpha = 0.6f)
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        topLeft = offsetRect,
                        size = sizeRect,
                        style = Stroke(width = strokeWidth + 1.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw 60 surrounding clock tick metrics
                    val center = size.width / 2f
                    val outerRadius = diameter / 2f + 4.dp.toPx()
                    val innerRadius = outerRadius - 6.dp.toPx()

                    for (i in 0 until 60) {
                        val angleDeg = (i * 6).toFloat()
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        
                        val isTickActive = (i.toFloat() / 60.0f) <= (1.0f - progressFraction)
                        val tickColor = if (isTickActive) {
                            selectedMode.color.copy(alpha = 0.8f)
                        } else {
                            baseTickColor
                        }
                        
                        val tickWidth = if (i % 5 == 0) 2.5.dp.toPx() else 1.dp.toPx()
                        val lengthMultiplier = if (i % 5 == 0) 1.5f else 1.0f
                        
                        val startX = (center + innerRadius * cos(angleRad)).toFloat()
                        val startY = (center + innerRadius * sin(angleRad)).toFloat()
                        val endX = (center + (innerRadius + (outerRadius - innerRadius) * lengthMultiplier) * cos(angleRad)).toFloat()
                        val endY = (center + (innerRadius + (outerRadius - innerRadius) * lengthMultiplier) * sin(angleRad)).toFloat()

                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = tickWidth
                        )
                    }
                }

                // Internal text / layout counting timer
                Column(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val minutesRemaining = timeRemainingSec / 60
                    val secondsRemaining = timeRemainingSec % 60
                    val formattedTime = String.format("%02d:%02d", minutesRemaining, secondsRemaining)

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Mode Pill indicator
                    Box(
                        modifier = Modifier
                            .background(
                                color = selectedMode.color.copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                            .border(width = 1.dp, color = selectedMode.color.copy(alpha = 0.3f), shape = CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (LocalContext.current.resources.configuration.locales[0].language == "ar") selectedMode.labelAr else selectedMode.labelEn,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = selectedMode.color
                        )
                    }

                    if (isTimerRunning) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (selectedMode == FocusMode.FOCUS) "ركّز بعقد صفاء الذهن" else "خذ قسطاً من الراحة",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Quick Select Focus Mode Buttons (Material 3 Expressive Shapes)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FocusMode.values().forEach { mode ->
                    val isCurrent = selectedMode == mode
                    val shape = if (isCurrent) {
                        RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)
                    } else {
                        RoundedCornerShape(12.dp)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(shape)
                            .clickable {
                                selectedMode = mode
                                isTimerRunning = false
                                timeRemainingSec = (if (mode == FocusMode.FOCUS) focusDurationMin
                                else if (mode == FocusMode.SHORT_BREAK) shortBreakDurationMin
                                else longBreakDurationMin) * 60
                            }
                            .background(
                                if (isCurrent) mode.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                            .border(
                                width = if (isCurrent) 1.5.dp else 1.dp,
                                color = if (isCurrent) mode.color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                shape = shape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (LocalContext.current.resources.configuration.locales[0].language == "ar") mode.labelAr else mode.labelEn,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Medium
                            ),
                            color = if (isCurrent) mode.color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MAIN INTERACTIVE SLIDING CONTROL BUTTONS Group
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RESET BUTTON
                IconButton(
                    onClick = {
                        isTimerRunning = false
                        timeRemainingSec = targetMinutes * 60
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // PLAY / PAUSE FLOAT BUTTON (Liquified style responsive)
                val playButtonShape = RoundedCornerShape(24.dp)
                val scaleFactor by animateFloatAsState(
                    targetValue = if (isTimerRunning) 0.95f else 1.05f,
                    label = "PlayBtnScale"
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                        }
                        .clip(playButtonShape)
                        .clickable { isTimerRunning = !isTimerRunning }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    selectedMode.color,
                                    selectedMode.color.copy(alpha = 0.85f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Control",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(28.dp))

                // SKIP BUTTON
                IconButton(
                    onClick = {
                        isTimerRunning = false
                        selectedMode = when (selectedMode) {
                            FocusMode.FOCUS -> FocusMode.SHORT_BREAK
                            FocusMode.SHORT_BREAK -> FocusMode.FOCUS
                            FocusMode.LONG_BREAK -> FocusMode.FOCUS
                        }
                        timeRemainingSec = (if (selectedMode == FocusMode.FOCUS) focusDurationMin
                        else if (selectedMode == FocusMode.SHORT_BREAK) shortBreakDurationMin
                        else longBreakDurationMin) * 60
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Skip Session",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // TIME SETTINGS / EXPRESSIVE CONTROLS BLOCK
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(16.dp)
                            .background(selectedMode.color, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Customize Focus Periods | إعداد فترات التركيز",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Slider 1: Focus duration
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Duration / فترة التركيز", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$focusDurationMin mins", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = FocusMode.FOCUS.color))
                    }
                    Slider(
                        value = focusDurationMin.toFloat(),
                        onValueChange = { 
                            if (!isTimerRunning) {
                                focusDurationMin = it.toInt()
                            }
                        },
                        valueRange = 10f..60f,
                        colors = SliderDefaults.colors(
                            thumbColor = FocusMode.FOCUS.color,
                            activeTrackColor = FocusMode.FOCUS.color,
                            inactiveTrackColor = FocusMode.FOCUS.color.copy(alpha = 0.2f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Slider 2: Break duration
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Short Break / استراحة قصيرة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$shortBreakDurationMin mins", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = FocusMode.SHORT_BREAK.color))
                    }
                    Slider(
                        value = shortBreakDurationMin.toFloat(),
                        onValueChange = { 
                            if (!isTimerRunning) {
                                shortBreakDurationMin = it.toInt()
                            }
                        },
                        valueRange = 2f..15f,
                        colors = SliderDefaults.colors(
                            thumbColor = FocusMode.SHORT_BREAK.color,
                            activeTrackColor = FocusMode.SHORT_BREAK.color,
                            inactiveTrackColor = FocusMode.SHORT_BREAK.color.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MUSIC CONTROL COMPANION (MELODY INTEGRATION)
            val currentSong by musicViewModel.currentSong.collectAsState()
            val isPlaying by musicViewModel.isPlaying.collectAsState()
            val albumArtShape = RoundedCornerShape(12.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focus Companion Music | مرافقة الموسيقى",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (currentSong != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = albumArtShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSong!!.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentSong!!.artist,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        // Playback fast buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { musicViewModel.playPrevious() }) {
                                Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous")
                            }
                            IconButton(
                                onClick = { musicViewModel.togglePlayback() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = { musicViewModel.playNext() }) {
                                Icon(Icons.Rounded.SkipNext, contentDescription = "Next")
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs playing. Select music in Library tab.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
