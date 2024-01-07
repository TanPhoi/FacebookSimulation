package com.phoint.facebooksimulation.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Shortcut
import com.phoint.facebooksimulation.databinding.ItemShortcutsBinding

class ShortcutsAdapter(private val listShortcut: List<Shortcut>) :
    RecyclerView.Adapter<ShortcutsAdapter.ShortcutsViewHolder>() {
    private var showAllItems = false
    private var visibleItemsCount = 14

    inner class ShortcutsViewHolder(val binding: ItemShortcutsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bin(shortcut: Shortcut) {
            binding.shortcut = shortcut
        }
    }

    fun showAllItems() {
        val previousItemCount = visibleItemsCount
        visibleItemsCount = listShortcut.size

        // Thông báo rằng tất cả các mục đã thay đổi
        for (i in previousItemCount until visibleItemsCount) {
            notifyItemChanged(i)
        }
    }

    fun showLimitedItems() {
        val previousItemCount = visibleItemsCount
        visibleItemsCount = 14

        // Thông báo rằng tất cả các mục đã thay đổi
        for (i in visibleItemsCount until previousItemCount) {
            notifyItemChanged(i)
        }
    }

    fun applyShowAllAnimation(context: Context, view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_custom_recycle)
        view.startAnimation(animation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutsViewHolder {
        val binding =
            ItemShortcutsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShortcutsViewHolder(binding)
    }

    var itemOnClick: ((Shortcut?) -> Unit)? = null
    override fun onBindViewHolder(holder: ShortcutsViewHolder, position: Int) {
        holder.bin(listShortcut[position])
        val shortcut: Shortcut = listShortcut[position]
        shortcut.url?.let {
            holder.binding.imgIcon.setImageResource(it)
        }

        applyShowAllAnimation(holder.itemView.context, holder.itemView)

        holder.itemView.setOnClickListener {
            itemOnClick?.invoke(listShortcut[position])
        }
    }

    override fun getItemCount(): Int {
        return visibleItemsCount
    }

}