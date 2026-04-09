package com.jukco.waitforme.ui.store_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.jukco.waitforme.R
import com.jukco.waitforme.data.mock.MockBookmarkRepository
import com.jukco.waitforme.data.mock.MockStoreApi
import com.jukco.waitforme.data.network.model.StoreDto
import com.jukco.waitforme.data.repository.StoreRepositoryImplementation
import com.jukco.waitforme.ui.ErrorScreen
import com.jukco.waitforme.ui.LoadingScreen
import com.jukco.waitforme.ui.components.BookmarkRectStoreItem
import com.jukco.waitforme.ui.components.NoBookmarkStoreItem
import com.jukco.waitforme.ui.components.SearchAndNoticeTopBar
import com.jukco.waitforme.ui.components.Title
import com.jukco.waitforme.ui.theme.MainBlack
import com.jukco.waitforme.ui.theme.MainBlue
import com.jukco.waitforme.ui.theme.MainGreen
import com.jukco.waitforme.ui.theme.NotoSansKR
import com.jukco.waitforme.ui.theme.WaitForMeTheme
import kotlin.math.absoluteValue

@Composable
fun StoreListScreen(
    onNoticeButtonClicked: () -> Unit,
    moveToSearching: () -> Unit,
    moveToMoreList: () -> Unit,
    onPopItemClicked: (id: Int) -> Unit,
) {
    val viewModel: StoreListViewModel = viewModel(factory = StoreListViewModel.Factory)

    StoreListLayout(
        uiState = viewModel.storeListUiState,
        refresh = viewModel::refresh,
        onNoticeButtonClicked = onNoticeButtonClicked,
        onSearchingClicked = moveToSearching,
        moveToMoreList = moveToMoreList,
        onPopItemClicked = onPopItemClicked,
        onItemBookmarkChecked = viewModel::checkBookmark,
    )
}


@Composable
fun StoreListLayout(
    uiState: StoreListUiState,
    refresh: () -> Unit,
    onNoticeButtonClicked: () -> Unit,
    onSearchingClicked: () -> Unit,
    moveToMoreList: () -> Unit,
    onPopItemClicked: (id: Int) -> Unit,
    onItemBookmarkChecked: (shopId: Int) -> Unit,
) {
    Scaffold(
        topBar = { SearchAndNoticeTopBar(onNoticeButtonClicked, onSearchingClicked) },
    ) { scaffoldPadding ->
        when (uiState) {
            is StoreListUiState.Loading -> LoadingScreen(modifier = Modifier.padding(scaffoldPadding))
            is StoreListUiState.Error -> ErrorScreen(refreshAction = refresh, modifier = Modifier.padding(scaffoldPadding))
            is StoreListUiState.Success -> {
                StoreListContent(
                    ongoingStores = uiState.ongoingStores.collectAsLazyPagingItems(),
                    upcomingStores = uiState.upcomingStores.collectAsLazyPagingItems(),
                    moveToMoreList = moveToMoreList,
                    onPopItemClicked = onPopItemClicked,
                    onItemBookmarkChecked = onItemBookmarkChecked,
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }
        }
    }
}

@Composable
fun StoreListContent(
    ongoingStores: LazyPagingItems<StoreDto>,
    upcomingStores: LazyPagingItems<StoreDto>,
    moveToMoreList: () -> Unit,
    onPopItemClicked: (id: Int) -> Unit,
    onItemBookmarkChecked: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 154.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
        ,
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Title(
                imageSrc = R.drawable.img_user_main_title01,
                text = buildAnnotatedString {
                    append(stringResource(R.string.title_ongoing))
                    withStyle(style = SpanStyle(color = MainBlue)) {
                        append(stringResource(R.string.pops))
                    }
                },
                action = moveToMoreList,
                actionIconSrc = R.drawable.ic_arrow_more_right,
                actionDescription = R.string.btn_more,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        items(
            count = ongoingStores.itemCount,
            key = ongoingStores.itemKey { it.id },
        ) {index ->
            val store = ongoingStores.itemSnapshotList[index] ?: return@items

            BookmarkRectStoreItem(
                store = store,
                onItemClicked = onPopItemClicked,
                onBookmarkChecked = onItemBookmarkChecked,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Title(
                imageSrc = R.drawable.img_user_main_title02,
                text = buildAnnotatedString {
                    append(stringResource(R.string.title_ongoing))
                    withStyle(style = SpanStyle(color = MainBlue)) {
                        append(stringResource(R.string.pops))
                    }
                },
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            UpcomingStores(
                stores = upcomingStores,
                onPopItemClicked = onPopItemClicked,
                modifier = Modifier.padding(bottom = 36.dp),
            )
        }
    }
}

@Composable
private fun UpcomingStores(
    stores: LazyPagingItems<StoreDto>,
    onPopItemClicked: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        items(
            count = stores.itemCount,
            key = stores.itemKey { it.id },
        ) { index ->
            val store = stores.itemSnapshotList[index] ?: return@items

            UpcomingStore(store, onPopItemClicked)
        }
    }
}

@Composable
private fun UpcomingStore(
    store: StoreDto,
    onPopItemClicked: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        NoBookmarkStoreItem(store = store, onClicked = { onPopItemClicked(store.id) })
        Text(
            text = stringResource(R.string.dDay, if (store.dDay >= 0) "-" else "+", store.dDay.absoluteValue),
            style = TextStyle(
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                letterSpacing = (-0.05).em,
            ),
            color = MainGreen,
            modifier = Modifier
                .padding(top = 6.dp, end = 6.dp)
                .align(Alignment.TopEnd)
                .drawBehind {
                    drawRoundRect(
                        color = MainBlack,
                        alpha = 0.6f,
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                }
                .padding(top = 4.dp, bottom = 4.dp, start = 6.dp, end = 6.dp),
        )
    }
}

// =================================== Preview =============================================
@Preview(
    name = "LandScape Mode",
    showBackground = true,
    device = Devices.AUTOMOTIVE_1024p,
    heightDp = 640,
)
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Preview(name = "Foldable Mode", showBackground = true, device = Devices.FOLDABLE)
@Preview(name = "Tablet Mode", showBackground = true, device = Devices.TABLET)
@Composable
private fun PopListPreview() {
    val viewModel = remember {
        StoreListViewModel(StoreRepositoryImplementation(MockStoreApi), MockBookmarkRepository)
    }

    WaitForMeTheme {
        StoreListLayout(
            uiState = viewModel.storeListUiState,
            refresh = viewModel::refresh,
            onNoticeButtonClicked = {},
            moveToMoreList = {},
            onSearchingClicked = {},
            onPopItemClicked = {},
            onItemBookmarkChecked = viewModel::checkBookmark,
        )
    }
}
