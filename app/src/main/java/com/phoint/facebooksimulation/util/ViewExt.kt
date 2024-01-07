package com.phoint.facebooksimulation.util

import android.view.View
import com.phoint.facebooksimulation.listener.OnSingleClickListener

fun View.setOnSingClickListener(onClick: (View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(view: View) {
            onClick.invoke(view)
        }
    })
}