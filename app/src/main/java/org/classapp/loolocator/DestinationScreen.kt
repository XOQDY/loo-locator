package org.classapp.loolocator

sealed class DestinationScreen(val route: String) {
    data object Listed : DestinationScreen("listed")
    data object NearMe : DestinationScreen("nearMe")
    data object AddToilet : DestinationScreen("addToilet")

}