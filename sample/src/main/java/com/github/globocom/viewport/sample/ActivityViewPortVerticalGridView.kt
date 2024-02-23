package com.github.globocom.viewport.sample

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.globocom.viewport.mobile.Threshold
import com.github.globocom.viewport.sample.databinding.ActivityViewPortVerticalGridViewBinding

class ActivityViewPortVerticalGridView : AppCompatActivity() {

    private var activityBinding: ActivityViewPortVerticalGridViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(ActivityViewPortVerticalGridViewBinding
            .inflate(layoutInflater)
             .also { activityBinding = it }
             .root)

        activityBinding?.activityViewPortVerticalGridView?.apply {
            viewedItemsLiveData.observe(this@ActivityViewPortVerticalGridView, Observer {
                activityBinding?.activityViewPortVerticalGridViewTextViewVisibleItems?.text =
                    getString(R.string.view_port_viewed_items, it.toString())
            })
            onlyNewViewedItemsLiveData.observe(this@ActivityViewPortVerticalGridView, Observer {
                activityBinding?.activityViewPortVerticalGridViewTextViewNewVisibleItems?.text =
                    getString(R.string.view_port_new_visible_items, it.toString())
            })
            lifecycleOwner = this@ActivityViewPortVerticalGridView
            setHasFixedSize(true)
            adapter = ViewPortAdapter((0..100).toList())
        }
    }

    override fun onDestroy() {
        activityBinding = null
        super.onDestroy()
    }
}
