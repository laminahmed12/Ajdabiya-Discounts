package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MerchantLoginDialog
import com.example.ui.components.NotificationSettingsDialog
import com.example.ui.components.NotificationsCenterDialog
import com.example.ui.components.SecretAdminDialog
import com.example.ui.components.StoreRegistrationFormDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MasterAdminScreen
import com.example.ui.screens.MerchantDashboardScreen
import com.example.ui.screens.StoreDetailScreen
import com.example.ui.theme.AjdabiyaAppTheme
import com.example.ui.viewmodel.AjdabiyaViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AjdabiyaViewModel = viewModel()
            val systemInDark = isSystemInDarkTheme()
            val userDarkMode by viewModel.isDarkMode.collectAsState()
            val isDark = userDarkMode ?: systemInDark

            AjdabiyaAppTheme(darkTheme = isDark) {
                // Natural Right-To-Left direction for Arabic language
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AjdabiyaApp(viewModel = viewModel, isDarkMode = isDark)
                }
            }
        }
    }
}

@Composable
fun AjdabiyaApp(
    viewModel: AjdabiyaViewModel = viewModel(),
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val showSecretPinDialog by viewModel.showSecretPinDialog.collectAsState()
    val showMerchantLoginDialog by viewModel.showMerchantLoginDialog.collectAsState()
    val showStoreRegistrationDialog by viewModel.showStoreRegistrationDialog.collectAsState()
    val showNotificationsDialog by viewModel.showNotificationsDialog.collectAsState()
    val showNotificationSettingsDialog by viewModel.showNotificationSettingsDialog.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                is Screen.Home -> {
                    HomeScreen(viewModel = viewModel, isDarkMode = isDarkMode)
                }
                is Screen.StoreDetail -> {
                    StoreDetailScreen(viewModel = viewModel)
                }
                is Screen.MerchantDashboard -> {
                    MerchantDashboardScreen(viewModel = viewModel)
                }
                is Screen.MasterAdmin -> {
                    MasterAdminScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Secret Admin Master PIN dialog
    // Opens only after 3 consecutive taps on the app logo, input field starts completely blank
    if (showSecretPinDialog) {
        SecretAdminDialog(
            onDismiss = { viewModel.closeSecretPinDialog() },
            onVerifyPin = { pin -> viewModel.verifySecretPin(pin) }
        )
    }

    // Merchant Login dialog
    if (showMerchantLoginDialog) {
        MerchantLoginDialog(
            onDismiss = { viewModel.closeMerchantLogin() },
            onLogin = { username, password, onResult ->
                viewModel.loginMerchant(username, password, onResult)
            }
        )
    }

    // Store & Deal Registration Form Dialog
    if (showStoreRegistrationDialog) {
        StoreRegistrationFormDialog(
            regularPrice = appSettings.regularPrice,
            hotPrice = appSettings.hotPrice,
            onDismiss = { viewModel.closeStoreRegistrationDialog() },
            onSubmit = { storeName, location, category, phone, whatsapp, facebookUrl, discountType, dealContent, isHot ->
                viewModel.registerStoreAndDeal(
                    storeName = storeName,
                    location = location,
                    category = category,
                    phone = phone,
                    whatsapp = whatsapp,
                    facebookUrl = facebookUrl,
                    discountType = discountType,
                    dealContent = dealContent,
                    isHot = isHot
                )
            }
        )
    }

    // Notifications Center Dialog
    if (showNotificationsDialog) {
        NotificationsCenterDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeNotificationsDialog() }
        )
    }

    // Notification & Proximity Settings Dialog
    if (showNotificationSettingsDialog) {
        NotificationSettingsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeNotificationSettingsDialog() }
        )
    }
}
