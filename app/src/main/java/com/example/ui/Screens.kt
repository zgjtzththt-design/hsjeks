package com.example.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.data.Song

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
    
    if (showOnlineSearch) {
        OnlineSearchDialog(
            musicViewModel = musicViewModel,
            onDismiss = { showOnlineSearch = false }
        )
    }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

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
            Scaffold(
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
                            onSongClick = { musicViewModel.playSong(it) }
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
    }
}

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
        
        LazyColumn(contentPadding = PaddingValues(bottom = 8.dp)) {
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
    playlists: List<com.example.data.Playlist>,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (com.example.data.Playlist) -> Unit
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
fun AlbumArt(uri: String?, size: androidx.compose.ui.unit.Dp, shape: Shape = MaterialTheme.shapes.medium) {
    Surface(
        modifier = Modifier
            .size(size)
            .clip(shape),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (uri != null) {
            SubcomposeAsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { PlaceholderArt() },
                error = { PlaceholderArt() }
            )
        } else {
            PlaceholderArt()
        }
    }
}

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

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
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

            Spacer(modifier = Modifier.weight(1f))

            AlbumArt(
                uri = song.albumArtUri,
                size = 300.dp,
                shape = artShape
            )

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Global Music Search") 
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
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
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
            .padding(16.dp)
    ) {
        Text("Appearance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        SettingsToggle(
            title = "Dynamic Color",
            subtitle = "Use colors derived from wallpaper (Android 12+)",
            checked = dynamicColor,
            onCheckedChange = { viewModel.setDynamicColor(it) }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        SettingsToggle(
            title = "Liquid Glass Effect",
            subtitle = "Apply transparent blur to lists and navigation",
            checked = isGlassEnabled,
            onCheckedChange = { viewModel.setGlassEffectEnabled(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsToggle(
            title = "Compact Layout",
            subtitle = "Smaller list items to show more content",
            checked = useCompactLayout,
            onCheckedChange = { viewModel.setCompactLayout(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Album Art Shape", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AlbumArtShapes.forEachIndexed { index, shape ->
                Surface(
                    onClick = { viewModel.setAlbumArtShape(index) },
                    shape = shape,
                    modifier = Modifier.size(56.dp),
                    color = if (albumArtShapeIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (albumArtShapeIndex == index) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }

        if (!dynamicColor) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Accent Color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ColorOption(Color(0xFF673AB7), primaryColor == Color(0xFF673AB7)) { viewModel.setPrimaryColor(Color(0xFF673AB7)) } // Purple
                ColorOption(Color(0xFF2196F3), primaryColor == Color(0xFF2196F3)) { viewModel.setPrimaryColor(Color(0xFF2196F3)) } // Blue
                ColorOption(Color(0xFFF44336), primaryColor == Color(0xFFF44336)) { viewModel.setPrimaryColor(Color(0xFFF44336)) } // Red
                ColorOption(Color(0xFF4CAF50), primaryColor == Color(0xFF4CAF50)) { viewModel.setPrimaryColor(Color(0xFF4CAF50)) } // Green
                ColorOption(Color(0xFFFF9800), primaryColor == Color(0xFFFF9800)) { viewModel.setPrimaryColor(Color(0xFFFF9800)) } // Orange
                ColorOption(Color(0xFFE91E63), primaryColor == Color(0xFFE91E63)) { viewModel.setPrimaryColor(Color(0xFFE91E63)) } // Pink
            }
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
