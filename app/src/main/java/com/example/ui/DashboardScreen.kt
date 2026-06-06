package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.api.TikWmVideoData
import com.example.data.local.DownloadHistoryItem

// Elite Color Palette (Obsidian Black & Teal-Cyan Accent Glow)
private val ObsidianBackground = Color(0xFF090A0F)
private val SurfaceGrey = Color(0xFF14161D)
private val CardBorderGrey = Color(0xFF232731)
private val GlowingCyan = Color(0xFF00F2FE)
private val DeepTeal = Color(0xFF4FACFE)
private val TextWhite = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF8A909C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val urlInput by viewModel.inputUrl.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val downloadHistory by viewModel.historyState.collectAsState()

    var previewVideoUrl by remember { mutableStateOf<String?>(null) }

    // Auto load clipboard link on start
    LaunchedEffect(Unit) {
        viewModel.tryLoadUrlFromClipboard(context)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Premium Header Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TIKDOWNLOAD",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.testTag("app_title"),
                        style = TextStyle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GlowingCyan, DeepTeal)
                            )
                        )
                    )
                    Text(
                        text = "Premium Video Downloader",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Small clear history indicator if lists are filled
                if (downloadHistory.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearAllHistory() },
                        modifier = Modifier
                            .background(SurfaceGrey, CircleShape)
                            .size(38.dp)
                            .testTag("clear_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear All History",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Link Input & Quick Operations Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
                        border = BorderStroke(1.dp, CardBorderGrey)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Paste TikTok link below",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Glass Custom Input Field
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { viewModel.onUrlChange(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("url_input_field"),
                                placeholder = { Text("https://www.tiktok.com/...", color = TextMuted) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Link,
                                        contentDescription = "Link Icon",
                                        tint = GlowingCyan
                                    )
                                },
                                trailingIcon = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (urlInput.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.clearInput() }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear",
                                                    tint = TextMuted
                                                )
                                            }
                                        }
                                        Button(
                                            onClick = { viewModel.tryLoadUrlFromClipboard(context) },
                                            modifier = Modifier
                                                .padding(end = 4.dp)
                                                .height(32.dp)
                                                .testTag("paste_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262A36)),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentPaste,
                                                contentDescription = "Paste Clipboard",
                                                tint = GlowingCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Paste", color = TextWhite, fontSize = 11.sp)
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Uri,
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(onSearch = {
                                    keyboardController?.hide()
                                    viewModel.searchVideo()
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GlowingCyan,
                                    unfocusedBorderColor = CardBorderGrey,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = ObsidianBackground,
                                    unfocusedContainerColor = ObsidianBackground
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Large Glowing Core Parse Button
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    viewModel.searchVideo()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("parse_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(DeepTeal, GlowingCyan)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDownload,
                                            contentDescription = "Download Arrow",
                                            tint = ObsidianBackground,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "FETCH & DOWNLOAD",
                                            color = ObsidianBackground,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Parser Result Section with Animation
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            when (val state = uiState) {
                                is SearchUiState.Idle -> {
                                    // Nothing shown here
                                }
                                is SearchUiState.Loading -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = GlowingCyan)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Analyzing video link...",
                                            color = TextMuted,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                is SearchUiState.Success -> {
                                    VideoDetailCard(
                                        videoData = state.videoData,
                                        onPlayClick = { previewVideoUrl = state.videoData.play },
                                        onDownloadNoWm = { viewModel.downloadVideoNoWatermark(context, state.videoData) },
                                        onDownloadW_Wm = { viewModel.downloadVideoWatermark(context, state.videoData) },
                                        onDownloadMp3 = { viewModel.downloadAudioMp3(context, state.videoData) }
                                    )
                                }
                                is SearchUiState.Error -> {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1515)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = "Error icon",
                                                tint = Color.Red,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = state.message,
                                                color = Color(0xFFFFB4AB),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Historical Downloads Catalog Header
                item {
                    Text(
                        text = "RECENT DOWNLOADS",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
                    )
                }

                if (downloadHistory.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryToggleOff,
                                contentDescription = "History template",
                                tint = TextMuted.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your download history is clean.",
                                color = TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Successfully downloaded tracks appear here.",
                                color = TextMuted.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    items(downloadHistory) { historyItem ->
                        HistoryRowItem(
                            item = historyItem,
                            onPlayClick = { previewVideoUrl = historyItem.playUrl },
                            onDeleteClick = { viewModel.deleteHistoryItem(historyItem.id) }
                        )
                    }
                }
            }
        }

        // Floating full cover video preview player
        previewVideoUrl?.let { url ->
            VideoPreviewPlayerDialog(
                videoUrl = url,
                onDismiss = { previewVideoUrl = null }
            )
        }
    }
}

// Result Presentation Card (M3 Glass UI)
@Composable
fun VideoDetailCard(
    videoData: TikWmVideoData,
    onPlayClick: () -> Unit,
    onDownloadNoWm: () -> Unit,
    onDownloadW_Wm: () -> Unit,
    onDownloadMp3: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("result_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, CardBorderGrey)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Upper Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overlay Thumbnail Player
                Box(
                    modifier = Modifier
                        .size(100.dp, 130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .clickable { onPlayClick() }
                        .testTag("media_thumbnail_preview")
                ) {
                    AsyncImage(
                        model = videoData.cover,
                        contentDescription = "Video cover image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Play Vector
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Preview Play",
                            tint = GlowingCyan,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // Duration Tag
                    videoData.duration?.let { seconds ->
                        val durationText = "${seconds / 60}:${String.format("%02d", seconds % 60)}"
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = durationText,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Text Description Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Author information
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        AsyncImage(
                            model = videoData.author?.avatar,
                            contentDescription = "Author Avatar",
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(
                                text = videoData.author?.nickname ?: "Unknown",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "@${videoData.author?.unique_id ?: "unknown"}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Video description
                    Text(
                        text = videoData.title ?: "No description provided.",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row (Views, hearts, shares, downloads)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianBackground, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(imageVector = Icons.Default.PlayArrow, value = formatCount(videoData.play_count))
                StatItem(imageVector = Icons.Default.Favorite, value = formatCount(videoData.digg_count))
                StatItem(imageVector = Icons.Default.Chat, value = formatCount(videoData.comment_count))
                StatItem(imageVector = Icons.Default.Share, value = formatCount(videoData.share_count))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            // 1. Watermark-Free Direct Glowing DL Button
            Button(
                onClick = onDownloadNoWm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("download_no_watermark_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GlowingCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download no watermark icon",
                    tint = ObsidianBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DOWNLOAD VIDEO (NO WATERMARK)",
                    color = ObsidianBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dual Secondary triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Watermark-Included DL Button
                OutlinedButton(
                    onClick = onDownloadW_Wm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("download_with_watermark_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, CardBorderGrey),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = "Watermark tag",
                        modifier = Modifier.size(16.dp),
                        tint = TextWhite
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WITH WATERMARK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Audio Only MP3 Button
                OutlinedButton(
                    onClick = onDownloadMp3,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("download_music_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GlowingCyan),
                    border = BorderStroke(1.dp, CardBorderGrey),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Audio tag",
                        modifier = Modifier.size(16.dp),
                        tint = GlowingCyan
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "DOWNLOAD AUDIO MP3",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Single Stat layout widget
@Composable
fun StatItem(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Stat indicator",
            tint = TextMuted,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// History row logging layout
@Composable
fun HistoryRowItem(
    item: DownloadHistoryItem,
    onPlayClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
        border = BorderStroke(1.dp, CardBorderGrey)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail with click play action
            Box(
                modifier = Modifier
                    .size(60.dp, 75.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black)
                    .clickable { onPlayClick() }
            ) {
                AsyncImage(
                    model = item.coverUrl,
                    contentDescription = "cover thumb",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play icon",
                        tint = GlowingCyan,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Texts details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title.ifEmpty { "TikTok Video" },
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "By @${item.authorUsername}",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Operation columns (Share + Delete)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Share button
                IconButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out this video: ${item.title}\n\nLink: ${item.playUrl}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = LightGrayAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Preview dialogue wrapper carrying native VideoView
@Composable
fun VideoPreviewPlayerDialog(
    videoUrl: String,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceGrey),
            border = BorderStroke(1.dp, CardBorderGrey)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal header bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIDEO PREVIEW",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 14.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(ObsidianBackground, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close player modal",
                            tint = TextWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Media player viewports
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            android.widget.VideoView(ctx).apply {
                                val controller = android.widget.MediaController(ctx)
                                controller.setAnchorView(this)
                                setMediaController(controller)
                                setVideoURI(Uri.parse(videoUrl))
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// Utility function to support millions or thousands formats
private fun formatCount(number: Int?): String {
    if (number == null) return "0"
    return when {
        number >= 1_000_000 -> "${String.format("%.1f", number / 1_000_000.0)}M"
        number >= 1_000 -> "${String.format("%.1f", number / 1_000.0)}K"
        else -> number.toString()
    }
}

// Helper styling accent
private val LightGrayAccent = Color(0xFFA1AEC4)
