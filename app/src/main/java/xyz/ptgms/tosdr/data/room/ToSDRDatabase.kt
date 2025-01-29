package xyz.ptgms.tosdr.data.room

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Keep
@Database(entities = [ServiceEntity::class], version = 1)
abstract class ToSDRDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: ToSDRDatabase? = null

        fun getDatabase(context: Context): ToSDRDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ToSDRDatabase::class.java,
                    "tosdr_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
} 