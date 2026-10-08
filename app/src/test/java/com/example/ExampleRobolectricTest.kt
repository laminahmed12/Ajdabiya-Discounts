package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("تخفيضات أجدابيا", appName)
  }

  @Test
  fun `verify correct secret pin 116936`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.AjdabiyaViewModel(application)
    val isCorrect = viewModel.verifySecretPin("116936")
    assertEquals(true, isCorrect)
  }

  @Test
  fun `verify store registration form state`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.AjdabiyaViewModel(application)
    viewModel.openStoreRegistrationDialog()
    assertEquals(true, viewModel.showStoreRegistrationDialog.value)
    viewModel.closeStoreRegistrationDialog()
    assertEquals(false, viewModel.showStoreRegistrationDialog.value)
  }

  @Test
  fun `verify notifications dialog open and close`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.AjdabiyaViewModel(application)
    viewModel.openNotificationsDialog()
    assertEquals(true, viewModel.showNotificationsDialog.value)
    viewModel.closeNotificationsDialog()
    assertEquals(false, viewModel.showNotificationsDialog.value)
  }

  @Test
  fun `verify notification settings dialog open and close`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.AjdabiyaViewModel(application)
    viewModel.openNotificationSettingsDialog()
    assertEquals(true, viewModel.showNotificationSettingsDialog.value)
    viewModel.closeNotificationSettingsDialog()
    assertEquals(false, viewModel.showNotificationSettingsDialog.value)
  }
}
