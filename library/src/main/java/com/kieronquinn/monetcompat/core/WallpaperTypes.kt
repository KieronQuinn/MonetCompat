package com.kieronquinn.monetcompat.core

import android.annotation.SuppressLint
import android.app.WallpaperManager
import androidx.annotation.IntDef

@SuppressLint("InlinedApi")
class WallpaperTypes {

    @IntDef(WallpaperManager.FLAG_LOCK, WallpaperManager.FLAG_SYSTEM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class WallpaperType

    companion object {
        /**
         *  The system (home screen) wallpaper
         */
        const val WALLPAPER_SYSTEM = WallpaperManager.FLAG_SYSTEM
        /**
         *  The lock screen wallpaper
         */
        const val WALLPAPER_LOCK_SCREEN = WallpaperManager.FLAG_LOCK
    }

}