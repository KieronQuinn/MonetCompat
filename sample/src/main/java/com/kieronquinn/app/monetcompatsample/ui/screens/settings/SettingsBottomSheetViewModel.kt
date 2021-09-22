package com.kieronquinn.app.monetcompatsample.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.processphoenix.ProcessPhoenix
import com.kieronquinn.app.monetcompatsample.utils.PreferenceUtils
import com.kieronquinn.app.monetcompatsample.utils.extensions.combine
import com.kieronquinn.monetcompat.core.MonetCompat
import dev.kdrag0n.monet.factory.ColorSchemeFactory
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class SettingsBottomSheetViewModel : ViewModel() {

    abstract val overrideEnabled: Flow<Boolean>
    abstract val useSystem: Flow<Boolean>
    abstract val zCamEnabled: Flow<Boolean>
    abstract val chromaFactor: Flow<Double>
    abstract val accurateShades: Flow<Boolean>
    abstract val whiteLuminance: Flow<Int>
    abstract val linearBrightness: Flow<Boolean>

    abstract val changed: Flow<ColorSchemeFactory?>

    abstract fun setOverrideEnabled(context: Context, enabled: Boolean)
    abstract fun setUseSystemEnabled(context: Context, enabled: Boolean)
    abstract fun setZCamEnabled(context: Context, enabled: Boolean)
    abstract fun setChromaFactor(context: Context, factor: Double)
    abstract fun setAccurateShades(context: Context, enabled: Boolean)
    abstract fun setWhiteLuminance(context: Context, luminance: Int)
    abstract fun setLinearBrightness(context: Context, enabled: Boolean)

    abstract fun resetSettings(context: Context)

}

class SettingsBottomSheetViewModelImpl(context: Context) : SettingsBottomSheetViewModel() {

    companion object {
        private const val CHANGED_DEBOUNCE = 500L
    }

    private val _overrideEnabled = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getOverrideEnabled(context))
        }
    }
    override val overrideEnabled = _overrideEnabled.asSharedFlow()

    private val _useSystem = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getUseSystem(context))
        }
    }
    override val useSystem = _useSystem.asSharedFlow()

    private val _zCamEnabled = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getZCamEnabled(context))
        }
    }
    override val zCamEnabled = _zCamEnabled.asSharedFlow()

    private val _chromaFactor = MutableSharedFlow<Double>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getChromaFactor(context))
        }
    }
    override val chromaFactor = _chromaFactor.asSharedFlow()

    private val _accurateShades = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getAccurateShadesEnabled(context))
        }
    }
    override val accurateShades = _accurateShades.asSharedFlow()

    private val _whiteLuminance = MutableSharedFlow<Int>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getWhiteLuminance(context))
        }
    }
    override val whiteLuminance = _whiteLuminance.asSharedFlow()

    private val _linearBrightness = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).apply {
        viewModelScope.launch {
            emit(PreferenceUtils.getAccurateShadesEnabled(context))
        }
    }
    override val linearBrightness = _linearBrightness.asSharedFlow()

    override val changed = combine(
        overrideEnabled.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        useSystem.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        zCamEnabled.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        chromaFactor.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        accurateShades.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        whiteLuminance.distinctUntilChanged().debounce(CHANGED_DEBOUNCE),
        linearBrightness.distinctUntilChanged().debounce(CHANGED_DEBOUNCE)
    ) { enabled, useSystem, zCam, chroma, shades, luminance, brightness ->
        MonetCompat.useSystemColorsOnAndroid12 = useSystem
        PreferenceUtils.getColorSchemeFactory(
            context, enabled, zCam, chroma, shades, PreferenceUtils.convertRawLuminance(luminance), brightness
        )
    }

    override fun setOverrideEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            PreferenceUtils.setOverrideEnabled(context, enabled)
            _overrideEnabled.emit(enabled)
        }
    }

    override fun setUseSystemEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            PreferenceUtils.setUseSystem(context, enabled)
            _useSystem.emit(enabled)
        }
    }

    override fun setZCamEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            PreferenceUtils.setZCamEnabled(context, enabled)
            _zCamEnabled.emit(enabled)
        }
    }

    override fun setChromaFactor(context: Context, factor: Double) {
        viewModelScope.launch {
            PreferenceUtils.setChromaFactor(context, factor)
            _chromaFactor.emit(factor)
        }
    }

    override fun setAccurateShades(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            PreferenceUtils.setAccurateShadesEnabled(context, enabled)
            _accurateShades.emit(enabled)
        }
    }

    override fun setWhiteLuminance(context: Context, luminance: Int) {
        viewModelScope.launch {
            PreferenceUtils.setWhiteLuminance(context, luminance)
            _whiteLuminance.emit(luminance)
        }
    }

    override fun setLinearBrightness(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            PreferenceUtils.setLinearBrightnessEnabled(context, enabled)
            _linearBrightness.emit(enabled)
        }
    }

    override fun resetSettings(context: Context) {
        viewModelScope.launch {
            PreferenceUtils.clearSettings(context)
            //Force a full restart to reload all settings
            ProcessPhoenix.triggerRebirth(context)
        }
    }

}
