package com.kieronquinn.app.monetcompatsample.ui.screens.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.app.monetcompatsample.databinding.ItemListBinding
import com.kieronquinn.monetcompat.extensions.views.applyMonet

class ListAdapter(context: Context, private val items: List<Item>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val layoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding){
            itemCheck.setOnCheckedChangeListener(null)
            itemCheck.isChecked = item.checked
            itemCheck.setOnCheckedChangeListener { _, isChecked ->
                item.checked = isChecked
            }
            itemText.text = item.text
            itemCheck.applyMonet()
            root.setOnClickListener {
                itemCheck.toggle()
            }
        }
    }

    class ViewHolder(val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root)
    data class Item(val text: String, var checked: Boolean)

}