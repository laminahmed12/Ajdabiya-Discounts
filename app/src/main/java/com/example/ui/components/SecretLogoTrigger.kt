package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role

/**
 * A discreet wrapper component that wraps the app logo (or any child composable)
 * and detects sequential taps (default: 3 taps within 1500ms) to trigger
 * the secret admin authentication dialog.
 *
 * It is completely silent and displays no visual count or indicators to ordinary users.
 */
@Composable
fun SecretLogoTrigger(
    onTrigger: () -> Unit,
    modifier: Modifier = Modifier,
    requiredTaps: Int = 3,
    maxIntervalMillis: Long = 1500L,
    content: @Composable () -> Unit
) {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTimestamp by remember { mutableLongStateOf(0L) }

    Box(
        modifier = modifier
            .testTag("secret_logo_trigger_wrapper")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Silent without ripple or visual clue to maintain confidentiality
                role = Role.Button
            ) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTapTimestamp > maxIntervalMillis) {
                    tapCount = 1
                } else {
                    tapCount++
                }
                lastTapTimestamp = currentTime

                if (tapCount >= requiredTaps) {
                    tapCount = 0
                    lastTapTimestamp = 0L
                    onTrigger()
                }
            }
    ) {
        content()
    }
}
