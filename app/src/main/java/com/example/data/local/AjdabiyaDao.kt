package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY id ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM stores WHERE id = :id LIMIT 1")
    suspend fun getStoreById(id: Long): StoreEntity?

    @Query("SELECT * FROM stores WHERE merchantUsername = :merchantUsername LIMIT 1")
    suspend fun getStoreByMerchant(merchantUsername: String): StoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity): Long

    @Update
    suspend fun updateStore(store: StoreEntity)

    @Delete
    suspend fun deleteStore(store: StoreEntity)

    @Query("SELECT COUNT(*) FROM stores")
    suspend fun getStoreCount(): Int

    @Query("DELETE FROM stores WHERE merchantUsername IN ('alhawari', 'future_pharma', 'elegance', 'nakheel_cafe')")
    suspend fun deleteMockStores()
}

@Dao
interface DealDao {
    @Query("SELECT * FROM deals WHERE isActive = 1 ORDER BY timestamp DESC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE dealType = 'HOT' AND isActive = 1 ORDER BY timestamp DESC")
    fun getHotDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE storeId = :storeId AND isActive = 1 ORDER BY timestamp DESC")
    fun getDealsByStore(storeId: Long): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE merchantUsername = :merchantUsername ORDER BY timestamp DESC")
    fun getDealsByMerchant(merchantUsername: String): Flow<List<DealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: DealEntity): Long

    @Query("DELETE FROM deals WHERE id = :dealId")
    suspend fun deleteDealById(dealId: Long)

    @Query("DELETE FROM deals WHERE merchantUsername IN ('alhawari', 'future_pharma', 'elegance', 'nakheel_cafe')")
    suspend fun deleteMockDeals()

    @Query("SELECT COUNT(*) FROM deals WHERE isActive = 1")
    suspend fun getDealCount(): Int
}

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchants ORDER BY createdAt DESC")
    fun getAllMerchants(): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE id = :id LIMIT 1")
    suspend fun getMerchantById(id: Long): MerchantEntity?

    @Query("SELECT * FROM merchants WHERE username = :username LIMIT 1")
    suspend fun getMerchantByUsername(username: String): MerchantEntity?

    @Query("SELECT * FROM merchants WHERE username = :username AND password = :password LIMIT 1")
    suspend fun authenticate(username: String, password: String): MerchantEntity?

    @Query("SELECT * FROM merchants WHERE location LIKE '%' || :location || '%' ORDER BY storeName ASC")
    fun getMerchantsByLocation(location: String): Flow<List<MerchantEntity>>

    @Query("SELECT * FROM merchants WHERE discountType LIKE '%' || :discountType || '%' ORDER BY storeName ASC")
    fun getMerchantsByDiscountType(discountType: String): Flow<List<MerchantEntity>>

    @Query("""
        SELECT * FROM merchants 
        WHERE storeName LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%' 
           OR discountType LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchMerchants(query: String): Flow<List<MerchantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchant(merchant: MerchantEntity): Long

    @Update
    suspend fun updateMerchant(merchant: MerchantEntity)

    @Query("""
        UPDATE merchants 
        SET storeName = :storeName, location = :location, discountType = :discountType, phone = :phone 
        WHERE username = :username
    """)
    suspend fun updateMerchantProfile(
        username: String,
        storeName: String,
        location: String,
        discountType: String,
        phone: String
    )

    @Query("DELETE FROM merchants WHERE username = :username")
    suspend fun deleteMerchantByUsername(username: String)

    @Query("DELETE FROM merchants WHERE username IN ('alhawari', 'future_pharma', 'elegance', 'nakheel_cafe')")
    suspend fun deleteMockMerchants()

    @Query("SELECT COUNT(*) FROM merchants")
    suspend fun getMerchantCount(): Int
}

@Dao
interface FinancialRecordDao {
    @Query("SELECT * FROM financial_records WHERE merchantUsername = :merchantUsername ORDER BY timestamp DESC")
    fun getRecordsByMerchant(merchantUsername: String): Flow<List<FinancialRecordEntity>>

    @Query("SELECT * FROM financial_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FinancialRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FinancialRecordEntity): Long

    @Query("DELETE FROM financial_records WHERE merchantUsername IN ('alhawari', 'future_pharma', 'elegance', 'nakheel_cafe')")
    suspend fun deleteMockFinancialRecords()

    @Query("SELECT SUM(cost) FROM financial_records WHERE merchantUsername = :merchantUsername")
    suspend fun getTotalDueForMerchant(merchantUsername: String): Double?

    @Query("SELECT SUM(cost) FROM financial_records")
    suspend fun getTotalRevenue(): Double?
}

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AppSettingsEntity)
}

@Dao
interface ActivationCodeDao {
    @Query("SELECT * FROM activation_codes ORDER BY createdAt DESC")
    fun getAllCodes(): Flow<List<ActivationCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCode(code: ActivationCodeEntity): Long

    @Query("UPDATE activation_codes SET isUsed = 1, merchantUsername = :merchantUsername, usedAt = :usedAt WHERE code = :code")
    suspend fun markCodeAsUsed(code: String, merchantUsername: String, usedAt: Long): Int

    @Query("DELETE FROM activation_codes WHERE id = :id")
    suspend fun deleteCode(id: Long)

    @Query("SELECT * FROM activation_codes WHERE code = :code LIMIT 1")
    suspend fun getCodeByString(code: String): ActivationCodeEntity?
}

@Dao
interface FollowedStoreDao {
    @Query("SELECT * FROM followed_stores ORDER BY followedAt DESC")
    fun getAllFollowedStores(): Flow<List<FollowedStoreEntity>>

    @Query("SELECT storeId FROM followed_stores")
    fun getFollowedStoreIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM followed_stores WHERE storeId = :storeId)")
    fun isStoreFollowed(storeId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM followed_stores WHERE storeId = :storeId)")
    suspend fun isStoreFollowedSync(storeId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followStore(entity: FollowedStoreEntity)

    @Query("DELETE FROM followed_stores WHERE storeId = :storeId")
    suspend fun unfollowStore(storeId: Long)
}

@Dao
interface UserNotificationSettingsDao {
    @Query("SELECT * FROM user_notification_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserNotificationSettingsEntity?>

    @Query("SELECT * FROM user_notification_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): UserNotificationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserNotificationSettingsEntity)
}

@Dao
interface AppNotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotificationEntity): Long

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM app_notifications")
    suspend fun clearAll()
}

