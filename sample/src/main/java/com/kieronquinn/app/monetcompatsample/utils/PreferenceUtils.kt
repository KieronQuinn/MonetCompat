package com.kieronquinn.app.monetcompatsample.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import dev.kdrag0n.monet.factory.ColorSchemeFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log10
import kotlin.math.pow

object PreferenceUtils {

    private const val SHARED_PREFS_NAME = "_prefs"
    private const val KEY_SELECTED_COLOR = "selected_color"
    private const val KEY_OVERRIDE_ENABLED = "override_defaults"
    private const val KEY_ZCAM_ENABLED = "zcam_enabled"
    private const val KEY_CHROMA_FACTOR = "chroma_factor"
    private const val KEY_ACCURATE_SHADES = "accurate_shades"
    private const val KEY_WHITE_LUMINANCE = "white_luminance"
    private const val KEY_USE_LINEAR_BRIGHTNESS = "linear_brightness"
    private const val KEY_USE_SYSTEM_COLORS = "use_system_colors"

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

    suspend fun setOverrideEnabled(context: Context, enabled: Boolean) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putBoolean(KEY_OVERRIDE_ENABLED, enabled).commit()
    }

    suspend fun getOverrideEnabled(context: Context): Boolean = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getBoolean(KEY_OVERRIDE_ENABLED, false)
    }

    suspend fun getZCamEnabled(context: Context): Boolean = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getBoolean(KEY_ZCAM_ENABLED, false)
    }

    suspend fun setZCamEnabled(context: Context, enabled: Boolean) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putBoolean(KEY_ZCAM_ENABLED, enabled).commit()
    }

    suspend fun getChromaFactor(context: Context): Double = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getString(KEY_CHROMA_FACTOR, "")?.toDoubleOrNull() ?: 1.0
    }

    suspend fun setChromaFactor(context: Context, value: Double) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putString(KEY_CHROMA_FACTOR, value.toString()).commit()
    }

    suspend fun getAccurateShadesEnabled(context: Context): Boolean = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getBoolean(KEY_ACCURATE_SHADES, true)
    }

    suspend fun setAccurateShadesEnabled(context: Context, enabled: Boolean) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putBoolean(KEY_ACCURATE_SHADES, enabled).commit()
    }

    suspend fun getLinearBrightnessEnabled(context: Context): Boolean = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getBoolean(KEY_USE_LINEAR_BRIGHTNESS, true)
    }

    suspend fun setLinearBrightnessEnabled(context: Context, enabled: Boolean) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        sharedPreferences.edit().putBoolean(KEY_USE_LINEAR_BRIGHTNESS, enabled).commit()
    }

    suspend fun getWhiteLuminance(context: Context): Int = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getInt(KEY_WHITE_LUMINANCE, WHITE_LUMINANCE_USER_DEFAULT)
    }

    suspend fun setWhiteLuminance(context: Context, value: Int) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.edit().putInt(KEY_WHITE_LUMINANCE, value).commit()
    }

    suspend fun getUseSystem(context: Context): Boolean = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.getBoolean(KEY_USE_SYSTEM_COLORS, true) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    suspend fun setUseSystem(context: Context, value: Boolean) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        return@withContext sharedPreferences.edit().putBoolean(KEY_USE_SYSTEM_COLORS, value).commit()
    }

    suspend fun clearSettings(context: Context) = withContext(Dispatchers.IO){
        val sharedPreferences = context.getSharedPreferences()
        val settingsKeys = arrayOf(
            KEY_OVERRIDE_ENABLED, KEY_USE_SYSTEM_COLORS, KEY_ZCAM_ENABLED, KEY_CHROMA_FACTOR, KEY_ACCURATE_SHADES, KEY_USE_LINEAR_BRIGHTNESS, KEY_WHITE_LUMINANCE
        )
        sharedPreferences.edit().apply {
            settingsKeys.forEach {
                remove(it)
            }
        }.commit()
    }

    suspend fun getColorSchemeFactory(
        context: Context,
        overrideEnabled: Boolean? = null,
        zCamEnabled: Boolean? = null,
        chromaFactor: Double? = null,
        accurateShades: Boolean? = null,
        whiteLuminance: Double? = null,
        linearBrightness: Boolean? = null
    ): ColorSchemeFactory? {
        val _overrideEnabled = overrideEnabled ?: getOverrideEnabled(context)
        val _zCamEnabled = zCamEnabled ?: getZCamEnabled(context)
        val _chromaFactor = chromaFactor ?: getChromaFactor(context)
        val _accurateShades = accurateShades ?: getAccurateShadesEnabled(context)
        val _whiteLuminance = whiteLuminance ?: convertRawLuminance(getWhiteLuminance(context))
        val _linearBrightness = linearBrightness ?: getLinearBrightnessEnabled(context)
        if(!_overrideEnabled) return null
        return ColorSchemeFactory.getFactory(
            _zCamEnabled,
            _chromaFactor,
            _accurateShades,
            _whiteLuminance,
            _linearBrightness
        )
    }

    private fun Context.getSharedPreferences(): SharedPreferences {
        return getSharedPreferences(packageName + SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    //Copied from SettingsRepository https://github.com/kdrag0n/android12-extensions/blob/main/app/src/main/java/dev/kdrag0n/android12ext/data/SettingsRepository.kt

    private const val WHITE_LUMINANCE_MIN = 1.0
    private const val WHITE_LUMINANCE_MAX = 10000.0
    const val WHITE_LUMINANCE_USER_MAX = 1000
    const val WHITE_LUMINANCE_USER_STEP = 25 // both max and default must be divisible by this
    const val WHITE_LUMINANCE_USER_DEFAULT = 425 // ~200.0 divisible by step (decoded = 199.526)

    fun convertRawLuminance(userValue: Int): Double {
        val userSrc = userValue.toDouble() / WHITE_LUMINANCE_USER_MAX
        val userInv = 1.0 - userSrc
        return (10.0).pow(userInv * log10(WHITE_LUMINANCE_MAX))
            .coerceAtLeast(WHITE_LUMINANCE_MIN)
    }

}