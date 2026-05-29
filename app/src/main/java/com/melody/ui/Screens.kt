package com.melody.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
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
    val isGlassEnabled by themeViewModel.isGlassEffectEnabled.collectAsState()
    val albumArtShapeIndex by themeViewModel.albumArtShape.collectAsState()

    val albumArtShape = remember(albumArtShapeIndex) { AlbumArtShapes.getOrElse(albumArtShapeIndex) { AlbumArtShapes[0] } }
    val playlists by musicViewModel.playlists.collectAsState(emptyList())

    val filteredSongs = remember(songs, searchQuery) {
        if (searchQuery.isEmpty()) songs
        else songs.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.artist.contains(searchQuery, ignoreCase = true) 
        }
    }

            var showOnlineSearch by remember { mutableStateOf(false) }
            var showPlayer by remember { mutableStateOf(false) }
            var showCastSheet by remember { mutableStateOf(false) }
            
            if (showOnlineSearch) {
                OnlineSearchDialog(
                    musicViewModel = musicViewModel,
                    onDismiss = { showOnlineSearch = false }
                )
            }

            if (showCastSheet) {
                CastBottomSheet(onDismiss = { showCastSheet = false })
            }

            val pagerState = rememberPagerState(pageCount = { 4 })
            val coroutineScope = rememberCoroutineScope()

            val showVolumeBar by musicViewModel.showVolumeBar.collectAsState()
            val volumeLevel by musicViewModel.volumeLevel.collectAsState()

            Box(modifier = Modifier.fillMaxSize()) {
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
                    
                    Scaffold(
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
                                isGlassEnabled = isGlassEnabled,
                                onTogglePlayback = { musicViewModel.togglePlayback() },
                                onClick = { showPlayer = true }
                            )
                        }
                        NavigationBar(
                            containerColor = if (isGlassEnabled) Color.Transparent else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = if (isGlassEnabled) Modifier.glassEffect(shape = androidx.compose.ui.graphics.RectangleShape) else Modifier,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            NavigationBarItem(
                                selected = pagerState.currentPage == 0,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                                icon = { Icon(if (pagerState.currentPage == 0) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic, contentDescription = "Library") },
                                label = { Text("Library") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                            NavigationBarItem(
                                selected = pagerState.currentPage == 1,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                                icon = { Icon(if (pagerState.currentPage == 1) Icons.Filled.Folder else Icons.Outlined.Folder, contentDescription = "Folders") },
                                label = { Text("Folders") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                            NavigationBarItem(
                                selected = pagerState.currentPage == 2,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                                icon = { Icon(if (pagerState.currentPage == 2) Icons.Filled.PlaylistPlay else Icons.Outlined.PlaylistPlay, contentDescription = "Playlists") },
                                label = { Text("Playlists") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                            NavigationBarItem(
                                selected = pagerState.currentPage == 3,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                                icon = { Icon(if (pagerState.currentPage == 3) Icons.Filled.Settings else Icons.Outlined.Settings, contentDescription = "Settings") },
                                label = { Text("Settings") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) { page ->
                    when (page) {
                        0 -> SongList(
                            songs = filteredSongs, 
                            searchQuery = searchQuery,
                            onSearchQueryChange = { musicViewModel.setSearchQuery(it) },
                            useCompact = useCompactLayout, 
                            artShape = albumArtShape, 
                            isGlassEnabled = isGlassEnabled,
                            onOnlineSearchClick = { showOnlineSearch = true },
                            onSongClick = { musicViewModel.playSong(it, filteredSongs) }
                        )
                        1 -> FolderList(songs = songs, useCompact = useCompactLayout)
                        2 -> PlaylistList(
                            playlists = playlists, 
                            onCreatePlaylist = { musicViewModel.createPlaylist(it) },
                            onDeletePlaylist = { musicViewModel.deletePlaylist(it) }
                        )
                        3 -> SettingsScreen(themeViewModel)
                    }
                }
            }
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
    isGlassEnabled: Boolean = false,
    onOnlineSearchClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
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
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FilledIconButton(
                onClick = onOnlineSearchClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(52.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Default.Language, contentDescription = "Online Search")
            }
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp, start = 16.dp) // Added start padding for the handle
            ) {
                items(songs, key = { it.id }) { song ->
                    SongItem(
                        song = song, 
                        isCompact = useCompact, 
                        artShape = artShape, 
                        isGlassEnabled = isGlassEnabled,
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
    isGlassEnabled: Boolean = false,
    onClick: () -> Unit
) {
    val imageSize = if (isCompact) 40.dp else 56.dp
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SongItemScale"
    )

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isGlassEnabled) Modifier.glassEffect(alpha = 0.15f, shape = RoundedCornerShape(12.dp)) else Modifier)
    ) {
        ListItem(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable { 
                    isPressed = true
                    onClick()
                },
            headlineContent = { 
                Text(
                    song.title, 
                    fontWeight = FontWeight.Bold, 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge
                ) 
            },
            supportingContent = { 
                Text(
                    "${song.artist} • ${song.album}", 
                    maxLines = 1, 
                    overflow = TextOverflow.Ellipsis,
                    style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall
                ) 
            },
            leadingContent = {
                AlbumArt(song.albumArtUri, imageSize, artShape)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
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
    onDeletePlaylist: (com.melody.data.Playlist) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }

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
                Button(onClick = {
                    if (playlistName.isNotBlank()) {
                        onCreatePlaylist(playlistName)
                        playlistName = ""
                        showAddDialog = false
                    }
                }) {
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
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(playlists, key = { it.id }) { playlist ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        ListItem(
                            headlineContent = { Text(playlist.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Playlist", style = MaterialTheme.typography.bodySmall) },
                            leadingContent = {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            },
                            trailingContent = {
                                IconButton(onClick = { onDeletePlaylist(playlist) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
            override fun onRouteAdded(router: androidx.mediarouter.media.MediaRouter?, route: androidx.mediarouter.media.MediaRouter.RouteInfo?) {
                routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
            }
            override fun onRouteRemoved(router: androidx.mediarouter.media.MediaRouter?, route: androidx.mediarouter.media.MediaRouter.RouteInfo?) {
                routes = mediaRouter.routes.filter { it.isEnabled && it.matchesSelector(selector) }
            }
            override fun onRouteChanged(router: androidx.mediarouter.media.MediaRouter?, route: androidx.mediarouter.media.MediaRouter.RouteInfo?) {
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

@Composable
fun RainEffect(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    amplitude: Float = 0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RainTransition")
    val rainPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RainPhase"
    )

    val raindrops = remember { 
        List(100) { 
            object {
                val x = (0..1000).random() / 1000f
                val size = (5..15).random().toFloat()
                val speed = (10..30).random().toFloat()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (isPlaying) {
            val h = size.height
            val w = size.width
            val baseSpeed = 1000f // pixels per second
            val rhythmicSpeedFactor = 1f + (amplitude * 2f)

            raindrops.forEach { drop ->
                val startX = drop.x * w
                val currentY = ((rainPhase * drop.speed * rhythmicSpeedFactor * 100) % h)
                
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(startX, currentY),
                    end = Offset(startX, currentY + drop.size + (amplitude * 20f)),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
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
    val folderOffset by animateDpAsState(
        targetValue = if (enableRhythmicFolder && isPlaying) (audioAmplitude * 100).dp else 0.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "FolderMovement"
    )

    val folderRotation by animateFloatAsState(
        targetValue = if (enableRhythmicFolder && isPlaying) audioAmplitude * 45f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "FolderRotation"
    )

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

    val rhythmScale = if (enableBeatBounce && isPlaying) (1f + audioAmplitude * 0.2f) else 1f

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
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Back", modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1.2f))

                Box(
                    modifier = Modifier.graphicsLayer {
                        val activeBeatScale = if (enableBeatBounce && isPlaying) rhythmScale else 1f
                        scaleX = albumScale * animatedPopScale * activeBeatScale
                        scaleY = albumScale * animatedPopScale * activeBeatScale
                        rotationY = animatedFlip + folderRotation
                        translationY = folderOffset.toPx()
                        cameraDistance = 8 * density
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { showAlbumMenu = true }
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
                        RainEffect(
                            modifier = Modifier.matchParentSize(),
                            isPlaying = isPlaying,
                            amplitude = audioAmplitude
                        )
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
                    // We don't set sliderDragging back to false immediately to prevent jumping
                    // The ViewModel progress update will eventually catch up
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
                    if (abs(currentProgress - localSliderValue) < 0.03f) {
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
fun OnlineSearchDialog(
    musicViewModel: MusicViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results by musicViewModel.onlineSearchResults.collectAsState()
    val isSearching by musicViewModel.isSearchingOnline.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Global Discovery", style = MaterialTheme.typography.titleLarge)
                    if (isSearching) {
                        Text("Connecting to servers...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                if (isSearching) {
                    HyperLoadingIndicator(modifier = Modifier.size(width = 32.dp, height = 4.dp))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search by song name...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = { musicViewModel.performMusicSearch(query) }) {
                                Icon(Icons.Default.Send, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { musicViewModel.performMusicSearch(query) }),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        HyperLoadingIndicator()
                    }
                } else if (results.isNotEmpty()) {
                    Text("Internet Search Results:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(results) { result ->
                            Card(
                                onClick = { musicViewModel.downloadSong(result.downloadUrl, result.title) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(40.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(result.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${result.source} • MP3", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Icon(Icons.Default.Download, contentDescription = "Download", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Language, 
                                contentDescription = null, 
                                modifier = Modifier.size(48.dp).graphicsLayer { alpha = 0.3f }
                             )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Fast Search All Music Sources", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
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
fun FolderList(songs: List<Song>, useCompact: Boolean) {
    val folders = remember(songs) { songs.groupBy { it.folderPath } }
    val shape = MaterialTheme.shapes.extraLarge
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        folders.forEach { (path, folderSongs) ->
            item(key = path) {
                Card(
                    onClick = { /* Folder detail browsing */ },
                    shape = shape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { 
                            Text(
                                path.substringAfterLast("/"), 
                                fontWeight = FontWeight.Bold,
                                style = if (useCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge
                            ) 
                        },
                        supportingContent = { Text("${folderSongs.size} songs • $path", style = MaterialTheme.typography.bodySmall) },
                        leadingContent = { 
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(if (useCompact) 40.dp else 48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Folder, 
                                        contentDescription = null, 
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(if (useCompact) 20.dp else 24.dp)
                                    )
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
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
    isGlassEnabled: Boolean = false,
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
        color = if (isGlassEnabled) MaterialTheme.colorScheme.surface.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = if (isGlassEnabled) 2.dp else 4.dp,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .then(if (isGlassEnabled) Modifier.glassEffect(shape = MaterialTheme.shapes.extraLarge) else Modifier)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconContainerColor,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                trailing()
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            letterSpacing = 1.2.sp
        )
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: ThemeViewModel) {
    val dynamicColor by viewModel.useDynamicColor.collectAsState()
    val primaryColor by viewModel.primaryColor.collectAsState()
    val isGlassEnabled by viewModel.isGlassEffectEnabled.collectAsState()
    val useCompactLayout by viewModel.useCompactLayout.collectAsState()
    val albumArtShapeIndex by viewModel.albumArtShape.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp, bottom = 32.dp)
    ) {
        SettingsGroup(title = "Visual Essence") {
            SettingsItem(
                title = "Dynamic Theming",
                subtitle = "Sync interface with system wallpaper",
                icon = Icons.Rounded.Palette,
                iconContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                iconColor = Color(0xFF2E7D32),
                trailing = {
                    Switch(
                        checked = dynamicColor,
                        onCheckedChange = { viewModel.setDynamicColor(it) }
                    )
                }
            )
            
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            SettingsItem(
                title = "Liquid Glass",
                subtitle = "Vibrant transparency & blur",
                icon = Icons.Rounded.BlurOn,
                iconContainerColor = Color(0xFF2196F3).copy(alpha = 0.2f),
                iconColor = Color(0xFF1976D2),
                trailing = {
                    Switch(
                        checked = isGlassEnabled,
                        onCheckedChange = { viewModel.setGlassEffectEnabled(it) }
                    )
                }
            )
        }

        SettingsGroup(title = "Interface") {
            SettingsItem(
                title = "Compact Mode",
                subtitle = "Maximize content on screen",
                icon = Icons.Rounded.ViewList,
                iconContainerColor = Color(0xFFFF9800).copy(alpha = 0.2f),
                iconColor = Color(0xFFE65100),
                trailing = {
                    Switch(
                        checked = useCompactLayout,
                        onCheckedChange = { viewModel.setCompactLayout(it) }
                    )
                }
            )
        }

        SettingsGroup(title = "Creative Canvas") {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Album Art Geometry", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AlbumArtShapes.forEachIndexed { index, shape ->
                        val isSelected = albumArtShapeIndex == index
                        Surface(
                            onClick = { viewModel.setAlbumArtShape(index) },
                            shape = shape,
                            modifier = Modifier.size(64.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = if (isSelected) 8.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    }
                }
            }

            if (!dynamicColor) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Atmospheric Accent", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
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
        
        SettingsGroup(title = "Information") {
            SettingsItem(
                title = "Melody Stream",
                subtitle = "Stable Build 4.2.1-Liquid",
                icon = Icons.Rounded.Info,
                iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                iconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            SettingsItem(
                title = "Open Source License",
                subtitle = "Inspired by HyperBridge Project",
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
                        Surface(
                            onClick = { /* Clicking could potentially trigger connect, but usually system handles it */ },
                            shape = RoundedCornerShape(12.dp),
                            color = if (device.isConnected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center, 
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            if (device.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, 
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (device.name.lowercase().contains("headphones") || device.name.lowercase().contains("earbuds")) 
                                            Icons.Default.Headset else Icons.Default.Bluetooth,
                                        contentDescription = null,
                                        tint = if (device.isConnected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        device.name, 
                                        style = MaterialTheme.typography.bodyLarge, 
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (device.isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (device.isConnected) {
                                            Text(
                                                "Connected • ", 
                                                style = MaterialTheme.typography.labelSmall, 
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(device.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                if (device.batteryLevel != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (device.batteryLevel > 20) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (device.batteryLevel > 20) Color.Green else Color.Red
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${device.batteryLevel}%", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val intent = android.content.Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pair New")
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
