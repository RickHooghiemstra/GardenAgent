package com.gardenagent.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gardenagent.data.local.db.dao.GardenDao
import com.gardenagent.data.local.db.dao.JournalEntryDao
import com.gardenagent.data.local.db.dao.MaintenanceTaskDao
import com.gardenagent.data.local.db.dao.ManualCameraDao
import com.gardenagent.data.local.db.dao.PlantDao
import com.gardenagent.data.local.db.dao.SensorDeviceDao
import com.gardenagent.data.local.db.dao.SensorReadingDao
import com.gardenagent.data.local.db.dao.WeatherReadingDao
import com.gardenagent.data.local.db.entity.GardenEntity
import com.gardenagent.data.local.db.entity.JournalEntryEntity
import com.gardenagent.data.local.db.entity.MaintenanceTaskEntity
import com.gardenagent.data.local.db.entity.ManualCameraEntity
import com.gardenagent.data.local.db.entity.PlantEntity
import com.gardenagent.data.local.db.entity.SensorDeviceEntity
import com.gardenagent.data.local.db.entity.SensorReadingEntity
import com.gardenagent.data.local.db.entity.WeatherReadingEntity

@Database(
    entities = [
        PlantEntity::class,
        JournalEntryEntity::class,
        SensorReadingEntity::class,
        WeatherReadingEntity::class,
        GardenEntity::class,
        ManualCameraEntity::class,
        SensorDeviceEntity::class,
        MaintenanceTaskEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class GardenDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun sensorReadingDao(): SensorReadingDao
    abstract fun weatherReadingDao(): WeatherReadingDao
    abstract fun gardenDao(): GardenDao
    abstract fun manualCameraDao(): ManualCameraDao
    abstract fun sensorDeviceDao(): SensorDeviceDao
    abstract fun maintenanceTaskDao(): MaintenanceTaskDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `maintenance_tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `description` TEXT NOT NULL, `scheduledFor` INTEGER NOT NULL, `plantName` TEXT, `reason` TEXT NOT NULL, `workManagerId` TEXT)")
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `gardens` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `description` TEXT, `createdAt` INTEGER NOT NULL)")
                db.execSQL("INSERT INTO `gardens` (`name`, `type`, `description`, `createdAt`) VALUES ('My Garden', 'OUTDOOR', NULL, ${System.currentTimeMillis()})")
                db.execSQL("ALTER TABLE `plants` ADD COLUMN `gardenId` INTEGER")
                db.execSQL("ALTER TABLE `journal_entries` ADD COLUMN `gardenId` INTEGER")
                db.execSQL("ALTER TABLE `journal_entries` ADD COLUMN `isAiGenerated` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("CREATE TABLE IF NOT EXISTS `manual_cameras` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `rtspUrl` TEXT NOT NULL, `locationDescription` TEXT, `gardenId` INTEGER)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `sensor_devices` (`address` TEXT NOT NULL, `deviceName` TEXT NOT NULL, `locationDescription` TEXT, `gardenId` INTEGER, PRIMARY KEY(`address`))")
            }
        }
    }
}
