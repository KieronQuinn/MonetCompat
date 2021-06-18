package com.kieronquinn.app.monetcompatsample.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PreferenceUtils {

    private const val SHARED_PREFS_NAME = "_prefs"
    private const val KEY_SELECTED_COLOR = "selected_color"

    suspend fun getSelectedColor(context: Context): Int? = withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences()
        val color = sharedPreferences.getInt(KEY_SELECTED_COLOR, Integer.MAX_VALUE)
        return@withContext if(color == Integer.MAX_VALUE) null
        else color
    }

    suspend fun setSelectedColor(context: Context, color: Int) = withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putInt(KEY_SELECTED_COLOR, color).commit()
    }

    private fun Context.getSharedPreferences(): SharedPreferences {
        return getSharedPreferences(packageName + SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

}