package com.skydoves.pokedex.binding

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.skydoves.pokedex.ui.main.MainViewModel

object SearchBindingAdapter {

  @JvmStatic
  @BindingAdapter("searchText")
  fun bindSearchText(view: EditText, text: String?) {
    if (view.text.toString() != text) {
      view.setText(text ?: "")
    }
  }

  @JvmStatic
  @BindingAdapter("clearButtonVisibility")
  fun bindClearButtonVisibility(view: ImageView, text: String?) {
    view.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
  }

  @JvmStatic
  @BindingAdapter("onClearSearch")
  fun bindOnClearSearch(view: ImageView, viewModel: MainViewModel) {
    view.setOnClickListener {
      viewModel.clearSearch()
    }
  }
}