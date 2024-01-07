package com.phoint.facebooksimulation.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.ChildSupport
import com.phoint.facebooksimulation.data.local.model.ParentSupport
import com.phoint.facebooksimulation.databinding.ItemSupportBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ParentSupportAdapter(private var parentSupportList: List<ParentSupport>) :
    RecyclerView.Adapter<ParentSupportAdapter.SupportViewHolder>() {

    inner class SupportViewHolder(val binding: ItemSupportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(parentSupport: ParentSupport) {
            binding.parentSupport = parentSupport
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SupportViewHolder(binding)
    }

    var onItemChildSupport: ((ParentSupport, ChildSupport) -> Unit)? = null
    var itemOnClick: ((ParentSupport?) -> Unit)? = null
    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.bind(parentSupportList[position])

        val parentSupport: ParentSupport = parentSupportList[position]

        val isExpanded = parentSupport.isExpandable
        val imageResource = if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
        holder.binding.imgShow.setImageResource(imageResource)

        parentSupport.url.let {
            holder.binding.imgIcon.setImageResource(it)
        }

        holder.itemView.setOnSingClickListener {
            itemOnClick?.invoke(parentSupportList[position])
        }

        holder.binding.childRecyclerView.setHasFixedSize(true)
        val adapter = ChildSupportAdapter(parentSupport.childSupport)
        holder.binding.childRecyclerView.adapter = adapter

        val isExpandable = parentSupport.isExpandable
        if (isExpandable) {
            holder.binding.childRecyclerView.visibility = View.VISIBLE
        } else {
            holder.binding.childRecyclerView.visibility = View.GONE
        }

        if (isExpandable) {
            val slideUp = AnimationUtils.loadAnimation(holder.binding.root.context, R.anim.fade_in)
            holder.binding.childRecyclerView.startAnimation(slideUp)

            holder.binding.childRecyclerView.visibility = View.VISIBLE
        } else {
            val slideDown =
                AnimationUtils.loadAnimation(holder.binding.root.context, R.anim.fade_out)
            holder.binding.childRecyclerView.startAnimation(slideDown)

            holder.binding.childRecyclerView.visibility = View.GONE
        }

        holder.binding.btnCard.setOnSingClickListener {
            val downAnim = AnimationUtils.loadAnimation(holder.binding.root.context, R.anim.down)

            isAnyItemExpanded(position)

            parentSupport.isExpandable = !parentSupport.isExpandable
            if (parentSupport.isExpandable) {
                holder.binding.childRecyclerView.startAnimation(downAnim)
            }
            notifyDataSetChanged()
        }

        adapter.onClickItem = { view ->
            onItemChildSupport?.invoke(parentSupport, view)
        }

    }

    private fun isAnyItemExpanded(position: Int) {
        val temp = parentSupportList.indexOfFirst {
            it.isExpandable
        }

        if (temp >= 0 && temp != position) {
            parentSupportList[temp].isExpandable = false

            notifyItemChanged(temp)
        }
    }

    override fun getItemCount(): Int {
        return if (parentSupportList.isNotEmpty()) parentSupportList.size else 0
    }

}