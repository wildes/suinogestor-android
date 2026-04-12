package br.com.suinogestor.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Seeds primárias SuinoGestor
val SuinoGreen = Color(0xFF5D7A3E)
val SuinoAmber = Color(0xFF8B6914)
val SuinoTerra = Color(0xFFC0522A)

val LightColorScheme = lightColorScheme(
    primary             = Color(0xFF5D7A3E),
    onPrimary           = Color(0xFFFFFFFF),
    primaryContainer    = Color(0xFFDEF0BB),
    onPrimaryContainer  = Color(0xFF1A2E00),
    secondary           = Color(0xFF8B6914),
    onSecondary         = Color(0xFFFFFFFF),
    secondaryContainer  = Color(0xFFFFDEA0),
    onSecondaryContainer= Color(0xFF2B1D00),
    tertiary            = Color(0xFFC0522A),
    onTertiary          = Color(0xFFFFFFFF),
    tertiaryContainer   = Color(0xFFFFDBCF),
    onTertiaryContainer = Color(0xFF3E0E00),
    error               = Color(0xFFBA1A1A),
    onError             = Color(0xFFFFFFFF),
    errorContainer      = Color(0xFFFFDAD6),
    onErrorContainer    = Color(0xFF410002),
    background          = Color(0xFFFCFCF4),
    onBackground        = Color(0xFF1B1C17),
    surface             = Color(0xFFFCFCF4),
    onSurface           = Color(0xFF1B1C17),
    surfaceVariant      = Color(0xFFE2E4D5),
    onSurfaceVariant    = Color(0xFF45483C)
)

val DarkColorScheme = darkColorScheme(
    primary             = Color(0xFFC2D89E),
    onPrimary           = Color(0xFF2D4A0E),
    primaryContainer    = Color(0xFF446122),
    onPrimaryContainer  = Color(0xFFDEF0BB),
    secondary           = Color(0xFFEFBE4A),
    onSecondary         = Color(0xFF472D00),
    secondaryContainer  = Color(0xFF664400),
    onSecondaryContainer= Color(0xFFFFDEA0),
    tertiary            = Color(0xFFFFB59A),
    onTertiary          = Color(0xFF612200),
    tertiaryContainer   = Color(0xFF8A3914),
    onTertiaryContainer = Color(0xFFFFDBCF),
    error               = Color(0xFFFFB4AB),
    onError             = Color(0xFF690005),
    errorContainer      = Color(0xFF93000A),
    onErrorContainer    = Color(0xFFFFDAD6),
    background          = Color(0xFF13140F),
    onBackground        = Color(0xFFE3E4D8),
    surface             = Color(0xFF13140F),
    onSurface           = Color(0xFFE3E4D8),
    surfaceVariant      = Color(0xFF45483C),
    onSurfaceVariant    = Color(0xFFC5C8B9)
)
