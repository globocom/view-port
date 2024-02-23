package com.github.globocom.viewport.sample

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.globocom.viewport.sample.R
import com.github.globocom.viewport.sample.databinding.ViewHolderViewPortSampleBinding

class ViewPortViewHolder(binding: ViewHolderViewPortSampleBinding) :
    RecyclerView.ViewHolder(binding.root), View.OnFocusChangeListener {
    private val headline = binding.viewHolderViewPortTextViewHeadline
    private val root = binding.root

    fun bind() {
        root.isFocusable = true
        root.onFocusChangeListener = this
        with(headline) {
            text = context.getString(R.string.view_port_title_with_position, bindingAdapterPosition)
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            v?.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
        } else {
            v?.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
        }
    }
}
