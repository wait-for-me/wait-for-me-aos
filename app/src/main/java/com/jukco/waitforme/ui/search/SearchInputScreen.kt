package com.jukco.waitforme.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jukco.waitforme.ui.components.SearchTopBar
import com.jukco.waitforme.ui.theme.WaitForMeTheme

@Composable
fun SearchInputScreen(
    onBackButtonClicked: () -> Unit,
    onSearch: (String) -> Unit,
) {
    val viewModel: SearchInputViewModel = viewModel(factory = SearchInputViewModel.Factory)

    SearchInputLayout(
        query = viewModel.query,
        onQueryChange = viewModel::changeQuery,
        onBackButtonClicked = onBackButtonClicked,
        onSearch = { onSearch(viewModel.query) },
    )
}

@Composable
fun SearchInputLayout(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackButtonClicked: () -> Unit,
    onSearch: () -> Unit,
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                value = query,
                onValueChange = onQueryChange,
                focus = true,
                onBackButtonClicked = onBackButtonClicked,
                onSearch = onSearch,
            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(scaffoldPadding)
        ) {
            // TODO: 최근검색어
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    var query by remember {
        mutableStateOf("")
    }

    WaitForMeTheme {
        SearchInputLayout(
            query = query,
            onQueryChange = { query = it },
            onBackButtonClicked = {},
            onSearch = {},
        )
    }
}