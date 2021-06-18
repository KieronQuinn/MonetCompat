package com.kieronquinn.app.monetcompatsample.ui.screens.colorpicker

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentBottomSheetColorPickerBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BaseBottomSheetDialogFragment
import com.kieronquinn.app.monetcompatsample.utils.PreferenceUtils
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  This is an example of how you can give the user an option to pick from the available wallpaper colors
 *  for which they want to use as Monet's base.
 *
 *  Look up all the available colors with [MonetCompat.getAvailableWallpaperColors], and the currently
 *  selected one (ie. the one that's gone through the [MonetCompat.wallpaperColorPicker]) with
 *  [MonetCompat.getSelectedWallpaperColor], and display all the available colors with the selection
 *  checked. When the user selects a color, save the color to SharedPreferences, (to be looked up from
 *  your custom [MonetCompat.wallpaperColorPicker]). Then call [MonetCompat.updateMonetColors] to trigger
 *  the colors to update as if a wallpaper change had happened.
 */
class ColorPickerBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentBottomSheetColorPickerBinding>(FragmentBottomSheetColorPickerBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenResumed {
            with(binding){
                val availableColors = monet.getAvailableWallpaperColors() ?: emptyList()
                //No available colors = likely using a live wallpaper, show a toast and dismiss
                if(availableColors.isEmpty()){
                    Toast.makeText(requireContext(), getString(R.string.color_picker_unavailable), Toast.LENGTH_LONG).show()
                    dismiss()
                    return@launchWhenResumed
                }
                root.backgroundTintList = ColorStateList.valueOf(monet.getBackgroundColor(requireContext()))
                colorPickerList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                colorPickerList.adapter = ColorPickerAdapter(requireContext(), monet.getSelectedWallpaperColor(), availableColors){
                    onColorPicked(it)
                }
                colorPickerOk.setOnClickListener {
                    dialog?.dismiss()
                }
                colorPickerOk.setTextColor(monet.getAccentColor(requireContext()))
            }
        }
    }

    private fun onColorPicked(color: Int) = lifecycleScope.launchWhenResumed {
        PreferenceUtils.setSelectedColor(requireContext(), color)
        //Trigger a manual update
        monet.updateMonetColors()
    }

}