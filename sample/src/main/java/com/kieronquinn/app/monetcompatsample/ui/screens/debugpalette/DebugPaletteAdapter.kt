package com.kieronquinn.app.monetcompatsample.ui.screens.debugpalette

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.kieronquinn.app.monetcompatsample.databinding.RowDebugPaletteColorBinding
import com.kieronquinn.app.monetcompatsample.databinding.RowDebugPaletteColorsBinding
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.applyMonet
import com.kieronquinn.monetcompat.extensions.views.setOverscrollTint
import dev.kdrag0n.monet.colors.Color
import dev.kdrag0n.monet.theme.ColorScheme
import dev.kdrag0n.monet.theme.DynamicColorScheme

class DebugPaletteAdapter(private val context: Context): RecyclerView.Adapter<DebugPaletteAdapter.ViewHolder>() {

    private val layoutInflater by lazy {
        val mainLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mainLayoutInflater.cloneInContext(context)
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private var colors: List<Pair<String, Map<Int, Color>>>? = null
    private var themeColors: List<Pair<String, Int>>? = null

    fun getColors(context: Context){
        colors = monet.getMonetColors().getColorList()
        themeColors = monet.getThemeColors(context)
        notifyDataSetChanged()
    }

    override fun getItemCount() = (colors?.size ?: 0) + (themeColors?.size ?: 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(ItemType.values().get(viewType)){
            ItemType.COLOR -> ViewHolder.Color(RowDebugPaletteColorBinding.inflate(layoutInflater, parent, false))
            ItemType.ROW -> ViewHolder.Row(RowDebugPaletteColorsBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val themeColorsSize = themeColors?.size ?: 0
        return if(position < themeColorsSize){
            ItemType.COLOR.ordinal
        }else{
            ItemType.ROW.ordinal
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val themeColorsSize = themeColors?.size ?: 0
        if(position < themeColorsSize){
            val item = themeColors!![position]
            holder as ViewHolder.Color
            setupColor(holder.binding, item)
        }else{
            val item = colors!![position - themeColorsSize]
            holder as ViewHolder.Row
            setupRow(holder.binding, item)
        }
    }

    private fun setupRow(binding: RowDebugPaletteColorsBinding, item: Pair<String, Map<Int, Color>>){
        with(binding){
            rowDebugPaletteTitle.text = item.first
            rowDebugPaletteRecyclerview.run {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = DebugPaletteRowAdapter(layoutInflater, item.first, item.second)
                setOverscrollTint(monet.getAccentColor(context))
            }
        }
    }

    private fun setupColor(binding: RowDebugPaletteColorBinding, item: Pair<String, Int>){
        with(binding){
            rowDebugPaletteColorBackground.backgroundTintList = ColorStateList.valueOf(item.second)
            rowDebugPaletteColorTitle.text = item.first
            root.setOnClickListener {
                Snackbar.make(it, "${item.first}: #${Integer.toHexString(item.second)}", Snackbar.LENGTH_LONG).apply {
                    applyMonet()
                }.show()
            }
        }
    }

    sealed class ViewHolder(open val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
        data class Color(override val binding: RowDebugPaletteColorBinding): ViewHolder(binding)
        data class Row(override val binding: RowDebugPaletteColorsBinding): ViewHolder(binding)
    }

    private fun ColorScheme.getColorList(): List<Pair<String, Map<Int, Color>>> {
        return listOf(
            Pair("Accent 1", accent1),
            Pair("Accent 2", accent2),
            Pair("Accent 3", accent3),
            Pair("Neutral 1", neutral1),
            Pair("Neutral 2", neutral2)
        )
    }

    private fun MonetCompat.getThemeColors(context: Context): List<Pair<String, Int>> {
        return listOf(
            Pair("Wallpaper", wallpaperPrimaryColor),
            Pair("Background", getBackgroundColor(context)),
            Pair("Background Secondary", getBackgroundColorSecondary(context)),
            Pair("Accent", getAccentColor(context)),
            Pair("Primary", getPrimaryColor(context)),
            Pair("Secondary", getSecondaryColor(context))
        ).filterNot { it.second == null } as List<Pair<String, Int>>
    }

    private enum class ItemType {
        COLOR, ROW
    }

}