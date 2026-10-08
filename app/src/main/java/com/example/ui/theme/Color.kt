package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary Branding - Libyan Royal Blue / Indigo
val BrandPrimary = Color(0xFF1D4ED8)
val BrandPrimaryLight = Color(0xFF3B82F6)
val BrandPrimaryDark = Color(0xFF1E3A8A)

// Secondary - Hot Deals Fire & Amber
val FireRed = Color(0xFFDC2626)
val FireRedLight = Color(0xFFEF4444)
val FireOrange = Color(0xFFEA580C)
val FlameAmber = Color(0xFFF59E0B)
val FlameBadgeBackgroundLight = Color(0xFFFEE2E2)
val FlameBadgeBackgroundDark = Color(0xFF450A0A)

// Libyan Dinar & Financial Green
val DinarGreen = Color(0xFF059669)
val DinarGreenLight = Color(0xFF10B981)
val DinarGreenContainer = Color(0xFFD1FAE5)

// Neutral Grays & Surfaces - Light Mode
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightOutline = Color(0xFFCBD5E1)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF1E293B)
val LightOnSurfaceVariant = Color(0xFF64748B)

// Neutral Grays & Surfaces - Dark Mode
val DarkBackground = Color(0xFF0B1120)
val DarkSurface = Color(0xFF151E32)
val DarkSurfaceVariant = Color(0xFF1E293B)
val DarkOutline = Color(0xFF334155)
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFF1F5F9)
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

// Third-party Action Brand Colors
val FacebookBrandBlue = Color(0xFF1877F2)
val WhatsAppBrandGreen = Color(0xFF25D366)
val MessengerBrandBlue = Color(0xFF0084FF)

// Custom Semantic Colors for Ajdabiya Marketplace
data class AjdabiyaCustomColors(
    val hotDealBadgeBackground: Color,
    val hotDealBadgeText: Color,
    val hotDealBorder: Color,
    val facebookColor: Color = FacebookBrandBlue,
    val whatsAppColor: Color = WhatsAppBrandGreen,
    val messengerColor: Color = MessengerBrandBlue,
    val currencyColor: Color
)

val LocalAjdabiyaCustomColors = staticCompositionLocalOf {
    AjdabiyaCustomColors(
        hotDealBadgeBackground = FlameBadgeBackgroundLight,
        hotDealBadgeText = FireRed,
        hotDealBorder = FireOrange.copy(alpha = 0.5f),
        currencyColor = DinarGreen
    )
}
