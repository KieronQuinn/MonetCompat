package com.kieronquinn.app.monetcompatsample.ui.screens.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentBottomSheetSettingsBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BaseBottomSheetDialogFragment
import com.kieronquinn.app.monetcompatsample.utils.PreferenceUtils
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.views.applyMonet
import com.kieronquinn.monetcompat.extensions.views.applyMonetLight
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class SettingsBottomSheetFragment: BaseBottomSheetDialogFragment<FragmentBottomSheetSettingsBinding>(FragmentBottomSheetSettingsBinding::inflate) {

    private val viewModel by viewModel<SettingsBottomSheetViewModel>()
    private var hasMadeChange = false

    private val alphaDisableableViews by lazy {
        arrayOf(
            binding.settingsAndroid12System,
            binding.settingsZcam,
            binding.settingsChromaFactor,
            binding.settingsChromaFactorSlider,
            binding.settingsAccurateShades,
            binding.settingsWhiteLuminance,
            binding.settingsWhiteLuminanceSlider,
            binding.settingsLinearBrightness
        )
    }

    private val disableableViews by lazy {
        arrayOf(
            binding.settingsAndroid12System,
            binding.settingsAndroid12SystemSwitch,
            binding.settingsZcamSwitch,
            binding.settingsZcam,
            binding.settingsChromaFactorSlider,
            binding.settingsAccurateShades,
            binding.settingsAccurateShadesSwitch,
            binding.settingsWhiteLuminanceSlider,
            binding.settingsLinearBrightness,
            binding.settingsLinearBrightnessSwitch
        )
    }

    private val alphaDisableableViewsSystem by lazy {
        alphaDisableableViews.filterNot { it.id == R.id.settings_android_12_system }
    }

    private val disableableViewsSystem by lazy {
        disableableViews.filterNot { it.id == R.id.settings_android_12_system || it.id == R.id.settings_android_12_system_switch }
    }

    private val alphaDisableableViewsZcam by lazy {
        arrayOf(
            binding.settingsWhiteLuminance,
            binding.settingsWhiteLuminanceSlider,
            binding.settingsLinearBrightness
        )
    }

    private val disableableViewsZcam by lazy {
        arrayOf(
            binding.settingsWhiteLuminanceSlider,
            binding.settingsLinearBrightness,
            binding.settingsLinearBrightnessSwitch
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEnabledListener()
        setupOverrideSwitch()
        setupUseSystemSwitch()
        setupZcamSwitch()
        setupChromaFactorSlider()
        setupAccurateShades()
        setupWhiteLuminanceSlider()
        setupLinearBrightness()
        setupChangeListener()
        setupClose()
        setupReset()
    }

    override fun getThemedContext(): Context {
        return ContextThemeWrapper(requireContext(), R.style.Theme_MonetCompat)
    }

    private fun setupOverrideSwitch() = lifecycleScope.launchWhenResumed {
        binding.settingsOverrideSwitch.applyMonetLight()
        binding.settingsOverrideSwitch.isChecked = viewModel.overrideEnabled.first()
        launch {
            binding.settingsOverrideSwitch.onChange().collect {
                hasMadeChange = true
                viewModel.setOverrideEnabled(requireContext(), it)
            }
        }
        launch {
            viewModel.overrideEnabled.collect {
                binding.settingsOverrideSwitch.isChecked = it
            }
        }
        binding.settingsOverride.setOnClickListener {
            binding.settingsOverrideSwitch.toggle()
        }
    }

    private fun setupUseSystemSwitch() = lifecycleScope.launchWhenResumed {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            binding.settingsAndroid12System.visibility = View.GONE
            return@launchWhenResumed
        }
        binding.settingsAndroid12SystemSwitch.applyMonetLight()
        binding.settingsAndroid12SystemSwitch.isChecked = viewModel.useSystem.first()
        launch {
            binding.settingsAndroid12SystemSwitch.onChange().collect {
                hasMadeChange = true
                viewModel.setUseSystemEnabled(requireContext(), it)
            }
        }
        launch {
            viewModel.useSystem.collect {
                binding.settingsAndroid12SystemSwitch.isChecked = it
            }
        }
        binding.settingsAndroid12System.setOnClickListener {
            binding.settingsAndroid12SystemSwitch.toggle()
        }
    }

    private fun setupZcamSwitch() = lifecycleScope.launchWhenResumed {
        binding.settingsZcamSwitch.applyMonetLight()
        binding.settingsZcamSwitch.isChecked = viewModel.zCamEnabled.first()
        binding.settingsZcamContent.movementMethod = LinkMovementMethod.getInstance()
        launch {
            binding.settingsZcamSwitch.onChange().collect {
                hasMadeChange = true
                viewModel.setZCamEnabled(requireContext(), it)
            }
        }
        launch {
            viewModel.zCamEnabled.collect {
                binding.settingsZcamSwitch.isChecked = it
            }
        }
        binding.settingsZcam.setOnClickListener {
            binding.settingsZcamSwitch.toggle()
        }
    }

    private fun setupChromaFactorSlider() = lifecycleScope.launchWhenResumed {
        with(binding.settingsChromaFactorSlider) {
            applyMonet()
            valueTo = 100f
            value = viewModel.chromaFactor.first().toFloat()
            setLabelFormatter {
                String.format("%.01fx", it / 50f)
            }
            launch {
                viewModel.chromaFactor.collect {
                    value = it.toFloat() * 50f
                }
            }
            launch {
                onChange().collect {
                    hasMadeChange = true
                    viewModel.setChromaFactor(requireContext(), (it / 50f).toDouble())
                }
            }
        }
    }

    private fun setupAccurateShades() = lifecycleScope.launchWhenResumed {
        binding.settingsAccurateShadesSwitch.applyMonetLight()
        binding.settingsAccurateShadesSwitch.isChecked = viewModel.accurateShades.first()
        launch {
            viewModel.accurateShades.collect {
                binding.settingsAccurateShadesSwitch.isChecked = it
            }
        }
        launch {
            binding.settingsAccurateShadesSwitch.onChange().collect {
                hasMadeChange = true
                viewModel.setAccurateShades(requireContext(), it)
            }
        }
        binding.settingsAccurateShades.setOnClickListener {
            binding.settingsAccurateShadesSwitch.toggle()
        }
    }

    private fun setupWhiteLuminanceSlider() = lifecycleScope.launchWhenResumed {
        with(binding.settingsWhiteLuminanceSlider) {
            applyMonet()
            setLabelFormatter {
                String.format("%.01fx", it / 50f)
            }
            stepSize = PreferenceUtils.WHITE_LUMINANCE_USER_STEP.toFloat()
            valueTo = PreferenceUtils.WHITE_LUMINANCE_USER_MAX.toFloat()
            value = viewModel.whiteLuminance.first().toFloat()
            launch {
                viewModel.whiteLuminance.collect {
                    value = it.toFloat()
                }
            }
            launch {
                onChange().collect {
                    hasMadeChange = true
                    viewModel.setWhiteLuminance(requireContext(), it.roundToInt())
                }
            }
        }
    }

    private fun setupLinearBrightness() = lifecycleScope.launchWhenResumed {
        binding.settingsLinearBrightnessSwitch.applyMonetLight()
        binding.settingsLinearBrightnessSwitch.isChecked = viewModel.linearBrightness.first()
        launch {
            viewModel.linearBrightness.collect {
                binding.settingsLinearBrightnessSwitch.isChecked = it
            }
        }
        launch {
            binding.settingsLinearBrightnessSwitch.onChange().collect {
                hasMadeChange = true
                viewModel.setLinearBrightness(requireContext(), it)
            }
        }
        binding.settingsLinearBrightness.setOnClickListener {
            binding.settingsLinearBrightnessSwitch.toggle()
        }
    }

    private fun setupChangeListener() = lifecycleScope.launchWhenResumed {
        viewModel.changed.filter { hasMadeChange }.collect { factory ->
            MonetCompat.colorSchemeFactory = factory
            MonetCompat.getInstance().updateMonetColors()
        }
    }

    private fun setupClose() = with(binding.settingsClose) {
        setOnClickListener {
            dismiss()
        }
        overrideRippleColor()
        setTextColor(monet.getAccentColor(requireContext()))
    }

    private fun setupReset() = with(binding.settingsReset) {
        setOnClickListener {
            viewModel.resetSettings(requireContext())
        }
        overrideRippleColor()
        setTextColor(monet.getAccentColor(requireContext()))
    }

    private fun setupEnabledListener() = lifecycleScope.launchWhenResumed {
        combine(viewModel.overrideEnabled, viewModel.zCamEnabled, viewModel.useSystem){ override, zcam, useSystem ->
            if(!override){
                alphaDisableableViews.forEach { it.alpha = 0.5f }
                disableableViews.forEach { it.isEnabled = false }
                return@combine
            }
            if(useSystem){
                alphaDisableableViews.forEach { it.alpha = 1f }
                disableableViews.forEach { it.isEnabled = true }
                alphaDisableableViewsSystem.forEach { it.alpha = 0.5f }
                disableableViewsSystem.forEach { it.isEnabled = false }
                return@combine
            }
            if(!zcam){
                alphaDisableableViews.forEach { it.alpha = 1f }
                disableableViews.forEach { it.isEnabled = true }
                alphaDisableableViewsZcam.forEach { it.alpha = 0.5f }
                disableableViewsZcam.forEach { it.isEnabled = false }
                return@combine
            }
            alphaDisableableViews.forEach { it.alpha = 1f }
            disableableViews.forEach { it.isEnabled = true }
        }.collect()
    }

    private suspend fun CompoundButton.onChange(): Flow<Boolean> = callbackFlow {
        val listener = CompoundButton.OnCheckedChangeListener { _, checked ->
            trySend(checked)
        }
        setOnCheckedChangeListener(listener)
        awaitClose {
            setOnCheckedChangeListener(null)
        }
    }.distinctUntilChanged()

    private suspend fun Slider.onChange(): Flow<Float> = callbackFlow {
        val listener = Slider.OnChangeListener { _, value, fromUser ->
            if(!fromUser) return@OnChangeListener
            trySend(value)
        }
        addOnChangeListener(listener)
        awaitClose {
            removeOnChangeListener(listener)
        }
    }.distinctUntilChanged()

}