package com.example.data.repository

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
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DealCreationResult(
    val dealId: Long,
    val triggeredNotification: AppNotificationEntity?
)

data class StoreRegistrationResult(
    val storeId: Long,
    val triggeredNotification: AppNotificationEntity?
)

class AjdabiyaRepository(private val database: AppDatabase) {

    private val storeDao = database.storeDao()
    private val dealDao = database.dealDao()
    private val merchantDao = database.merchantDao()
    private val financialDao = database.financialRecordDao()
    private val settingsDao = database.appSettingsDao()
    private val activationDao = database.activationCodeDao()
    private val followedStoreDao = database.followedStoreDao()
    private val notificationSettingsDao = database.userNotificationSettingsDao()
    private val appNotificationDao = database.appNotificationDao()

    val allStores: Flow<List<StoreEntity>> = storeDao.getAllStores()
    val allDeals: Flow<List<DealEntity>> = dealDao.getAllDeals()
    val hotDeals: Flow<List<DealEntity>> = dealDao.getHotDeals()
    val allMerchants: Flow<List<MerchantEntity>> = merchantDao.getAllMerchants()
    val allFinancialRecords: Flow<List<FinancialRecordEntity>> = financialDao.getAllRecords()
    val allActivationCodes: Flow<List<ActivationCodeEntity>> = activationDao.getAllCodes()
    val appSettings: Flow<AppSettingsEntity?> = settingsDao.getSettings()

    // Followed stores & Notifications flows
    val allFollowedStores: Flow<List<FollowedStoreEntity>> = followedStoreDao.getAllFollowedStores()
    val followedStoreIds: Flow<List<Long>> = followedStoreDao.getFollowedStoreIds()
    val userNotificationSettings: Flow<UserNotificationSettingsEntity?> = notificationSettingsDao.getSettings()
    val allNotifications: Flow<List<AppNotificationEntity>> = appNotificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = appNotificationDao.getUnreadCount()

    suspend fun getUserNotificationSettingsSync(): UserNotificationSettingsEntity {
        return notificationSettingsDao.getSettingsSync() ?: UserNotificationSettingsEntity()
    }

    suspend fun saveUserNotificationSettings(settings: UserNotificationSettingsEntity) {
        notificationSettingsDao.saveSettings(settings)
    }

    fun isStoreFollowed(storeId: Long): Flow<Boolean> = followedStoreDao.isStoreFollowed(storeId)

    suspend fun isStoreFollowedSync(storeId: Long): Boolean = followedStoreDao.isStoreFollowedSync(storeId)

    suspend fun toggleFollowStore(storeId: Long, storeName: String): Boolean {
        val isCurrentlyFollowed = followedStoreDao.isStoreFollowedSync(storeId)
        return if (isCurrentlyFollowed) {
            followedStoreDao.unfollowStore(storeId)
            false
        } else {
            followedStoreDao.followStore(
                FollowedStoreEntity(
                    storeId = storeId,
                    storeName = storeName
                )
            )
            true
        }
    }

    suspend fun markNotificationAsRead(id: Long) {
        appNotificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        appNotificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(id: Long) {
        appNotificationDao.deleteNotification(id)
    }

    suspend fun clearAllNotifications() {
        appNotificationDao.clearAll()
    }

    suspend fun insertManualNotification(notification: AppNotificationEntity): Long {
        return appNotificationDao.insertNotification(notification)
    }

    private fun isStoreNearby(storeAddress: String, userNeighborhood: String): Boolean {
        val storeAddr = storeAddress.trim().lowercase(Locale.ROOT)
        val userArea = userNeighborhood.trim().lowercase(Locale.ROOT)
        if (userArea.isBlank() || userArea.contains("كامل") || userArea.contains("الكل")) {
            return true
        }
        val words = userArea.split(" ", "،", "/", "-", "(", ")")
            .map { it.trim() }
            .filter { it.length >= 3 && it !in listOf("شارع", "منطقة", "طريق", "حي", "سوق") }
        if (words.any { storeAddr.contains(it) }) return true
        return storeAddr.contains(userArea) || userArea.contains(storeAddr)
    }

    suspend fun getSettingsSync(): AppSettingsEntity {
        return settingsDao.getSettingsSync() ?: AppSettingsEntity()
    }

    suspend fun checkAndSeedIfEmpty() {
        try {
            // Delete any legacy mock/dummy stores, deals, and records
            dealDao.deleteMockDeals()
            storeDao.deleteMockStores()
            merchantDao.deleteMockMerchants()
            financialDao.deleteMockFinancialRecords()

            // Initialize app settings if not present
            if (settingsDao.getSettingsSync() == null) {
                AppDatabase.populateInitialData(database)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDealsForStore(storeId: Long): Flow<List<DealEntity>> {
        return dealDao.getDealsByStore(storeId)
    }

    suspend fun getStoreById(storeId: Long): StoreEntity? {
        return storeDao.getStoreById(storeId)
    }

    suspend fun getStoreByMerchant(merchantUsername: String): StoreEntity? {
        return storeDao.getStoreByMerchant(merchantUsername)
    }

    fun getDealsForMerchant(merchantUsername: String): Flow<List<DealEntity>> {
        return dealDao.getDealsByMerchant(merchantUsername)
    }

    fun getFinancialRecordsForMerchant(merchantUsername: String): Flow<List<FinancialRecordEntity>> {
        return financialDao.getRecordsByMerchant(merchantUsername)
    }

    suspend fun authenticateMerchant(username: String, password: String): MerchantEntity? {
        return merchantDao.authenticate(username.trim(), password.trim())
    }

    fun searchMerchants(query: String): Flow<List<MerchantEntity>> = merchantDao.searchMerchants(query)

    fun getMerchantsByLocation(location: String): Flow<List<MerchantEntity>> = merchantDao.getMerchantsByLocation(location)

    fun getMerchantsByDiscountType(discountType: String): Flow<List<MerchantEntity>> = merchantDao.getMerchantsByDiscountType(discountType)

    suspend fun updateMerchantProfile(
        username: String,
        storeName: String,
        location: String,
        discountType: String,
        phone: String
    ) {
        merchantDao.updateMerchantProfile(username, storeName, location, discountType, phone)
    }

    suspend fun addDeal(
        storeId: Long,
        storeName: String,
        merchantUsername: String,
        content: String,
        facebookPostUrl: String,
        dealType: String, // "HOT" or "REGULAR"
        priceCost: Double,
        phone: String,
        whatsapp: String,
        messengerUrl: String = ""
    ): DealCreationResult {
        val deal = DealEntity(
            storeId = storeId,
            storeName = storeName,
            merchantUsername = merchantUsername,
            content = content,
            facebookPostUrl = facebookPostUrl,
            dealType = dealType,
            priceCost = priceCost,
            phone = phone,
            whatsapp = whatsapp,
            messengerUrl = messengerUrl,
            timestamp = System.currentTimeMillis()
        )
        val dealId = dealDao.insertDeal(deal)

        // Automatically log local financial record
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        val typeArabic = if (dealType == "HOT") "ساخن" else "عادي"
        val snippet = if (content.length > 60) content.take(60) + "..." else content

        financialDao.insertRecord(
            FinancialRecordEntity(
                merchantUsername = merchantUsername,
                storeName = storeName,
                dealId = dealId,
                dealType = typeArabic,
                dealContentPreview = snippet,
                cost = priceCost,
                timestamp = System.currentTimeMillis(),
                monthKey = currentMonth
            )
        )

        // Check notifications for followed stores or nearby stores
        var triggeredNotification: AppNotificationEntity? = null
        try {
            val userSettings = getUserNotificationSettingsSync()
            if (userSettings.notificationsEnabled) {
                val store = storeDao.getStoreById(storeId)
                val isFollowed = followedStoreDao.isStoreFollowedSync(storeId)
                val storeAddress = store?.address ?: ""
                val isNearby = isStoreNearby(storeAddress, userSettings.userNeighborhood)

                val shouldNotifyFollowed = isFollowed && userSettings.notifyFollowedStores
                val shouldNotifyNearby = isNearby && userSettings.notifyNearbyStores

                if (shouldNotifyFollowed || shouldNotifyNearby) {
                    val notifType = when {
                        shouldNotifyFollowed && shouldNotifyNearby -> "BOTH"
                        shouldNotifyFollowed -> "FOLLOWED_STORE"
                        else -> "NEARBY_STORE"
                    }
                    val title = when (notifType) {
                        "BOTH" -> "⭐ عرض جديد من متجر تتابعه وقريب منك: $storeName"
                        "FOLLOWED_STORE" -> "🔔 عرض جديد من متجر تتابعه: $storeName"
                        else -> "📍 عرض جديد قريب منك ($storeAddress): $storeName"
                    }
                    val notifEntity = AppNotificationEntity(
                        title = title,
                        message = snippet,
                        storeId = storeId,
                        storeName = storeName,
                        dealId = dealId,
                        notificationType = notifType,
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                    val notifId = appNotificationDao.insertNotification(notifEntity)
                    triggeredNotification = notifEntity.copy(id = notifId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return DealCreationResult(dealId = dealId, triggeredNotification = triggeredNotification)
    }

    suspend fun registerStoreAndDeal(
        storeName: String,
        location: String,
        category: String,
        phone: String,
        whatsapp: String,
        facebookUrl: String,
        discountType: String,
        dealContent: String,
        isHot: Boolean
    ): StoreRegistrationResult {
        val cleanName = storeName.trim().replace("\\s+".toRegex(), "_").lowercase(Locale.ROOT)
        val username = cleanName.take(15) + "_" + (System.currentTimeMillis() % 10000)

        val merchant = MerchantEntity(
            username = username,
            password = "123",
            storeName = storeName.trim(),
            location = location.trim(),
            discountType = discountType.trim(),
            phone = phone.trim(),
            category = category.trim(),
            address = location.trim()
        )
        merchantDao.insertMerchant(merchant)

        val store = StoreEntity(
            name = storeName.trim(),
            category = category.trim(),
            address = location.trim(),
            phone = phone.trim(),
            whatsapp = whatsapp.trim().ifEmpty { phone.trim() },
            facebookUrl = facebookUrl.trim().ifEmpty { "https://facebook.com" },
            messengerUrl = "https://m.me",
            merchantUsername = username,
            discountType = discountType.trim().ifEmpty { "عروض ساخنة وتخفيضات موسمية" }
        )
        val storeId = storeDao.insertStore(store)

        var triggeredNotif: AppNotificationEntity? = null
        if (dealContent.isNotBlank()) {
            val settings = getSettingsSync()
            val dealType = if (isHot) "HOT" else "REGULAR"
            val cost = if (isHot) settings.hotPrice else settings.regularPrice

            val dealRes = addDeal(
                storeId = storeId,
                storeName = store.name,
                merchantUsername = username,
                content = dealContent.trim(),
                facebookPostUrl = facebookUrl.trim().ifEmpty { "https://facebook.com" },
                dealType = dealType,
                priceCost = cost,
                phone = phone.trim(),
                whatsapp = whatsapp.trim().ifEmpty { phone.trim() },
                messengerUrl = store.messengerUrl
            )
            triggeredNotif = dealRes.triggeredNotification
        }

        return StoreRegistrationResult(storeId = storeId, triggeredNotification = triggeredNotif)
    }

    suspend fun deleteDeal(dealId: Long) {
        dealDao.deleteDealById(dealId)
    }

    suspend fun createMerchant(
        username: String,
        password: String,
        storeName: String,
        phone: String,
        category: String,
        address: String,
        whatsapp: String = "",
        facebookUrl: String = "",
        location: String = "",
        discountType: String = ""
    ): Boolean {
        val existing = merchantDao.getMerchantByUsername(username.trim())
        if (existing != null) {
            return false // username already taken
        }

        val finalAddress = address.trim().ifEmpty { "أجدابيا" }
        val finalLocation = location.trim().ifEmpty { finalAddress }
        val finalDiscountType = discountType.trim().ifEmpty { "عروض ساخنة وتخفيضات موسمية" }

        val merchant = MerchantEntity(
            username = username.trim(),
            password = password.trim(),
            storeName = storeName.trim(),
            location = finalLocation,
            discountType = finalDiscountType,
            phone = phone.trim(),
            category = category.trim().ifEmpty { "عام" },
            address = finalAddress
        )
        merchantDao.insertMerchant(merchant)

        // Also create store for merchant
        val store = StoreEntity(
            name = storeName.trim(),
            category = category.trim().ifEmpty { "عام" },
            address = finalLocation,
            phone = phone.trim(),
            whatsapp = whatsapp.trim().ifEmpty { phone.trim() },
            facebookUrl = facebookUrl.trim().ifEmpty { "https://facebook.com" },
            messengerUrl = "https://m.me",
            merchantUsername = username.trim(),
            discountType = finalDiscountType
        )
        storeDao.insertStore(store)
        return true
    }

    suspend fun deleteMerchant(username: String) {
        merchantDao.deleteMerchantByUsername(username)
    }

    suspend fun updateMerchantAndStore(
        username: String,
        newPassword: String,
        storeName: String,
        location: String,
        discountType: String,
        phone: String,
        category: String,
        whatsapp: String = "",
        facebookUrl: String = ""
    ): Boolean {
        val existing = merchantDao.getMerchantByUsername(username.trim()) ?: return false
        val updatedMerchant = existing.copy(
            password = newPassword.trim().ifEmpty { existing.password },
            storeName = storeName.trim(),
            location = location.trim(),
            discountType = discountType.trim(),
            phone = phone.trim(),
            category = category.trim().ifEmpty { existing.category },
            address = location.trim()
        )
        merchantDao.updateMerchant(updatedMerchant)

        val existingStore = storeDao.getStoreByMerchant(username.trim())
        if (existingStore != null) {
            val updatedStore = existingStore.copy(
                name = storeName.trim(),
                category = category.trim().ifEmpty { existingStore.category },
                address = location.trim(),
                phone = phone.trim(),
                whatsapp = whatsapp.trim().ifEmpty { phone.trim() },
                facebookUrl = facebookUrl.trim().ifEmpty { existingStore.facebookUrl },
                discountType = discountType.trim().ifEmpty { existingStore.discountType }
            )
            storeDao.updateStore(updatedStore)
        }
        return true
    }

    // Activation Codes Management for Master Admin
    suspend fun generateActivationCode(planType: String): ActivationCodeEntity {
        val randomPart = (10000..99999).random()
        val prefix = when {
            planType.contains("شهري") -> "AJD-M"
            planType.contains("سنوي") -> "AJD-Y"
            planType.contains("إعلانات") -> "AJD-ADS"
            else -> "AJD-ACT"
        }
        val codeString = "$prefix-$randomPart"
        val entity = ActivationCodeEntity(
            code = codeString,
            planType = planType,
            isUsed = false,
            createdAt = System.currentTimeMillis()
        )
        val id = activationDao.insertCode(entity)
        return entity.copy(id = id)
    }

    suspend fun deleteActivationCode(id: Long) {
        activationDao.deleteCode(id)
    }

    suspend fun verifyAndRedeemActivationCode(code: String, merchantUsername: String): Boolean {
        val existing = activationDao.getCodeByString(code.trim().uppercase()) ?: return false
        if (existing.isUsed) return false
        activationDao.markCodeAsUsed(existing.code, merchantUsername, System.currentTimeMillis())
        return true
    }

    suspend fun updateSettings(
        regularPrice: Double,
        hotPrice: Double,
        ownerPhone: String
    ) {
        val current = getSettingsSync()
        settingsDao.insertOrUpdate(
            current.copy(
                regularPrice = regularPrice,
                hotPrice = hotPrice,
                ownerPhone = ownerPhone.trim()
            )
        )
    }
}
