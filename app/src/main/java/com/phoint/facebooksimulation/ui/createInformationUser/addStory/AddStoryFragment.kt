package com.phoint.facebooksimulation.ui.createInformationUser.addStory

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.data.local.model.Story
import com.phoint.facebooksimulation.databinding.FragmentAddStoryBinding
import com.phoint.facebooksimulation.ui.base.BaseActivity
import com.phoint.facebooksimulation.ui.base.BaseFragment

class AddStoryFragment : BaseFragment<FragmentAddStoryBinding, AddStoryViewModel>() {
    override fun layoutRes(): Int {
        return R.layout.fragment_add_story
    }

    override fun viewModelClass(): Class<AddStoryViewModel> {
        return AddStoryViewModel::class.java
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        viewModel.startLoading()
        viewModel.isLoading.observe(this) {
            if (it == true) {
                (activity as? BaseActivity<*, *>)?.showLoading()
            } else {
                (activity as? BaseActivity<*, *>)?.hiddenLoading()
            }
        }

        viewModel.isFunctionalityVisible.observe(this) {
            if (it == true) {
                binding.llStory.visibility = View.VISIBLE
            } else {
                binding.llStory.visibility = View.GONE
            }
        }

        viewModel.getInformationUser(viewModel.getIdPreferences())

        viewModel.informationUser.observe(this) {
            if (it.story != null) {
                binding.edtStory.setText(it.story?.nameStory)
                binding.edtStory.hint = null
            } else {
                binding.edtStory.hint =
                    "Bạn có thể thêm nếu tiểu sử ngắn để cho mọi người biết thêm " +
                            "về bản thân mình. Hãy thêm bất " +
                            "cứ gì nà bạn mun, chẳng hạn như châm ngôn yêu " +
                            "thích hoặc diều làm mình vui."
            }
        }

        binding.edtStory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength: Int = s?.length ?: 0
                binding.tvSumText.text = "$currentLength/101"
                if (currentLength > 101) {
                    val limitedText: String = s?.subSequence(0, 101).toString()
                    binding.edtStory.setText(limitedText)
                    binding.edtStory.setSelection(101)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                binding.abStory.setBackGroundButtonContent(s.toString())
            }

        })

        binding.abStory.onSingClickListenerLeft {
            findNavController().popBackStack()
        }

        binding.abStory.onSingClickListenerRight {
            val edtStory = binding.edtStory.text.toString().trim()
            if (edtStory.isNotEmpty()) {
                viewModel.informationUser.observe(this) {
                    val story = Story().apply {
                        storyId = System.currentTimeMillis()
                        nameStory = binding.edtStory.text.toString().trim()
                        privacy = binding.btnPermission.text.toString().trim()
                    }
                    it.story = story
                    viewModel.updateStory(it)
                }
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập story!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.done.observe(this) {
            if (it == true) {
                findNavController().popBackStack()
            }
        }
    }

}
