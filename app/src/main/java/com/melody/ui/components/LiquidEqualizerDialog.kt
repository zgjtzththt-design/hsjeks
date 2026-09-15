package com.melody.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class EqualizerPreset(
    val name: String,
    val nameAr: String,
    val bass: Float,
    val mid: Float,
    val treble: Float,
    val virtualizer: Float
)

val EqualizerPresets = listOf(
    EqualizerPreset("Glass Acoustic", "صوت زجاجي نقي", 0.3f, 0.6f, 0.8f, 0.4f),
    EqualizerPreset("Bass Boom", "مضخم صوت جهوري", 0.9f, 0.4f, 0.3f, 0.5f),
    EqualizerPreset("Vocal Clarity", "وضوح الصوت البشري", 0.2f, 0.85f, 0.6f, 0.2f),
    EqualizerPreset("Electronic", "موسيقى إلكترونية", 0.8f, 0.5f, 0.85f, 0.7f),
    EqualizerPreset("Club Live", "أجواء صالة الحفلات", 0.75f, 0.65f, 0.7f, 0.9f)
)

@Composable
fun LiquidEqualizerDialog(
    onDismiss: () -> Unit,
    currentGainBoost: Int = 100,
    onGainChange: (Int) -> Unit = {}
) {
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    val currentPreset = EqualizerPresets[selectedPresetIndex]

    var bassLevel by remember(selectedPresetIndex) { mutableFloatStateOf(currentPreset.bass) }
    var midLevel by remember(selectedPresetIndex) { mutableFloatStateOf(currentPreset.mid) }
    var trebleLevel by remember(selectedPresetIndex) { mutableFloatStateOf(currentPreset.treble) }
    var spatialSurround by remember(selectedPresetIndex) { mutableFloatStateOf(currentPreset.virtualizer) }
    var extraGain by remember { mutableIntStateOf(currentGainBoost) }

    val isDark = isSystemInDarkTheme()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            (if (isDark) Color(0xFF1E2436) else Color.White).copy(alpha = if (isDark) 0.88f else 0.95f),
                            (if (isDark) Color(0xFF121624) else Color(0xFFF3F4F6)).copy(alpha = if (isDark) 0.80f else 0.90f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.20f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.40f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            modifier = Modifier.size(44.dp)
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
                                text = "معادل الصوت الزجاجي",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Liquid Sound Equalizer & DSP",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LiquidGlassIconButton(
                        onClick = onDismiss,
                        icon = Icons.Rounded.Close,
                        size = 36.dp,
                        iconSize = 20.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Presets horizontal selector
                Text(
                    text = "الأنماط الصوتية المسبقة",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(EqualizerPresets.indices.toList()) { index ->
                        val preset = EqualizerPresets[index]
                        val isSelected = selectedPresetIndex == index
                        val bgTint by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f),
                            label = "PresetBg"
                        )

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = bgTint,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    selectedPresetIndex = index
                                }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                        ) {
                            Text(
                                text = "${preset.nameAr} (${preset.name})",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive 3-Band Equalizer Sliders
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        EqualizerSliderRow(
                            title = "جهير فائق (Bass Boost)",
                            value = bassLevel,
                            onValueChange = { bassLevel = it },
                            icon = Icons.Rounded.GraphicEq
                        )

                        EqualizerSliderRow(
                            title = "نقاء الترددات المتوسطة (Midrange)",
                            value = midLevel,
                            onValueChange = { midLevel = it },
                            icon = Icons.Rounded.Tune
                        )

                        EqualizerSliderRow(
                            title = "حدة الأصوات العالية (Treble)",
                            value = trebleLevel,
                            onValueChange = { trebleLevel = it },
                            icon = Icons.Rounded.Audiotrack
                        )

                        EqualizerSliderRow(
                            title = "صوت محيطي 3D (Spatial Glass)",
                            value = spatialSurround,
                            onValueChange = { spatialSurround = it },
                            icon = Icons.Rounded.SurroundSound
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Loudness Boost up to 300%
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "مضخم القدرة (Volume Boost)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "زيادة علو الصوت حتى 300% مع تحسين ديناميكي",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "${extraGain}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (extraGain > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = extraGain.toFloat(),
                        onValueChange = {
                            extraGain = it.toInt()
                            onGainChange(extraGain)
                        },
                        valueRange = 0f..300f,
                        steps = 29
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LiquidGlassActionBtn(
                    onClick = onDismiss,
                    text = "تطبيق وحفظ الإعدادات",
                    icon = Icons.Rounded.Check,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun EqualizerSliderRow(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
