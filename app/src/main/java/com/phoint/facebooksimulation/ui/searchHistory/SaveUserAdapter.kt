package com.phoint.facebooksimulation.ui.searchHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phoint.facebooksimulation.data.local.model.SaveOtherUser
import com.phoint.facebooksimulation.data.local.model.User
import com.phoint.facebooksimulation.databinding.ItemSaveProfileUserBinding
import com.phoint.facebooksimulation.databinding.ItemSaveUserBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SaveUserAdapter(private var saveUserList: ArrayList<SaveOtherUser>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_SAVE_USER = 0
    private val VIEW_TYPE_SAVE_PROFILE_USER = 1
    private var userList: List<User>? = null

    inner class SaveUserViewHolder(val binding: ItemSaveUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(saveOtherUser: SaveOtherUser?) {
            binding.saveOtherUser = saveOtherUser
        }
    }

    inner class SaveProfileOtherUserViewHolder(val binding: ItemSaveProfileUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(saveOtherUser: SaveOtherUser?, user: User?) {
            binding.saveOtherUser = saveOtherUser
            binding.user = user
        }
    }

    fun removeItem(saveOtherUser: SaveOtherUser) {
        val position = saveUserList.indexOf(saveOtherUser)
        if (position != -1) {
            saveUserList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    private fun getUserForPost(saveOtherUser: SaveOtherUser): User? {
        return userList?.find { it.idUser == saveOtherUser.idUserOther }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < saveUserList.size) {
            val saveOtherUser = saveUserList[position]
            if (!saveOtherUser.isAvatar) {
                return VIEW_TYPE_SAVE_USER
            }
        }
        return VIEW_TYPE_SAVE_PROFILE_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SAVE_USER -> {
                val binding =
                    ItemSaveUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SaveUserViewHolder(binding)
            }

            VIEW_TYPE_SAVE_PROFILE_USER -> {
                val binding = ItemSaveProfileUserBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SaveProfileOtherUserViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    var onItemClickOtherUser: ((SaveOtherUser) -> Unit)? = null
    var onDeleteOtherUser: ((SaveOtherUser) -> Unit)? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SaveUserViewHolder) {
            val saveOtherUser = saveUserList[position]
            holder.bind(saveOtherUser)

            holder.itemView.setOnSingClickListener {
                onItemClickOtherUser?.invoke(saveOtherUser)
            }

            holder.binding.btnDelete.setOnSingClickListener {
                onDeleteOtherUser?.invoke(saveOtherUser)
            }

        } else if (holder is SaveProfileOtherUserViewHolder) {
            val saveOtherUser = saveUserList[position]
            val user = getUserForPost(saveOtherUser)
            holder.bind(saveOtherUser, user)


            holder.itemView.setOnSingClickListener {
                onItemClickOtherUser?.invoke(saveOtherUser)
            }

            holder.binding.btnDelete.setOnSingClickListener {
                onDeleteOtherUser?.invoke(saveOtherUser)
            }
        }
    }

//    override fun onBindViewHolder(holder: SaveUserViewHolder, position: Int) {
//        val saveOtherUser = saveUserList[position]
//        holder.bind(saveOtherUser)
//
//        holder.itemView.setOnSingClickListener {
//            onItemClickOtherUser?.invoke(saveOtherUser)
//        }
//
//        holder.binding.btnDelete.setOnSingClickListener {
//            onDeleteOtherUser?.invoke(saveOtherUser)
//        }
//    }

    override fun getItemCount(): Int = if (saveUserList.isNotEmpty()) saveUserList.size else 0
}