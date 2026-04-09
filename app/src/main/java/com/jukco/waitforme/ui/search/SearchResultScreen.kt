package com.jukco.waitforme.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect 
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.jukco.waitforme.R
import com.jukco.waitforme.data.mock.MockBookmarkRepository
import com.jukco.waitforme.data.mock.MockStoreApi
import com.jukco.waitforme.data.network.model.ShopSorter
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.repository.StoreRepositoryImplementation
import com.jukco.waitforme.ui.ErrorScreen
import com.jukco.waitforme.ui.LoadingScreen
import com.jukco.waitforme.ui.components.BookmarkRectStoreItem
import com.jukco.waitforme.ui.components.BookmarkSquareStoreItem
import com.jukco.waitforme.ui.components.SearchTopBar
import com.jukco.waitforme.ui.components.Title
import com.jukco.waitforme.ui.theme.GreyAAA
import com.jukco.waitforme.ui.theme.GreyCCC
import com.jukco.waitforme.ui.theme.MainBlack
import com.jukco.waitforme.ui.theme.MainBlue
import com.jukco.waitforme.ui.theme.MainGreen
import com.jukco.waitforme.ui.theme.NotoSansKR
import com.jukco.waitforme.ui.theme.WaitForMeTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchResultScreen(
    onPopItemClicked: (Int) -> Unit,
    onBackButtonClicked: () -> Unit,
    onSearchBarClicked: (String) -> Unit,
) {
    val viewModel: SearchResultViewModel = viewModel(factory = SearchResultViewModel.Factory)

    SearchResultLayout(
        query = viewModel.query,
        searchedStores = viewModel.searchedStores,
        recommendedStores = viewModel.recommendStores,
        currentTab = viewModel.currentTab,
        onFilterSelected = viewModel::sortStores,
        refresh = viewModel::refresh,
        recommend = viewModel::recommend,
        onItemBookmarkChecked = viewModel::checkBookmark,
        onPopItemClicked = onPopItemClicked,
        onBackButtonClicked = onBackButtonClicked,
        onSearchBarClicked =  { onSearchBarClicked(viewModel.query) }
    )
}

@Composable
fun SearchResultLayout(
    query: String,
    searchedStores: Flow<PagingData<StoreDto>>,
    recommendedStores: Flow<PagingData<StoreDto>>,
    currentTab: Int,
    onFilterSelected: (Int) -> Unit,
    refresh: (String, ShopSorter) -> Unit,
    recommend: () -> Unit,
    onItemBookmarkChecked: (Int) -> Unit,
    onPopItemClicked: (Int) -> Unit,
    onBackButtonClicked: () -> Unit,
    onSearchBarClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                value = query,
                onValueChange = {},
                readOnly = true,
                focus = false,
                onBackButtonClicked = onBackButtonClicked,
                onSearchBarClicked = onSearchBarClicked,
            )
        }
    ) { scaffoldPadding ->
        val stores = searchedStores.collectAsLazyPagingItems()

        when (stores.loadState.refresh) {
            is LoadState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(scaffoldPadding))
            }
            is LoadState.Error -> {
                ErrorScreen(
                    refreshAction = { refresh(query, ShopSorter.NEWEST) },
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }
            is LoadState.NotLoading -> {
                if (stores.itemCount > 0) {
                    SearchResultFound(
                        currentTab = currentTab,
                        onFilterSelected = onFilterSelected,
                        stores = stores,
                        onPopItemClicked = onPopItemClicked,
                        onItemBookmarkChecked = onItemBookmarkChecked,
                        modifier = Modifier.padding(scaffoldPadding),
                    )
                } else {
                    LaunchedEffect(Unit) {
                        recommend()
                    }

                    SearchResultNotFound(
                        stores = recommendedStores.collectAsLazyPagingItems(),
                        onPopItemClicked = onPopItemClicked,
                        onItemBookmarkChecked = onItemBookmarkChecked,
                        modifier = Modifier.padding(scaffoldPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultNotFound(
    stores: LazyPagingItems<StoreDto>,
    onPopItemClicked: (Int) -> Unit,
    onItemBookmarkChecked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
        ,
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .padding(top = 54.dp, bottom = 69.dp)
                    .padding(horizontal = 56.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_image_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 176.dp, height = 142.dp)
                        .padding(start = 8.dp, bottom = 24.dp)
                )
                Text(
                    text = stringResource(R.string.search_not_found),
                    style = TextStyle(
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        lineHeight = 14.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        letterSpacing = (-0.05).em,
                    )
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Title(
                imageSrc = R.drawable.img_user_main_title02,
                text = buildAnnotatedString {
                    append(stringResource(R.string.title_recommend_1))
                    withStyle(style = SpanStyle(color = MainBlue)) {
                        append(stringResource(R.string.title_recommend_2))
                    }
                    append(stringResource(R.string.title_recommend_3))
                },
            )
        }
        items(
            count = stores.itemCount,
            key = stores.itemKey { it.id },
        ) {index ->
            val store = stores.itemSnapshotList[index] ?: return@items

            BookmarkSquareStoreItem(
                store = store,
                onItemClicked = onPopItemClicked,
                onBookmarkChecked = onItemBookmarkChecked,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun SearchResultFound(
    currentTab: Int,
    onFilterSelected: (Int) -> Unit,
    stores: LazyPagingItems<StoreDto>,
    onPopItemClicked: (Int) -> Unit,
    onItemBookmarkChecked: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        FilterTab(
            currentTab = currentTab,
            filterGroup = stringArrayResource(R.array.pops_filter_group),
            onFilterSelected = onFilterSelected,
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 154.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            items(
                count = stores.itemCount,
                key = stores.itemKey { it.id },
            ) {index ->
                val store = stores[index] ?: return@items

                BookmarkRectStoreItem(
                    store = store,
                    onItemClicked = onPopItemClicked,
                    onBookmarkChecked = onItemBookmarkChecked,
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


@Composable
fun FilterTab(
    currentTab: Int,
    filterGroup: Array<String>,
    onFilterSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        filterGroup.forEachIndexed { index, filterText ->
            val selected = (index == currentTab)

            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) MainGreen else Color.Transparent,
                    contentColor = if (selected) MainBlack else GreyAAA,
                ),
                border = if (selected) null else  BorderStroke(1.dp, GreyCCC),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 5.dp),
                onClick = { onFilterSelected(index) },
                modifier = Modifier.wrapContentSize(),
            ) {
                Text(
                    text = filterText,
                    style = TextStyle(
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        letterSpacing = (-0.05).em,
                    ),
                    modifier= Modifier.padding(0.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SearchResultLayoutPreview() {
    val viewModel = remember {
        SearchResultViewModel(SavedStateHandle(), StoreRepositoryImplementation(MockStoreApi), MockBookmarkRepository)
    }

    WaitForMeTheme {
        SearchResultLayout(
            query = viewModel.query,
            searchedStores = viewModel.searchedStores,
            recommendedStores = viewModel.recommendStores,
            currentTab = viewModel.currentTab,
            onFilterSelected = viewModel::sortStores,
            refresh = viewModel::refresh,
            recommend = viewModel::recommend,
            onItemBookmarkChecked = viewModel::checkBookmark,
            onPopItemClicked = {},
            onBackButtonClicked = {},
            onSearchBarClicked = {},
        )
    }
}