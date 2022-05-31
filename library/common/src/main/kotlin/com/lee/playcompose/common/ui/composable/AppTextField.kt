package com.lee.playcompose.common.ui.composable

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.lee.playcompose.common.ui.theme.AppTheme
import com.lee.playcompose.common.ui.theme.FontSizeLarge
import com.lee.playcompose.common.ui.theme.FontSizeMedium

/**
 * appTextField封装
 * @author jv.lee
 * @date 2022/3/21
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLine: Int = 1,
    hintText: String = "",
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle.Default.copy(fontSize = FontSizeMedium),
        singleLine = singleLine,
        maxLines = maxLine,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = AppTheme.colors.accent,
            textColor = AppTheme.colors.accent,
        ),
        placeholder = {
            Text(
                text = hintText,
                color = AppTheme.colors.primary,
                fontSize = FontSizeLarge
            )
        },
        modifier = modifier,
    )
}