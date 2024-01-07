package com.phoint.facebooksimulation.ui.saveFavoritePosts

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Post
import com.phoint.facebooksimulation.databinding.FragmentSaveFavoritePostsBinding
import com.phoint.facebooksimulation.ui.base.BaseFragment
import com.phoint.facebooksimulation.util.setOnSingClickListener

class SaveFavoritePostsFragment :
    BaseFragment<FragmentSaveFavoritePostsBinding, SaveFavoritePostsViewModel>() {
    private var adapter: SaveFavoritePostAdapter? = null
    private var postList = ArrayList<Post>()
    private var isShowingAllItems = false
    override fun layoutRes(): Int {
        return R.layout.fragment_save_favorite_posts
    }

    override fun viewModelClass(): Class<SaveFavoritePostsViewModel> {
        return SaveFavoritePostsViewModel::class.java
    }

    override fun initView() {
        val userOwnerId = viewModel.getIdUser()
        viewModel.getSavePostDataByUser(userOwnerId)
        adapter = SaveFavoritePostAdapter(postList)
        binding.rcvSavePost.adapter = adapter

        viewModel.savePostList.observe(this) { savePosts ->
            viewModel.postList.observe(this) { posts ->
                if (postList.size != posts.size) {
                    postList.clear()
                    val savedPosts = posts.filter { post ->
                        savePosts.any { savePost ->
                            savePost.postOwnerId == post.idPost
                        }
                    }
                    postList.addAll(savedPosts)
                    adapter?.notifyDataSetChanged()
                }
            }
        }

        viewModel.userList.observe(this) {
            adapter?.setUsers(it)
        }

        binding.abSaveFavoritePosts.setOnSingClickLeft {
            findNavController().popBackStack()
        }

        adapter?.setOnItemClick = {
            findNavController().navigate(
                R.id.action_saveFavoritePostsFragment_to_otherPeopleCommentFragment, bundleOf(
                    Pair("post", it)
                )
            )
        }

        binding.btnSeeSavePost.setOnSingClickListener {
            isShowingAllItems = !isShowingAllItems
            if (isShowingAllItems) {
                adapter?.showAllItems()
                binding.btnSeeSavePost.text = "Ẩn bớt"
            } else {
                adapter?.showLimitedItems()
                binding.btnSeeSavePost.text = "Xem thêm"
            }
        }
    }

}