package com.melody.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.melody.ui.MusicViewModel
import kotlin.math.roundToInt

data class AudioBand(
    val name: String,
    val frequencyLabel: String,
    val subLabel: String
)

val EqualizerBands = listOf(
    AudioBand("Sub-Bass", "60 Hz", "Sub"),
    AudioBand("Low-Mid", "230 Hz", "Bass"),
    AudioBand("Midrange", "910 Hz", "Mid"),
    AudioBand("High-Mid", "3.6 kHz", "Presence"),
    AudioBand("Treble", "14 kHz", "Air")
)

data class EqualizerPreset(
    val name: String,
    val nameAr: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val bands: List<Float>, // dB values for each of 5 bands (-12dB to +12dB)
    val bassBoost: Float,    // 0f..1f
    val virtualizer: Float   // 0f..1f
)

val EqualizerPresets = listOf(
    EqualizerPreset(
        name = "Flat",
        nameAr = "مسطح / استوديو",
        icon = Icons.Rounded.GraphicEq,
        bands = listOf(0f, 0f, 0f, 0f, 0f),
        bassBoost = 0.0f,
        virtualizer = 0.0f
    ),
    EqualizerPreset(
        name = "Bass Boost",
        nameAr = "مضخم الجهير",
        icon = Icons.Rounded.Speaker,
        bands = listOf(9f, 6.5f, 1f, -1f, -2f),
        bassBoost = 0.85f,
        virtualizer = 0.35f
    ),
    EqualizerPreset(
        name = "Treble Boost",
        nameAr = "نقاء الترددات العالية",
        icon = Icons.Rounded.Hearing,
        bands = listOf(-2f, -1f, 2f, 6.5f, 9.5f),
        bassBoost = 0.1f,
        virtualizer = 0.3f
    ),
    EqualizerPreset(
        name = "Rock",
        nameAr = "روك ديناميكي",
        icon = Icons.Rounded.ElectricBolt,
        bands = listOf(6.5f, 3.5f, -2f, 4f, 7f),
        bassBoost = 0.6f,
        virtualizer = 0.45f
    ),
    EqualizerPreset(
        name = "Pop",
        nameAr = "بوب ناصع",
        icon = Icons.Rounded.Celebration,
        bands = listOf(-1.5f, 2.5f, 6.5f, 3f, -1f),
        bassBoost = 0.4f,
        virtualizer = 0.3f
    ),
    EqualizerPreset(
        name = "Electronic",
        nameAr = "إلكترونيك ودانـس",
        icon = Icons.Rounded.Bolt,
        bands = listOf(8.5f, 5f, 0f, 4.5f, 7.5f),
        bassBoost = 0.8f,
        virtualizer = 0.6f
    ),
    EqualizerPreset(
        name = "Classical",
        nameAr = "أوركسترا كلاسيك",
        icon = Icons.Rounded.Piano,
        bands = listOf(4.5f, 3f, -1f, 3f, 5f),
        bassBoost = 0.2f,
        virtualizer = 0.5f
    ),
    EqualizerPreset(
        name = "Jazz",
        nameAr = "جاز دافئ",
        icon = Icons.Rounded.Nightlife,
        bands = listOf(3.5f, 1.5f, -1.5f, 2f, 4.5f),
        bassBoost = 0.35f,
        virtualizer = 0.4f
    ),
    EqualizerPreset(
        name = "Vocal Clarity",
        nameAr = "نقاء الصوت البشري",
        icon = Icons.Rounded.RecordVoiceOver,
        bands = listOf(-3.5f, 1f, 7.5f, 4.5f, 1f),
        bassBoost = 0.15f,
        virtualizer = 0.2f
    ),
    EqualizerPreset(
        name = "Acoustic",
        nameAr = "صوت أكوستيك طبيعي",
        icon = Icons.Rounded.Audiotrack,
        bands = listOf(3.5f, 2.5f, 1f, 3.5f, 4f),
        bassBoost = 0.25f,
        virtualizer = 0.3f
    ),
    EqualizerPreset(
        name = "Custom",
        nameAr = "تخصيص يدوي",
        icon = Icons.Rounded.Tune,
        bands = listOf(0f, 0f, 0f, 0f, 0f),
        bassBoost = 0.3f,
        virtualizer = 0.2f
    )
)

@Composable
fun LiquidEqualizerDialog(
    onDismiss: () -> Unit,
    musicViewModel: MusicViewModel? = null,
    currentGainBoost: Int = 100,
    onGainChange: (Int) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()

    // Collect from ViewModel if available, or maintain local state
    val vmEqualizerEnabled = musicViewModel?.equalizerEnabled?.collectAsState()
    val vmPresetIndex = musicViewModel?.equalizerPresetIndex?.collectAsState()
    val vmBandLevels = musicViewModel?.bandLevels?.collectAsState()
    val vmBassBoost = musicViewModel?.bassBoostLevel?.collectAsState()
    val vmVirtualizer = musicViewModel?.virtualizerLevel?.collectAsState()

    var isEnabled by remember { mutableStateOf(vmEqualizerEnabled?.value ?: true) }
    var selectedPresetIndex by remember { mutableIntStateOf(vmPresetIndex?.value ?: 0) }
    var bandLevels by remember { mutableStateOf(vmBandLevels?.value ?: listOf(0f, 0f, 0f, 0f, 0f)) }
    var bassBoost by remember { mutableFloatStateOf(vmBassBoost?.value ?: 0.3f) }
    var virtualizer by remember { mutableFloatStateOf(vmVirtualizer?.value ?: 0.2f) }
    var extraGain by remember { mutableIntStateOf(currentGainBoost) }

    // Sync from ViewModel updates
    LaunchedEffect(vmBandLevels?.value) {
        vmBandLevels?.value?.let { bandLevels = it }
    }
    LaunchedEffect(vmPresetIndex?.value) {
        vmPresetIndex?.value?.let { selectedPresetIndex = it }
    }
    LaunchedEffect(vmEqualizerEnabled?.value) {
        vmEqualizerEnabled?.value?.let { isEnabled = it }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            (if (isDark) Color(0xFF161B2B) else Color.White).copy(alpha = if (isDark) 0.94f else 0.98f),
                            (if (isDark) Color(0xFF0D111E) else Color(0xFFF1F5F9)).copy(alpha = if (isDark) 0.92f else 0.95f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar: Title, Power Switch, Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Equalizer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "معادل الصوت الرقمي",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "5-Band Visual DSP Equalizer",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // EQ Power Switch
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { checked ->
                                isEnabled = checked
                                musicViewModel?.setEqualizerEnabled(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("equalizer_toggle_switch")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Close",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Presets Horizontal Carousel
                Text(
                    text = "الأنماط الصوتية المسبقة (Presets)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(EqualizerPresets) { index, preset ->
                        val isSelected = selectedPresetIndex == index
                        val bgTint by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                            else if (isDark) Color.White.copy(alpha = 0.08f)
                            else Color.Black.copy(alpha = 0.05f),
                            label = "PresetBg"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White
                            else MaterialTheme.colorScheme.onSurface,
                            label = "PresetContent"
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = bgTint,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedPresetIndex = index
                                    if (index < EqualizerPresets.size - 1) {
                                        bandLevels = preset.bands
                                        bassBoost = preset.bassBoost
                                        virtualizer = preset.virtualizer
                                        musicViewModel?.setEqualizerPreset(
                                            presetIndex = index,
                                            bands = preset.bands,
                                            bass = preset.bassBoost,
                                            virtualizer = preset.virtualizer
                                        )
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    else if (isDark) Color.White.copy(alpha = 0.12f)
                                    else Color.Black.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = contentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = contentColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Visual Frequency Response Curve Display
                VisualFrequencyCurve(
                    bandLevels = bandLevels,
                    isEnabled = isEnabled,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5-Band Vertical Equalizer Faders
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1F2538).copy(alpha = 0.65f)
                        else Color.White.copy(alpha = 0.7f)
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ترددات الصوت (Frequency Sliders)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            TextButton(
                                onClick = {
                                    val flat = listOf(0f, 0f, 0f, 0f, 0f)
                                    bandLevels = flat
                                    selectedPresetIndex = 0
                                    musicViewModel?.resetEqualizer()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إعادة تعيين", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EqualizerBands.forEachIndexed { index, band ->
                                val level = bandLevels.getOrElse(index) { 0f }
                                VerticalBandFader(
                                    band = band,
                                    levelDb = level,
                                    isEnabled = isEnabled,
                                    onLevelChange = { newDb ->
                                        val updated = bandLevels.toMutableList()
                                        updated[index] = newDb
                                        bandLevels = updated
                                        selectedPresetIndex = EqualizerPresets.size - 1 // Custom
                                        musicViewModel?.setBandLevel(index, newDb)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sound Effects: Bass Boost & Spatial Surround (Virtualizer)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bass Boost Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1F2538).copy(alpha = 0.65f)
                            else Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Speaker,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("جهير فائق", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Text(
                                    "${(bassBoost * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = bassBoost,
                                onValueChange = {
                                    bassBoost = it
                                    selectedPresetIndex = EqualizerPresets.size - 1 // Custom
                                    musicViewModel?.setBassBoost(it)
                                },
                                valueRange = 0f..1f,
                                enabled = isEnabled,
                                modifier = Modifier.fillMaxWidth().testTag("bass_boost_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    // Spatial Surround Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1F2538).copy(alpha = 0.65f)
                            else Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.SurroundSound,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("صوت محيطي 3D", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Text(
                                    "${(virtualizer * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = virtualizer,
                                onValueChange = {
                                    virtualizer = it
                                    selectedPresetIndex = EqualizerPresets.size - 1 // Custom
                                    musicViewModel?.setVirtualizer(it)
                                },
                                valueRange = 0f..1f,
                                enabled = isEnabled,
                                modifier = Modifier.fillMaxWidth().testTag("virtualizer_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.secondary,
                                    activeTrackColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Loudness Boost up to 300%
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1F2538).copy(alpha = 0.65f)
                        else Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.VolumeUp,
                                    contentDescription = null,
                                    tint = if (extraGain > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "مضخم القدرة (Volume Boost)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "زيادة القدرة الصوتية حتى 300%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${extraGain}%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (extraGain > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Slider(
                            value = extraGain.toFloat(),
                            onValueChange = {
                                extraGain = it.toInt()
                                onGainChange(extraGain)
                            },
                            valueRange = 0f..300f,
                            steps = 29,
                            modifier = Modifier.fillMaxWidth().testTag("volume_boost_slider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save & Apply Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_equalizer_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "تطبيق وإغلاق",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun VisualFrequencyCurve(
    bandLevels: List<Float>,
    isEnabled: Boolean,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val animatedLevels = bandLevels.map { level ->
        animateFloatAsState(
            targetValue = if (isEnabled) level else 0f,
            animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
            label = "CurveLevel"
        ).value
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDark) listOf(Color(0xFF141929), Color(0xFF0C0F1B))
                    else listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                )
            )
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            // Draw dB horizontal grid lines
            val gridSteps = listOf(
                -12f to "-12",
                -6f to "-6",
                0f to "0 dB",
                6f to "+6",
                12f to "+12"
            )
            val dbRange = 24f // from -12 to +12

            gridSteps.forEach { (db, _) ->
                // Map dB to Y: -12dB -> height - padding, +12dB -> padding
                val y = midY - (db / 12f) * (height * 0.42f)
                drawLine(
                    color = if (db == 0f) primaryColor.copy(alpha = 0.4f)
                    else if (isDark) Color.White.copy(alpha = 0.08f)
                    else Color.Black.copy(alpha = 0.08f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = if (db == 0f) 1.5f else 0.8f
                )
            }

            // Calculate points for the 5 bands
            val pointCount = animatedLevels.size
            val xStep = width / (pointCount + 1)
            val points = mutableListOf<Offset>()

            // Start off-screen anchor
            val firstY = midY - (animatedLevels.first() / 12f) * (height * 0.42f)
            points.add(Offset(0f, firstY))

            for (i in 0 until pointCount) {
                val x = xStep * (i + 1)
                val y = midY - (animatedLevels[i] / 12f) * (height * 0.42f)
                points.add(Offset(x, y))
            }

            // End off-screen anchor
            val lastY = midY - (animatedLevels.last() / 12f) * (height * 0.42f)
            points.add(Offset(width, lastY))

            // Build smooth Bezier path
            val path = Path()
            val fillPath = Path()

            path.moveTo(points[0].x, points[0].y)
            fillPath.moveTo(0f, height)
            fillPath.lineTo(points[0].x, points[0].y)

            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val midPointX = (p0.x + p1.x) / 2f
                path.cubicTo(
                    midPointX, p0.y,
                    midPointX, p1.y,
                    p1.x, p1.y
                )
                fillPath.cubicTo(
                    midPointX, p0.y,
                    midPointX, p1.y,
                    p1.x, p1.y
                )
            }

            fillPath.lineTo(width, height)
            fillPath.close()

            // Draw glowing gradient fill under the curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (isEnabled) 0.35f else 0.10f),
                        primaryColor.copy(alpha = if (isEnabled) 0.08f else 0.02f),
                        Color.Transparent
                    )
                )
            )

            // Draw frequency curve stroke
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.9f),
                        primaryColor
                    )
                ),
                style = Stroke(
                    width = if (isEnabled) 3.5f else 2f,
                    cap = StrokeCap.Round
                )
            )

            // Draw frequency node circles
            for (i in 1 until points.size - 1) {
                val pt = points[i]
                // Outer glowing ring
                drawCircle(
                    color = primaryColor.copy(alpha = if (isEnabled) 0.45f else 0.15f),
                    radius = 9f,
                    center = pt
                )
                // Solid center dot
                drawCircle(
                    color = if (isEnabled) Color.White else Color.Gray,
                    radius = 4.5f,
                    center = pt
                )
            }
        }
    }
}

@Composable
fun VerticalBandFader(
    band: AudioBand,
    levelDb: Float,
    isEnabled: Boolean,
    onLevelChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val formattedDb = if (levelDb > 0) "+${"%.1f".format(levelDb)}" else "${"%.1f".format(levelDb)}"

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // dB value indicator badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (levelDb != 0f && isEnabled) primaryColor.copy(alpha = 0.18f)
            else if (isDark) Color.White.copy(alpha = 0.06f)
            else Color.Black.copy(alpha = 0.05f),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = "${formattedDb}dB",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (levelDb != 0f && isEnabled) primaryColor
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Custom Vertical Drag Slider Track
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(95.dp)
                .pointerInput(isEnabled) {
                    if (isEnabled) {
                        detectDragGestures { change, _ ->
                            val y = change.position.y.coerceIn(0f, size.height.toFloat())
                            // 0 at top = +12 dB, height at bottom = -12 dB
                            val fraction = 1f - (y / size.height.toFloat())
                            val newDb = (fraction * 24f) - 12f
                            onLevelChange(newDb.coerceIn(-12f, 12f))
                            change.consume()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val trackWidth = 8.dp.toPx()
                val centerX = size.width / 2f
                val topY = 6.dp.toPx()
                val bottomY = size.height - 6.dp.toPx()
                val trackHeight = bottomY - topY
                val midY = topY + trackHeight / 2f

                // Inactive track background
                drawLine(
                    color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.1f),
                    start = Offset(centerX, topY),
                    end = Offset(centerX, bottomY),
                    strokeWidth = trackWidth,
                    cap = StrokeCap.Round
                )

                // 0 dB center tick mark
                drawLine(
                    color = if (isDark) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.25f),
                    start = Offset(centerX - 10.dp.toPx(), midY),
                    end = Offset(centerX + 10.dp.toPx(), midY),
                    strokeWidth = 2f
                )

                // Active level thumb position
                // normalized level: -12 dB -> 0f, 0 dB -> 0.5f, +12 dB -> 1f
                val normLevel = ((levelDb + 12f) / 24f).coerceIn(0f, 1f)
                val thumbY = bottomY - (normLevel * trackHeight)

                // Active filled segment from center
                if (isEnabled && levelDb != 0f) {
                    drawLine(
                        color = primaryColor,
                        start = Offset(centerX, midY),
                        end = Offset(centerX, thumbY),
                        strokeWidth = trackWidth,
                        cap = StrokeCap.Round
                    )
                }

                // Thumb handle
                drawCircle(
                    color = if (isEnabled) primaryColor else Color.Gray,
                    radius = 9.dp.toPx(),
                    center = Offset(centerX, thumbY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(centerX, thumbY)
                )
            }
        }

        // Frequency Label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = band.frequencyLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = band.subLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
