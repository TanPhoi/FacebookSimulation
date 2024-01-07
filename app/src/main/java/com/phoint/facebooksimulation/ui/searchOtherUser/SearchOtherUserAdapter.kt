package com.phoint.facebooksimulation.ui.searchOtherUser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemSearchOtherUserBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SearchOtherUserAdapter(private var userList: ArrayList<User>) :
    RecyclerView.Adapter<SearchOtherUserAdapter.SearchOtherUserViewHolder>() {
    inner class SearchOtherUserViewHolder(val binding: ItemSearchOtherUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User?) {
            binding.otherUser = user
        }
    }

    fun setData(newList: List<User>) {
        if (userList != newList) {
            val diffResult = DiffUtil.calculateDiff(
                UserDiffCallback(
                    userList,
                    newList
                )
            )
            userList.clear()
            userList.addAll(newList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    class UserDiffCallback(private val oldList: List<User>, private val newList: List<User>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].idUser == newList[newItemPosition].idUser
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchOtherUserViewHolder {
        val binding =
            ItemSearchOtherUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchOtherUserViewHolder(binding)
    }

    var onItemClickOtherUser: ((User) -> Unit)? = null
    override fun onBindViewHolder(holder: SearchOtherUserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        holder.itemView.setOnSingClickListener {
            onItemClickOtherUser?.invoke(user)
        }
    }

    override fun getItemCount(): Int = if (userList.isNotEmpty()) userList.size else 0
}