package com.github.globocom.viewport.tv

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.github.globocom.viewport.commons.ViewPortGridViewHelper
import com.github.globocom.viewport.commons.ViewPortLiveData
import com.github.globocom.viewport.commons.ViewPortManager

open class ViewPortHorizontalGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalGridView(context, attrs, defStyleAttr), LifecycleObserver {
    init {
        isSaveEnabled = true
    }

    private companion object {
        const val INSTANCE_STATE_SUPER_STATE = "instanceStateSuperState"
        const val INSTANCE_STATE_IS_HEAR_BEAT_STARTED = "instanceStateIsHearBeatStarted"
        const val INSTANCE_STATE_IS_LIB_STARTED = "instanceStateIsLibStarted"
        const val INSTANCE_STATE_CURRENT_VISIBLE_ITEMS_LIST = "instanceStateCurrentVisibleItemsList"
        const val INSTANCE_STATE_PREVIOUSLY_VISIBLE_ITEMS_LIST =
            "instanceStatePreviouslyVisibleItemsList"
        const val INSTANCE_STATE_OLD_ITEMS_LIST = "instanceStateOldItemsList"
    }

    private var viewPortManager: ViewPortManager? = null

    /**
     * [ViewPortLiveData] used to handle first and last visible items positions.
     *
     * Send this live data to [ViewPortManager] in order to obtain viewed items interval
     * between first and last visible items.
     */
    private var firstAndLastVisibleItemsLiveData = ViewPortLiveData<Pair<Int, Int>>()

    private val childSelectedListener by lazy {
        object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelectedAndPositioned(
                parent: RecyclerView?,
                child: ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                super.onChildViewHolderSelectedAndPositioned(parent, child, position, subposition)

                parent?.let {
                    val firstVisibleItemPosition =
                        ViewPortGridViewHelper.findFirstVisibleItemPosition(parent)

                    val lastVisibleItemPosition =
                        ViewPortGridViewHelper.findLastCompletelyVisibleItemPosition(parent)

                    firstAndLastVisibleItemsLiveData.value =
                        Pair(firstVisibleItemPosition, lastVisibleItemPosition)
                }
            }
        }
    }

    /**
     * [MutableLiveData] to be manipulated in that class. Client should access it's value by using
     * [viewedItemsLiveData].
     */
    val viewedItemsLiveData = ViewPortLiveData<List<Int>>()

    /**
     * A [LiveData] that emits only the newest visible items, ignoring items that remained visible since the last emission.
     * The main use case of this is to send impression events for metrics of those items.
     */
    val onlyNewViewedItemsLiveData = ViewPortLiveData<List<Int>>()


    /**
     * Client should set this value with a [LifecycleOwner] to pause/return sending values by
     * [viewedItemsLiveData] when your activity/fragment pauses or resumes.
     *
     * * Attempt to, if client not provides a valid LifecycleOwner, it could not receives updates
     * about viewed items through [viewedItemsLiveData]
     */
    var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            if (field == value) return

            field?.lifecycle?.removeObserver(this)
            field = value
            field?.lifecycle?.addObserver(this)

            viewPortManager?.stopLib()
            viewPortManager = ViewPortManager(firstAndLastVisibleItemsLiveData, field)

            field?.let {
                viewPortManager?.viewedItemsLiveData?.observe(it, Observer { viewedItems ->
                    this.viewedItemsLiveData.value = viewedItems
                })
                viewPortManager?.onlyNewViewedItemsLiveData?.observe(it, Observer { viewedItems ->
                    this.onlyNewViewedItemsLiveData.value = viewedItems
                })
            }

            // Attach and listen a ChildViewHolderSelectedListener.
            addOnChildViewHolderSelectedListener(childSelectedListener)

            // Starts view port manager.
            viewPortManager?.startLib()
        }


    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        windowAlignment = WINDOW_ALIGN_BOTH_EDGE
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = Bundle()
        myState.putParcelable(INSTANCE_STATE_SUPER_STATE, superState)

        viewPortManager?.let {
            myState.putBoolean(INSTANCE_STATE_IS_HEAR_BEAT_STARTED, it.isHearBeatStarted)
            myState.putBoolean(INSTANCE_STATE_IS_LIB_STARTED, it.isLibStarted)
            myState.putIntArray(
                INSTANCE_STATE_CURRENT_VISIBLE_ITEMS_LIST,
                it.currentVisibleItemsList.toIntArray()
            )
            myState.putIntArray(
                INSTANCE_STATE_PREVIOUSLY_VISIBLE_ITEMS_LIST,
                it.previouslyVisibleItemsList.toIntArray()
            )
            myState.putIntArray(INSTANCE_STATE_OLD_ITEMS_LIST, it.oldItemsList.toIntArray())
        }

        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE_SUPER_STATE))
            viewPortManager?.apply {
                isHearBeatStarted = state.getBoolean(INSTANCE_STATE_IS_HEAR_BEAT_STARTED)
                isLibStarted = state.getBoolean(INSTANCE_STATE_IS_LIB_STARTED)
                currentVisibleItemsList =
                    state.getIntArray(INSTANCE_STATE_CURRENT_VISIBLE_ITEMS_LIST)?.toMutableList()
                        ?: mutableListOf()
                previouslyVisibleItemsList =
                    state.getIntArray(INSTANCE_STATE_PREVIOUSLY_VISIBLE_ITEMS_LIST)?.toMutableList()
                        ?: mutableListOf()
                oldItemsList = state.getIntArray(INSTANCE_STATE_OLD_ITEMS_LIST)?.toMutableList()
                    ?: mutableListOf()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * [Lifecycle.Event.ON_PAUSE] client events are triggered.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        viewPortManager?.pauseLib()
    }

    /**
     * [Lifecycle.Event.ON_RESUME] client events are triggered.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        viewPortManager?.resumeLib()
    }

    /**
     * [Lifecycle.Event.ON_DESTROY] client events are triggered.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        viewPortManager?.stopLib()
        lifecycleOwner?.lifecycle?.removeObserver(this)
        removeOnChildViewHolderSelectedListener(childSelectedListener)
    }
}