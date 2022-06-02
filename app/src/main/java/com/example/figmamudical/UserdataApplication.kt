package com.example.figmamudical

import android.app.Application
import com.example.figmamudical.data.UserdataRoomDatabase

class UserdataApplication : Application() {
    val database: UserdataRoomDatabase by lazy { UserdataRoomDatabase.getDatabase(this) }
}