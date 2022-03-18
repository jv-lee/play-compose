package com.lee.playcompose.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeLarge
import com.lee.playcompose.common.ui.widget.AppBarView

/**
 * @author jv.lee
 * @date 2022/3/16
 * @description 搜索页
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        SearchAppBar {
            keyboardController?.hide()
            navController.popBackStack()
        }
        SearchContent {
            keyboardController?.hide()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchAppBar(navigationClick: () -> Unit) {
    val textValue = remember { mutableStateOf("") }
    AppBarView(navigationClick = navigationClick) {
        TextField(
            value = textValue.value,
            onValueChange = { textValue.value = it },
            textStyle = TextStyle.Default.copy(fontSize = FontSizeLarge),
            singleLine = true,
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
private fun SearchContent(contentClick: () -> Unit) {

}