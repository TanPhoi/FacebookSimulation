package com.phoint.facebooksimulation.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.core.content.ContextCompat
import com.phoint.facebooksimulation.R
import com.phoint.facebooksimulation.databinding.LayoutActionBarLoginBinding
import com.phoint.facebooksimulation.util.setOnSingClickListener

@SuppressLint("Recycle")
class ActionBarLogin(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val binding: LayoutActionBarLoginBinding
    private var lastSearchText = ""
    private var searchTextChanged = false
    private var queryTextFocusChangeListener: ((Boolean) -> Unit)? = null

    init {
        binding = LayoutActionBarLoginBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.ActionBarLogin)

        binding.searchView.queryHint = "Tìm kiếm..."

        val imgBack =
            typeArray?.getResourceId(R.styleable.ActionBarLogin_action_bar_login_src_back, -1) ?: -1
        if (imgBack != -1) {
            binding.imgBack.setImageDrawable(ContextCompat.getDrawable(context!!, imgBack))
        }

        val textView = typeArray?.getString(R.styleable.ActionBarLogin_action_bar_login_text_view)
        binding.tvSearchName.text = textView

        val enableBack = typeArray?.getBoolean(
            R.styleable.ActionBarLogin_action_bar_login_enable_src_back,
            false
        ) ?: false
        binding.imgBack.visibility = if (enableBack) View.VISIBLE else View.GONE

        val enableSearchView = typeArray?.getBoolean(
            R.styleable.ActionBarLogin_action_bar_login_search_view_enable,
            false
        ) ?: false
        binding.searchView.visibility = if (enableSearchView) View.VISIBLE else View.GONE

        val enableTextView = typeArray?.getBoolean(
            R.styleable.ActionBarLogin_action_bar_login_text_view_enable,
            false
        ) ?: false
        binding.tvSearchName.visibility = if (enableTextView) View.VISIBLE else View.GONE

        typeArray?.recycle()
    }

    fun setOnBackClick(callBack: (() -> Unit)) {
        binding.imgBack.setOnSingClickListener {
            callBack.invoke()
        }
    }

    fun setOnClickQueryTextFocusChangeListener(listener: (String) -> Unit) {
        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        listener.invoke(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        listener.invoke(newText)
                        if (newText != lastSearchText) {
                            searchTextChanged = true
                        }
                        return true
                    }
                })
            } else {
                listener.invoke("")
            }
        }
    }

    fun setTextSearchUser(name: String, submit: Boolean = false) {
        lastSearchText = name
        binding.searchView.apply {
            setQuery(name, false)
            clearFocus()
        }
    }

    fun setTextUser(name: String) {
        binding.tvSearchName.text = name
    }

    fun setTextSearch(name: String) {
        binding.searchView.setQuery(name, false)
    }

    fun setOnSearchClickListener(callBack: () -> Unit) {
        binding.tvSearchName.setOnSingClickListener {
            callBack.invoke()
        }
    }

    fun requestSearchViewFocus() {
        binding.searchView.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)
    }

}
