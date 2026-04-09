package com.jukco.waitforme.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jukco.waitforme.R
import com.jukco.waitforme.ui.navi.Route
import com.jukco.waitforme.ui.theme.NotoSansKR
import com.jukco.waitforme.ui.theme.WaitForMeTheme

@Composable
fun BottomNaviBar(
    navController: NavHostController,
    startDestination: Route,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    when (currentDestination?.route) {
        BottomNaviItem.StoreManagement.route -> BottomNavi(navController, startDestination, currentDestination)
        BottomNaviItem.StoreList.route -> BottomNavi(navController, startDestination, currentDestination)
        BottomNaviItem.WaitInfo.route -> BottomNavi(navController, startDestination, currentDestination)
        BottomNaviItem.Bookmark.route -> BottomNavi(navController, startDestination, currentDestination)
        BottomNaviItem.MyInfo.route -> BottomNavi(navController, startDestination, currentDestination)
        else -> Unit
    }
}

@Composable
fun BottomNavi(
    navController: NavHostController,
    startDestination: Route,
    currentDestination: NavDestination?,
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        BOTTOM_NAVI_ROUTE.forEach { bottomNaviItem ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == bottomNaviItem.route } == true
            val opacity = if (isSelected) 1f else 0.3f

            NavigationBarItem(
                selected = isSelected,
                modifier = Modifier.alpha(opacity),
                onClick = {
                    navController.navigate(bottomNaviItem.route) {
                        popUpTo(startDestination.name) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(bottomNaviItem.icon),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme
                        .surfaceColorAtElevation(LocalAbsoluteTonalElevation.current),
                ),
                label = {
                    Text(
                        text = stringResource(bottomNaviItem.title),
                        style = TextStyle(
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = (17.76).sp,
                            platformStyle = PlatformTextStyle(includeFontPadding = false),
                        ),
                        softWrap = false,
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun BottomNaviBarPreview() {
    WaitForMeTheme {
        BottomNavi(
            navController = rememberNavController(),
            startDestination = Route.StoreList,
            currentDestination = null,
        )
    }
}

sealed class BottomNaviItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String,
) {
    object StoreManagement : BottomNaviItem(R.string.navi_pops_management, R.drawable.ic_nav_set_popup, Route.StoreManagement.name)
    object StoreList : BottomNaviItem(R.string.navi_pops_list, R.drawable.ic_nav_list, Route.StoreList.name)
    object Bookmark : BottomNaviItem(R.string.navi_bookmark, R.drawable.ic_nav_bookmark, Route.Bookmark.name)
    object WaitInfo : BottomNaviItem(R.string.navi_wait_info, R.drawable.ic_nav_waiting, Route.WaitInfo.name)
    object MyInfo : BottomNaviItem(R.string.navi_my_info, R.drawable.ic_nav_my, Route.MyInfo.name)
}

val BOTTOM_NAVI_ROUTE = listOf(
    BottomNaviItem.StoreManagement,
    BottomNaviItem.StoreList,
    BottomNaviItem.Bookmark,
    BottomNaviItem.WaitInfo,
    BottomNaviItem.MyInfo,
)
