package com.phoint.facebooksimulation.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.ChildSupport
import com.phoint.facebooksimulation.databinding.ItemChildSupportBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ChildSupportAdapter(private val listChildSupport: List<ChildSupport>) :
    RecyclerView.Adapter<ChildSupportAdapter.ChildSupportViewHolder>() {
    inner class ChildSupportViewHolder(val binding: ItemChildSupportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bin(childSupport: ChildSupport) {
            binding.childSupport = childSupport
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildSupportViewHolder {
        val binding =
            ItemChildSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildSupportViewHolder(binding)
    }

    var onClickItem: ((ChildSupport) -> Unit)? = null
    override fun onBindViewHolder(holder: ChildSupportViewHolder, position: Int) {
        holder.bin(listChildSupport[position])

        val childSupport: ChildSupport = listChildSupport[position]
        childSupport.url.let {
            holder.binding.imgIcon.setImageResource(it)
        }

        // holder.binding.materialCardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_custom_recycle))
// Tạo animation từ XML
//        val slideInAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_custom_recycle)
//
//        // Listener để cập nhật vị trí thực của item sau khi hoàn thành hiệu ứng
//        slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation?) {}
//
//            override fun onAnimationEnd(animation: Animation?) {
//                // Cập nhật vị trí thực của item sau khi hoàn thành hiệu ứng
//                holder.itemView.translationX = 0f
//            }
//
//            override fun onAnimationRepeat(animation: Animation?) {}
//        })

        // Áp dụng animation cho item
//        holder.itemView.startAnimation(slideInAnimation)
        holder.itemView.setOnSingClickListener {
            val positions = holder.adapterPosition
            if (positions != RecyclerView.NO_POSITION) {
                onClickItem?.invoke(childSupport)
            }
        }
    }

    override fun getItemCount(): Int =
        if (listChildSupport.isNotEmpty()) listChildSupport.size else 0
}