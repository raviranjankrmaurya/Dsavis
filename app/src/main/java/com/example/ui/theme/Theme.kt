package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DSAFlowDarkColorScheme = darkColorScheme(
  primary = CyanBright,
  onPrimary = Color(0xFF031B2C),
  primaryContainer = Color(0xFF0C4A6E),
  onPrimaryContainer = Color(0xFFBAE6FD),
  secondary = IndigoAccent,
  onSecondary = Color(0xFF1E1B4B),
  secondaryContainer = Color(0xFF312E81),
  onSecondaryContainer = Color(0xFFE0E7FF),
  tertiary = PurpleAI,
  onTertiary = Color(0xFF3B0764),
  tertiaryContainer = Color(0xFF581C87),
  onTertiaryContainer = Color(0xFFF3E8FF),
  background = BgDeepDark,
  onBackground = TextPrimary,
  surface = BgCard,
  onSurface = TextPrimary,
  surfaceVariant = BgCardElevated,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
  outlineVariant = BorderAccent,
  error = ErrorRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  // Always use dark theme for DSAFlow developer aesthetic as specified
  MaterialTheme(
    colorScheme = DSAFlowDarkColorScheme,
    typography = Typography,
    content = content
  )
}
