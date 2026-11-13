/*
 * Copyright (C) 2026 Kingkor Roy Tirtho and Spotube Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spotube.spotube.modules.emotune

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.composeunstyled.Text
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.spotube.millennium.composables.MillenniumIcon
import org.spotube.spotube.core.audioplayer.AudioPlayerInterface
import org.spotube.spotube.core.audioplayer.AudioPlayerQueue
import org.spotube.spotube.core.audioplayer.LoopState
import org.spotube.spotube.core.jam.JamRole
import org.spotube.spotube.core.jam.JamRoomService
import org.spotube.spotube.modules.saved_tracks.SAVED_TRACKS_COLLECTION_ID
import org.spotube.spotube.modules.saved_tracks.SavedTracksViewModel
import org.spotube.spotube.modules.shell.PlayerHeartButton
import org.spotube.spotube.modules.shell.formatPlayerTime
import org.spotube.spotube.modules.shell.rememberPlayerUiState
import org.spotube.spotube.resources.iconsax.Iconsax
import org.spotube.spotube.resources.iconsax.IconsaxMusicFilter
import org.spotube.spotube.resources.iconsax.IconsaxNext
import org.spotube.spotube.resources.iconsax.IconsaxPause
import org.spotube.spotube.resources.iconsax.IconsaxPlay
import org.spotube.spotube.resources.iconsax.IconsaxPrevious
import org.spotube.spotube.resources.iconsax.IconsaxRepeatMusic
import org.spotube.spotube.resources.iconsax.IconsaxRepeateMusic
import org.spotube.spotube.resources.iconsax.IconsaxRepeateOne
import org.spotube.spotube.resources.iconsax.IconsaxShuffle
import org.spotube.spotube.resources.iconsax.IconsaxVolumeCross
import org.spotube.spotube.resources.iconsax.IconsaxVolumeHigh
import org.spotube.spotube.resources.iconsax.IconsaxVolumeLow
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.map
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * EMOTUNE bottom player: cover + title on the left, shuffle/prev/play/next/loop with the seek
 * bar in the centre, favourite/queue/volume on the right. Drives Spotube's AudioPlayerInterface.
 */
@Composable
fun EmotunePlayerBar(
    modifier: Modifier = Modifier,
    onQueue: () -> Unit = {},
    audioPlayer: AudioPlayerInterface = koinInject(),
    audioPlayerQueue: AudioPlayerQueue = koinInject(),
    savedTracksViewModel: SavedTracksViewModel = koinViewModel<SavedTracksViewModel>(
        key = SAVED_TRACKS_COLLECTION_ID,
        parameters = { parametersOf() }
    )
) {
    val ui = rememberPlayerUiState(audioPlayer, audioPlayerQueue)
    val jamRoomService: JamRoomService = koinInject()
    val isJamGuest by jamRoomService.role.map { it == JamRole.Guest }
        .collectAsStateWithLifecycle(initialValue = false)
    val scope = rememberCoroutineScope()
    var isSeeking by remember { mutableStateOf(false) }
    var seekProgress by remember { mutableFloatStateOf(ui.progress) }
    var lastVolume by remember { mutableFloatStateOf(if (ui.volume > 0f) ui.volume else 0.6f) }

    LaunchedEffect(ui.progress, isSeeking) { if (!isSeeking) seekProgress = ui.progress }
    LaunchedEffect(ui.volume) { if (ui.volume > 0f) lastVolume = ui.volume }

    Column(modifier.windowInsetsPadding(WindowInsets.navigationBars).background(Color.Black)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF282828)))
        Row(
            Modifier.fillMaxWidth().height(96.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track details (w-1/4)
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ui.coverModel, contentDescription = ui.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
                        .background(EmotuneColors.Card)
                        .border(1.dp, EmotuneColors.Hairline, RoundedCornerShape(12.dp))
                )
                Column(Modifier.weight(1f, fill = false).padding(start = 16.dp)) {
                    Text(
                        ui.title, color = EmotuneColors.Text, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        ui.artists, color = EmotuneColors.Muted, fontSize = 12.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(8.dp))
                PlayerHeartButton(audioPlayerQueue, savedTracksViewModel)
            }

            // Controls (w-2/4)
            Column(
                Modifier.weight(2f).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconTap(
                        Iconsax.IconsaxShuffle, 16.dp,
                        if (ui.isShuffling) EmotuneColors.Accent else EmotuneColors.Subtle
                    ) { if (!isJamGuest) scope.launch { audioPlayer.shuffle(!ui.isShuffling) } }
                    IconTap(Iconsax.IconsaxPrevious, 20.dp, Color(0xFFD4D4D8)) {
                        if (!isJamGuest) scope.launch { audioPlayer.skipToPrevious() }
                    }
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(EmotuneColors.Accent)
                            .clickable {
                                scope.launch { if (ui.isPlaying) audioPlayer.pause() else audioPlayer.play() }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        MillenniumIcon(
                            if (ui.isPlaying) Iconsax.IconsaxPause else Iconsax.IconsaxPlay,
                            contentDescription = null, tint = EmotuneColors.OnAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconTap(Iconsax.IconsaxNext, 20.dp, Color(0xFFD4D4D8)) {
                        if (!isJamGuest) scope.launch { audioPlayer.skipToNext() }
                    }
                    IconTap(
                        when (ui.loopState) {
                            LoopState.NONE -> Iconsax.IconsaxRepeateMusic
                            LoopState.ONE -> Iconsax.IconsaxRepeateOne
                            LoopState.ALL -> Iconsax.IconsaxRepeatMusic
                        }, 16.dp,
                        if (ui.loopState == LoopState.NONE) EmotuneColors.Subtle else EmotuneColors.Accent
                    ) { if (!isJamGuest) scope.launch { audioPlayer.loop(ui.loopState.next()) } }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    Modifier.fillMaxWidth().widthIn(max = 560.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        formatPlayerTime(ui.position), color = EmotuneColors.Muted, fontSize = 11.sp,
                        modifier = Modifier.width(40.dp)
                    )
                    EmotuneSlider(
                        value = seekProgress,
                        enabled = ui.seekDuration.inWholeMilliseconds > 0L,
                        modifier = Modifier.weight(1f),
                        onChange = { seekProgress = it; isSeeking = true },
                        onFinished = {
                            isSeeking = false
                            val total = ui.seekDuration.inWholeMilliseconds
                            if (total > 0L) scope.launch {
                                audioPlayer.seekTo((total * seekProgress.coerceIn(0f, 1f)).toLong().milliseconds)
                            }
                        }
                    )
                    Text(
                        formatPlayerTime(ui.displayDuration), color = EmotuneColors.Muted, fontSize = 11.sp,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }

            // Volume & options (w-1/4)
            Row(
                Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconTap(Iconsax.IconsaxMusicFilter, 20.dp, EmotuneColors.Muted, onQueue)
                IconTap(
                    when {
                        ui.volume <= 0f -> Iconsax.IconsaxVolumeCross
                        ui.volume < 0.5f -> Iconsax.IconsaxVolumeLow
                        else -> Iconsax.IconsaxVolumeHigh
                    }, 20.dp, EmotuneColors.Muted
                ) {
                    scope.launch { audioPlayer.setVolume(if (ui.volume <= 0f) lastVolume else 0f) }
                }
                EmotuneSlider(
                    value = ui.volume, modifier = Modifier.width(80.dp),
                    onChange = { lastVolume = it; scope.launch { audioPlayer.setVolume(it) } },
                    onFinished = {}
                )
            }
        }
    }
}

@Composable
private fun IconTap(icon: ImageVector, size: androidx.compose.ui.unit.Dp, tint: Color, onClick: () -> Unit) {
    Box(Modifier.clip(CircleShape).clickable(onClick = onClick).padding(4.dp)) {
        MillenniumIcon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size))
    }
}

/** Thin green slider (6dp track, white thumb) equivalent to input[type=range] in the HTML. */
@Composable
private fun EmotuneSlider(
    value: Float,
    onChange: (Float) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BoxWithConstraints(modifier.height(16.dp), contentAlignment = Alignment.CenterStart) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val v = value.coerceIn(0f, 1f)
        Box(
            Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        )
        Box(
            Modifier.fillMaxWidth(v).height(6.dp).clip(CircleShape)
                .background(if (enabled) EmotuneColors.Accent else EmotuneColors.Subtle)
        )
        Box(
            Modifier.fillMaxSize().pointerInput(enabled, widthPx) {
                if (!enabled) return@pointerInput
                detectTapGestures { offset ->
                    onChange((offset.x / widthPx).coerceIn(0f, 1f)); onFinished()
                }
            }.pointerInput(enabled, widthPx) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragEnd = onFinished, onDragCancel = onFinished
                ) { change, _ ->
                    change.consume()
                    onChange((change.position.x / widthPx).coerceIn(0f, 1f))
                }
            }
        )
    }
}
