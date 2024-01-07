package com.phoint.facebooksimulation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.LayoutActionBarDisplayImageBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

class ActionBarDisplayImage(context: Context?, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {
    private val binding: LayoutActionBarDisplayImageBinding

    init {
        binding = LayoutActionBarDisplayImageBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.ActionBarDisplayImage)

        val srcOut = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_out,
            -1
        ) ?: -1
        if (srcOut != -1) {
            binding.btnOut.setImageDrawable(ContextCompat.getDrawable(context!!, srcOut))
        }

        val srcMenu = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_menu,
            -1
        ) ?: -1
        if (srcMenu != -1) {
            binding.btnMenu.setImageDrawable(ContextCompat.getDrawable(context!!, srcMenu))
        }

        val srcCheckIn = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_check_in,
            -1
        ) ?: -1
        if (srcCheckIn != -1) {
            binding.btnCheckIn.setImageDrawable(ContextCompat.getDrawable(context!!, srcCheckIn))
        }

        val srcTag = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_tag,
            -1
        ) ?: -1
        if (srcTag != -1) {
            binding.btnTag.setImageDrawable(ContextCompat.getDrawable(context!!, srcTag))
        }

        val src1 = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_1,
            -1
        ) ?: -1
        if (src1 != -1) {
            binding.imageView1.setImageDrawable(ContextCompat.getDrawable(context!!, src1))
        }

        val src2 = typeArray?.getResourceId(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_2,
            -1
        ) ?: -1
        if (src2 != -1) {
            binding.imageView2.setImageDrawable(ContextCompat.getDrawable(context!!, src2))
        }

        val tv1 =
            typeArray?.getString(R.styleable.ActionBarDisplayImage_action_bar_display_text_view_1)
                ?: ""
        binding.textView1.text = tv1

        val tv2 =
            typeArray?.getString(R.styleable.ActionBarDisplayImage_action_bar_display_text_view_2)
                ?: ""
        binding.textView2.text = tv2


        //enable image
        val enableOut = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_out_enable,
            false
        ) ?: false
        binding.btnOut.visibility = if (enableOut) View.VISIBLE else View.GONE

        val enableMenu = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_menu_enable,
            false
        ) ?: false
        binding.btnMenu.visibility = if (enableMenu) View.VISIBLE else View.GONE

        val enableCheckIn = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_check_in_enable,
            false
        ) ?: false
        binding.btnCheckIn.visibility = if (enableCheckIn) View.VISIBLE else View.GONE

        val enableTag = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_tag_enable,
            false
        ) ?: false
        binding.btnTag.visibility = if (enableTag) View.VISIBLE else View.GONE

        val enableSrc1 = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_1_enable,
            false
        ) ?: false
        binding.imageView1.visibility = if (enableSrc1) View.VISIBLE else View.GONE

        val enableSrc2 = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_image_src_2_enable,
            false
        ) ?: false
        binding.imageView2.visibility = if (enableSrc2) View.VISIBLE else View.GONE

        val enableTv1 = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_text_view_1_enable,
            false
        ) ?: false
        binding.textView1.visibility = if (enableTv1) View.VISIBLE else View.GONE

        val enableTv2 = typeArray?.getBoolean(
            R.styleable.ActionBarDisplayImage_action_bar_display_text_view_2_enable,
            false
        ) ?: false
        binding.textView2.visibility = if (enableTv2) View.VISIBLE else View.GONE

        typeArray?.recycle()
    }

    fun setTextUser(name: String) {
        binding.textView1.text = name
    }

    @SuppressLint("CheckResult")
    fun setImageUser(uri: String) {
        Glide.with(context).load(uri).into(binding.imageView1).apply {
            RequestOptions()
                .error(R.drawable.ic_avatar_user)
        }
    }

    fun setDateTime(time: String) {
        binding.textView2.text = time
    }

    fun setPermissionPost(permission: Int) {
        binding.imageView2.background = ContextCompat.getDrawable(context, permission)
    }

    fun setExitCurrentScreen(callback: (() -> Unit)) {
        binding.btnOut.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setPostMenu(callback: (() -> Unit)) {
        binding.btnMenu.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setOnClickImageUser(callback: (() -> Unit)) {
        binding.imageView1.setOnSingClickListener {
            callback.invoke()
        }
    }

    fun setOnClickNameUser(callback: (() -> Unit)) {
        binding.textView1.setOnSingClickListener {
            callback.invoke()
        }
    }

}
