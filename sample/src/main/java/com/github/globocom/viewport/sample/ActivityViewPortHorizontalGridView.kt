package com.github.globocom.viewport.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.globocom.viewport.sample.databinding.ActivityViewPortHorizontalGridViewBinding

class ActivityViewPortHorizontalGridView : AppCompatActivity() {

    private var activityBinding: ActivityViewPortHorizontalGridViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(ActivityViewPortHorizontalGridViewBinding
            .inflate(layoutInflater)
             .also { activityBinding = it }
             .root)

        activityBinding?.activityViewPortHorizontalRecyclerView?.apply {
            setHasFixedSize(true)
            adapter = ViewPortAdapter((0..100).toList())
            lifecycleOwner = this@ActivityViewPortHorizontalGridView
            viewedItemsLiveData.observe(this@ActivityViewPortHorizontalGridView) {
                activityBinding?.activityViewPortHorizontalRecyclerViewTextViewVisibleItems?.text =
                    getString(R.string.view_port_viewed_items, it.toString())
            }
            onlyNewViewedItemsLiveData.observe(this@ActivityViewPortHorizontalGridView) {
                activityBinding?.activityViewPortHorizontalRecyclerViewTextViewNewVisibleItems?.text =
                    getString(R.string.view_port_new_visible_items, it.toString())
            }
        }
    }

    override fun onDestroy() {
        activityBinding = null
        super.onDestroy()
    }
}
