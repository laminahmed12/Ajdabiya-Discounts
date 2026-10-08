package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = BrandPrimaryDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = FireRedLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = DinarGreenLight,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF1E293B)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = BrandPrimaryDark,
    secondary = FireRed,
    onSecondary = Color.White,
    secondaryContainer = FlameBadgeBackgroundLight,
    onSecondaryContainer = Color(0xFF991B1B),
    tertiary = DinarGreen,
    onTertiary = Color.White,
    tertiaryContainer = DinarGreenContainer,
    onTertiaryContainer = Color(0xFF065F46),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = Color(0xFFE2E8F0)
)

@Composable
fun AjdabiyaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) {
        AjdabiyaCustomColors(
            hotDealBadgeBackground = FlameBadgeBackgroundDark,
            hotDealBadgeText = FireRedLight,
            hotDealBorder = FireRedLight.copy(alpha = 0.5f),
            facebookColor = FacebookBrandBlue,
            whatsAppColor = WhatsAppBrandGreen,
            messengerColor = MessengerBrandBlue,
            currencyColor = DinarGreenLight
        )
    } else {
        AjdabiyaCustomColors(
            hotDealBadgeBackground = FlameBadgeBackgroundLight,
            hotDealBadgeText = FireRed,
            hotDealBorder = FireOrange.copy(alpha = 0.5f),
            facebookColor = FacebookBrandBlue,
            whatsAppColor = WhatsAppBrandGreen,
            messengerColor = MessengerBrandBlue,
            currencyColor = DinarGreen
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            try {
                var ctx = view.context
                while (ctx is android.content.ContextWrapper) {
                    if (ctx is Activity) break
                    ctx = ctx.baseContext
                }
                val window = (ctx as? Activity)?.window
                if (window != null) {
                    val insetsController = WindowCompat.getInsetsController(window, view)
                    insetsController.isAppearanceLightStatusBars = !darkTheme
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    CompositionLocalProvider(
        LocalAjdabiyaCustomColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

/**
 * Backward compatible alias
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AjdabiyaAppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}
