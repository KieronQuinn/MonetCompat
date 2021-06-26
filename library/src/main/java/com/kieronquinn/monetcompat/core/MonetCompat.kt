package com.kieronquinn.monetcompat.core

import android.Manifest
import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.kieronquinn.monetcompat.R
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.kieronquinn.monetcompat.extensions.getAttributeColor
import com.kieronquinn.monetcompat.extensions.isDarkMode
import com.kieronquinn.monetcompat.extensions.isSameAs
import com.kieronquinn.monetcompat.extensions.toArgb
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.colors.Srgb
import dev.kdrag0n.monet.theme.DynamicColorScheme
import dev.kdrag0n.monet.theme.TargetColors
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class MonetCompat private constructor(context: Context) {

    companion object {
        private var INSTANCE: MonetCompat? = null
        private var paletteCompatEnabled = false
        private const val TAG = "MonetCompat"

        /**
         *  Set the multiplier of the chroma of the generated colors, defaults to `1.0`
         */
        @JvmStatic
        var chromaMultiplier = 1.0

        /**
         *  Enable some debug logging to the "MonetCompat" tag
         */
        @JvmStatic
        var debugLog = false

        /**
         *  Set the wallpaper source to use, either [WallpaperTypes.WALLPAPER_SYSTEM] (home screen)
         *  or [WallpaperTypes.WALLPAPER_LOCK_SCREEN] (lock screen)
         *  **Important**: This is ignored on Android 8.0 or below when in Palette compat mode,
         *  due to API limitations.
         */
        @JvmStatic
        @WallpaperTypes.WallpaperType
        var wallpaperSource = WallpaperTypes.WALLPAPER_SYSTEM

        /**
         *  Set up MonetCompat with a given context. Due to the Configuration.uiMode being used,
         *  use your themed context for this (usually the Activity context, or returned from
         *  Fragment.requireContext() in a fragment), not the Application context.
         *  @param context Your **themed** context
         */
        @JvmStatic
        fun setup(context: Context): MonetCompat {
            if(INSTANCE != null){
                INSTANCE!!.updateConfiguration(context)
                return INSTANCE!!
            }
            INSTANCE = MonetCompat(context).apply {
                updateConfiguration(context)
                registerWallpaperChangedReceiver(context)
            }
            return INSTANCE!!
        }

        /**
         *  Get the current MonetCompat instance, throws a [MonetInstanceException] if unavailable
         */
        @JvmStatic
        fun getInstance(): MonetCompat {
            return INSTANCE ?: throw MonetInstanceException()
        }

        /**
         *  Enable getting the wallpaper's primary color on Android 8.0 and below, using
         *  androidx.palette. You must include Palette as a dependency in your gradle to use this
         *  and the user should have granted READ_EXTERNAL_STORAGE, though this doesn't seem
         *  to be required on all devices.
         *  If the wallpaper is unavailable as the permission has not been granted and is required,
         *  the [defaultPrimaryColor] will be used instead.
         */
        @JvmStatic
        @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE, conditional = true)
        fun enablePaletteCompat(){
            try {
                //Check palette is available
                Class.forName("androidx.palette.graphics.Palette")
                paletteCompatEnabled = true
            }catch (e: ClassNotFoundException){
                throw MonetPaletteException()
            }
        }

        /**
         *  Optionally implement your own picker to choose from the (up to) three wallpaper picked colors.
         *  You may wish to have a color picker elsewhere in your app, using SharedPreferences
         *  (and default to `firstOrNull()`) to pick it from the list if it's still there.
         *
         *  Note that this list will change with the wallpaper, so if you have saved a preference here,
         *  don't just return it blind - make sure it's still in the list first.
         *
         *  **Note:** The list can be `null` if the wallpaper colors failed to be extracted. You can
         *  either return your chosen default color, or return `null` and MonetCompat will set the default
         *  for you.
         */
        @JvmStatic
        var wallpaperColorPicker: suspend (List<Int>?) -> Int? = {
            it?.firstOrNull()
        }
    }

    private val wallpaperManager by lazy {
        context.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
    }

    private val wallpaperChangedReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateMonetColorsInternal()
        }
    }

    private var monetColorsChangedListeners = mutableListOf<MonetColorsChangedListener>()

    /**
     *  Default app Primary color, will be set to [android.R.attr.colorPrimary] from the
     *  *creation context's* theme if unchanged
     *  This is used as the "wallpaper color" when unable to get a wallpaper and in [getPrimaryColor]
     *  when there are no generated monet colors
     */
    var defaultPrimaryColor: Int? = null
        get() {
            return if(field == null){
                theme.getAttributeColor(android.R.attr.colorPrimary)
                    ?: throw MonetAttributeNotFoundException("android.R.attr.colorPrimary")
            }else field
        }

    /**
     *  Default app Secondary color, will be set to [R.attr.colorPrimaryVariant] from the
     *  *creation context's* theme if unchanged
     *  This is used as the "wallpaper color" when unable to get a wallpaper and in [getSecondaryColor]
     *  when there are no generated monet colors
     */
    var defaultSecondaryColor: Int? = null
        get() {
            return if(field == null){
                theme.getAttributeColor(R.attr.colorPrimaryVariant)
                    ?: throw MonetAttributeNotFoundException("R.attr.colorPrimaryVariant")
            }else field
        }

    /**
     *  Default app Background color, will be set to [android.R.attr.windowBackground] from the
     *  *creation context's* theme if unchanged
     *  This is used in in [getBackgroundColor] when there are no generated monet colors
     */
    var defaultBackgroundColor: Int? = null
        get() {
            return if(field == null){
                theme.getAttributeColor(android.R.attr.windowBackground)
                    ?: throw MonetAttributeNotFoundException("android.R.attr.windowBackground")
            }else field
        }

    /**
     *  Default app Accent color, will be set to [android.R.attr.colorAccent] from the
     *  *creation context's* theme if unchanged
     *  This is used in in [getAccentColor] when there are no generated monet colors
     */
    var defaultAccentColor: Int? = null
        get() {
            return if(field == null){
                theme.getAttributeColor(android.R.attr.colorAccent)
                    ?: throw MonetAttributeNotFoundException("android.R.attr.colorAccent")
            }else field
        }

    /**
     *  If Monet extraction has been run, this will be the primary color returned from
     *  [WallpaperManager.getWallpaperColors] or [Palette.getDominantColor] via
     *  [WallpaperManager.getDrawable] if using [paletteCompatEnabled]
     */
    var wallpaperPrimaryColor: Int? = null

    private val defaultColorScheme by lazy {
        getDefaultColors()
    }

    private var monetColors: DynamicColorScheme? = null
    private var darkTheme: Boolean? = null
    private lateinit var theme: Resources.Theme

    /**
     *  Gets the wallpaper primary color, creates a [DynamicColorScheme] with it (or use defaults),
     *  then calls out to all the listeners if it's different to the previous scheme.
     *
     *  Getting the wallpaper colors from [WallpaperManager] is recommended to be run on an I/O thread
     *  due to IPC, and Palette needs to be run on I/O in the case of being in Palette compat mode,
     *  so the [Dispatchers.IO] dispatcher is used here.
     */
    private fun updateMonetColorsInternal(isUiModeChange: Boolean = false) = GlobalScope.launch(Dispatchers.IO) {
        val primaryColor = getWallpaperPrimaryColorCompat()
        wallpaperPrimaryColor = primaryColor
        val newMonetColors = if(primaryColor != null){
            if(debugLog){
                Log.i(TAG, "Got wallpaper primary color #${Integer.toHexString(primaryColor)}")
            }
            DynamicColorScheme(TargetColors(chromaMultiplier), Srgb(primaryColor), chromaMultiplier)
        }else{
            if(debugLog){
                Log.w(TAG, "Unable to get primary color from wallpaper, using default app colors")
            }
            defaultColorScheme
        }
        val hasChanged = isUiModeChange || !newMonetColors.isSameAs(monetColors)
        val isInitialChange = monetColors == null
        monetColors = newMonetColors
        if(hasChanged) {
            withContext(Dispatchers.Main) {
                monetColorsChangedListeners.forEach {
                    it.onMonetColorsChanged(this@MonetCompat, newMonetColors, isInitialChange)
                }
            }
        }
    }

    /**
     *  Manually trigger a re-processing of wallpaper colors, without a configuration change
     *  Result will be sent to [MonetColorsChangedListener.onMonetColorsChanged]
     */
    fun updateMonetColors(){
        updateMonetColorsInternal()
    }

    /**
     *  Call [MonetColorsChangedListener.onMonetColorsChanged] with the current Monet colors, if
     *  available. This prevents the need to re-calculate all the colors and notify all the listeners
     *  after one has been attached.
     */
    private fun notifySelfListener(listener: MonetColorsChangedListener){
        monetColors?.let {
            listener.onMonetColorsChanged(this, it, false)
        }
    }

    /**
     *  Returns the full set of Monet colors produced, or the default if they've not been
     *  generated / can't be generated
     */
    fun getMonetColors(): DynamicColorScheme {
        return monetColors ?: defaultColorScheme
    }

    /**
     *  Returns a color suitable for use as a background of an activity or fragment
     *  If Monet colors aren't available, the [defaultBackgroundColor] will be returned instead
     *  @param context Your context, ideally a non-application context as it will be used to check
     *  if dark mode is enabled
     *  @param darkMode An optional override for whether to use dark mode
     */
    fun getBackgroundColor(context: Context, darkMode: Boolean? = null): Int {
        return if(darkMode ?: context.isDarkMode){
            monetColors?.neutral1?.get(900)?.toArgb() ?: ContextCompat.getColor(context, defaultBackgroundColor!!)
        }else{

            monetColors?.neutral1?.get(50)?.toArgb() ?: ContextCompat.getColor(context, defaultBackgroundColor!!)
        }
    }

    /**
     *  Returns a secondary color suitable for use as a background of a toolbar or similar element
     *  if you do not want to use elevation.
     *  There is no default secondary color, so `null` will be returned if Monet colors are not
     *  available.
     *  @param context Your context, ideally a non-application context as it will be used to check
     *  if dark mode is enabled
     *  @param darkMode An optional override for whether to use dark mode
     */
    fun getBackgroundColorSecondary(context: Context, darkMode: Boolean? = null): Int? {
        return if(darkMode ?: context.isDarkMode){
            monetColors?.neutral1?.get(700)?.toArgb()
        }else{
            monetColors?.neutral1?.get(100)?.toArgb()
        }
    }

    /**
     *  Returns a color suitable for use as an app's accent
     *  If Monet colors aren't available, the [defaultAccentColor] will be returned instead
     *  @param context Your context, ideally a non-application context as it will be used to check
     *  if dark mode is enabled
     *  @param darkMode An optional override for whether to use dark mode
     */
    fun getAccentColor(context: Context, darkMode: Boolean? = null): Int {
        return if(darkMode ?: context.isDarkMode){
            monetColors?.accent1?.get(100)?.toArgb() ?: ContextCompat.getColor(context, defaultAccentColor!!)
        }else{
            monetColors?.accent1?.get(700)?.toArgb() ?: ContextCompat.getColor(context, defaultAccentColor!!)
        }
    }

    /**
     *  Returns a color suitable for use as an app's primary color
     *  If Monet colors aren't available, the [defaultPrimaryColor] will be returned instead
     *  @param context Your context, ideally a non-application context as it will be used to check
     *  if dark mode is enabled
     *  @param darkMode An optional override for whether to use dark mode
     */
    fun getPrimaryColor(context: Context, darkMode: Boolean? = null): Int {
        return if(darkMode ?: context.isDarkMode){
            monetColors?.accent2?.get(600)?.toArgb() ?: ContextCompat.getColor(context, defaultPrimaryColor!!)
        }else{
            monetColors?.accent2?.get(100)?.toArgb() ?: ContextCompat.getColor(context, defaultPrimaryColor!!)
        }
    }

    /**
     *  Returns a color suitable for use as an app's secondary color
     *  If Monet colors aren't available, the [defaultSecondaryColor] will be returned instead
     *  @param context Your context, ideally a non-application context as it will be used to check
     *  if dark mode is enabled
     *  @param darkMode An optional override for whether to use dark mode
     */
    fun getSecondaryColor(context: Context, darkMode: Boolean? = null): Int {
        return if(darkMode ?: context.isDarkMode){
            monetColors?.accent2?.get(400)?.toArgb() ?: ContextCompat.getColor(context, defaultSecondaryColor!!)
        }else{
            monetColors?.accent2?.get(300)?.toArgb() ?: ContextCompat.getColor(context, defaultSecondaryColor!!)
        }
    }

    /**
     *  Manually update the dark mode configuration for a given context
     *  If you are handling configChanges="uiMode" manually, you should call this from
     *  onConfigurationChanged(Context) in your activity
     *  @param context Your context that has configuration, **not** application context
     */
    fun updateConfiguration(context: Context){
        val isDarkTheme = context.isDarkMode
        theme = context.theme
        if(darkTheme != null && darkTheme != isDarkTheme){
            updateMonetColorsInternal(true)
        }
        darkTheme = context.isDarkMode
    }

    /**
     *  Add an interface to receive Monet color changes
     *  [MonetColorsChangedListener.onMonetColorsChanged] will be called with the new Monet colors
     *  and MonetCompat instance when the colors change
     *  @param listener The interface to receive Monet color changes
     */
    fun addMonetColorsChangedListener(listener: MonetColorsChangedListener, notifySelf: Boolean = false){
        if(!monetColorsChangedListeners.contains(listener)){
            monetColorsChangedListeners.add(listener)
            if(notifySelf){
                notifySelfListener(listener)
            }
        }
    }

    /**
     *  Remove an interface from receiving Monet color changes
     *  @param listener The interface to stop receive Monet color changes
     */
    fun removeMonetColorsChangedListener(listener: MonetColorsChangedListener){
        if(monetColorsChangedListeners.contains(listener)){
            monetColorsChangedListeners.remove(listener)
        }
    }

    /**
     *  Suspend function that waits until Monet is ready, if it isn't already.
     *  This is useful if you are using [MonetCompatActivity.recreateMode] and want to wait for
     *  Monet to be ready before you change screen.
     */
    suspend fun awaitMonetReady() = suspendCancellableCoroutine<Unit> {
        //Just resume if already ready
        if(monetColors != null){
            it.resume(Unit)
            return@suspendCancellableCoroutine
        }
        //Not ready, add a listener and resume when called
        val listener = object: MonetColorsChangedListener {
            override fun onMonetColorsChanged(
                monet: MonetCompat,
                monetColors: DynamicColorScheme,
                isInitialChange: Boolean
            ) {
                removeMonetColorsChangedListener(this)
                it.resume(Unit)
            }
        }
        addMonetColorsChangedListener(listener)
        it.invokeOnCancellation {
            removeMonetColorsChangedListener(listener)
        }
    }

    /**
     *  Adds a BroadcastReceiver for ACTION_WALLPAPER_CHANGED to automatically update the colors
     *  when the wallpaper is changed
     *  ACTION_WALLPAPER_CHANGED is deprecated (but still works) - we can't use FLAG_SHOW_WALLPAPER
     *  as it's not suitable for our use case
     */
    private fun registerWallpaperChangedReceiver(context: Context){
        @Suppress("DEPRECATION")
        context.registerReceiver(wallpaperChangedReceiver, IntentFilter(Intent.ACTION_WALLPAPER_CHANGED))
    }

    /**
     *  Get a [DynamicColorScheme] instance for the [defaultPrimaryColor] and specified [chromaMultiplier]
     */
    private fun getDefaultColors(): DynamicColorScheme {
        return DynamicColorScheme(TargetColors(chromaMultiplier), Srgb(defaultPrimaryColor!!), chromaMultiplier)
    }

    /**
     *  Gets the color(s) from the wallpaper using the following logic:
     *  - If using Android 8.1 or above, use [WallpaperManager.getWallpaperColors]
     *  - If using Android 8.1 and below and Palette compat is enabled, use Palette to extract
     *     the color from the wallpaper and use that
     *  - Otherwise, null and the default app colors will be used elsewhere instead
     *
     *  These colors are then passed into the [wallpaperColorPicker] (defaults to just returning the
     *  first item from the list), to allow custom implementations of pickers.
     *
     *  **Important**: getWallpaperColors relies on a *developer implementation* in live wallpapers to
     *  return the correct colors, and Palette compat does not work with live wallpapers at all.
     *  `null` will be returned in the case of incompatibility (and the default colors will be used
     *  instead)
     */
    private suspend fun getWallpaperPrimaryColorCompat(): Int? {
        val wallpaperColors = getAvailableWallpaperColors() ?: return null
        return wallpaperColorPicker.invoke(wallpaperColors)
    }

    /**
     *  Returns a list of the available colors for the currently set wallpaper, which you can then
     *  use to show a picker for the user to pick and save their preferred color.
     *  You can then implement [wallpaperColorPicker] to pick this from the list (if it's still
     *  available) to use in the Monet theming.
     *
     *  **Note:** This list may be empty, if the wallpaper colors are unable to be extracted.
     */
    suspend fun getAvailableWallpaperColors(): List<Int>? = withContext(Dispatchers.IO) {
        return@withContext when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
                val wallpaperColors = wallpaperManager.getWallpaperColors(wallpaperSource)
                wallpaperColors?.getColorOptions()
            }
            paletteCompatEnabled -> {
                val wallpaper = wallpaperManager.drawable ?: return@withContext null
                Palette.from((wallpaper as BitmapDrawable).bitmap).generate().getColorOptions()
            }
            else -> null
        }
    }

    /**
     *  Returns the currently selected wallpaper color (or null). This value is passed through
     *  the [wallpaperColorPicker], and is suitable for use to show the user's current selection
     *  in color pickers.
     */
    suspend fun getSelectedWallpaperColor(): Int? {
        val wallpaperColors = getAvailableWallpaperColors() ?: return null
        return wallpaperColorPicker.invoke(wallpaperColors)
    }

    /**
     *  Creates an array of the three picked colors from [WallpaperColors] to go into the color picker
     */
    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun WallpaperColors.getColorOptions(): List<Int> {
        return arrayOf(primaryColor, secondaryColor, tertiaryColor).filterNot { it == null }.distinct().map { it!!.toArgb() }
    }

    /**
     *  Creates an array of the three picked colors from [Palette] to go into the color picker
     */
    private fun Palette.getColorOptions(): List<Int>? {
        val colors = arrayOf(getDominantColor(0), getVibrantColor(0), this.getMutedColor(0)).filterNot { it == 0 }.distinct()
        return if(colors.isNotEmpty()) colors
        else null
    }

}