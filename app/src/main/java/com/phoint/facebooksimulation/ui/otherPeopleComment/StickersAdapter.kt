package com.phoint.facebooksimulation.ui.otherPeopleComment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.Sticker
import com.phoint.facebooksimulation.databinding.ItemStickersBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class StickersAdapter(private var stickerList: List<Sticker>) :
    RecyclerView.Adapter<StickersAdapter.StickersViewHolder>() {
    inner class StickersViewHolder(val binding: ItemStickersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sticker: Sticker) {
            binding.sticker = sticker
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickersViewHolder {
        val binding =
            ItemStickersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StickersViewHolder(binding)
    }

    var onItemClickSticker: ((Sticker) -> Unit)? = null
    override fun onBindViewHolder(holder: StickersViewHolder, position: Int) {
        holder.bind(stickerList[position])

        val sticker: Sticker = stickerList[position]
        sticker.srcSticker.let {
            holder.binding.imgSticker.setImageResource(it ?: 0)
        }

        holder.itemView.setOnSingClickListener {
            onItemClickSticker?.invoke(stickerList[position])
        }
    }

    override fun getItemCount(): Int = if (stickerList.isNotEmpty()) stickerList.size else 0
}