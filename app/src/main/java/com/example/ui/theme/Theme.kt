package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
  darkColorScheme(
    primary = GptSystemText,
    secondary = GptAccent,
    background = GptBackground,
    surface = GptBackground,
    surfaceVariant = GptSidebar,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GptTextPrimary,
    onSurface = GptTextPrimary,
    onSurfaceVariant = GptTextPrimary,
    outline = GptDivider
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark mode for ChatGPT clone
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.background.toArgb()
      window.navigationBarColor = colorScheme.background.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

