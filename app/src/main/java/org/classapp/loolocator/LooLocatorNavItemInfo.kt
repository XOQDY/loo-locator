package org.classapp.loolocator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

data class LooLocatorNavItemInfo(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.List,
    val route: String = ""
) {
    fun getAllNavItems(): List<LooLocatorNavItemInfo> {
        return listOf(
            LooLocatorNavItemInfo("Listed", Icons.Filled.List, DestinationScreen.Listed.route),
            LooLocatorNavItemInfo("Near Me", Icons.Filled.LocationOn, DestinationScreen.NearMe.route),
            LooLocatorNavItemInfo("Add Toilet", Icons.Filled.AddCircle, DestinationScreen.AddToilet.route)
        )
    }
}
