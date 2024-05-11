package org.classapp.loolocator.utils

import kotlin.math.*

// Radius of the Earth in kilometers
private const val EARTH_RADIUS = 6371.01

/**
 * Calculate the distance between two points on the Earth's surface given their longitudes and latitudes.
 * @param lat1 Latitude of the first point
 * @param lon1 Longitude of the first point
 * @param lat2 Latitude of the second point
 * @param lon2 Longitude of the second point
 * @return The distance between the two points in kilometers
 */
fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = EARTH_RADIUS * c

    // Format the distance as a string with two decimal places
    val formattedDistance = String.format("%.2f", distance)

    // Convert the formatted string back to a double
    println("Distance: $formattedDistance")
    return formattedDistance.toDouble()
}