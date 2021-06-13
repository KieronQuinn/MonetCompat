package com.kieronquinn.app.monetcompatsample.ui.screens.debugpalette

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kieronquinn.app.monetcompatsample.databinding.ItemDebugPaletteBinding
import com.kieronquinn.monetcompat.extensions.applyMonet
import com.kieronquinn.monetcompat.extensions.toArgb
import dev.kdrag0n.monet.colors.Color as MonetColor

class DebugPaletteRowAdapter(private val layoutInflater: LayoutInflater, private val type: String, private val colors: Map<Int, MonetColor>): RecyclerView.Adapter<DebugPaletteRowAdapter.ViewHolder>() {

    override fun getItemCount() = colors.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDebugPaletteBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = colors.values.toTypedArray()[position]
        val itemShade = colors.keys.toTypedArray()[position]
        with(holder.binding){
            itemDebugPaletteBackground.backgroundTintList = ColorStateList.valueOf(item.toArgb())
            root.setOnClickListener {
                Snackbar.make(it, "$type, Shade $itemShade: #${Integer.toHexString(item.toArgb())}", Snackbar.LENGTH_LONG).apply {
                    applyMonet()
                }.show()
            }
        }
    }

    data class ViewHolder(val binding: ItemDebugPaletteBinding): RecyclerView.ViewHolder(binding.root)

}