package com.github.globocom.viewport.commons

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

object ViewPortPartialHelper {

    fun findPartialVisibleChild(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager,
        threshold: Float,
        fromEndToStart: Boolean,
        hasThresholdPadding: Boolean,
    ): Int {
        val isVertical = layoutManager.canScrollVertically()

        val helper: OrientationHelper = if (isVertical) {
            OrientationHelper.createVerticalHelper(layoutManager)
        } else {
            OrientationHelper.createHorizontalHelper(layoutManager)
        }

        val start: Int = if (hasThresholdPadding) helper.startAfterPadding else getStart(recyclerView, isVertical)
        val end: Int = if (hasThresholdPadding) helper.endAfterPadding else getEnd(recyclerView, isVertical)

        val progression = 0.until(layoutManager.childCount).let {
            if (fromEndToStart) it.reversed() else it
        }

        for (index in progression) {
            val child: View? = layoutManager.getChildAt(index)
            val childStart: Int = helper.getDecoratedStart(child)
            val childEnd: Int = helper.getDecoratedEnd(child)
            val viewSize: Int = helper.getDecoratedMeasurement(child)

            if (checkThreshold(threshold, childStart, childEnd, start, end, viewSize)) {
                child?.let { return recyclerView.getChildAdapterPosition(it) }
            }
        }
        return RecyclerView.NO_POSITION
    }

    private fun getStart(recyclerView: RecyclerView, vertical: Boolean) =
        if (vertical) recyclerView.top else recyclerView.left

    private fun getEnd(recyclerView: RecyclerView, vertical: Boolean) =
        if (vertical) recyclerView.bottom else recyclerView.right

    private fun checkThreshold(
        threshold: Float,
        childStart: Int,
        childEnd: Int,
        start: Int,
        end: Int,
        viewSize: Int
    ): Boolean {
        val visibleStart = childStart.coerceIn(start, end)
        val visibleEnd = childEnd.coerceIn(start, end)
        val visibleSize = visibleEnd - visibleStart
        val visibleProportion = visibleSize.toFloat() / viewSize.toFloat()
        return visibleProportion >= threshold
    }
}
