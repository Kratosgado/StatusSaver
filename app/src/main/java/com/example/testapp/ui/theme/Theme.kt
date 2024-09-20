package com.example.testapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.testapp.utils.AppPreferences

private val DarkColorScheme = darkColorScheme(
  primary = Green,
  secondary = LightGreen,
  tertiary = Blue,
  background = DarkGray,
)

private val LightColorScheme = lightColorScheme(
  primary = Green,
  secondary = LightGreen,
  onSecondary = Color(0xfffffbfe),
  tertiary = Blue,
  background = Background,
  onSurface = Color(0xFF1C1B1F),
)

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  var darkPref by remember {
    mutableStateOf(darkTheme)
  }
  val prefs = AppPreferences.getInstance(LocalContext.current)
  prefs.registerListener { shared, key ->
    if (key == "darkMode") darkPref = shared.getBoolean(key, darkPref)
  }
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkPref -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}