package com.example.trackrscope

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase de aplicación que se utiliza para configurar la aplicación.
 */
@HiltAndroidApp
class TrackrScopeApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}