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

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.spotube.spotube.core.ui.theming.AppColors

/** Design tokens ported from the EMOTUNE interface (theme-green, zinc-950 base). */
object EmotuneColors {
    val Background = Color(0xFF09090B)
    val GradientFrom = Color(0xFF064E3B)
    val Panel = Color(0xFF0C0C0F)
    val Card = Color(0xFF18181B)
    val Accent = Color(0xFF10B981)
    val AccentHover = Color(0xFF059669)
    val OnAccent = Color(0xFF07140B)
    val Text = Color(0xFFF4F4F5)
    val Muted = Color(0xFFA1A1AA)
    val Subtle = Color(0xFF71717A)
    val Hairline = Color.White.copy(alpha = 0.08f)
    val Selected = Color(0xFF242424)
    val Heart = Color(0xFFFB7185)

    /** Equivalent of .app-gradient in the HTML. */
    val AppGradient = Brush.verticalGradient(listOf(GradientFrom.copy(alpha = 0.55f), Background))
}

/** Emotions from the HTML (EMOTIONS array + HUE map) with a search query for each one. */
enum class Emotion(val label: String, val color: Color, val query: String) {
    Felicidad("Felicidad", Color(0xFFFBBF24), "música feliz alegre"),
    Tristeza("Tristeza", Color(0xFF60A5FA), "música triste baladas tristes"),
    Amor("Amor", Color(0xFFFB7185), "canciones de amor románticas"),
    Nostalgia("Nostalgia", Color(0xFFC084FC), "música nostalgia recuerdos"),
    Tranquilidad("Tranquilidad", Color(0xFF34D399), "música relajante tranquila"),
    Energia("Energía", Color(0xFFFB923C), "música energética para entrenar"),
    Motivacion("Motivación", Color(0xFFF87171), "música motivación"),
    Desamor("Desamor", Color(0xFF818CF8), "canciones de desamor")
}

/** Maps the EMOTUNE palette onto Spotube's semantic colour roles. */
fun emotuneAppColors(base: AppColors): AppColors = base.copy(
    primary = EmotuneColors.Accent,
    onPrimary = EmotuneColors.OnAccent,
    secondary = EmotuneColors.Accent,
    onSecondary = EmotuneColors.OnAccent,
    primaryContainer = EmotuneColors.Card,
    onPrimaryContainer = EmotuneColors.Text,
    secondaryContainer = EmotuneColors.Card,
    onSecondaryContainer = EmotuneColors.Text,
    tertiaryContainer = EmotuneColors.Card,
    onTertiaryContainer = EmotuneColors.Text,
    background = EmotuneColors.Background,
    onBackground = EmotuneColors.Text,
    surface = EmotuneColors.Panel,
    onSurface = EmotuneColors.Text,
    surfaceVariant = EmotuneColors.Card,
    onSurfaceVariant = EmotuneColors.Muted,
    surfaceContainer = EmotuneColors.Panel,
    surfaceContainerHigh = EmotuneColors.Card,
    surfaceContainerHighest = EmotuneColors.Card,
    outline = EmotuneColors.Hairline,
    outlineVariant = EmotuneColors.Hairline
)
