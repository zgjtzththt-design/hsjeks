package com.melody.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class OnboardingFeatureItem(
    val icon: ImageVector,
    val text: String
)

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColors: List<Color>,
    val features: List<OnboardingFeatureItem>
)

@Composable
fun WelcomeScreen(
    onFinishOnboarding: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val coroutineScope = rememberCoroutineScope()

    val pages = remember {
        listOf(
            OnboardingPageData(
                title = "مرحباً بك في Melody",
                subtitle = "تجربة موسيقية استثنائية",
                description = "مشغل الموسيقى الأسرع والأكثر أناقة لجهازك. استمتع بأغانيك المفضلة بصوت فائق الدقة بدون أي قيود أو إعلانات.",
                icon = Icons.Rounded.MusicNote,
                accentColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFD946EF)),
                features = listOf(
                    OnboardingFeatureItem(Icons.Rounded.Bolt, "تشغيل فوري عالي السرعة"),
                    OnboardingFeatureItem(Icons.Rounded.OfflinePin, "تشغيل كامل بدون إنترنت"),
                    OnboardingFeatureItem(Icons.Rounded.HighQuality, "دقة صوت عالية Hi-Res")
                )
            ),
            OnboardingPageData(
                title = "معادل احترافي ومؤثرات حية",
                subtitle = "تحكم صوتي مخصص بالكامل",
                description = "معادل رقمي 5-Band متقدم، تضخيم الجهير Bass Boost، ومؤثرات محيطية 3D واقعية تمنحك إحساس استوديو احترافي.",
                icon = Icons.Rounded.GraphicEq,
                accentColors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF6366F1)),
                features = listOf(
                    OnboardingFeatureItem(Icons.Rounded.Equalizer, "معادل 5 ترددات مع رسوم حية"),
                    OnboardingFeatureItem(Icons.Rounded.Speaker, "مضخم جهير فائق Bass Boost"),
                    OnboardingFeatureItem(Icons.Rounded.SurroundSound, "صوت محيطي تفاعلي 3D")
                )
            ),
            OnboardingPageData(
                title = "تصميم زجاجي فاخر وانسيابي",
                subtitle = "سلاسة وتخصيص لا مثيل لهما",
                description = "واجهة سائلة مستوحاة من Liquid Glass مع مؤثرات بصرية متفاعلة مع الإيقاع وتخصيص شامل لجميع التفاصيل.",
                icon = Icons.Rounded.AutoAwesome,
                accentColors = listOf(Color(0xFFEC4899), Color(0xFFF43F5E), Color(0xFFFB923C)),
                features = listOf(
                    OnboardingFeatureItem(Icons.Rounded.Palette, "سمات زجاجية وإضاءات محيطية"),
                    OnboardingFeatureItem(Icons.Rounded.Album, "أشكال فنية مخصصة للألبومات"),
                    OnboardingFeatureItem(Icons.Rounded.Speed, "رسوم متحركة فائقة السلاسة")
                )
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val currentPage = pagerState.currentPage
    val activeColor = pages[currentPage].accentColors.first()

    Scaffold(
        containerColor = if (isDark) Color(0xFF0B0E17) else Color(0xFFF8FAFC),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.06f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        (if (isDark) Color.White else Color.Black).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Audiotrack,
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Melody",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Skip Button
                AnimatedVisibility(
                    visible = currentPage < pages.size - 1,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.05f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onFinishOnboarding() }
                            .testTag("skip_onboarding_button")
                    ) {
                        Text(
                            text = "تخطي",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Expanding Pill Indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 32.dp else 8.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "IndicatorWidth"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) activeColor
                            else (if (isDark) Color.White else Color.Black).copy(alpha = 0.15f),
                            animationSpec = tween(400),
                            label = "IndicatorColor"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                // Action Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back Button (if page > 0)
                    AnimatedVisibility(
                        visible = currentPage > 0,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(currentPage - 1)
                                    }
                                }
                                .border(
                                    1.dp,
                                    (if (isDark) Color.White else Color.Black).copy(alpha = 0.12f),
                                    CircleShape
                                ),
                            shape = CircleShape,
                            color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.06f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "السابق",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // Main Action Button (Next / Get Started)
                    val isLastPage = currentPage == pages.size - 1
                    val buttonInteraction = remember { MutableInteractionSource() }
                    val isPressed by buttonInteraction.collectIsPressedAsState()
                    val buttonScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.96f else 1f,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                        label = "BtnScale"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .graphicsLayer {
                                scaleX = buttonScale
                                scaleY = buttonScale
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = buttonInteraction,
                                indication = ripple(color = Color.White.copy(alpha = 0.35f)),
                                onClick = {
                                    if (currentPage < pages.size - 1) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(currentPage + 1)
                                        }
                                    } else {
                                        onFinishOnboarding()
                                    }
                                }
                            )
                            .testTag("onboarding_primary_button"),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = pages[currentPage].accentColors
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.7f),
                                            Color.White.copy(alpha = 0.15f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AnimatedContent(
                                    targetState = isLastPage,
                                    transitionSpec = {
                                        (fadeIn(tween(300)) + slideInVertically { it / 2 })
                                            .togetherWith(fadeOut(tween(200)) + slideOutVertically { -it / 2 })
                                    },
                                    label = "ButtonTextAnim"
                                ) { lastPage ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (lastPage) "ابدأ الاستماع الآن" else "التالي",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (lastPage) Icons.Rounded.PlayArrow else Icons.AutoMirrored.Rounded.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ambient Aura Background
            WelcomeAmbientBackground(
                colors = pages[currentPage].accentColors,
                isDark = isDark,
                modifier = Modifier.fillMaxSize()
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val page = pages[pageIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Floating Animated Hero Icon Card
                    WelcomeHeroGraphic(
                        icon = page.icon,
                        accentColors = page.accentColors,
                        isDark = isDark,
                        modifier = Modifier.size(170.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Subtitle Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = page.accentColors.first().copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            page.accentColors.first().copy(alpha = 0.35f)
                        )
                    ) {
                        Text(
                            text = page.subtitle,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = page.accentColors.first(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            lineHeight = 34.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description
                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp,
                            fontSize = 14.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Feature Chips Row
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        page.features.forEach { feature ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = (if (isDark) Color(0xFF1E2433) else Color.White).copy(alpha = if (isDark) 0.6f else 0.85f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    (if (isDark) Color.White else Color.Black).copy(alpha = 0.08f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = page.accentColors.first().copy(alpha = 0.16f),
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = feature.icon,
                                                contentDescription = null,
                                                tint = page.accentColors.first(),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = feature.text,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeHeroGraphic(
    icon: ImageVector,
    accentColors: List<Color>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HeroAnim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotateGlow"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glowing Outer Ambient Rings
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    rotationZ = rotateAngle
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.1f

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = accentColors + accentColors.first()
                ),
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                alpha = 0.6f
            )

            // Outer subtle dots
            val dotCount = 6
            for (i in 0 until dotCount) {
                val angleRad = (i * (360f / dotCount)) * (Math.PI / 180.0)
                val dotX = center.x + (radius * cos(angleRad)).toFloat()
                val dotY = center.y + (radius * sin(angleRad)).toFloat()
                drawCircle(
                    color = accentColors.first(),
                    radius = 4.dp.toPx(),
                    center = Offset(dotX, dotY)
                )
            }
        }

        // Inner Glass Surface with Icon
        Surface(
            modifier = Modifier
                .size(125.dp)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color.White.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            shape = CircleShape,
            color = (if (isDark) Color(0xFF161B29) else Color.White).copy(alpha = if (isDark) 0.85f else 0.95f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColors.first().copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = accentColors.first()
                )
            }
        }
    }
}

@Composable
fun WelcomeAmbientBackground(
    colors: List<Color>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BgOrbs")
    val orbOffset1 by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb1"
    )
    val orbOffset2 by infiniteTransition.animateFloat(
        initialValue = 25f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Orb2"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Top-right glowing orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.first().copy(alpha = if (isDark) 0.28f else 0.18f),
                    Color.Transparent
                ),
                center = Offset(w * 0.85f + orbOffset1, h * 0.25f + orbOffset2),
                radius = w * 0.55f
            ),
            center = Offset(w * 0.85f + orbOffset1, h * 0.25f + orbOffset2),
            radius = w * 0.55f
        )

        // Bottom-left secondary orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.last().copy(alpha = if (isDark) 0.20f else 0.14f),
                    Color.Transparent
                ),
                center = Offset(w * 0.15f - orbOffset2, h * 0.70f + orbOffset1),
                radius = w * 0.50f
            ),
            center = Offset(w * 0.15f - orbOffset2, h * 0.70f + orbOffset1),
            radius = w * 0.50f
        )
    }
}
