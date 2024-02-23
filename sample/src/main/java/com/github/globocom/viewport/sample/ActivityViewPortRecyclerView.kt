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
import com.github.globocom.viewport.sample.databinding.ActivityViewPortRecyclerViewBinding

class ActivityViewPortRecyclerView : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private const val LINEAR_LAYOUT_MANAGER = 0
        private const val GRID_LAYOUT_MANAGER = 1
    }

    private val spinnerThresholdValues = arrayListOf(
        Threshold.VISIBLE.name,
        Threshold.HALF.name,
        Threshold.ALMOST_VISIBLE.name,
        Threshold.ALMOST_HIDDEN.name
    )

    private var activityBinding: ActivityViewPortRecyclerViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(ActivityViewPortRecyclerViewBinding
            .inflate(layoutInflater)
             .also { activityBinding = it }
             .root)

        setRecyclerView(LINEAR_LAYOUT_MANAGER, Threshold.VISIBLE)

        activityBinding?.activityMainViewPortRecyclerView?.apply {
            viewedItemsLiveData.observe(this@ActivityViewPortRecyclerView) {
                activityBinding?.activityMainTextViewItemsViewedTv?.text =
                    getString(R.string.view_port_viewed_items, it.toString())
            }
            onlyNewViewedItemsLiveData.observe(this@ActivityViewPortRecyclerView) {
                activityBinding?.activityMainTextViewNewViewedItems?.text =
                    getString(R.string.view_port_new_visible_items, it.toString())
            }
            lifecycleOwner = this@ActivityViewPortRecyclerView
            threshold(Threshold.VISIBLE)
        }

        activityBinding?.activityMainButtonLinearLayout?.setOnClickListener {
            setRecyclerView(LINEAR_LAYOUT_MANAGER, getSelectedThreshold())
            activityBinding?.activityMainViewPortRecyclerView?.invalidate()
        }

        activityBinding?.activityMainButtonGridLayout?.setOnClickListener {
            setRecyclerView(GRID_LAYOUT_MANAGER, getSelectedThreshold())
            activityBinding?.activityMainViewPortRecyclerView?.invalidate()
        }

        activityBinding?.activityMainSpinnerThreshold?.run {
            this.adapter = ArrayAdapter(
                this@ActivityViewPortRecyclerView,
                android.R.layout.simple_spinner_item,
                spinnerThresholdValues
            )
            onItemSelectedListener = this@ActivityViewPortRecyclerView
        }
    }

    override fun onDestroy() {
        activityBinding = null
        super.onDestroy()
    }

    private fun setRecyclerView(
        layoutManagerOption: Int,
        threshold: Threshold
    ) {
        activityBinding?.activityMainViewPortRecyclerView?.apply {
            setHasFixedSize(true)
            threshold(threshold)
            layoutManager =
                if (layoutManagerOption == GRID_LAYOUT_MANAGER) GridLayoutManager(context, 4)
                else LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            adapter = ViewPortAdapter((0..100).toList())
        }
    }

    private fun getSelectedThreshold(): Threshold {
        return activityBinding?.activityMainSpinnerThreshold?.selectedItemPosition?.let { Threshold.valueOf(spinnerThresholdValues[it]) }
            ?: Threshold.HALF
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        activityBinding?.activityMainViewPortRecyclerView?.threshold(Threshold.valueOf(spinnerThresholdValues[position]))
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}
