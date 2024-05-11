package org.classapp.loolocator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.classapp.loolocator.ui.theme.LooLocatorTheme

class MainActivity : ComponentActivity() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        setContent {
            LooLocatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenWithBottomNavBar(sharedViewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreenWithBottomNavBar(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()
    var navSelectedItem by remember {
        mutableIntStateOf(0)
    }
    Scaffold(bottomBar = {
        NavigationBar {
            LooLocatorNavItemInfo().getAllNavItems().forEachIndexed { index, itemInfo ->
                NavigationBarItem(selected = false,
                    onClick = {
                        navSelectedItem = index
                        navController.navigate(itemInfo.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = itemInfo.icon,
                            contentDescription = itemInfo.label
                        )
                    },
                    label = { Text(text = itemInfo.label) })
            }
        }
    }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = DestinationScreen.NearMe.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = DestinationScreen.Listed.route) {
                ListedScreen(sharedViewModel, navController)
            }
            composable(route = DestinationScreen.NearMe.route) {
                NearMeScreen(sharedViewModel)
            }
            composable(route = DestinationScreen.AddToilet.route) {
                AddToiletScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val sharedViewModel = SharedViewModel()
    LooLocatorTheme {
        MainScreenWithBottomNavBar(sharedViewModel)
    }
}