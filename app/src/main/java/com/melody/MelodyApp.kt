package com.melody

import android.app.Application
import com.melody.data.MusicDatabase

class MelodyApp : Application() {
    lateinit var database: MusicDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = MusicDatabase.getDatabase(this)
    }
}
