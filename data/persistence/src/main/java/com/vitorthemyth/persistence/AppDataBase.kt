package com.vitorthemyth.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vitorthemyth.persistence.models.TimeEntity

@Database(entities = [TimeEntity::class], version = 1, exportSchema = false)
@TypeConverters(PartidaListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                instance = db
                db
            }
        }
    }
}
