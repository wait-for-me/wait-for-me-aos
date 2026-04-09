package com.jukco.waitforme.ui.navi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.jukco.waitforme.ui.BookmarkScreen
import com.jukco.waitforme.ui.notice.list.NoticeScreen
import com.jukco.waitforme.ui.notice.detail.NoticeDetailScreen
import com.jukco.waitforme.ui.StoreManagementScreen
import com.jukco.waitforme.ui.search.SearchInputScreen
import com.jukco.waitforme.ui.sign.sign_in.SignInScreen
import com.jukco.waitforme.ui.sign.sign_up.InputCredentialsScreen
import com.jukco.waitforme.ui.WaitScreen
import com.jukco.waitforme.ui.my_info.MyInfoScreen
import com.jukco.waitforme.ui.search.SearchResultScreen
import com.jukco.waitforme.ui.sign.SignViewModel
import com.jukco.waitforme.ui.sign.sign_up.CompleteScreen
import com.jukco.waitforme.ui.sign.sign_up.InputNameScreen
import com.jukco.waitforme.ui.sign.sign_up.SelectCustomerOwnerScreen
import com.jukco.waitforme.ui.store_detail.PopupStoreScreen
import com.jukco.waitforme.ui.store_list.StoreListScreen

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Route.StoreList.name,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        SignNav(navController)

        composable(Route.StoreManagement.name) {
            StoreManagementScreen(
                onPopItemClicked = {
                    /* TODO: id 전달 */
                    navController.navigate(Route.PopupStore.name)
                },
            )
        }
        composable(Route.StoreList.name) {
            StoreListScreen(
                onNoticeButtonClicked = { navController.navigate(Route.Notice.name) },
                moveToSearching = { navController.navigate(Route.SearchInput.name) },
                moveToMoreList = { navController.navigate(Route.SearchResult.name) },
                onPopItemClicked = { navController.navigate("${Route.PopupStore.name}/$it") },
            )
        }
        composable(Route.WaitInfo.name) {
            WaitScreen()
        }
        composable(Route.Bookmark.name) {
            BookmarkScreen()
        }
        composable(Route.MyInfo.name) {
            MyInfoScreen(
                onBackButtonPressed = { navController.navigateUp() },
                goSignIn = {
                    navController.navigate(Route.SignGraph.name) {
                        popUpTo(Route.StoreList.name) { inclusive = true }
                    }
                }
            )
        }
        composable(Route.Notice.name) {
            NoticeScreen(
                onNoticeItemClicked = { noticeId -> navController.navigate("${Route.NoticeDetail.name}/$noticeId") },
                onBackButtonClicked = { navController.navigateUp() },
            )
        }
        composable(
            route = "${Route.NoticeDetail.name}/{noticeId}",
            arguments = listOf(navArgument("noticeId") { type = NavType.IntType }),
        ) {
            NoticeDetailScreen(
                onBackButtonClicked = { navController.navigateUp() }
            )
        }
        composable(
            route = "${Route.SearchInput.name}?query={query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            SearchInputScreen(
                onBackButtonClicked = { navController.navigateUp() },
                onSearch = { query ->
                    if (query != "") {
                        navController.navigate("${Route.SearchResult.name}?query=$query") {
                            popUpTo(Route.StoreList.name)
                        }
                    }
                }
            )
        }
        composable(
            route = "${Route.SearchResult.name}?query={query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            SearchResultScreen(
                onPopItemClicked = { storeId -> navController.navigate("${Route.PopupStore.name}/$storeId") },
                onBackButtonClicked = { navController.navigateUp() },
                onSearchBarClicked = { query -> navController.navigate("${Route.SearchInput.name}?query=$query") },
            )
        }
        composable(
            route = "${Route.PopupStore.name}/{storeId}",
            arguments = listOf(navArgument("storeId") { type = NavType.IntType }),
        ) {
            PopupStoreScreen(
                onBackButtonClicked = { navController.popBackStack() },
            )
        }
    }
}

fun NavGraphBuilder.SignNav(
    navController: NavHostController,
) {
    navigation(startDestination = Route.SignIn.name, route = Route.SignGraph.name) {
        composable(Route.SignIn.name) { navBackStackEntry ->
            val signBackStackEntry = remember(navBackStackEntry) { navController.getBackStackEntry(Route.SignGraph.name) }
            val signViewModel: SignViewModel = viewModel(viewModelStoreOwner = signBackStackEntry, factory = SignViewModel.Factory)

            SignInScreen(
                form = signViewModel.signInform,
                isLoading = signViewModel.isLoading,
                socialSignIn = signViewModel::getSocialSign,
                onEvent = signViewModel::onSignInEvent,
                goMain = {
                    navController.navigate(Route.StoreList.name) {
                        popUpTo(Route.SignGraph.name) { inclusive = true }
                    }
                },
                goSignUp = { navController.navigate(Route.SignUpInputCredentials.name) },
            )
        }
        composable(Route.SignUpInputCredentials.name) { navBackStackEntry ->
            val signBackStackEntry = remember(navBackStackEntry) { navController.getBackStackEntry(Route.SignGraph.name) }
            val signViewModel: SignViewModel = viewModel(viewModelStoreOwner = signBackStackEntry, factory = SignViewModel.Factory)

            InputCredentialsScreen(
                onNextButtonClicked = { navController.navigate(Route.SignUpInputName.name) },
                hasPhoneNumber = signViewModel.signUpDto.phoneNumber.isNotBlank(),
                provider = signViewModel.signUpDto.provider,
                form = signViewModel.signUpForm,
                errorMessage = signViewModel.errorMessage,
                currentLimitTime = signViewModel.currentLimitTime,
                enabledReRequestVerificationCode = signViewModel.enabledReRequestAuthnNum,
                authnNumInputTimer = signViewModel.authnNumInputTimer,
                onEvent = signViewModel::onSignUpEvent,
            )
        }
        composable(Route.SignUpInputName.name) { navBackStackEntry ->
            val signBackStackEntry = remember(navBackStackEntry) { navController.getBackStackEntry(Route.SignGraph.name) }
            val signViewModel: SignViewModel = viewModel(viewModelStoreOwner = signBackStackEntry, factory = SignViewModel.Factory)

            InputNameScreen(
                onNextButtonClicked = { navController.navigate(Route.SignUpSelectCustomerOwner.name) },
                dto = signViewModel.signUpDto,
                form = signViewModel.signUpForm,
                errorMessage = signViewModel.errorMessage,
                isLoading = signViewModel.isLoading,
                onEvent = signViewModel::onSignUpEvent,
            )
        }
        composable(Route.SignUpSelectCustomerOwner.name) { navBackStackEntry ->
            val signBackStackEntry = remember(navBackStackEntry) { navController.getBackStackEntry(Route.SignGraph.name) }
            val signViewModel: SignViewModel = viewModel(viewModelStoreOwner = signBackStackEntry, factory = SignViewModel.Factory)

            SelectCustomerOwnerScreen(
                onSignUpButtonClicked = {
                    navController.navigate(Route.SignUpComplete.name) {
                        popUpTo(Route.SignUpInputCredentials.name) { inclusive = true }
                    }
                },
                dto = signViewModel.signUpDto,
                customerOwner = SignViewModel.CUSTOMER_OWNER,
                isLoading = signViewModel.isLoading,
                onEvent = signViewModel::onSignUpEvent,
            )
        }
        composable(Route.SignUpComplete.name) {
            CompleteScreen(
                onStartButtonClicked = {
                    navController.navigate(Route.StoreList.name) {
                        popUpTo(Route.SignGraph.name) { inclusive = true }
                    }
                }
            )
        }
    }
}
