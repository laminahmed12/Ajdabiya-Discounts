package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StoreEntity::class,
        DealEntity::class,
        MerchantEntity::class,
        FinancialRecordEntity::class,
        AppSettingsEntity::class,
        ActivationCodeEntity::class,
        FollowedStoreEntity::class,
        UserNotificationSettingsEntity::class,
        AppNotificationEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
    abstract fun dealDao(): DealDao
    abstract fun merchantDao(): MerchantDao
    abstract fun financialRecordDao(): FinancialRecordDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun activationCodeDao(): ActivationCodeDao
    abstract fun followedStoreDao(): FollowedStoreDao
    abstract fun userNotificationSettingsDao(): UserNotificationSettingsDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                var createdInstance: AppDatabase? = null
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ajdabiya_discounts.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial settings safely on IO thread
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    createdInstance?.let { populateInitialData(it) }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    })
                    .build()
                createdInstance = instance
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Initialize default settings (pricing, owner phone, secret admin PIN)
            // Completely clean database: Zero mock stores, zero dummy deals!
            database.appSettingsDao().insertOrUpdate(
                AppSettingsEntity(
                    id = 1,
                    regularPrice = 3.0,
                    hotPrice = 5.0,
                    ownerPhone = "0911234567",
                    secretPin = "116936"
                )
            )
        }
    }
}
