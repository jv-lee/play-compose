package com.lee.playcompose.search.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lee.playcompose.base.ktx.LocalNavController
import com.lee.playcompose.base.ktx.onTap
import com.lee.playcompose.common.entity.SearchHistory
import com.lee.playcompose.common.ktx.toast
import com.lee.playcompose.common.ui.composable.AppTextField
import com.lee.playcompose.common.ui.theme.ColorsTheme
import com.lee.playcompose.common.ui.theme.FontSizeTheme
import com.lee.playcompose.common.ui.theme.OffsetLarge
import com.lee.playcompose.common.ui.theme.OffsetMedium
import com.lee.playcompose.common.ui.theme.OffsetRadiusMedium
import com.lee.playcompose.common.ui.theme.OffsetSmall
import com.lee.playcompose.common.ui.widget.header.AppBarViewContainer
import com.lee.playcompose.router.RoutePage
import com.lee.playcompose.router.navigateArgs
import com.lee.playcompose.search.R
import com.lee.playcompose.search.model.entity.SearchHotUI
import com.lee.playcompose.search.viewmodel.SearchViewEvent
import com.lee.playcompose.search.viewmodel.SearchViewIntent
import com.lee.playcompose.search.viewmodel.SearchViewModel
import com.lee.playcompose.search.viewmodel.SearchViewState

/**
 * 搜索页
 * @author jv.lee
 * @date 2022/3/16
 */
@Composable
fun SearchPage(
    navController: NavController = LocalNavController.current,
    viewModel: SearchViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val viewState = viewModel.viewStates()

    // 监听单发事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                // 导航到搜索结果页
                is SearchViewEvent.NavigationSearch -> {
                    focusManager.clearFocus()
                    navController.navigateArgs(RoutePage.Search.SearchResult.route, event.key)
                }
                // 页面错误toast提示
                is SearchViewEvent.FailedEvent -> {
                    toast(event.error.message)
                }
            }
        }
    }

    SearchAppBarContainer(
        viewState = viewState,
        navigationClick = {
            focusManager.clearFocus()
            navController.popBackStack()
        },
        onSearchClick = { searchKey ->
            viewModel.dispatch(SearchViewIntent.NavigationSearchKey(searchKey))
        },
        onValueChange = { searchKey ->
            viewModel.dispatch(SearchViewIntent.ChangeSearchKey(searchKey))
        }
    ) {
        SearchContent(
            viewState = viewState,
            contentClick = {
                focusManager.clearFocus()
            },
            onSearchClick = { searchKey ->
                viewModel.dispatch(SearchViewIntent.NavigationSearchKey(searchKey))
            },
            onDeleteHistoryClick = { searchKey ->
                viewModel.dispatch(SearchViewIntent.DeleteSearchHistory(searchKey))
            },
            onClearClick = {
                viewModel.dispatch(SearchViewIntent.ClearSearchHistory)
            }
        )
    }
}

@Composable
private fun SearchAppBarContainer(
    viewState: SearchViewState,
    navigationClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onValueChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    AppBarViewContainer(
        navigationClick = navigationClick,
        appBarContent = {
            AppTextField(
                value = viewState.searchKey,
                onValueChange = onValueChange,
                keyboardActions = KeyboardActions(
                    onSearch = { onSearchClick(viewState.searchKey) }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                hintText = stringResource(id = R.string.search_hint),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 56.dp, end = 56.dp)
            )
        },
        content = content
    )
}

@Composable
private fun SearchContent(
    viewState: SearchViewState,
    contentClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onDeleteHistoryClick: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .onTap { contentClick() }
    ) {
        SearchHotContent(viewState = viewState, onSearchClick)
        SearchHistoryContent(
            viewState = viewState,
            onSearchClick = onSearchClick,
            onDeleteHistoryClick = onDeleteHistoryClick,
            onClearClick = onClearClick
        )
        SearchHistoryEmptyLayout(viewState = viewState)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchHotContent(
    viewState: SearchViewState,
    onSearchClick: (String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.search_hot_label),
        fontSize = FontSizeTheme.sizes.medium,
        color = ColorsTheme.colors.accent,
        modifier = Modifier.padding(OffsetLarge)
    )
    FlowRow(modifier = Modifier.padding(start = OffsetLarge, end = OffsetLarge)) {
        viewState.searchHot.forEach { hot ->
            SearchHotItem(hot, onSearchClick)
        }
    }
}

@Composable
private fun SearchHistoryContent(
    viewState: SearchViewState,
    onSearchClick: (String) -> Unit,
    onDeleteHistoryClick: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = OffsetLarge,
                end = OffsetLarge,
                top = OffsetLarge,
                bottom = OffsetMedium
            )
    ) {
        Text(
            text = stringResource(id = R.string.search_history_label),
            fontSize = FontSizeTheme.sizes.medium,
            color = ColorsTheme.colors.accent,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
        Text(
            text = stringResource(id = R.string.search_clear),
            fontSize = FontSizeTheme.sizes.medium,
            color = ColorsTheme.colors.primary,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onClearClick() }
        )
    }
    LazyColumn(modifier = Modifier.padding(start = OffsetLarge, end = OffsetLarge), content = {
        itemsIndexed(viewState.searchHistory) { _, item ->
            SearchHistoryItem(
                item = item,
                onSearchClick = onSearchClick,
                onDeleteHistoryClick = onDeleteHistoryClick
            )
        }
    })
}

@Composable
private fun ColumnScope.SearchHistoryEmptyLayout(viewState: SearchViewState) {
    if (viewState.searchHistory.isEmpty()) {
        Text(
            text = stringResource(id = R.string.search_history_empty_text),
            fontSize = FontSizeTheme.sizes.medium,
            color = ColorsTheme.colors.primary,
            modifier = Modifier
                .padding(top = 26.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SearchHotItem(item: SearchHotUI, onSearchClick: (String) -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.cardColors().copy(containerColor = ColorsTheme.colors.label),
        shape = RoundedCornerShape(OffsetRadiusMedium),
        modifier = Modifier.padding(end = OffsetSmall, bottom = OffsetSmall)
    ) {
        Text(
            text = item.key,
            color = item.color,
            fontSize = FontSizeTheme.sizes.small,
            modifier = Modifier
                .clickable { onSearchClick(item.key) }
                .padding(OffsetMedium)
        )
    }
}

@Composable
private fun SearchHistoryItem(
    item: SearchHistory,
    onSearchClick: (String) -> Unit,
    onDeleteHistoryClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick(item.key) }
            .padding(top = OffsetMedium, bottom = OffsetMedium)
    ) {
        Text(
            text = item.key,
            fontSize = FontSizeTheme.sizes.small,
            color = ColorsTheme.colors.primary,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
        )
        Icon(
            painter = painterResource(id = R.drawable.vector_close),
            contentDescription = null,
            tint = ColorsTheme.colors.accent,
            modifier = Modifier
                .size(16.dp)
                .align(alignment = Alignment.CenterEnd)
                .clickable { onDeleteHistoryClick(item.key) }
        )
    }
}