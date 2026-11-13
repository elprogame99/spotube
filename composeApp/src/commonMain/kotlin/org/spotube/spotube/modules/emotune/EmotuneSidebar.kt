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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.spotube.millennium.composables.MillenniumIcon
import org.spotube.spotube.core.navigation.NavigationState
import org.spotube.spotube.core.navigation.Navigator
import org.spotube.spotube.core.navigation.Routes
import org.spotube.spotube.modules.library.LibraryState
import org.spotube.spotube.modules.library.LibraryTab
import org.spotube.spotube.resources.iconsax.Iconsax
import org.spotube.spotube.resources.iconsax.IconsaxAddSquare
import org.spotube.spotube.resources.iconsax.IconsaxHeart2
import org.spotube.spotube.resources.iconsax.IconsaxHome
import org.spotube.spotube.resources.iconsax.IconsaxMagic
import org.spotube.spotube.resources.iconsax.IconsaxMusicDashboard
import org.spotube.spotube.resources.iconsax.IconsaxMusicLibrary
import org.spotube.spotube.resources.iconsax.IconsaxSearch
import org.spotube.spotube.resources.iconsax.IconsaxSetting2
import org.spotube.spotube.resources.iconsax.IconsaxSoundTwotone
import spotube.composeapp.generated.resources.Res
import spotube.composeapp.generated.resources.emotune_add_music
import spotube.composeapp.generated.resources.emotune_brand_subtitle
import spotube.composeapp.generated.resources.emotune_nav_all_music
import spotube.composeapp.generated.resources.emotune_nav_collection
import spotube.composeapp.generated.resources.emotune_nav_discover
import spotube.composeapp.generated.resources.emotune_nav_emotions
import spotube.composeapp.generated.resources.emotune_nav_favorites
import spotube.composeapp.generated.resources.emotune_nav_playlists
import spotube.composeapp.generated.resources.emotune_nav_search
import spotube.composeapp.generated.resources.emotune_search_hint
import spotube.composeapp.generated.resources.settings_screen_title

/** EMOTUNE sidebar: logo, quick search, "Descubrir" / "Mi Colección" sections, add-music CTA. */
@Composable
fun EmotuneSidebar(
    navigator: Navigator,
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
    libraryState: LibraryState = koinInject()
) {
    val currentLibraryTab by libraryState.currentTab.collectAsState()
    val route = navigationState.topLevelRoute

    Column(
        modifier = modifier
            .width(256.dp)
            .fillMaxHeight()
            .background(EmotuneColors.Panel)
    ) {
        // Logo
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(EmotuneColors.Accent),
                contentAlignment = Alignment.Center
            ) {
                MillenniumIcon(
                    Iconsax.IconsaxSoundTwotone, contentDescription = null,
                    tint = EmotuneColors.OnAccent, modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    "EMOTUNE", color = EmotuneColors.Text, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp
                )
                Text(
                    stringResource(Res.string.emotune_brand_subtitle).uppercase(),
                    color = EmotuneColors.Muted, fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp
                )
            }
        }

        // Quick search pill -> opens the Search screen
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(EmotuneColors.Card)
                .clickable { navigator.navigate(Routes.Search) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MillenniumIcon(
                Iconsax.IconsaxSearch, contentDescription = null,
                tint = EmotuneColors.Muted, modifier = Modifier.size(16.dp)
            )
            Text(
                stringResource(Res.string.emotune_search_hint),
                color = EmotuneColors.Muted, fontSize = 12.sp, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SectionLabel(stringResource(Res.string.emotune_nav_discover))
            NavRow(
                stringResource(Res.string.emotune_nav_emotions), Iconsax.IconsaxMagic,
                EmotuneColors.Accent, route == Routes.Home
            ) { navigator.navigate(Routes.Home) }
            NavRow(
                stringResource(Res.string.emotune_nav_search),
                Iconsax.IconsaxSearch, Color(0xFF818CF8), route == Routes.Search
            ) { navigator.navigate(Routes.Search) }

            Spacer(Modifier.height(16.dp))
            SectionLabel(stringResource(Res.string.emotune_nav_collection))
            NavRow(
                stringResource(Res.string.emotune_nav_all_music), Iconsax.IconsaxMusicLibrary,
                Color(0xFF34D399), route == Routes.Library && currentLibraryTab == LibraryTab.LocalTracks
            ) {
                libraryState.currentTab.value = LibraryTab.LocalTracks
                navigator.navigate(Routes.Library)
            }
            NavRow(
                stringResource(Res.string.emotune_nav_favorites), Iconsax.IconsaxHeart2,
                EmotuneColors.Heart, route == Routes.SavedTracks
            ) { navigator.navigate(Routes.SavedTracks) }
            NavRow(
                stringResource(Res.string.emotune_nav_playlists), Iconsax.IconsaxMusicDashboard,
                Color(0xFFFBBF24), route == Routes.Library && currentLibraryTab == LibraryTab.Playlists
            ) {
                libraryState.currentTab.value = LibraryTab.Playlists
                navigator.navigate(Routes.Library)
            }
            NavRow(
                stringResource(Res.string.settings_screen_title), Iconsax.IconsaxSetting2,
                EmotuneColors.Muted, route == Routes.Settings
            ) { navigator.navigate(Routes.Settings) }
        }

        // "Añadir música o vídeos" -> Spotube local tracks (folder scanning lives there)
        Box(Modifier.fillMaxWidth().height(1.dp).background(EmotuneColors.Hairline))
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EmotuneColors.Accent)
                .clickable {
                    libraryState.currentTab.value = LibraryTab.LocalTracks
                    navigator.navigate(Routes.Library)
                }
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MillenniumIcon(
                Iconsax.IconsaxAddSquare, contentDescription = null,
                tint = EmotuneColors.OnAccent, modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(Res.string.emotune_add_music), color = EmotuneColors.OnAccent,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text.uppercase(), color = EmotuneColors.Subtle, fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun NavRow(
    label: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) EmotuneColors.Selected else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MillenniumIcon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Text(
            label, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1,
            color = if (selected) EmotuneColors.Text else EmotuneColors.Muted
        )
    }
}
