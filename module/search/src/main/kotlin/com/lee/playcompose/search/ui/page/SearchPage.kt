package com.lee.playcompose.search.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.lee.playcompose.base.extensions.onTap
import com.lee.playcompose.common.entity.SearchHistory
import com.lee.playcompose.common.ui.theme.*
import com.lee.playcompose.common.ui.widget.AppBarView
import com.lee.playcompose.router.PageRoute
import com.lee.playcompose.search.R
import com.lee.playcompose.search.model.entity.SearchHot
import com.lee.playcompose.search.viewmodel.SearchViewAction
import com.lee.playcompose.search.viewmodel.SearchViewEvent
import com.lee.playcompose.search.viewmodel.SearchViewModel
import com.lee.playcompose.search.viewmodel.SearchViewState
import kotlinx.coroutines.flow.collect

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description 搜索页
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(
    navController: NavController,
    viewModel: SearchViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewState = viewModel.viewStates

    // 监听单发事件
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect { event ->
            when (event) {
                // 导航到搜索结果页
                is SearchViewEvent.NavigationSearch -> {
                    keyboardController?.hide()
                    navController.navigate(PageRoute.SearchResult.route)
                }
            }
        }
    }

    Column {
        SearchAppBar(
            viewState = viewState,
            navigationClick = {
                keyboardController?.hide()
                navController.popBackStack()
            }, onSearchClick = { searchKey ->
                viewModel.dispatch(SearchViewAction.NavigationSearchKey(searchKey))
            }, onValueChange = { searchKey ->
                viewModel.dispatch(SearchViewAction.ChangeSearchKey(searchKey))
            })
        SearchContent(
            viewState = viewState,
            contentClick = {
                keyboardController?.hide()
            }, onSearchClick = { searchKey ->
                viewModel.dispatch(SearchViewAction.NavigationSearchKey(searchKey))
            }, onDeleteHistoryClick = { searchKey ->
                viewModel.dispatch(SearchViewAction.DeleteSearchHistory(searchKey))
            }, onClearClick = {
                viewModel.dispatch(SearchViewAction.ClearSearchHistory)
            })
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchAppBar(
    viewState: SearchViewState,
    navigationClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onValueChange: (String) -> Unit,
) {
    AppBarView(navigationClick = navigationClick) {
        TextField(
            value = viewState.searchKey,
            onValueChange = { onValueChange(it) },
            textStyle = TextStyle.Default.copy(fontSize = FontSizeLarge),
            singleLine = true,
            keyboardActions = KeyboardActions(onSearch = { onSearchClick(viewState.searchKey) }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AppTheme.colors.accent,
                textColor = AppTheme.colors.accent,
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_hint),
                    color = AppTheme.colors.primary,
                    fontSize = FontSizeLarge
                )
            },
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(start = 56.dp, end = 56.dp),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchContent(
    viewState: SearchViewState,
    contentClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onDeleteHistoryClick: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .onTap { contentClick() }) {
        SearchHotContent(viewState = viewState, onSearchClick)
        SearchHistoryContent(
            viewState = viewState,
            onSearchClick = onSearchClick,
            onClearClick = onClearClick,
            onDeleteHistoryClick = onDeleteHistoryClick
        )
        SearchHistoryEmptyLayout(viewState = viewState)
    }
}

@Composable
private fun ColumnScope.SearchHotContent(
    viewState: SearchViewState,
    onSearchClick: (String) -> Unit
) {
    Text(
        text = stringResource(id = R.string.search_hot_label),
        fontSize = FontSizeMedium,
        color = AppTheme.colors.accent,
        modifier = Modifier.padding(OffsetLarge)
    )
    FlowRow(modifier = Modifier.padding(start = OffsetLarge, end = OffsetLarge)) {
        viewState.searchHot.forEach { hot ->
            SearchHotItem(hot, onSearchClick)
        }
    }
}

@Composable
private fun ColumnScope.SearchHistoryContent(
    viewState: SearchViewState,
    onSearchClick: (String) -> Unit,
    onDeleteHistoryClick: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.search_history_label),
            fontSize = FontSizeMedium,
            color = AppTheme.colors.accent,
            modifier = Modifier
                .padding(OffsetLarge)
                .align(Alignment.CenterStart)
        )
        Text(
            text = stringResource(id = R.string.search_clear),
            fontSize = FontSizeMedium,
            color = AppTheme.colors.primary,
            modifier = Modifier
                .padding(OffsetLarge)
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
            fontSize = FontSizeMedium,
            color = AppTheme.colors.primary,
            modifier = Modifier
                .padding(top = 26.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SearchHotItem(item: SearchHot, onSearchClick: (String) -> Unit) {
    Card(
        elevation = 0.dp,
        backgroundColor = Color(0xFFEBEBEB),
        shape = RoundedCornerShape(OffsetRadiusMedium),
        modifier = Modifier.padding(OffsetSmall)
    ) {
        Box(modifier = Modifier.clickable { onSearchClick(item.key) }) {
            Text(
                text = item.key,
                color = item.color,
                fontSize = FontSizeSmall,
                modifier = Modifier.padding(OffsetMedium)
            )
        }
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
            .padding(OffsetMedium)
    ) {
        Text(
            text = item.key,
            fontSize = FontSizeSmall,
            color = AppTheme.colors.primary,
            modifier = Modifier
                .align(alignment = Alignment.CenterStart)
                .clickable { onSearchClick(item.key) }
        )
        Icon(
            painter = painterResource(id = R.drawable.vector_close),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .align(alignment = Alignment.CenterEnd)
                .clickable { onDeleteHistoryClick(item.key) }
        )
    }
}