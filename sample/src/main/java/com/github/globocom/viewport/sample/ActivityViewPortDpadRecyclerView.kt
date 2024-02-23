package com.github.globocom.viewport.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.github.globocom.viewport.sample.databinding.ActivityViewPortDpadRecyclerViewTvBinding

class ActivityViewPortDpadRecyclerView : AppCompatActivity() {

    private var activityViewPortDpadRecyclerBinding: ActivityViewPortDpadRecyclerViewTvBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(ActivityViewPortDpadRecyclerViewTvBinding
            .inflate(layoutInflater)
             .also { activityViewPortDpadRecyclerBinding = it }
             .root)

        activityViewPortDpadRecyclerBinding?.activityViewPortDpadRecyclerView?.apply {
            setHasFixedSize(true)
            viewedItemsLiveData.observe(this@ActivityViewPortDpadRecyclerView) {
                activityViewPortDpadRecyclerBinding?.activityViewPortDpadRecyclerViewTextViewVisibleItems?.text =
                    getString(R.string.view_port_viewed_items, it.toString())
            }
            onlyNewViewedItemsLiveData.observe(this@ActivityViewPortDpadRecyclerView) {
                activityViewPortDpadRecyclerBinding?.activityViewPortDpadRecyclerViewTextViewNewVisibleItems?.text =
                    getString(R.string.view_port_new_visible_items, it.toString())
            }
            lifecycleOwner = this@ActivityViewPortDpadRecyclerView
            adapter = ViewPortAdapter((0..100).toList())
        }
    }

    override fun onDestroy() {
        activityViewPortDpadRecyclerBinding = null
        super.onDestroy()
    }

}
