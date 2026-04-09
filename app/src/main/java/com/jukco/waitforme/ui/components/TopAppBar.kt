package com.jukco.waitforme.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jukco.waitforme.R
import com.jukco.waitforme.ui.theme.WaitForMeTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchAndNoticeTopBar(
    onNoticeButtonClicked: () -> Unit,
    onSearchBarClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            TopBarTextField(
                value = "",
                onValueChange = {},
                hint = stringResource(R.string.searching_hint),
                readOnly = true,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp,
                ),
                modifier = Modifier
                    .padding(end = 2.dp)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.hide()
                            onSearchBarClicked()
                        }
                    }
                ,
            )
        },
        actions = {
            IconButton(onClick = { onNoticeButtonClicked() }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_notification),
                    contentDescription = stringResource(R.string.notice),
                    tint = Color.Unspecified,
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        modifier = modifier
            .padding(start = 16.dp, end = 10.dp)
        ,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchTopBar(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    focus: Boolean,
    onBackButtonClicked: () -> Unit,
    onSearch: () -> Unit = {},
    onSearchBarClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TopAppBar(
        title = {
            TopBarTextField(
                value = value,
                onValueChange = onValueChange,
                hint = stringResource(R.string.searching_hint),
                readOnly = readOnly,
                trailingIcon = {
                    if (value != "") {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cancel_circle),
                                contentDescription = stringResource(R.string.cancel),
                                tint = Color.Unspecified
                            )
                        }
                    }
                },
                focus = focus,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearch() },
                ),
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) {
                            onSearchBarClicked?.apply {
                                keyboardController?.hide()
                                this()
                            }
                        }
                    },
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(start = 10.dp, end = 16.dp),
    )
}



@Preview(showBackground = true)
@Composable
private fun SearchAndNoticeTopBarPreview() {
    WaitForMeTheme {
        SearchAndNoticeTopBar(
            onNoticeButtonClicked = {},
            onSearchBarClicked = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    var text by remember { mutableStateOf("") }

    WaitForMeTheme {
        SearchTopBar(
            value = text,
            onValueChange = { text = it },
            focus = false,
            onBackButtonClicked = {},
        )
    }
}
