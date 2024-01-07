package com.phoint.facebooksimulation.ui.seeImages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.databinding.ItemImagesBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ImageAdapter(private val imageList: List<Post>) :
    RecyclerView.Adapter<ImageAdapter.ImagesViewHolder>() {
    inner class ImagesViewHolder(val binding: ItemImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.post = post
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ItemImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImagesViewHolder(binding)
    }

    var onItemClick: ((Post) -> Unit)? = null
    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val post = imageList[position]
        holder.bind(post)

        holder.itemView.setOnSingClickListener {
            onItemClick?.invoke(post)
        }
    }

    override fun getItemCount(): Int = if (imageList.isNotEmpty()) imageList.size else 0
}