package com.github.globocom.viewport.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.globocom.viewport.sample.databinding.ViewHolderViewPortSampleBinding

class ViewPortAdapter(private val listItems: List<Int>) :
    RecyclerView.Adapter<ViewPortViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewPortViewHolder(
        ViewHolderViewPortSampleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewPortViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = listItems.size
}