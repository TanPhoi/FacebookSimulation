package com.phoint.facebooksimulation.ui.base

import android.graphics.Color
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.phoint.facebooksimulation.R

object BindingAdapterOval {
    @JvmStatic
    @androidx.databinding.BindingAdapter(value = ["image_url_oval"], requireAll = false)
    fun imageUrlOval(imageView: ImageView, imageUrlOval: String?) {
        imageView.setBackgroundColor(Color.parseColor("#CDCDCD"))
        imageView.setBackgroundColor(Color.parseColor("#C1C1C1"))
        Glide.with(imageView.context)
            .load(imageUrlOval)
            .apply(
                RequestOptions()
                    .error(R.drawable.ic_avatar_user)
                    .fitCenter()
            )
            .circleCrop()
            .into(imageView)
    }
}