@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.littlechat.ui.commoncomposables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex

@Composable
fun <T> PullToRefreshLazyColumn(
    items: List<T>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    content: @Composable (T) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection).zIndex(-1f)) {
        LazyColumn(state = lazyListState, modifier = modifier) {
            items(items) {
                content(it)
            }
        }

        if (pullToRefreshState.isRefreshing)
            LaunchedEffect(true) {
                onRefresh()
            }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) pullToRefreshState.startRefresh()
            else pullToRefreshState.endRefresh()

        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            indicator = {
                PullToRefreshDefaults.Indicator(it, modifier = Modifier.background(Color.Transparent))
            }

        )
    }
}