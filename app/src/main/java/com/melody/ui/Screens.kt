package com.melody.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.Canvas
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.palette.graphics.Palette
import com.melody.data.Song

val SquircleShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    moveTo(0f, height / 2f)
    cubicTo(0f, 0f, 0f, 0f, width / 2f, 0f)
    cubicTo(width, 0f, width, 0f, width, height / 2f)
    cubicTo(width, height, width, height, width / 2f, height)
    cubicTo(0f, height, 0f, height, 0f, height / 2f)
    close()
}

// Organic Blob Shape
val BlobShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w * 0.2f, h * 0.1f)
    cubicTo(w * 0.5f, 0f, w * 0.9f, h * 0.1f, w * 0.95f, h * 0.45f)
    cubicTo(w, h * 0.8f, w * 0.7f, h, w * 0.4f, h * 0.95f)
    cubicTo(w * 0.1f, h * 0.9f, 0f, h * 0.6f, w * 0.05f, h * 0.3f)
    cubicTo(w * 0.1f, h * 0.15f, w * 0.15f, h * 0.1f, w * 0.2f, h * 0.1f)
    close()
}

val FlowerShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2
    val cy = h / 2
    val r = w / 2
    val petals = 8
    for (i in 0 until 360 step 5) {
        val angle = Math.toRadians(i.toDouble())
        val petalFactor = 0.2 * Math.sin(petals * angle)
        val dr = r * (0.8 + petalFactor)
        val x = cx + dr * Math.cos(angle)
        val y = cy + dr * Math.sin(angle)
        if (i == 0) moveTo(x.toFloat(), y.toFloat())
        else lineTo(x.toFloat(), y.toFloat())
    }
    close()
}

val HeartShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    
    moveTo(width / 2f, height * 0.25f)
    cubicTo(width * 0.2f, height * 0.1f, 0f, height * 0.4f, width * 0.05f, height * 0.6f)
    cubicTo(width * 0.15f, height * 0.9f, width * 0.5f, height, width * 0.5f, height)
    cubicTo(width * 0.5f, height, width * 0.85f, height * 0.9f, width * 0.95f, height * 0.6f)
    cubicTo(width, height * 0.4f, width * 0.8f, height * 0.1f, width / 2f, height * 0.25f)
    close()
}

val WavyBadgeShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2
    val cy = h / 2
    val r = w / 2
    val waves = 12
    for (i in 0 until 360 step 2) {
        val angle = Math.toRadians(i.toDouble())
        val waveFactor = 0.1 * Math.sin(waves * angle)
        val dr = r * (0.9 + waveFactor)
        val x = cx + dr * Math.cos(angle)
        val y = cy + dr * Math.sin(angle)
        if (i == 0) moveTo(x.toFloat(), y.toFloat())
        else lineTo(x.toFloat(), y.toFloat())
    }
    close()
}

val StarShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2
    val cy = h / 2
    val outerRadius = w / 2
    val innerRadius = w / 4
    val points = 5
    for (i in 0 until 2 * points) {
        val angle = Math.toRadians(i * 360.0 / (2 * points) - 90)
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = cx + r * Math.cos(angle)
        val y = cy + r * Math.sin(angle)
        if (i == 0) moveTo(x.toFloat(), y.toFloat())
        else lineTo(x.toFloat(), y.toFloat())
    }
    close()
}

val HexagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2
    val cy = h / 2
    val r = w / 2
    for (i in 0 until 6) {
        val angle = Math.toRadians(i * 60.0 - 90)
        val x = cx + r * Math.cos(angle)
        val y = cy + r * Math.sin(angle)
        if (i == 0) moveTo(x.toFloat(), y.toFloat())
        else lineTo(x.toFloat(), y.toFloat())
    }
    close()
}

val OctagonShape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val cx = w / 2
    val cy = h / 2
    val r = w / 2
    for (i in 0 until 8) {
        val angle = Math.toRadians(i * 45.0 - 67.5)
        val x = cx + r * Math.cos(angle)
        val y = cy + r * Math.sin(angle)
        if (i == 0) moveTo(x.toFloat(), y.toFloat())
        else lineTo(x.toFloat(), y.toFloat())
    }
    close()
}

val FolderShapes = listOf(
    RoundedCornerShape(12.dp),
    SquircleShape,
    BlobShape,
    FlowerShape,
    HeartShape,
    WavyBadgeShape,
    StarShape,
    HexagonShape,
    OctagonShape,
    CutCornerShape(12.dp),
    CircleShape
)

val AlbumArtShapes = listOf(
    RoundedCornerShape(12.dp),
    SquircleShape,
    BlobShape,
    FlowerShape,
    HeartShape,
    WavyBadgeShape,
    StarShape,
    HexagonShape,
    OctagonShape,
    CircleShape,
    RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
    CutCornerShape(12.dp)
)

@Composable
fun MainScreen(
    musicViewModel: MusicViewModel,
    themeViewModel: ThemeViewModel,
    onNavigateToSettings: () -> Unit
) {
    val songs by musicViewModel.songs.collectAsState()
    val searchQuery by musicViewModel.searchQuery.collectAsState()
    val currentSong by musicViewModel.currentSong.collectAsState()
    val isPlaying by musicViewModel.isPlaying.collectAsState()
    val useCompactLayout by themeViewModel.useCompactLayout.collectAsState()
    val albumArtShapeIndex by themeViewModel.albumArtShape.collectAsState()

    val albumArtShape = remember(albumArtShapeIndex) { AlbumArtShapes.getOrElse(albumArtShapeIndex) { AlbumArtShapes[0] } }
    val playlists by musicViewModel.playlists.collectAsState(emptyList())
    val bluetoothDevices by musicViewModel.bluetoothDevices.collectAsState()

    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isEmpty()) songs
        else songs.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.artist.contains(searchQuery, ignoreCase = true) 
        }
    }

            var showPlayer by remember { mutableStateOf(false) }
            var showCastSheet by remember { mutableStateOf(false) }
            
            if (showCastSheet) {
                CastBottomSheet(onDismiss = { showCastSheet = false })
            }

            val pagerState = rememberPagerState(pageCount = { 4 })
            val coroutineScope = rememberCoroutineScope()

            val showVolumeBar by musicViewModel.showVolumeBar.collectAsState()
            val volumeLevel by musicViewModel.volumeLevel.collectAsState()
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = showPlayer && currentSong != null,
                    transitionSpec = {
                        if (targetState) {
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> height } + fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "PlayerTransition"
                ) { isPlayerVisible ->
                if (isPlayerVisible && currentSong != null) {
                    PlayerScreen(
                        musicViewModel = musicViewModel,
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        artShape = albumArtShape,
                        onDismiss = { showPlayer = false }
                    )
                } else {
                    var showBluetoothDialog by remember { mutableStateOf(false) }

                    if (showBluetoothDialog) {
                        BluetoothBatterySheet(musicViewModel = musicViewModel, onDismiss = { showBluetoothDialog = false })
                    }

                    val context = androidx.compose.ui.platform.LocalContext.current
                    val customBackgroundPath by themeViewModel.customBackgroundPath.collectAsState()
                    
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (customBackgroundPath != null) {
                            AsyncImage(
                                model = customBackgroundPath,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.Black.copy(alpha = 0.5f)
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                            )
                        }

                        Scaffold(
                            modifier = Modifier,
                            containerColor = if (customBackgroundPath != null) Color.Transparent else MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                        topBar = {
                            @OptIn(ExperimentalMaterial3Api::class)
                            TopAppBar(
                                title = { Text(text = "Melody", fontWeight = FontWeight.Bold) },
                                actions = {
                                    IconButton(onClick = { showCastSheet = true }) {
                                        Icon(Icons.Default.Cast, contentDescription = "Cast")
                                    }
                                    IconButton(onClick = { 
                                        musicViewModel.updateBluetoothDevices()
                                        showBluetoothDialog = true 
                                    }) {
                                        Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth Devices")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent
                                )
                            )
                        },
                bottomBar = {
                    Column {
                        if (currentSong != null) {
                            MiniPlayer(
                                song = currentSong!!,
                                isPlaying = isPlaying,
                                albumArtShape = albumArtShape,
                                onTogglePlayback = { musicViewModel.togglePlayback() },
                                onClick = { showPlayer = true }
                            )
                        }
                        val tabShape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(start = 24.dp, end = 24.dp, bottom = 20.dp, top = 8.dp)
                                .height(64.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = tabShape
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    shape = tabShape
                                )
                        ) {
                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val barWidth = maxWidth
                                val tabCount = 4
                                val tabWidth = barWidth / tabCount

                                val targetFraction by remember {
                                    derivedStateOf { pagerState.currentPage.toFloat() }
                                }
                                val animatedFraction by animateFloatAsState(
                                    targetValue = targetFraction,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ),
                                    label = "LiquidTabIndicator"
                                )

                                val diff = abs(targetFraction - animatedFraction)
                                val scaleXFactor = 1f + (diff * 0.22f).coerceIn(0f, 0.35f)
                                val scaleYFactor = 1f - (diff * 0.08f).coerceIn(0f, 0.15f)

                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = tabWidth * animatedFraction + (tabWidth * (1f - scaleXFactor) / 2f)
                                        )
                                        .width(tabWidth * scaleXFactor)
                                        .fillMaxHeight()
                                        .padding(vertical = 6.dp, horizontal = 12.dp)
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
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                )

                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val tabsList = listOf(
                                        0 to ("Library" to Icons.Filled.LibraryMusic to Icons.Outlined.LibraryMusic),
                                        1 to ("Folders" to Icons.Filled.Folder to Icons.Outlined.Folder),
                                        2 to ("Focus" to Icons.Filled.Timer to Icons.Outlined.Timer),
                                        3 to ("Settings" to Icons.Filled.Settings to Icons.Outlined.Settings)
                                    )
                                    tabsList.forEach { (page, info) ->
                                        val (textAndIcons, inactiveIcon) = info
                                        val (label, activeIcon) = textAndIcons
                                        val isSelected = pagerState.currentPage == page

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    coroutineScope.launch {
                                                        pagerState.animateScrollToPage(page)
                                                    }
                                                }
                                                .testTag("nav_tab_${label.lowercase()}"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                val iconScale by animateFloatAsState(
                                                    targetValue = if (isSelected) 1.2f else 1.0f,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    ),
                                                    label = "IconScale"
                                                )
                                                Icon(
                                                    imageVector = if (isSelected) activeIcon else inactiveIcon,
                                                    contentDescription = label,
                                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
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
                                                        expandFrom = Alignment.End,
                                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                                    ) + fadeIn(animationSpec = tween(150)),
                                                    exit = shrinkHorizontally(
                                                        shrinkTowards = Alignment.End,
                                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                                    ) + fadeOut(animationSpec = tween(150))
                                                ) {
                                                    Text(
                                                        text = label,
                                                        color = MaterialTheme.colorScheme.onPrimary,
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
                }
            ) { paddingValues ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> SongList(
                            songs = filteredSongs, 
                            searchQuery = searchQuery,
                            onSearchQueryChange = { musicViewModel.setSearchQuery(it) },
                            useCompact = useCompactLayout, 
                            artShape = albumArtShape, 
                            bluetoothDevices = bluetoothDevices,
                            onSongClick = { musicViewModel.playSong(it, filteredSongs) },
                            contentPadding = paddingValues,
                            themeViewModel = themeViewModel
                        )
                        1 -> FolderList(
                            songs = songs, 
                            searchQuery = searchQuery,
                            onSearchQueryChange = { musicViewModel.setSearchQuery(it) },
                            useCompact = useCompactLayout,
                            contentPadding = paddingValues,
                            themeViewModel = themeViewModel
                        )
                        2 -> FocusScreen(
                            themeViewModel = themeViewModel,
                            musicViewModel = musicViewModel,
                            contentPadding = paddingValues
                        )
                        3 -> SettingsScreen(
                            viewModel = themeViewModel,
                            contentPadding = paddingValues
                        )
                    }
                }
            }
            } // close custom background Box
        }
        } // close AnimatedContent lambda
        
        // Custom Volume overlay
        AnimatedVisibility(
            visible = showVolumeBar,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 64.dp)
        ) {
            CustomVolumeBar(volumeLevel)
        }
    } // close Box
} // close MainScreen

@Composable
fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun SongList(
    songs: List<Song>, 
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    useCompact: Boolean, 
    artShape: Shape, 
    bluetoothDevices: List<MusicViewModel.BluetoothDeviceRecord> = emptyList(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    themeViewModel: ThemeViewModel,
    onSongClick: (Song) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showEarbudsDialog by remember { mutableStateOf(false) }

    val customBackgroundPath by themeViewModel.customBackgroundPath.collectAsState()
    val listOpacity by themeViewModel.listOpacity.collectAsState()
    val listSizing by themeViewModel.listSizing.collectAsState()

    val containerBgColor = if (customBackgroundPath != null) {
        MaterialTheme.colorScheme.surface.copy(alpha = listOpacity)
    } else {
        Color.Transparent
    }
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("song_search_field"),
                placeholder = { Text("Search library...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
            
            if (bluetoothDevices.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { showEarbudsDialog = true },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Rounded.Headphones, 
                        contentDescription = "Earbuds",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        if (showEarbudsDialog) {
            AlertDialog(
                onDismissRequest = { showEarbudsDialog = false },
                title = { Text("Bluetooth Devices") },
                text = {
                    Column {
                        bluetoothDevices.forEach { device ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(androidx.compose.material.icons.Icons.Rounded.Headphones, contentDescription = null)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(device.name, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        if (device.batteryLevel != null) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("L", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { device.batteryLevel / 100f },
                                                    modifier = Modifier.weight(1f).height(6.dp),
                                                    strokeCap = StrokeCap.Round
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("${device.batteryLevel}%", style = MaterialTheme.typography.bodySmall)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("R", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                LinearProgressIndicator(
                                                    progress = { device.batteryLevel / 100f },
                                                    modifier = Modifier.weight(1f).height(6.dp),
                                                    strokeCap = StrokeCap.Round
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("${device.batteryLevel}%", style = MaterialTheme.typography.bodySmall)
                                            }
                                        } else {
                                            Text("Battery: Unknown", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showEarbudsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (customBackgroundPath != null) 12.dp else 0.dp, vertical = if (customBackgroundPath != null) 8.dp else 0.dp)
                .background(
                    color = containerBgColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .then(
                    if (customBackgroundPath != null) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    bottom = contentPadding.calculateBottomPadding() + 8.dp,
                    start = 16.dp,
                    top = if (customBackgroundPath != null) 12.dp else contentPadding.calculateTopPadding(),
                    end = 16.dp
                )
            ) {
                items(songs, key = { it.id }) { song ->
                    SongItem(
                        song = song, 
                        isCompact = useCompact, 
                        artShape = artShape, 
                        listSizing = listSizing,
                        onClick = { onSongClick(song) }
                    )
                }
            }

            // Fast Scroll Handle on the left
            if (songs.size > 10) {
                BoxWithConstraints(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(24.dp)
                ) {
                    val heightPixels = constraints.maxHeight.toFloat()
                    val itemCount = songs.size
                    
                    // Simple handle position calculation
                    val scrollOffset = remember { derivedStateOf { 
                        if (itemCount > 1) {
                            (listState.firstVisibleItemIndex.toFloat() / (itemCount - 1).toFloat()).coerceIn(0f, 1f)
                        } else 0f
                    } }

                    var isDragging by remember { mutableStateOf(false) }
                    var lastTargetIndex by remember { mutableIntStateOf(-1) }
                    var scrollJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = scrollOffset.value * (heightPixels - 56.dp.toPx())
                            }
                            .size(width = 6.dp, height = 56.dp)
                            .background(
                                if (isDragging) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                            )
                            .pointerInput(songs.size) {
                                detectVerticalDragGestures(
                                    onDragStart = { isDragging = true },
                                    onDragEnd = { isDragging = false },
                                    onDragCancel = { isDragging = false },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        val currentY = scrollOffset.value * (heightPixels - 56.dp.toPx())
                                        val nextY = (currentY + dragAmount).coerceIn(0f, heightPixels - 56.dp.toPx())
                                        val percentage = nextY / (heightPixels - 56.dp.toPx())
                                        val targetIndex = (percentage * (itemCount - 1)).toInt()
                                        
                                        if (targetIndex != lastTargetIndex) {
                                            lastTargetIndex = targetIndex
                                            scrollJob?.cancel()
                                            scrollJob = coroutineScope.launch {
                                                listState.scrollToItem(targetIndex)
                                            }
                                        }
                                    }
                                )
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song, 
    isCompact: Boolean, 
    artShape: Shape, 
    listSizing: Float = 1.0f,
    onClick: () -> Unit
) {
    val imageSize = (if (isCompact) 40.dp else 56.dp) * listSizing
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "SongItemScale"
    )

    // Expressive Asymmetric Corner Shape
    val itemShape = RoundedCornerShape(
        topStart = 24.dp * listSizing,
        bottomStart = 8.dp * listSizing,
        topEnd = 8.dp * listSizing,
        bottomEnd = 24.dp * listSizing
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (3.dp * listSizing))
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { 
                isPressed = true
                onClick()
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = itemShape
            ),
        shape = itemShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant vertical accent block on the left representing active state guide
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp * listSizing)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Leading Album Art
            AlbumArt(song.albumArtUri, imageSize, artShape)

            Spacer(modifier = Modifier.width(16.dp))

            // Titles
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    style = (if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge).copy(
                        fontSize = (if (isCompact) 14.sp else 16.sp) * listSizing
                    )
                ) 
                
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${song.artist} • ${song.album}", 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    style = (if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall).copy(
                        fontSize = (if (isCompact) 10.sp else 12.sp) * listSizing
                    )
                ) 
            }

            // High aesthetic M3 Expressive play detail button
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<com.melody.data.Playlist>,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (com.melody.data.Playlist) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val application = context.applicationContext as android.app.Application
    val themeViewModel = remember { ThemeViewModel(application) }
    val useLiquidButtons by themeViewModel.useLiquidButtons.collectAsState()

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Playlist") },
            text = {
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    placeholder = { Text("Playlist Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                MelodyButton(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            onCreatePlaylist(playlistName)
                            playlistName = ""
                            showAddDialog = false
                        }
                    },
                    useLiquid = useLiquidButtons
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (playlists.isEmpty()) {
            EmptyState(Icons.Default.PlaylistPlay, "No playlists created yet")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    start = 16.dp, 
                    end = 16.dp
                )
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    val playlistShape = RoundedCornerShape(topStart = 12.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 12.dp)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = playlistShape
                            ),
                        shape = playlistShape,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        ListItem(
                            headlineContent = { 
                                Text(
                                    text = playlist.name, 
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                ) 
                            },
                            supportingContent = { 
                                Text(
                                    text = "قائمة تشغيل مخصصة | Playlist", 
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                ) 
                            },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PlaylistPlay, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { onDeletePlaylist(playlist) },
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete, 
                                        contentDescription = "Delete", 
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Playlist")
        }
    }
}

@Composable
fun AlbumArt(
    uri: String?, 
    size: androidx.compose.ui.unit.Dp, 
    shape: Shape = MaterialTheme.shapes.medium,
    onBitmapLoaded: ((Bitmap) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(shape),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PlaceholderArt()
            if (uri != null) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .allowHardware(false)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = { state ->
                        val drawable = state.result.drawable
                        if (drawable is BitmapDrawable && onBitmapLoaded != null) {
                            onBitmapLoaded(drawable.bitmap)
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastBottomSheet(
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val mediaRouter = remember { androidx.mediarouter.media.MediaRouter.getInstance(context) }
    val selector = remember { 
        androidx.mediarouter.media.MediaRouteSelector.Builder()
            .addControlCategory(androidx.mediarouter.media.MediaControlIntent.CATEGORY_LIVE_AUDIO)
            .addControlCategory(androidx.mediarouter.media.MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
            .build()
    }
    
    var routes by remember { mutableStateOf(emptyList<androidx.mediarouter.media.MediaRouter.RouteInfo>()) }
    
    DisposableEffect(Unit) {
        val callback = object : androidx.mediarouter.media.MediaRouter.Callback() {
            override fun onRouteAdded(router: androidx.mediarouter.media.MediaRouter, route: androidx.mediarouter.media.MediaRouter.RouteInfo) {
                routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
            }
            override fun onRouteRemoved(router: androidx.mediarouter.media.MediaRouter, route: androidx.mediarouter.media.MediaRouter.RouteInfo) {
                routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
            }
            override fun onRouteChanged(router: androidx.mediarouter.media.MediaRouter, route: androidx.mediarouter.media.MediaRouter.RouteInfo) {
                routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
            }
        }
        
        mediaRouter.addCallback(selector, callback, androidx.mediarouter.media.MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
        routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
        
        onDispose {
            mediaRouter.removeCallback(callback)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "Cast to Device",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (routes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Text("Searching for TVs and devices...", modifier = Modifier.padding(top = 48.dp))
                }
            } else {
                routes.forEach { route ->
                    ListItem(
                        headlineContent = { Text(route.name) },
                        supportingContent = { Text(route.description ?: "Ready to cast") },
                        leadingContent = {
                            Icon(
                                when {
                                    route.deviceType == androidx.mediarouter.media.MediaRouter.RouteInfo.DEVICE_TYPE_TV -> Icons.Default.Tv
                                    route.deviceType == androidx.mediarouter.media.MediaRouter.RouteInfo.DEVICE_TYPE_SPEAKER -> Icons.Default.Speaker
                                    else -> Icons.Default.Cast
                                },
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                route.select()
                                onDismiss()
                            }
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    musicViewModel: MusicViewModel,
    song: Song,
    isPlaying: Boolean,
    artShape: Shape,
    onDismiss: () -> Unit
) {
    val progress by musicViewModel.playbackProgress.collectAsState()
    val duration by musicViewModel.currentDuration.collectAsState()
    val rawDominantColor by musicViewModel.dominantColor.collectAsState()
    val audioAmplitude by musicViewModel.audioAmplitude.collectAsState()
    
    var showAlbumMenu by remember { mutableStateOf(false) }
    var enableRain by remember { mutableStateOf(false) }
    var enableBeatBounce by remember { mutableStateOf(false) }
    var enableRhythmicFolder by remember { mutableStateOf(false) }
    
    val dominantColor by animateColorAsState(
        targetValue = if (rawDominantColor == Color.Transparent) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else rawDominantColor,
        animationSpec = tween(1200),
        label = "SmoothColor"
    )

    // Rhythmic Folder Offset
    var manualMovementEnabled by remember { mutableStateOf(false) }
    var manualOffset by remember { mutableStateOf(Offset.Zero) }

    val folderOffset by animateDpAsState(
        targetValue = if (enableRhythmicFolder && isPlaying && !manualMovementEnabled) (audioAmplitude * 100).dp else 0.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "FolderMovement"
    )

    val folderRotation by animateFloatAsState(
        targetValue = if (enableRhythmicFolder && isPlaying && !manualMovementEnabled) audioAmplitude * 45f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "FolderRotation"
    )

    // Manual offset for manual movement
    val manualOffsetPx = with(LocalDensity.current) {
        Offset(manualOffset.x, manualOffset.y)
    }

    // 3D Flip animation when song changes
    var flipAngle by remember { mutableFloatStateOf(0f) }
    var songPopScale by remember { mutableFloatStateOf(1f) }
    
    LaunchedEffect(song.id) {
        flipAngle += 180f
        songPopScale = 1.15f
        kotlinx.coroutines.delay(200)
        songPopScale = 1f
    }
    
    val animatedFlip by animateFloatAsState(
        targetValue = flipAngle,
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow),
        label = "AlbumFlip"
    )
    
    val animatedPopScale by animateFloatAsState(
        targetValue = songPopScale,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "SongPop"
    )

    // Scale animation based on playback state
    val albumScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.85f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "AlbumScale"
    )

    val infiniteTransitionBeat = rememberInfiniteTransition(label = "BeatTransition")
    val beatScale by infiniteTransitionBeat.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BeatPulse"
    )

    val rhythmScale by remember(audioAmplitude, enableBeatBounce, isPlaying) {
        derivedStateOf { if (enableBeatBounce && isPlaying) (1f + audioAmplitude * 0.2f) else 1f }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ColorPulse"
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Atmospheric Radial Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                dominantColor.copy(alpha = animatedAlpha),
                                Color.Transparent
                            ),
                            center = Offset(0.5f, 0.8f),
                            radius = 2000f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Back", modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = { musicViewModel.setVolumeLevel(300) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Enhanced Audio", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.weight(1.2f))

                Box(
                    modifier = Modifier.graphicsLayer {
                        val activeBeatScale = if (enableBeatBounce && isPlaying) rhythmScale else 1f
                        scaleX = albumScale * animatedPopScale * activeBeatScale
                        scaleY = albumScale * animatedPopScale * activeBeatScale
                        rotationY = animatedFlip + folderRotation
                        translationY = folderOffset.toPx() + manualOffsetPx.y
                        translationX = manualOffsetPx.x
                        cameraDistance = 8 * density
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = { /* handle end */ },
                            onDrag = { change, dragAmount -> 
                                if (manualMovementEnabled) {
                                    manualOffset += dragAmount
                                    change.consume()
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { showAlbumMenu = true },
                            onDoubleTap = { manualMovementEnabled = !manualMovementEnabled }
                        )
                    }
                ) {
                    AlbumArt(
                        uri = song.albumArtUri,
                        size = 320.dp,
                        shape = if (enableRhythmicFolder) FolderShapes[1] else artShape,
                        onBitmapLoaded = { bitmap ->
                            Palette.from(bitmap).generate { palette ->
                                val color = palette?.vibrantSwatch?.rgb?.let { Color(it) }
                                    ?: palette?.dominantSwatch?.rgb?.let { Color(it) }
                                    ?: palette?.mutedSwatch?.rgb?.let { Color(it) }
                                
                                color?.let { musicViewModel.setDominantColor(it) }
                            }
                        }
                    )
                    
                    if (enableRain) {
                        AnimatedRainEffect(modifier = Modifier.matchParentSize())
                    }
                }

            if (showAlbumMenu) {
                ModalBottomSheet(
                    onDismissRequest = { showAlbumMenu = false },
                    sheetState = rememberModalBottomSheetState()
                ) {
                    Column(modifier = Modifier.padding(16.dp).padding(bottom = 24.dp)) {
                        Text(
                            text = "Visual Effects",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { enableRain = !enableRain }
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Thunderstorm, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
                            Text("Realistic Rain Effect", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Switch(checked = enableRain, onCheckedChange = { enableRain = it })
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { enableBeatBounce = !enableBeatBounce }
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Animation, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
                            Text("Beat Pulse (Visualizer)", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Switch(checked = enableBeatBounce, onCheckedChange = { enableBeatBounce = it })
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { enableRhythmicFolder = !enableRhythmicFolder }
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.padding(end = 16.dp))
                            Text("Rhythmic Folder Motion", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Switch(checked = enableRhythmicFolder, onCheckedChange = { enableRhythmicFolder = it })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            var sliderDragging by remember { mutableStateOf(false) }
            var localSliderValue by remember { mutableFloatStateOf(0f) }
            
            // Re-sync local value when dragging starts or when not dragging
            val sliderValue = if (sliderDragging) localSliderValue else (if (duration > 0) progress.toFloat() / duration.toFloat() else 0f)

            Slider(
                value = sliderValue,
                onValueChange = { 
                    sliderDragging = true
                    localSliderValue = it
                },
                onValueChangeFinished = {
                    musicViewModel.seekTo((localSliderValue * duration).toLong())
                },
                modifier = Modifier.testTag("playback_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            // Reset dragging state when progress catches up to sought value (roughly)
            LaunchedEffect(progress) {
                if (sliderDragging) {
                    val currentProgress = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f
                    if (abs(currentProgress - localSliderValue) < 0.05f) {
                        sliderDragging = false
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatDuration(if (sliderDragging) (localSliderValue * duration).toLong() else progress), 
                    style = MaterialTheme.typography.labelMedium
                )
                Text(formatDuration(duration), style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val prevInteractionSource = remember { MutableInteractionSource() }
                val prevPressed by prevInteractionSource.collectIsPressedAsState()
                val prevScale by animateFloatAsState(
                    targetValue = if (prevPressed) 0.8f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                    label = "PrevScale"
                )

                FilledIconButton(
                    onClick = { musicViewModel.playPrevious() },
                    interactionSource = prevInteractionSource,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("play_previous_button")
                        .graphicsLayer { scaleX = prevScale; scaleY = prevScale }
                        .border(3.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.width(32.dp))

                val playInteractionSource = remember { MutableInteractionSource() }
                val playPressed by playInteractionSource.collectIsPressedAsState()
                val playScale by animateFloatAsState(
                    targetValue = if (playPressed) 0.85f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                    label = "PlayScale"
                )

                FloatingActionButton(
                    onClick = { musicViewModel.togglePlayback() },
                    interactionSource = playInteractionSource,
                    modifier = Modifier
                        .size(96.dp)
                        .testTag("toggle_playback_button")
                        .graphicsLayer { scaleX = playScale; scaleY = playScale }
                        .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = "Toggle Playback",
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                val nextInteractionSource = remember { MutableInteractionSource() }
                val nextPressed by nextInteractionSource.collectIsPressedAsState()
                val nextScale by animateFloatAsState(
                    targetValue = if (nextPressed) 0.8f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                    label = "NextScale"
                )

                FilledIconButton(
                    onClick = { musicViewModel.playNext() },
                    interactionSource = nextInteractionSource,
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("play_next_button")
                        .graphicsLayer { scaleX = nextScale; scaleY = nextScale }
                        .border(3.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp))
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
}

@Composable
fun AnimatedRainEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Rain")
    val dropProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DropProgress"
    )
    
    val dropletAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DropletAlpha"
    )

    Canvas(modifier = modifier) {
        val dropCount = 40
        val screenDroplets = 15
        
        // Falling Rain
        repeat(dropCount) { i ->
            val randomX = (i * size.width / dropCount) + ((i * 13) % 20) * 3f
            
            // Fixed speed for drops to prevent jumping, use modulo for staggering
            val stagger = (i * 0.31f) % 1f
            val offsetY = (dropProgress + stagger) % 1f
            val yPos = offsetY * size.height
            
            // Angle the rain slightly
            val angleX = -20f 
            val length = 30f + (i % 5) * 15f
            val alpha = 0.3f + (i % 4) * 0.1f
            
            drawLine(
                color = Color.White.copy(alpha = alpha),
                start = Offset(randomX, yPos),
                end = Offset(randomX + angleX, yPos + length),
                strokeWidth = 2f + (i % 2)
            )
        }
        
        // Static Water Droplets on Lens/Screen
        repeat(screenDroplets) { i ->
            val dropletX = ((i * 37) % 100) / 100f * size.width
            val dropletY = ((i * 61) % 100) / 100f * size.height
            val dropletSize = 4f + (i % 5) * 2f
            
            // Draw highlight and shadow to make drop look 3d
            drawCircle(
                color = Color.Black.copy(alpha = 0.15f * dropletAlpha),
                radius = dropletSize,
                center = Offset(dropletX, dropletY + 1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.4f * dropletAlpha),
                radius = dropletSize * 0.8f,
                center = Offset(dropletX, dropletY)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f * dropletAlpha),
                radius = dropletSize * 0.3f,
                center = Offset(dropletX - dropletSize * 0.2f, dropletY - dropletSize * 0.2f)
            )
        }
    }
}

private fun formatDuration(duration: Long): String {
    val totalSeconds = duration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun PlaceholderArt() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun FolderList(
    songs: List<Song>, 
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    useCompact: Boolean,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    themeViewModel: ThemeViewModel
) {
    val folders = remember(songs, searchQuery) { 
        songs.filter { 
            it.folderPath.contains(searchQuery, ignoreCase = true) 
        }.groupBy { it.folderPath } 
    }
    val shape = MaterialTheme.shapes.extraLarge

    val customBackgroundPath by themeViewModel.customBackgroundPath.collectAsState()
    val listOpacity by themeViewModel.listOpacity.collectAsState()
    val listSizing by themeViewModel.listSizing.collectAsState()

    val containerBgColor = if (customBackgroundPath != null) {
        MaterialTheme.colorScheme.surface.copy(alpha = listOpacity)
    } else {
        Color.Transparent
    }

    val cardBgColor = if (customBackgroundPath != null) {
        MaterialTheme.colorScheme.surface.copy(alpha = listOpacity)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (customBackgroundPath != null) 12.dp else 0.dp, vertical = if (customBackgroundPath != null) 8.dp else 0.dp)
                .background(
                    color = containerBgColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .then(
                    if (customBackgroundPath != null) {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(24.dp)
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = contentPadding.calculateBottomPadding() + 16.dp,
                    top = if (customBackgroundPath != null) 32.dp else contentPadding.calculateTopPadding() + 16.dp,
                    start = 16.dp, 
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        placeholder = { Text("Search folders...", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                folders.forEach { (path, folderSongs) ->
                    item(key = path) {
                        val folderShape = RoundedCornerShape(topStart = 28.dp, bottomEnd = 28.dp, topEnd = 12.dp, bottomStart = 12.dp)
                        Card(
                            onClick = { /* Folder detail browsing */ },
                            shape = folderShape,
                            colors = CardDefaults.cardColors(
                                containerColor = cardBgColor.copy(alpha = 0.6f),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = (3.dp * listSizing))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = folderShape
                                )
                        ) {
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        text = path.substringAfterLast("/"), 
                                        fontWeight = FontWeight.Bold,
                                        style = (if (useCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge).copy(
                                            fontSize = (if (useCompact) 14.sp else 16.sp) * listSizing
                                        )
                                    ) 
                                },
                                supportingContent = { 
                                    Text(
                                        text = "${folderSongs.size} tracks • $path", 
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp * listSizing
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    ) 
                                },
                                leadingContent = { 
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                                        modifier = Modifier.size((if (useCompact) 40.dp else 48.dp) * listSizing)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Folder, 
                                                contentDescription = null, 
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size((if (useCompact) 20.dp else 24.dp) * listSizing)
                                            )
                                        }
                                    }
                                },
                                trailingContent = {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Details",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    albumArtShape: Shape,
    onTogglePlayback: () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "MiniPlayerScale"
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 4.dp,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AlbumArt(song.albumArtUri, 48.dp, albumArtShape)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    song.title, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artist, 
                    style = MaterialTheme.typography.bodySmall, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            val playInteractionSource = remember { MutableInteractionSource() }
            val isPlayPressed by playInteractionSource.collectIsPressedAsState()
            val playScale by animateFloatAsState(
                targetValue = if (isPlayPressed) 0.85f else 1f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
                label = "PlayBounce"
            )

            IconButton(
                onClick = onTogglePlayback,
                interactionSource = playInteractionSource,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .graphicsLayer { scaleX = playScale; scaleY = playScale },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


fun Modifier.glassEffect(
    alpha: Float = 0.05f,
    shape: Shape = RoundedCornerShape(28.dp)
): Modifier = this.then(
    Modifier
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 1.5f),
                    Color.White.copy(alpha = alpha * 0.5f)
                )
            ),
            shape = shape
        )
        .border(
            width = 0.8.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.1f)
                )
            ),
            shape = shape
        )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HyperLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "HyperLoading")
    
    val width by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = 64f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Width"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    Box(
        modifier = modifier
            .width(width.dp)
            .height(8.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val iconShape = RoundedCornerShape(
        topStart = 14.dp,
        bottomStart = 6.dp,
        topEnd = 6.dp,
        bottomEnd = 14.dp
    )
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "SettingsItemScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (onClick != null) {
                    Modifier.clickable {
                        isPressed = true
                        onClick()
                    }
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = iconShape,
            color = iconContainerColor,
            modifier = Modifier
                .size(42.dp)
                .border(
                    width = 1.dp,
                    color = iconColor.copy(alpha = 0.15f),
                    shape = iconShape
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = iconColor, 
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle, 
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), 
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
        
        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                trailing()
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(80)
            isPressed = false
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    val groupShape = RoundedCornerShape(
        topStart = 28.dp,
        bottomStart = 12.dp,
        topEnd = 12.dp,
        bottomEnd = 28.dp
    )
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.2.sp
            )
        }
        Surface(
            shape = groupShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = groupShape
                )
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: ThemeViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val dynamicColor by viewModel.useDynamicColor.collectAsState()
    val primaryColor by viewModel.primaryColor.collectAsState()
    val useCompactLayout by viewModel.useCompactLayout.collectAsState()
    val albumArtShapeIndex by viewModel.albumArtShape.collectAsState()
    val context = LocalContext.current
    val fontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importFont(it, context) }
    }
    
    val backgroundLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importBackground(it, context) }
    }
    val customFontPath by viewModel.customFontPath.collectAsState()
    val customBackgroundPath by viewModel.customBackgroundPath.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
            .verticalScroll(rememberScrollState())
            .padding(
                top = 20.dp, 
                bottom = contentPadding.calculateBottomPadding() + 40.dp
            )
    ) {
        SettingsGroup(title = "Visual Essence | المظهر البصري") {
            SettingsItem(
                title = "Dynamic Theming | السمات الديناميكية",
                subtitle = "Sync interface with system wallpaper\nمزامنة ألوان الواجهة مع خلفية النظام",
                icon = Icons.Rounded.Palette,
                iconContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                iconColor = Color(0xFF2E7D32),
                trailing = {
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = { viewModel.setDynamicColor(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            )
        }

        SettingsGroup(title = "Interface Controls | واجهة التحكم") {
            SettingsItem(
                title = "Compact Mode | الوضع المضغوط",
                subtitle = "Maximize content on screen\nعرض عناصر ومحتوى أكثر على الشاشة",
                icon = Icons.Rounded.ViewList,
                iconContainerColor = Color(0xFFFF9800).copy(alpha = 0.2f),
                iconColor = Color(0xFFE65100),
                trailing = {
                    Switch(
                        checked = useCompactLayout,
                        onCheckedChange = { viewModel.setCompactLayout(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.secondary,
                            checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            )

            val listSizing by viewModel.listSizing.collectAsState()
            val listOpacity by viewModel.listOpacity.collectAsState()

            // Expressive styled sliders block
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                // Slider 1: List Sizing
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.AspectRatio,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Item Sizing | حجم العناصر والخطوط", 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${(listSizing * 100).toInt()}%", 
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Slider(
                    value = listSizing,
                    onValueChange = { viewModel.setListSizing(it) },
                    valueRange = 0.8f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                    )
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Slider 2: Background Opacity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Opacity,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Background Opacity | شفافية الخلفية", 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "${(listOpacity * 100).toInt()}%", 
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary, 
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Slider(
                    value = listOpacity,
                    onValueChange = { viewModel.setListOpacity(it) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)
                    )
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Slider 3: Font Size Scale
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.FormatSize,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Font Size Scale | مقياس حجم الخط", 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val fontSizeScale by viewModel.fontSizeScale.collectAsState()
                    Text(
                        text = "${(fontSizeScale * 100).toInt()}%", 
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.tertiary, 
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                val fontSizeScale by viewModel.fontSizeScale.collectAsState()
                Slider(
                    value = fontSizeScale,
                    onValueChange = { viewModel.setFontSizeScale(it) },
                    valueRange = 0.8f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.tertiary,
                        activeTrackColor = MaterialTheme.colorScheme.tertiary,
                        inactiveTrackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.24f)
                    )
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            
            SettingsItem(
                title = "تأثير الأزرار السائل | Liquid Theme",
                subtitle = "تفعيل مظهر وتأثير الأزرار الزجاجية السائلة التفاعلية",
                icon = Icons.Rounded.TouchApp,
                iconContainerColor = Color(0xFFE91E63).copy(alpha = 0.2f),
                iconColor = Color(0xFFC2185B),
                trailing = {
                    val useLiquidButtons by viewModel.useLiquidButtons.collectAsState()
                    Switch(
                        checked = useLiquidButtons,
                        onCheckedChange = { viewModel.setUseLiquidButtons(it) }
                    )
                }
            )
            
            val useLiquidButtons by viewModel.useLiquidButtons.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val buttonShape = RoundedCornerShape(topStart = 16.dp, bottomEnd = 16.dp, topEnd = 6.dp, bottomStart = 6.dp)
                Surface(
                    shape = buttonShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            shape = buttonShape
                        )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        SettingsItem(
                            title = "Custom Font | خط مخصص",
                            subtitle = if (customFontPath != null) "Custom TTF Font applied" else "Import custom .ttf typography",
                            icon = Icons.Rounded.FontDownload,
                            onClick = { fontLauncher.launch("font/ttf") },
                            trailing = if (customFontPath != null) {
                                { IconButton(onClick = { viewModel.clearCustomFont(context) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) } }
                            } else null
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        SettingsItem(
                            title = "Custom Background | خلفية مخصصة",
                            subtitle = if (customBackgroundPath != null) "Vibrant image applied" else "Set custom wallpaper context",
                            icon = Icons.Rounded.Image,
                            onClick = { backgroundLauncher.launch("image/*") },
                            trailing = if (customBackgroundPath != null) {
                                { IconButton(onClick = { viewModel.clearCustomBackground(context) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) } }
                            } else null
                        )
                    }
                }
                
                MelodyButton(
                    onClick = {
                        viewModel.setUseLiquidButtons(!useLiquidButtons)
                    },
                    useLiquid = useLiquidButtons,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    tint = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (useLiquidButtons) Icons.Rounded.AutoAwesome else Icons.Rounded.TouchApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (useLiquidButtons) "معاينة: تم تفعيل الأزرار الزجاجية السائلة" else "معاينة: انقر لتفعيل التصميم الزجاجي",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        SettingsGroup(title = "Creative Canvas | أشكال الألبوم الحية") {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Album Art Geometry | هندسة غلاف الألبوم", 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlbumArtShapes.forEachIndexed { index, shape ->
                        val isSelected = albumArtShapeIndex == index
                        Surface(
                            onClick = { viewModel.setAlbumArtShape(index) },
                            shape = shape,
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    shape = shape
                                ),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            tonalElevation = if (isSelected) 6.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check, 
                                        contentDescription = null, 
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!dynamicColor) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Atmospheric Accent | اللون الأساسي", 
                        style = MaterialTheme.typography.titleSmall, 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val colors = listOf(
                            Color(0xFF673AB7), Color(0xFF2196F3), Color(0xFFF44336),
                            Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFFE91E63)
                        )
                        colors.forEach { color ->
                            ColorOption(color, primaryColor == color) { viewModel.setPrimaryColor(color) }
                        }
                    }
                }
            }
        }
        
        SettingsGroup(title = "Information | معلومات") {
            SettingsItem(
                title = "Melody Stream | ميلودي ستريم",
                subtitle = "Stable Build 4.2.1-Expressive\nإصدار ثابت ومحسن بالكامل",
                icon = Icons.Rounded.Info,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            SettingsItem(
                title = "Open Source License | رخصة مفتوحة",
                subtitle = "Inspired by HyperBridge Project\nمستوحى من مشروع هايبربريدج",
                icon = Icons.Rounded.Code,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                iconColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

private fun border(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

@Composable
fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ColorOption(color: androidx.compose.ui.graphics.Color, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = color,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface) else null
    ) {}
}

@Composable
fun CustomVolumeBar(volumeLevel: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(horizontal = 4.dp),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(27.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    volumeLevel == 0 -> Icons.Default.VolumeOff
                    volumeLevel > 100 -> Icons.Default.VolumeUp
                    else -> Icons.Default.VolumeDown
                },
                contentDescription = null,
                tint = if (volumeLevel > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
            ) {
                val progress = (volumeLevel / 300f).coerceIn(0f, 1f)
                val color = if (volumeLevel > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(color)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "${volumeLevel}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (volumeLevel > 100) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothBatterySheet(musicViewModel: MusicViewModel, onDismiss: () -> Unit) {
    val devices by musicViewModel.bluetoothDevices.collectAsState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Bluetooth Devices", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                "Real-time connection & battery status",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No paired devices found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    items(devices) { device ->
                        val deviceShape = RoundedCornerShape(14.dp, 24.dp, 14.dp, 24.dp)
                        Surface(
                            onClick = { /* Clicking could trigger connect */ },
                            shape = deviceShape,
                            color = if (device.isConnected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = if (device.isConnected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                                    shape = deviceShape
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center, 
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (device.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), 
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (device.name.lowercase().contains("headphones") || device.name.lowercase().contains("earbuds")) 
                                            Icons.Default.Headset else Icons.Default.Bluetooth,
                                        contentDescription = null,
                                        tint = if (device.isConnected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = device.name, 
                                        style = MaterialTheme.typography.bodyLarge, 
                                        fontWeight = FontWeight.Bold,
                                        color = if (device.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (device.isConnected) {
                                            Text(
                                                text = "Connected • ", 
                                                style = MaterialTheme.typography.labelSmall, 
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                        Text(
                                            text = device.address, 
                                            style = MaterialTheme.typography.bodySmall, 
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                if (device.batteryLevel != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(
                                                color = if (device.batteryLevel > 20) Color.Green.copy(alpha = 0.12f) else Color.Red.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (device.batteryLevel > 20) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (device.batteryLevel > 20) Color.Green else Color.Red
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${device.batteryLevel}%", 
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (device.batteryLevel > 20) Color.Green else Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            val application = context.applicationContext as android.app.Application
            val themeViewModel = remember { ThemeViewModel(application) }
            val useLiquidButtons by themeViewModel.useLiquidButtons.collectAsState()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MelodyButton(
                    onClick = {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    useLiquid = useLiquidButtons
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pair New")
                }

                MelodyButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    useLiquid = useLiquidButtons,
                    tint = MaterialTheme.colorScheme.secondary
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    backdrop: Any? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressedState by interactionSource.collectIsPressedAsState()
    
    var touchOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    val pressProgress by animateFloatAsState(
        targetValue = if (isPressedState || isDragging) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "PressProgress"
    )
    
    val animatedOffsetX by animateFloatAsState(
        targetValue = touchOffset.x,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 120f),
        label = "LiquidDragX"
    )
    val animatedOffsetY by animateFloatAsState(
        targetValue = touchOffset.y,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 120f),
        label = "LiquidDragY"
    )

    val scale = androidx.compose.ui.util.lerp(1f, 0.94f, pressProgress)
    val dragAngle = atan2(animatedOffsetY, animatedOffsetX)
    val dragDist = kotlin.math.hypot(animatedOffsetX, animatedOffsetY)
    val maxDist = 200f
    val normalizedDrag = (dragDist / maxDist).coerceIn(0f, 1f)
    
    val finalScaleX = scale + (0.12f * abs(cos(dragAngle)) * normalizedDrag * pressProgress)
    val finalScaleY = scale - (0.06f * abs(sin(dragAngle)) * normalizedDrag * pressProgress)
    
    val swayX = (animatedOffsetX * 0.15f).coerceIn(-12f, 12f) * pressProgress
    val swayY = (animatedOffsetY * 0.15f).coerceIn(-12f, 12f) * pressProgress

    val isDark = isSystemInDarkTheme()

    val containerColor = if (surfaceColor.isSpecified) {
        surfaceColor
    } else if (tint.isSpecified) {
        tint.copy(alpha = 0.32f)
    } else {
        if (isDark) Color(0xFF2C2C2C).copy(alpha = 0.48f) else Color(0xFFF0F0F0).copy(alpha = 0.65f)
    }

    val finalBorderColor = if (tint.isSpecified) {
        tint.copy(alpha = 0.60f)
    } else {
        if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.12f)
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = finalScaleX
                scaleY = finalScaleY
                translationX = swayX
                translationY = swayY
            }
            .pointerInput(Unit) {
                if (isInteractive) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            touchOffset = offset - Offset(size.width / 2f, size.height / 2f)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            touchOffset += dragAmount
                        },
                        onDragEnd = {
                            isDragging = false
                            touchOffset = Offset.Zero
                        },
                        onDragCancel = {
                            isDragging = false
                            touchOffset = Offset.Zero
                        }
                    )
                }
            }
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        val blurEffect = remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                RenderEffect.createBlurEffect(
                    30f, 30f, Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            } else {
                null
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    if (blurEffect != null) {
                        renderEffect = blurEffect
                    }
                }
                .drawWithCache {
                    onDrawWithContent {
                        // Base water glass gradient that mimics glass sheet refraction
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isDark) 0.08f else 0.22f),
                                    Color.White.copy(alpha = if (isDark) 0.02f else 0.07f)
                                )
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                        )
                        // Low-alpha tint overlay that makes the glass blend perfectly with the app theme
                        if (tint.isSpecified && tint != Color.Transparent) {
                            drawRoundRect(
                                color = tint.copy(alpha = if (isDark) 0.06f else 0.12f),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                                blendMode = androidx.compose.ui.graphics.BlendMode.SrcOver
                            )
                        }
                        // Liquid edge top shine highlighting (3D curved glass reflection effect)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isDark) 0.35f else 0.65f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 1.3.dp.toPx()
                            )
                        )
                    }
                }
        )

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val contentColor = if (tint.isSpecified) {
                tint
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            CompositionLocalProvider(
                LocalContentColor provides contentColor
            ) {
                content()
            }
        }
    }
}

@Composable
fun MelodyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    useLiquid: Boolean = false,
    tint: Color = Color.Unspecified,
    content: @Composable RowScope.() -> Unit
) {
    if (useLiquid) {
        LiquidButton(
            onClick = onClick,
            modifier = modifier,
            tint = if (tint != Color.Unspecified) tint else MaterialTheme.colorScheme.primary,
            content = content
        )
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.height(48.dp),
            colors = if (tint != Color.Unspecified) ButtonDefaults.buttonColors(containerColor = tint) else ButtonDefaults.buttonColors(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }
}
