package com.phoint.facebooksimulation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.LayoutActionBarPostBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

@SuppressLint("Recycle")
class ActionBarPost(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val binding: LayoutActionBarPostBinding

    init {
        binding = LayoutActionBarPostBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.ActionBarPost)

        val srcLeft =
            typeArray?.getResourceId(R.styleable.ActionBarPost_action_bar_post_src_left, -1) ?: -1
        if (srcLeft != -1) {
            binding.imgLeft.setImageResource(srcLeft)
        }

        val srcRight =
            typeArray?.getResourceId(R.styleable.ActionBarPost_action_bar_post_src_right, -1) ?: -1
        if (srcRight != -1) {
            binding.imgRight.setImageResource(srcRight)
        }

        val title = typeArray?.getString(R.styleable.ActionBarPost_action_bar_post_title) ?: ""
        binding.tvTitle.text = title

        val titleLeft =
            typeArray?.getString(R.styleable.ActionBarPost_action_bar_post_title_left) ?: ""
        binding.tvLeft.text = titleLeft

        val buttonRight = typeArray?.getString(R.styleable.ActionBarPost_action_bar_post_btn) ?: ""
        binding.btnRight.text = buttonRight


        val titleEnable =
            typeArray?.getBoolean(R.styleable.ActionBarPost_action_bar_post_title_enable, false)
                ?: false
        binding.tvTitle.visibility = if (titleEnable) View.VISIBLE else View.GONE

        val titleRightEnable = typeArray?.getBoolean(
            R.styleable.ActionBarPost_action_bar_post_title_left_enable,
            false
        ) ?: false
        binding.tvLeft.visibility = if (titleRightEnable) View.VISIBLE else View.GONE

        val srcLeftEnable =
            typeArray?.getBoolean(R.styleable.ActionBarPost_action_bar_post_src_left_enable, false)
                ?: false
        binding.imgLeft.visibility = if (srcLeftEnable) View.VISIBLE else View.GONE

        val srcRightEnable =
            typeArray?.getBoolean(R.styleable.ActionBarPost_action_bar_post_src_right_enable, false)
                ?: false
        binding.imgRight.visibility = if (srcRightEnable) View.VISIBLE else View.GONE

        val buttonRightEnable =
            typeArray?.getBoolean(R.styleable.ActionBarPost_action_bar_post_btn_enable, false)
                ?: false
        binding.btnRight.visibility = if (buttonRightEnable) View.VISIBLE else View.GONE

        typeArray?.recycle()
    }

    fun setBackGroundButtonContent(title: String) {
        if (title.isNotEmpty()) {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_blue)
        } else {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_gray)
        }
    }

    fun setBackGroundButtonImage(image: Uri?) {
        if (image != null) {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_blue)
        } else {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_gray)
        }
    }

    fun setBackGroundButtonImageString(image: String?) {
        if (image != null) {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_blue)
        } else {
            binding.btnRight.background =
                ContextCompat.getDrawable(context, R.drawable.background_gray)
        }
    }

    fun clickAccessToCamera(callback: (() -> Unit)) {
        binding.imgRight.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setOnSingClickImgLeft(callback: (() -> Unit)) {
        binding.imgRight.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setOnSingClickLeft(callback: (() -> Unit)) {
        binding.imgLeft.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun onSingClickListenerRight(callback: (() -> Unit)) {
        binding.btnRight.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun onSingClickListenerLeft(callback: (() -> Unit)) {
        binding.tvLeft.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun clickSaveImage(callback: (() -> Unit)) {
        binding.btnRight.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }
}
