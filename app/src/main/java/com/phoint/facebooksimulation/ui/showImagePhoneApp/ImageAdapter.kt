package com.phoint.facebooksimulation.ui.showImagePhoneApp

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phoint.facebooksimulation.databinding.ItemImageBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ImageAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val imageList = mutableListOf<Uri>()
    private var isShowImage = false

    inner class ImageViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUri: Uri) {
            Glide.with(context)
                .load(imageUri)
                .into(binding.imgSrc)
        }
    }

    fun setShowImage(isShowImage: Boolean) {
        this.isShowImage = isShowImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (!isShowImage && imageList.isNotEmpty()) {
            6
        } else {
            imageList.size
        }
    }

    var itemOnClick: ((Uri?) -> Unit)? = null
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (position < imageList.size) {
            holder.bind(imageList[position])

            holder.itemView.setOnSingClickListener {
                itemOnClick?.invoke(imageList[position])
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setImageList(images: List<Uri>) {
        imageList.clear()
        imageList.addAll(images)
        notifyDataSetChanged()
    }

}
