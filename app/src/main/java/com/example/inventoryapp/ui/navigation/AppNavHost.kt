package com.example.inventoryapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.inventoryapp.ui.groups.GroupsScreen
import com.example.inventoryapp.ui.inventory.InventoryScreen
import com.example.inventoryapp.ui.scanner.ScannerScreen

sealed class Screen(val route: String) {
    object Groups : Screen("groups")
    object Inventory : Screen("inventory/{groupId}/{groupName}") {
        fun createRoute(groupId: Long, groupName: String) =
            "inventory/$groupId/${java.net.URLEncoder.encode(groupName, "UTF-8")}"
    }
    object Scanner : Screen("scanner/{groupId}") {
        fun createRoute(groupId: Long) = "scanner/$groupId"
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Groups.route) {
        composable(Screen.Groups.route) {
            GroupsScreen(
                onGroupClick = { group ->
                    navController.navigate(Screen.Inventory.createRoute(group.id, group.name))
                }
            )
        }
        composable(
            route = Screen.Inventory.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.LongType },
                navArgument("groupName") { type = NavType.StringType }
            )
        ) { backStack ->
            val groupId = backStack.arguments!!.getLong("groupId")
            val groupName = java.net.URLDecoder.decode(
                backStack.arguments!!.getString("groupName", ""), "UTF-8"
            )
            InventoryScreen(
                groupId = groupId,
                groupName = groupName,
                onNavigateBack = { navController.popBackStack() },
                onScanClick = { navController.navigate(Screen.Scanner.createRoute(groupId)) }
            )
        }
        composable(
            route = Screen.Scanner.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { backStack ->
            val groupId = backStack.arguments!!.getLong("groupId")
            ScannerScreen(
                groupId = groupId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
