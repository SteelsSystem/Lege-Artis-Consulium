package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MicroscopeDarkColorScheme = darkColorScheme(
    primary = CerenkovCyanPrimary,
    onPrimary = Color(0xFF04131D),
    primaryContainer = Color(0xFF0C2D48),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = HelixIndigoSecondary,
    onSecondary = Color(0xFF0F1026),
    secondaryContainer = Color(0xFF1E2246),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = QMaxEmeraldTertiary,
    onTertiary = Color(0xFF022C22),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = ObsidianBackground,
    onBackground = DarkOnSurface,
    surface = InstrumentSlateSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = ElevatedSlateSurface,
    onSurfaceVariant = DarkOnSurfaceMuted,
    error = PathogenicCrimson,
    onError = Color.White
)

private val ClinicalLightColorScheme = lightColorScheme(
    primary = DeepCobaltPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF075985),
    secondary = LegalUltramarineSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF312E81),
    tertiary = ClinicalEmeraldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = AlabasterBackground,
    onBackground = LightOnSurface,
    surface = ClinicalPaperSurface,
    onSurface = LightOnSurface,
    surfaceVariant = ElevatedPaperSurface,
    onSurfaceVariant = LightOnSurfaceMuted,
    error = PathogenicCrimson,
    onError = Color.White
)

@Composable
fun LegeArtisTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MicroscopeDarkColorScheme else ClinicalLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
