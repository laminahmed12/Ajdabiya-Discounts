package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val address: String,
    val phone: String,
    val whatsapp: String,
    val facebookUrl: String,
    val messengerUrl: String = "",
    val merchantUsername: String = "",
    val discountType: String = "عروض ساخنة وتخفيضات موسمية",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deals")
data class DealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeId: Long,
    val storeName: String,
    val merchantUsername: String,
    val content: String,
    val facebookPostUrl: String,
    val dealType: String, // "HOT" or "REGULAR"
    val priceCost: Double,
    val phone: String,
    val whatsapp: String,
    val messengerUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "merchants")
data class MerchantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String, // اسم المستخدم للدخول
    val password: String, // كلمة المرور
    val storeName: String, // اسم المحل
    val location: String = "أجدابيا", // موقع المحل
    val discountType: String = "عروض ساخنة وتخفيضات موسمية", // نوع التخفيضات المقدمة
    val phone: String, // رقم الهاتف للتواصل
    val category: String = "عام",
    val address: String = "أجدابيا",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "financial_records")
data class FinancialRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val merchantUsername: String,
    val storeName: String,
    val dealId: Long,
    val dealType: String, // "عادي" أو "ساخن"
    val dealContentPreview: String,
    val cost: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val monthKey: String // e.g. "2026-10" or formatted month
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val regularPrice: Double = 3.0,
    val hotPrice: Double = 5.0,
    val ownerPhone: String = "0911234567",
    val secretPin: String = "116936"
)

@Entity(tableName = "activation_codes")
data class ActivationCodeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // رمز التفعيل المولد
    val merchantUsername: String = "", // اسم التاجر المرتبط (إن وجد)
    val planType: String = "اشتراك شهري كامل", // نوع الباقة أو التفعيل
    val isUsed: Boolean = false, // هل تم تفعيل الرمز
    val createdAt: Long = System.currentTimeMillis(),
    val usedAt: Long? = null
)

@Entity(tableName = "followed_stores")
data class FollowedStoreEntity(
    @PrimaryKey
    val storeId: Long,
    val storeName: String,
    val followedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_notification_settings")
data class UserNotificationSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val notifyFollowedStores: Boolean = true,
    val notifyNearbyStores: Boolean = true,
    val userNeighborhood: String = "وسط المدينة",
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val storeId: Long,
    val storeName: String,
    val dealId: Long = 0,
    val notificationType: String = "FOLLOWED_STORE", // "FOLLOWED_STORE", "NEARBY_STORE", "BOTH"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

