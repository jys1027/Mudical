package com.example.figmamudical.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Userdata::class], version = 1, exportSchema = false)
abstract class UserdataRoomDatabase : RoomDatabase() {

    abstract fun userdataDao(): UserdataDao

    companion object {
        @Volatile
        private var INSTANCE: UserdataRoomDatabase? = null

        fun getDatabase(context: Context): UserdataRoomDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserdataRoomDatabase::class.java,
                    "userdata_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}