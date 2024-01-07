package com.phoint.facebooksimulation.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.LayoutActionBarUserBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ActionBarUser(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val binding: LayoutActionBarUserBinding

    init {
        binding = LayoutActionBarUserBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.ActionBarUser)
        val imgOne =
            typeArray?.getResourceId(R.styleable.ActionBarUser_action_bar_user_src_one, -1) ?: -1
        if (imgOne != -1) {
            binding.imageViewOne.background = ContextCompat.getDrawable(context!!, imgOne)
        }
        val textViewOne =
            typeArray?.getString(R.styleable.ActionBarUser_action_bar_user_text_one) ?: ""
        binding.textViewOne.text = textViewOne

        val textViewTwo =
            typeArray?.getString(R.styleable.ActionBarUser_action_bar_user_text_two) ?: ""
        binding.textViewTwo.text = textViewTwo

        val buttonOne =
            typeArray?.getString(R.styleable.ActionBarUser_action_bar_user_button_one) ?: ""
        binding.buttonOne.text = buttonOne

        val imageViewTwo =
            typeArray?.getResourceId(R.styleable.ActionBarUser_action_bar_user_src_two, -1) ?: -1
        if (imageViewTwo != -1) {
            binding.imageViewTwo.background = ContextCompat.getDrawable(context!!, imageViewTwo)
        }

        val imageViewThree =
            typeArray?.getResourceId(R.styleable.ActionBarUser_action_bar_user_src_three, -1) ?: -1
        if (imageViewThree != -1) {
            binding.imageViewThree.background = ContextCompat.getDrawable(context!!, imageViewThree)
        }

        val enableTextViewOne =
            typeArray?.getBoolean(R.styleable.ActionBarUser_action_bar_user_text_one_enable, false)
                ?: false
        binding.textViewOne.visibility = if (enableTextViewOne) View.VISIBLE else View.GONE

        val enableTextViewTwo =
            typeArray?.getBoolean(R.styleable.ActionBarUser_action_bar_user_text_two_enable, false)
                ?: false
        binding.textViewTwo.visibility = if (enableTextViewTwo) View.VISIBLE else View.GONE

        val enableButtonOne = typeArray?.getBoolean(
            R.styleable.ActionBarUser_action_bar_user_button_one_enable,
            false
        ) ?: false
        binding.buttonOne.visibility = if (enableButtonOne) View.VISIBLE else View.GONE

        val enableSetting =
            typeArray?.getBoolean(R.styleable.ActionBarUser_action_bar_user_src_two_enable, false)
                ?: false
        binding.imageViewTwo.visibility = if (enableSetting) View.VISIBLE else View.GONE

        val enableImageViewThree =
            typeArray?.getBoolean(R.styleable.ActionBarUser_action_bar_user_src_three_enable, false)
                ?: false
        binding.imageViewThree.visibility = if (enableImageViewThree) View.VISIBLE else View.GONE

        val enableMenu =
            typeArray?.getBoolean(R.styleable.ActionBarUser_action_bar_user_src_one_enable, false)
                ?: false
        binding.imageViewOne.visibility = if (enableMenu) View.VISIBLE else View.GONE

        typeArray?.recycle()
    }

    fun setName(name: String) {
        binding.textViewTwo.text = name
    }

    fun setAbSearchOtherUser(callback: () -> Unit) {
        binding.imageViewThree.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setBackCurrentScreen(callback: () -> Unit) {
        binding.imageViewOne.setOnSingClickListener {
            callback.invoke()
        }
    }
}
