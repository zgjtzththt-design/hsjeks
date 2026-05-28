package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.MusicDatabase

class MelodyApp : Application() {
    lateinit var database: MusicDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = com.example.data.MusicDatabase.getDatabase(this)
    }
}
