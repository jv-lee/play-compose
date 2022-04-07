package com.lee.playcompose.todo.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.lee.playcompose.common.ui.theme.AppTheme

/**
 * @author jv.lee
 * @date 2022/4/7
 * @description 创建todo/编辑todo页面
 */
@Composable
fun CreateTodoPage(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.background))
}