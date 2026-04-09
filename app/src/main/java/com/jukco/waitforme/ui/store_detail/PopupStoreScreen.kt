package com.jukco.waitforme.ui.store_detail

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jukco.waitforme.R
import com.jukco.waitforme.data.network.model.ImageInfo
import com.jukco.waitforme.data.network.model.StoreDetailResponse
import com.jukco.waitforme.ui.ErrorScreen
import com.jukco.waitforme.ui.LoadingScreen
import com.jukco.waitforme.ui.theme.GreyAAA
import com.jukco.waitforme.ui.theme.GreyDDD
import com.jukco.waitforme.ui.theme.GreyEEE
import com.jukco.waitforme.ui.theme.MainBlack
import com.jukco.waitforme.ui.theme.MainBlue
import com.jukco.waitforme.ui.theme.MainWhite
import com.jukco.waitforme.ui.theme.NotoSansKR
import kotlinx.coroutines.launch

private fun shareStoreInformation(intentContext: Context, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            intentContext.getString(R.string.share_info, title),
        )
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)

    try {
        ContextCompat.startActivity(intentContext, shareIntent, null)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG,
        ).show()
    }
}

private fun copyStoreAddress(context: Context, address: String) {
    val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java) as ClipboardManager
    val clip = ClipData.newPlainText("address", address)
    clipboardManager.setPrimaryClip(clip)
}

@Composable
fun PopupStoreScreen(
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentContext = LocalContext.current
    val viewModel: StoreDetailViewModel = viewModel(factory = StoreDetailViewModel.Factory)

    when (val uiState = viewModel.storeDetailUiState) {
        is StoreDetailUiState.Loading -> LoadingScreen(modifier)
        is StoreDetailUiState.Error -> ErrorScreen(viewModel::load, modifier)
        is StoreDetailUiState.Success -> {
            val store by uiState.storeDetailResponse.collectAsState()

            StoreDetail(
                store = store,
                onBackButtonClicked = onBackButtonClicked,
                onShareButtonClicked = {
                    shareStoreInformation(
                        intentContext = currentContext,
                        title = store.title,
                    )
                },
                onBookmarkClicked = viewModel::onClickBookmark,
                copyStoreAddress = {
                    copyStoreAddress(currentContext, store.address)
                },
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetail(
    store: StoreDetailResponse,
    onBackButtonClicked: () -> Unit,
    onShareButtonClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    copyStoreAddress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.detail_page),
                        style = TextStyle(
                            color = MainBlack,
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 14.sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                            letterSpacing = (-0.05).em,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClicked) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back),
                            tint = Color.Unspecified,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onShareButtonClicked,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_share),
                            contentDescription = stringResource(R.string.share),
                            tint = Color.Unspecified,
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = { /*TODO : 예약하기 */ },
                contentPadding = PaddingValues(horizontal = 134.dp, vertical = (13.5).dp),
                shape = RoundedCornerShape(4.dp),
                enabled = !store.isReserved,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text(
                    text = stringResource(
                        if (store.isReserved) {
                            R.string.btn_on_site_reservation_true
                        } else {
                            R.string.btn_on_site_reservation_false
                        },
                    ),
                    style = TextStyle(
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        letterSpacing = 0.em,
                    ),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                if (store.images.isEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.baseline_image_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MainWhite),
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(GreyAAA),
                    )
                } else {
                    ImagePager(store.images)
                }
            }
            item {
                Title(
                    storeTitle = store.title,
                    storeHost = store.host,
                    isFavorite = store.isFavorite,
                    onBookmarkClicked = onBookmarkClicked,
                )
            }
            item {
                Spacer(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(1.dp)
                        .background(color = GreyEEE),
                )
            }
            item {
                BasicInformation(
                    date = store.operatingPeriod,
                    time = store.operatingTime,
                    address = store.address,
                    copyStoreAddress = copyStoreAddress,
                    modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp),
                )
            }
            item {
                SNSInformation(
                snsMap = store.snsMap,
                    modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 34.dp),
                )
            }
            item {
                Spacer(
                    modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(1.dp)
                        .background(color = GreyEEE),
                )
            }
            item {
                Text(
                    text = store.description,
                    style = TextStyle(
                        color = MainBlack,
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        lineHeight = (17.55).sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        letterSpacing = (-0.05).em,
                    ),
                    modifier = modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImagePager(
    images: List<ImageInfo>,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = {
        images.size
    })
    val coroutineScope = rememberCoroutineScope()

    Box {
        // MAIN, DETAIL이 여기서 쓰이는가? 순서대로 주는 것이 아닌가?
        HorizontalPager(
            state = pagerState,
            key = { it },
        ) { index ->
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(images[index].path)
                    .build(),
                contentDescription = "page $index",
                placeholder = painterResource(R.drawable.baseline_image_24),
                error = painterResource(R.drawable.img_store_example),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { /* TODO : 클릭하면 전체 이미지 보기, 필수는 아님 */ },
            )
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) MainBlue else GreyDDD
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .padding(bottom = 12.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                        ,
                )
            }
        }
    }
}

@Composable
private fun Title(
    storeTitle: String,
    storeHost: String,
    isFavorite: Boolean,
    onBookmarkClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 15.dp),
    ) {
        Column {
            Text(
                text = storeTitle,
                style = TextStyle(
                    color = MainBlack,
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    letterSpacing = (-0.05).em,
                ),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = storeHost,
                style = TextStyle(
                    color = GreyAAA,
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    letterSpacing = (-0.05).em,
                ),
            )
        }
        Spacer(modifier = modifier.weight(1f))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.clickable { onBookmarkClicked() },
        ) {
            Image(
                painter = painterResource(if (isFavorite) R.drawable.ic_bookmark_fill else R.drawable.ic_bookmark_line),
                contentDescription = stringResource(R.string.btn_bookmark),
            )
            /* TODO: 좋아요 숫자 카운트 넣을지 안 너을 지 미정 */
            Text(
                text = "0",
                textAlign = TextAlign.Center,
                color = if (isFavorite) MainBlue else MainBlack,
                style = TextStyle(
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    letterSpacing = (-0.05).em,
                ),
            )
        }
    }
}

@Composable
private fun BasicInformation(
    date: String,
    time: String,
    address: String,
    copyStoreAddress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val copyId = "copyIcon"
    val place = buildAnnotatedString {
        append(address)
        appendInlineContent(copyId, "[copyIcon]")
    }
    val copyIcon = mapOf(
        Pair(
            copyId,
            InlineTextContent(
                Placeholder(
                    width = 24.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_data_copy),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .clickable { copyStoreAddress() },
                )
            },
        ),
    )

    Column(modifier) {
        Text(
            text = stringResource(R.string.basic_info),
            style = TextStyle(
                color = MainBlack,
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                letterSpacing = (-0.05).em,
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconText(icon = R.drawable.img_calendar, text = date)
        Spacer(modifier = Modifier.height(10.dp))
        IconText(icon = R.drawable.img_clock, text = time)
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Top) {
            Image(
                painter = painterResource(R.drawable.img_location),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = place,
                inlineContent = copyIcon,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = TextStyle(
                    color = MainBlack,
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    letterSpacing = (-0.05).em,
                ),
            )
        }
    }
}

@Composable
private fun SNSInformation(
    snsMap: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = stringResource(R.string.sns),
            style = TextStyle(
                color = MainBlack,
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                letterSpacing = (-0.05).em,
            ),
        )
        Spacer(modifier = Modifier.height(6.dp))
        snsMap["INSTAGRAM"]?.let {
            Spacer(modifier = Modifier.height(10.dp))
            IconText(icon = R.drawable.img_sns_instar, text = it, description = it)
        }
//        IconText(icon = R.drawable.ic_nav_waiting, text = "트위터")
//        IconText(icon = R.drawable.ic_nav_waiting, text = "페이스북?")
        snsMap["FACEBOOK"]?.let {
            Spacer(modifier = Modifier.height(10.dp))
            IconText(icon = R.drawable.img_homepage, text = it, description = it)
        }
    }
}

@Composable
private fun IconText(
    @DrawableRes icon: Int,
    description: String? = null,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = description,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                color = MainBlack,
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                letterSpacing = (-0.05).em,
            ),
        )
    }
}

@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Preview(name = "Foldable Mode", showBackground = true, device = Devices.FOLDABLE)
@Preview(name = "Tablet Mode", showBackground = true, device = Devices.TABLET)
@Preview(showBackground = true)
@Composable
fun StoreDetailPreview() {
    StoreDetail(
        store = StoreDetailResponse(),
        onBackButtonClicked = {},
        onShareButtonClicked = {},
        onBookmarkClicked = {},
        copyStoreAddress = {},
    )
}
