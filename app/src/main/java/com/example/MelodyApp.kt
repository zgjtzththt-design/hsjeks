package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.MusicDatabase

class MelodyApp : Application() {
    lateinit var database: MusicDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            database = com.example.data.MusicDatabase.getDatabase(this)
        } catch (e: Exception) {
            e.printStackTrace()
            // In a real app we'd handle this better, but for a prototype 
            // we at least prevent the startup crash if possible.
        }
    }
}
