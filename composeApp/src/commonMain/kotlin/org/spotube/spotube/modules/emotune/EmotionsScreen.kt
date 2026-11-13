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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.composeunstyled.Text
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.spotube.plugin_interfaces.plugin_apis.metadata.search.MetadataSupportedSearchType
import org.spotube.plugin_interfaces.plugin_apis.metadata.track.MetadataTrack
import org.spotube.spotube.core.audioplayer.AudioPlayerQueue
import org.spotube.spotube.core.audioplayer.QueueEntry
import org.spotube.spotube.modules.search.SearchScreenViewModel
import spotube.composeapp.generated.resources.Res
import spotube.composeapp.generated.resources.emotune_all_emotions
import spotube.composeapp.generated.resources.emotune_how_do_you_feel
import spotube.composeapp.generated.resources.emotune_how_do_you_feel_desc
import spotube.composeapp.generated.resources.emotune_load_more
import spotube.composeapp.generated.resources.emotune_tracks_count

/**
 * EMOTUNE home: "¿Cómo te sientes hoy?" banner, emotion chips and a grid of track cards.
 * Tracks come from Spotube's own metadata plugin (search), played by Spotube's own player.
 */
@Composable
fun EmotionsScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel = koinViewModel(),
    audioPlayerQueue: AudioPlayerQueue = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<Emotion?>(null) }
    val tracks = state.tracks.items

    fun play(track: MetadataTrack) {
        scope.launch {
            audioPlayerQueue.load(
                entries = tracks.map { QueueEntry.StreamingTrack(track = it, url = "") },
                autoPlay = true,
                startPosition = tracks.indexOf(track).coerceAtLeast(0),
                collectionEntry = null
            )
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(168.dp),
        modifier = modifier.fillMaxSize().background(EmotuneColors.AppGradient),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(EmotuneColors.Card.copy(alpha = 0.6f))
                    .border(1.dp, EmotuneColors.Hairline, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Text(
                    stringResource(Res.string.emotune_how_do_you_feel), color = EmotuneColors.Text,
                    fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(Res.string.emotune_how_do_you_feel_desc),
                    color = EmotuneColors.Muted, fontSize = 14.sp
                )
                Spacer(Modifier.height(20.dp))
                EmotionChips(selected) { emotion ->
                    selected = emotion
                    viewModel.onTabSelected(MetadataSupportedSearchType.TRACK)
                    viewModel.onQueryChange(emotion.query)
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    selected?.label ?: stringResource(Res.string.emotune_all_emotions),
                    color = EmotuneColors.Text, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(Res.string.emotune_tracks_count, tracks.size),
                    color = EmotuneColors.Muted, fontSize = 12.sp
                )
            }
        }
        items(tracks, key = { it.id }) { track ->
            TrackCard(track, selected?.color ?: EmotuneColors.Accent) { play(track) }
        }
        if (state.tracks.nextPagination != null && !state.tracks.isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(Res.string.emotune_load_more), color = EmotuneColors.OnAccent,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clip(CircleShape).background(EmotuneColors.Accent)
                            .clickable { viewModel.loadNextTracks() }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionChips(selected: Emotion?, onSelect: (Emotion) -> Unit) {
    // Two rows of four, like the 8-column chip grid in the HTML.
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Emotion.entries.chunked(4).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { emotion ->
                    val isSelected = emotion == selected
                    Box(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(emotion.color.copy(alpha = if (isSelected) 0.32f else 0.14f))
                            .border(
                                1.dp, emotion.color.copy(alpha = if (isSelected) 0.9f else 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelect(emotion) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            emotion.label, color = emotion.color, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackCard(track: MetadataTrack, tag: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val cover = track.thumbnails?.maxByOrNull { it.width }?.url
        ?: track.album?.thumbnails?.maxByOrNull { it.width }?.url
    Column(
        Modifier.clip(RoundedCornerShape(16.dp))
            .background(EmotuneColors.Card)
            .border(1.dp, EmotuneColors.Hairline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        AsyncImage(
            model = cover, contentDescription = track.title, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp))
                .background(EmotuneColors.Selected)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            track.title, color = EmotuneColors.Text, fontSize = 14.sp, fontWeight = FontWeight.Bold,
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Text(
            track.artists.joinToString(", ") { it.name }, color = EmotuneColors.Muted, fontSize = 12.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Box(Modifier.size(width = 36.dp, height = 4.dp).clip(CircleShape).background(tag))
    }
}
