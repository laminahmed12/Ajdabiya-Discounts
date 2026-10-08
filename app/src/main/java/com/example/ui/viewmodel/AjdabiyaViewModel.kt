package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ActivationCodeEntity
import com.example.data.local.AppDatabase
import com.example.data.local.AppNotificationEntity
import com.example.data.local.AppSettingsEntity
import com.example.data.local.DealEntity
import com.example.data.local.FinancialRecordEntity
import com.example.data.local.FollowedStoreEntity
import com.example.data.local.MerchantEntity
import com.example.data.local.StoreEntity
import com.example.data.local.UserNotificationSettingsEntity
import com.example.data.repository.AjdabiyaRepository
import com.example.ui.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data class StoreDetail(val storeId: Long) : Screen()
    data object MerchantDashboard : Screen()
    data object MasterAdmin : Screen()
}

class AjdabiyaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AjdabiyaRepository(database)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation backstack support
    private val backStack = mutableListOf<Screen>()

    // Current Tab on Home Screen (0 = Hot Deals 🔥, 1 = Stores 🏪)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Search and filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Dynamic Theme Mode: null = Follow system, true = Dark Mode, false = Light Mode
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun toggleDarkMode(currentlyDark: Boolean) {
        _isDarkMode.value = !currentlyDark
    }

    // Secret Tap Trigger Tracking (3 taps within 1500ms, completely silent without visible count)
    private var lastTapTime = 0L
    private var tapCount = 0

    // Secret PIN dialog state
    private val _showSecretPinDialog = MutableStateFlow(false)
    val showSecretPinDialog: StateFlow<Boolean> = _showSecretPinDialog.asStateFlow()

    // Merchant Login dialog state
    private val _showMerchantLoginDialog = MutableStateFlow(false)
    val showMerchantLoginDialog: StateFlow<Boolean> = _showMerchantLoginDialog.asStateFlow()

    // Store & Deal Registration Form Dialog state
    private val _showStoreRegistrationDialog = MutableStateFlow(false)
    val showStoreRegistrationDialog: StateFlow<Boolean> = _showStoreRegistrationDialog.asStateFlow()

    fun openStoreRegistrationDialog() {
        _showStoreRegistrationDialog.value = true
    }

    fun closeStoreRegistrationDialog() {
        _showStoreRegistrationDialog.value = false
    }

    // Notification center and settings dialog states
    private val _showNotificationsDialog = MutableStateFlow(false)
    val showNotificationsDialog: StateFlow<Boolean> = _showNotificationsDialog.asStateFlow()

    fun openNotificationsDialog() {
        _showNotificationsDialog.value = true
    }

    fun closeNotificationsDialog() {
        _showNotificationsDialog.value = false
    }

    private val _showNotificationSettingsDialog = MutableStateFlow(false)
    val showNotificationSettingsDialog: StateFlow<Boolean> = _showNotificationSettingsDialog.asStateFlow()

    fun openNotificationSettingsDialog() {
        _showNotificationSettingsDialog.value = true
    }

    fun closeNotificationSettingsDialog() {
        _showNotificationSettingsDialog.value = false
    }

    fun registerStoreAndDeal(
        storeName: String,
        location: String,
        category: String,
        phone: String,
        whatsapp: String,
        facebookUrl: String,
        discountType: String,
        dealContent: String,
        isHot: Boolean,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val regResult = repository.registerStoreAndDeal(
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
            _showStoreRegistrationDialog.value = false
            showMessage("تم تسجيل المتجر والعرض وحفظهما بنجاح في قاعدة البيانات المحلية!")

            // If a notification was triggered (e.g. nearby store or user followed), send system notification
            if (regResult.triggeredNotification != null) {
                NotificationHelper.showDealNotification(
                    context = getApplication(),
                    title = regResult.triggeredNotification.title,
                    message = regResult.triggeredNotification.message,
                    storeName = storeName,
                    storeId = regResult.storeId
                )
            }

            onSuccess()
        }
    }

    // Currently logged-in Merchant
    private val _currentMerchant = MutableStateFlow<MerchantEntity?>(null)
    val currentMerchant: StateFlow<MerchantEntity?> = _currentMerchant.asStateFlow()

    // Store of logged-in merchant
    private val _merchantStore = MutableStateFlow<StoreEntity?>(null)
    val merchantStore: StateFlow<StoreEntity?> = _merchantStore.asStateFlow()

    // Merchant deals & financials
    private val _merchantDeals = MutableStateFlow<List<DealEntity>>(emptyList())
    val merchantDeals: StateFlow<List<DealEntity>> = _merchantDeals.asStateFlow()

    private val _merchantFinancials = MutableStateFlow<List<FinancialRecordEntity>>(emptyList())
    val merchantFinancials: StateFlow<List<FinancialRecordEntity>> = _merchantFinancials.asStateFlow()

    // Selected store details
    private val _selectedStore = MutableStateFlow<StoreEntity?>(null)
    val selectedStore: StateFlow<StoreEntity?> = _selectedStore.asStateFlow()

    private val _selectedStoreDeals = MutableStateFlow<List<DealEntity>>(emptyList())
    val selectedStoreDeals: StateFlow<List<DealEntity>> = _selectedStoreDeals.asStateFlow()

    // Settings
    val appSettings: StateFlow<AppSettingsEntity> = repository.appSettings
        .combine(MutableStateFlow(Unit)) { settings, _ ->
            settings ?: AppSettingsEntity()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsEntity()
        )

    // Base flows
    val allStoresRaw = repository.allStores
    val hotDealsRaw = repository.hotDeals
    val allMerchants = repository.allMerchants.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val allFinancials = repository.allFinancialRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val allActivationCodes: StateFlow<List<ActivationCodeEntity>> = repository.allActivationCodes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Followed Stores flows
    val allFollowedStores: StateFlow<List<FollowedStoreEntity>> = repository.allFollowedStores.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val followedStoreIds: StateFlow<List<Long>> = repository.followedStoreIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Notifications and Settings flows
    val allNotifications: StateFlow<List<AppNotificationEntity>> = repository.allNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
    val userNotificationSettings: StateFlow<UserNotificationSettingsEntity> = repository.userNotificationSettings
        .combine(MutableStateFlow(Unit)) { settings, _ ->
            settings ?: UserNotificationSettingsEntity()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserNotificationSettingsEntity()
        )

    // Filtered Hot Deals by search query
    val hotDeals: StateFlow<List<DealEntity>> = combine(hotDealsRaw, _searchQuery) { deals, query ->
        if (query.isBlank()) {
            deals
        } else {
            val q = query.trim()
            deals.filter { it.content.contains(q, ignoreCase = true) || it.storeName.contains(q, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered Stores by search query (Matches store name, discount type, category, or address)
    val allStores: StateFlow<List<StoreEntity>> = combine(allStoresRaw, _searchQuery) { stores, query ->
        if (query.isBlank()) {
            stores
        } else {
            val q = query.trim()
            stores.filter {
                it.name.contains(q, ignoreCase = true) ||
                it.discountType.contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true) ||
                it.address.contains(q, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Toast/Snackbar message notification
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedIfEmpty()
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        } else if (_currentScreen.value != Screen.Home) {
            _currentScreen.value = Screen.Home
            return true
        }
        return false
    }

    // Secret Tap Trigger on Logo (3 taps within 1500 ms)
    fun openSecretPinDialog() {
        _showSecretPinDialog.value = true
    }

    fun onLogoTapped() {
        val now = System.currentTimeMillis()
        if (now - lastTapTime > 1500) {
            tapCount = 1
        } else {
            tapCount++
        }
        lastTapTime = now

        if (tapCount >= 3) {
            tapCount = 0
            _showSecretPinDialog.value = true
        }
    }

    fun closeSecretPinDialog() {
        _showSecretPinDialog.value = false
    }

    fun verifySecretPin(enteredPin: String): Boolean {
        val validPin = appSettings.value.secretPin
        return if (enteredPin.trim() == validPin.trim()) {
            _showSecretPinDialog.value = false
            navigateTo(Screen.MasterAdmin)
            true
        } else {
            false
        }
    }

    // Merchant Login
    fun openMerchantLogin() {
        _showMerchantLoginDialog.value = true
    }

    fun closeMerchantLogin() {
        _showMerchantLoginDialog.value = false
    }

    fun loginMerchant(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val merchant = repository.authenticateMerchant(username, password)
            if (merchant != null) {
                _currentMerchant.value = merchant
                _merchantStore.value = repository.getStoreByMerchant(merchant.username)

                // Load merchant deals
                launch {
                    repository.getDealsForMerchant(merchant.username).collect {
                        _merchantDeals.value = it
                    }
                }
                // Load merchant financials
                launch {
                    repository.getFinancialRecordsForMerchant(merchant.username).collect {
                        _merchantFinancials.value = it
                    }
                }

                _showMerchantLoginDialog.value = false
                navigateTo(Screen.MerchantDashboard)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun logoutMerchant() {
        _currentMerchant.value = null
        _merchantStore.value = null
        _merchantDeals.value = emptyList()
        _merchantFinancials.value = emptyList()
        navigateTo(Screen.Home)
    }

    // Open Store Detail
    fun openStoreDetail(store: StoreEntity) {
        _selectedStore.value = store
        viewModelScope.launch {
            repository.getDealsForStore(store.id).collect {
                _selectedStoreDeals.value = it
            }
        }
        navigateTo(Screen.StoreDetail(store.id))
    }

    // Add Deal by Merchant
    fun publishDeal(
        content: String,
        facebookPostUrl: String,
        isHot: Boolean,
        onSuccess: () -> Unit
    ) {
        val merchant = _currentMerchant.value ?: return
        val store = _merchantStore.value ?: return

        val settings = appSettings.value
        val dealType = if (isHot) "HOT" else "REGULAR"
        val cost = if (isHot) settings.hotPrice else settings.regularPrice

        viewModelScope.launch {
            val dealResult = repository.addDeal(
                storeId = store.id,
                storeName = store.name,
                merchantUsername = merchant.username,
                content = content.trim(),
                facebookPostUrl = facebookPostUrl.trim(),
                dealType = dealType,
                priceCost = cost,
                phone = store.phone,
                whatsapp = store.whatsapp,
                messengerUrl = store.messengerUrl
            )
            showMessage("تم نشر العرض وتسجيل العملية في كشف الحساب بنجاح!")

            if (dealResult.triggeredNotification != null) {
                NotificationHelper.showDealNotification(
                    context = getApplication(),
                    title = dealResult.triggeredNotification.title,
                    message = dealResult.triggeredNotification.message,
                    storeName = store.name,
                    storeId = store.id
                )
            }

            onSuccess()
        }
    }

    fun deleteDeal(dealId: Long) {
        viewModelScope.launch {
            repository.deleteDeal(dealId)
            showMessage("تم حذف العرض بنجاح.")
        }
    }

    // Master Admin Actions
    fun createMerchantAccount(
        username: String,
        password: String,
        storeName: String,
        phone: String,
        category: String,
        address: String,
        whatsapp: String,
        facebookUrl: String,
        location: String = "",
        discountType: String = "",
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.createMerchant(
                username = username,
                password = password,
                storeName = storeName,
                phone = phone,
                category = category,
                address = address,
                whatsapp = whatsapp,
                facebookUrl = facebookUrl,
                location = location,
                discountType = discountType
            )
            if (success) {
                showMessage("تم إنشاء حساب التاجر ومتجره بنجاح!")
            } else {
                showMessage("اسم المستخدم مستخدم مسبقاً، يرجى اختيار اسم آخر!")
            }
            onResult(success)
        }
    }

    fun deleteMerchant(username: String) {
        viewModelScope.launch {
            repository.deleteMerchant(username)
            showMessage("تم حذف حساب التاجر بنجاح.")
        }
    }

    fun updateMerchantAccount(
        username: String,
        newPassword: String,
        storeName: String,
        location: String,
        discountType: String,
        phone: String,
        category: String,
        whatsapp: String = "",
        facebookUrl: String = "",
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.updateMerchantAndStore(
                username = username,
                newPassword = newPassword,
                storeName = storeName,
                location = location,
                discountType = discountType,
                phone = phone,
                category = category,
                whatsapp = whatsapp,
                facebookUrl = facebookUrl
            )
            if (success) {
                showMessage("تم تحديث بيانات التاجر والمحل بنجاح!")
            } else {
                showMessage("تعذر تحديث بيانات التاجر!")
            }
            onResult(success)
        }
    }

    fun updatePricingAndPhone(regularPrice: Double, hotPrice: Double, ownerPhone: String) {
        viewModelScope.launch {
            repository.updateSettings(regularPrice, hotPrice, ownerPhone)
            showMessage("تم حفظ التعديلات في الإعدادات المحلية بنجاح!")
        }
    }

    // Activation Codes Management
    fun generateActivationCode(planType: String, onGenerated: (ActivationCodeEntity) -> Unit = {}) {
        viewModelScope.launch {
            val code = repository.generateActivationCode(planType)
            showMessage("تم توليد رمز التفعيل بنجاح: ${code.code}")
            onGenerated(code)
        }
    }

    fun deleteActivationCode(id: Long) {
        viewModelScope.launch {
            repository.deleteActivationCode(id)
            showMessage("تم حذف رمز التفعيل بنجاح.")
        }
    }

    // Follow / Unfollow Store Actions
    fun toggleFollowStore(storeId: Long, storeName: String) {
        viewModelScope.launch {
            val nowFollowed = repository.toggleFollowStore(storeId, storeName)
            if (nowFollowed) {
                showMessage("⭐ تمت متابعة ($storeName)! ستصلك إشعارات بأي عروض جديدة ينشرها.")
            } else {
                showMessage("تم إلغاء متابعة ($storeName).")
            }
        }
    }

    fun isStoreFollowed(storeId: Long): Boolean {
        return followedStoreIds.value.contains(storeId)
    }

    // Notification Center Actions
    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("تم تحديد جميع الإشعارات كمقروءة.")
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            showMessage("تم تفريغ مركز الإشعارات بالكامل.")
        }
    }

    fun updateNotificationSettings(
        notifyFollowed: Boolean,
        notifyNearby: Boolean,
        neighborhood: String,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            val updated = UserNotificationSettingsEntity(
                id = 1,
                notifyFollowedStores = notifyFollowed,
                notifyNearbyStores = notifyNearby,
                userNeighborhood = neighborhood,
                notificationsEnabled = enabled
            )
            repository.saveUserNotificationSettings(updated)
            showMessage("تم حفظ إعدادات الإشعارات والمنطقة ($neighborhood) بنجاح!")
        }
    }

    // Test Notification generator for user verification
    fun sendTestNotification(storeName: String = "محلات أجدابيا للتخفيضات", area: String = "شارع الوفاق") {
        viewModelScope.launch {
            val notif = AppNotificationEntity(
                title = "⭐ عرض جديد تجريبي: تخفيضات كبرى تصل إلى 50%!",
                message = "تنبيه من متجر تتابعه وقريب منك في $area ($storeName)",
                storeId = 1L,
                storeName = storeName,
                notificationType = "BOTH",
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
            repository.insertManualNotification(notif)
            NotificationHelper.showDealNotification(
                context = getApplication(),
                title = notif.title,
                message = notif.message,
                storeName = storeName,
                storeId = 1L
            )
            showMessage("تم إرسال إشعار تجريبي بنجاح! تفقّد مركز الإشعارات أو شريط التنبيهات.")
        }
    }
}
