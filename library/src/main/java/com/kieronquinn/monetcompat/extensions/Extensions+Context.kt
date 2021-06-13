package com.kieronquinn.monetcompat.extensions

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.RestrictTo

/**
 *  Returns whether the current context has a dark configuration
 */
internal val Context.isDarkMode: Boolean
    get() {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }