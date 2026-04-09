package com.jukco.waitforme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.jukco.waitforme.ui.components.BottomNaviBar
import com.jukco.waitforme.ui.navi.NavigationGraph
import com.jukco.waitforme.ui.navi.Route

@Preview(showSystemUi = true)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNaviBar(navController = navController, startDestination = Route.StoreList) },
    ) { innerPadding ->
        NavigationGraph(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
//            startDescription = Route.StoreList.name,
            startDestination = Route.SignGraph.name,
        )
    }
}
