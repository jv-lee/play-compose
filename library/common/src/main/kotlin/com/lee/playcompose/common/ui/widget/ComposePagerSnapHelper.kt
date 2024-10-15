package com.lee.playcompose.common.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Creates a [PagerSnapState] that is remembered across compositions.
 */
@Composable
fun rememberPagerSnapState(): PagerSnapState {
    return remember {
        PagerSnapState()
    }
}

/**
 * This class maintains the state of the pager snap.
 */
class PagerSnapState {

    val isSwiping = mutableStateOf(false)

    val firstVisibleItemIndex = mutableIntStateOf(0)

    val offsetInfo = mutableIntStateOf(0)

    var currentIndex by mutableIntStateOf(0)

    internal fun updateScrollToItemPosition(itemPos: LazyListItemInfo?) {
        if (itemPos != null) {
            this.offsetInfo.intValue = itemPos.offset
            this.firstVisibleItemIndex.intValue = itemPos.index
        }
    }

    suspend fun scrollItemToSnapPosition(listState: LazyListState, position: Int) {
        currentIndex = position
        listState.animateScrollToItem(position)
    }
}

/**
 * [PagerSnapNestedScrollConnection] reacts to the scroll left to right and vice-versa.
 */
class PagerSnapNestedScrollConnection(
    private val state: PagerSnapState,
    private val listState: LazyListState,
    private val scrollAction: () -> Unit,
    private val scrollTo: () -> Unit
) : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
        when (source) {
            NestedScrollSource.UserInput -> onScroll()
            else -> Offset.Zero
        }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when (source) {
        NestedScrollSource.UserInput -> onScroll()
        else -> Offset.Zero
    }

    private fun onScroll(): Offset {
        state.updateScrollToItemPosition(listState.layoutInfo.visibleItemsInfo.firstOrNull())
        scrollAction()
        state.isSwiping.value = true
        return Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity = when {
        state.isSwiping.value -> {
            state.updateScrollToItemPosition(listState.layoutInfo.visibleItemsInfo.firstOrNull())

            scrollTo()

            Velocity.Zero
        }
        else -> {
            Velocity.Zero
        }
    }.also {
        state.isSwiping.value = false
    }
}

/**
 * [ComposePagerSnapHelper] is a pager style snapping in horizontal orientation.
 * Provide the width used in the item in horizontally scrolling list, and [ComposePagerSnapHelper] will calculate
 * when to snap to the target view.
 *
 * @param itemOffset width of the item in horizontally/vertically scrolling list.
 * @param content a block which describes the content. Inside this block, you will have
 * access to [LazyListState].
 *
 * [Dp] the width of the item returned in dp.
 */

@Composable
fun ComposePagerSnapHelper(
    state: PagerSnapState = rememberPagerSnapState(),
    itemOffset: Dp,
    content: @Composable (LazyListState) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val itemOffsetPx = with(LocalDensity.current) {
        itemOffset.roundToPx()
    }

    LaunchedEffect(state.currentIndex) {
        scope.launch {
            state.scrollItemToSnapPosition(listState, state.currentIndex)
        }
    }

    val connection = remember(state, listState) {
        PagerSnapNestedScrollConnection(state, listState, {
            state.currentIndex = findPosition(state, itemOffsetPx)
        }) {
            val position = findPosition(state, itemOffsetPx)
            scope.launch {
                state.scrollItemToSnapPosition(listState, position)
            }
        }
    }

    Box(
        modifier = Modifier
            .nestedScroll(connection)
    ) {
        content(listState)
    }
}

private fun findPosition(state: PagerSnapState, itemOffsetPx: Int): Int {
    val firstItemIndex = state.firstVisibleItemIndex.intValue
    val firstItemOffset = abs(state.offsetInfo.intValue)

    return when {
        firstItemOffset <= itemOffsetPx.div(2) -> firstItemIndex
        else -> firstItemIndex.plus(1)
    }
}