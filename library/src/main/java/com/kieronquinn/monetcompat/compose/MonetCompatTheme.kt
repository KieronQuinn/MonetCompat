package com.kieronquinn.monetcompat.compose

import androidx.annotation.IntRange
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb


private fun MonetCompat.getMonetNeutralColor(
    @IntRange(from = 1, to = 2) type: Int,
    @IntRange(from = 50, to = 900) shade: Int
): Color {
    val monetColor = when (type) {
        1 -> this.getMonetColors().neutral1[shade]
        else -> this.getMonetColors().neutral2[shade]
    }?.toArgb() ?: throw Exception("Neutral$type shade $shade doesn't exist")
    return Color(monetColor)
}

private fun MonetCompat.getMonetAccentColor(
    @IntRange(from = 1, to = 3) type: Int,
    @IntRange(from = 50, to = 900) shade: Int
): Color {
    val monetColor = when (type) {
        1 -> this.getMonetColors().accent1[shade]
        2 -> this.getMonetColors().accent2[shade]
        else -> this.getMonetColors().accent3[shade]
    }?.toArgb() ?: throw Exception("Accent$type shade $shade doesn't exist")
    return Color(monetColor)
}

/**
 * Any values that are not set will be chosen to best represent default values given by [dynamicLightColorScheme][androidx.compose.material3.dynamicLightColorScheme]
 * on Android 12+ devices
 */
@Composable
fun MonetCompat.lightMonetCompatScheme(
    primary: Color = getMonetAccentColor(1, 700),
    onPrimary: Color = getMonetNeutralColor(1, 50),
    primaryContainer: Color = getMonetAccentColor(2, 100),
    onPrimaryContainer: Color = getMonetAccentColor(1, 900),
    inversePrimary: Color = getMonetAccentColor(1, 200),
    secondary: Color = getMonetAccentColor(2, 700),
    onSecondary: Color = getMonetNeutralColor(1, 50),
    secondaryContainer: Color = getMonetAccentColor(2, 100),
    onSecondaryContainer: Color = getMonetAccentColor(2, 900),
    tertiary: Color = getMonetAccentColor(3, 600),
    onTertiary: Color = getMonetNeutralColor(1, 50),
    tertiaryContainer: Color = getMonetAccentColor(3, 100),
    onTertiaryContainer: Color = getMonetAccentColor(3, 900),
    background: Color = getMonetNeutralColor(1, 50),
    onBackground: Color = getMonetNeutralColor(1, 900),
    surface: Color = getMonetNeutralColor(1, 50),
    onSurface: Color = getMonetNeutralColor(1, 900),
    surfaceVariant: Color = getMonetNeutralColor(2, 100),
    onSurfaceVariant: Color = getMonetNeutralColor(2, 700),
    inverseSurface: Color = getMonetNeutralColor(1, 800),
    inverseOnSurface: Color = getMonetNeutralColor(2, 50),
    outline: Color = getMonetAccentColor(2, 500),
): ColorScheme =
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
    )

/**
 * Any values that are not set will be chosen to best represent default values given by [dynamicDarkColorScheme][androidx.compose.material3.dynamicDarkColorScheme]
 * on Android 12+ devices
 */
@Composable
fun MonetCompat.darkMonetCompatScheme(
    primary: Color = getMonetAccentColor(1, 200),
    onPrimary: Color = getMonetAccentColor(1, 800),
    primaryContainer: Color = getMonetAccentColor(1, 600),
    onPrimaryContainer: Color = getMonetAccentColor(2, 100),
    inversePrimary: Color = getMonetAccentColor(1, 600),
    secondary: Color = getMonetAccentColor(2, 200),
    onSecondary: Color = getMonetAccentColor(2, 800),
    secondaryContainer: Color = getMonetAccentColor(2, 700),
    onSecondaryContainer: Color = getMonetAccentColor(2, 100),
    tertiary: Color = getMonetAccentColor(3, 200),
    onTertiary: Color = getMonetAccentColor(3, 700),
    tertiaryContainer: Color = getMonetAccentColor(3, 700),
    onTertiaryContainer: Color = getMonetAccentColor(3, 100),
    background: Color = getMonetNeutralColor(1, 900),
    onBackground: Color = getMonetNeutralColor(1, 100),
    surface: Color = getMonetNeutralColor(1, 900),
    onSurface: Color = getMonetNeutralColor(1, 100),
    surfaceVariant: Color = getMonetNeutralColor(2, 700),
    onSurfaceVariant: Color = getMonetNeutralColor(2, 200),
    inverseSurface: Color = getMonetNeutralColor(1, 100),
    inverseOnSurface: Color = getMonetNeutralColor(1, 800),
    outline: Color = getMonetNeutralColor(2, 500),
): ColorScheme =
    darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        tertiaryContainer = tertiaryContainer,
        onTertiaryContainer = onTertiaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
    )

/**
 * Monet Compat Dynamic Theme aims to recreate dynamic color theme provided by [androidx.compose.material3]
 *
 * This theme will use default values chosen to best recreate values provided
 * by default values of [dynamicDarkColorScheme][androidx.compose.material3.dynamicDarkColorScheme] and [lightColorScheme][androidx.compose.material3.lightColorScheme]
 *
 * If you want to set custom colors to certain values use [MonetCompat.darkMonetCompatScheme] and
 * [MonetCompat.lightMonetCompatScheme] with [androidx.compose.material3.MaterialTheme]
 *
 * @param monet A monet object passed from your activity
 */
@Composable
fun MonetCompatDynamicTheme(monet: MonetCompat, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            monet.darkMonetCompatScheme()
        } else {
            monet.lightMonetCompatScheme()
        }, content = content
    )
}
