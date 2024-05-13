package org.classapp.loolocator

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.QuerySnapshot
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.classapp.loolocator.ui.theme.LooLocatorTheme
import org.classapp.loolocator.utils.calculateDistance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearMeScreen(sharedViewModel: SharedViewModel, navController: NavController) {
    val screenContext = LocalContext.current
    val locationProvider = LocationServices.getFusedLocationProviderClient(screenContext)
    val toiletList = remember {
        mutableStateListOf<Toilet?>()
    }

    val latValue = sharedViewModel.latValue.doubleValue
    val lonValue = sharedViewModel.lonValue.doubleValue

    val onFirebaseQueryFailed = { e: Exception ->
        // Display a toast message to the user
        Toast.makeText(screenContext, "Failed to load data: ${e.message}", Toast.LENGTH_LONG).show()
        // Log the error for debugging purposes
        Log.e("FirebaseQuery", "Failed to load data", e)
        Unit
    }
    val onFirebaseQuerySuccess = { result: QuerySnapshot ->
        if (!result.isEmpty) {
            toiletList.clear()

            val resultDocuments = result.documents
            for (document in resultDocuments) {
                val toilet: Toilet? = document.toObject(Toilet::class.java)
                if (toilet != null) {
                    toilet.distance = calculateDistance(
                        latValue, lonValue,
                        toilet.looLocation!!.latitude, toilet.looLocation.longitude
                    )
                    // Only add the toilet to the list if its distance is less than or equal to 10 km
                    if (toilet.distance!! <= sharedViewModel.maxRangeValue.intValue) {
                        // Check each filter condition
                        if (((sharedViewModel.haveMale.value && toilet.haveMale == true) || (!sharedViewModel.haveMale.value))
                            && ((sharedViewModel.haveFemale.value && toilet.haveFemale == true) || (!sharedViewModel.haveFemale.value))
                            && ((sharedViewModel.haveBaby.value && toilet.haveBaby == true) || (!sharedViewModel.haveBaby.value))
                            && ((sharedViewModel.havePrayer.value && toilet.havePrayer == true) || (!sharedViewModel.havePrayer.value))
                            && ((sharedViewModel.haveDisabled.value && toilet.haveDisabled == true) || (!sharedViewModel.haveDisabled.value)))
                        {
                            toiletList.add(toilet)
                        }
                    }
                }
            }
        }
    }
    getToiletFromFirebase(onFirebaseQuerySuccess, onFirebaseQueryFailed)

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            sharedViewModel.latValue.doubleValue = p0.lastLocation?.latitude ?: 0.0
            sharedViewModel.lonValue.doubleValue = p0.lastLocation?.longitude ?: 0.0
        }
    }
    val permissionDialog = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                /* Get user location */
                getCurrentUserLocation(locationProvider, locationCallback)
            }
        })

    DisposableEffect(key1 = locationProvider) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            screenContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            /* Get user location */
            getCurrentUserLocation(locationProvider, locationCallback)
        } else {
            permissionDialog.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        onDispose {
            // remove observer if any
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }

    LaunchedEffect(key1 = sharedViewModel.haveFemale.value) {
        Log.d("NearMeScreen", "haveFemale: ${sharedViewModel.haveFemale.value}")
        getToiletFromFirebase(onFirebaseQuerySuccess, onFirebaseQueryFailed)
    }

    LooLocatorTheme {
        Surface (
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            TopAppBar(
                title = { Text(text = "Toilet Locations Near Me") },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = {
                        val intent = Intent(context, FilterViewActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Filter")
                    }
                }
            )
                LocationCoordinateDisplay(lat = latValue.toString(), lon = lonValue.toString())
                if (latValue != null && lonValue != null) {
                    MapDisplay(lat = latValue!!, lon = lonValue!!, toilets = toiletList)
                } else {
                    MapDisplay()
                }
            }
        }
    }
}

@Composable
fun MapDisplay(
    lat: Double = 13.74466, lon: Double = 100.53291,
    zoomLevel: Float = 13f, mapType: MapType = MapType.NORMAL,
    toilets: List<Toilet?> = emptyList()
) {
    val location = LatLng(lat, lon)
    val cameraState = rememberCameraPositionState()
    val toiletIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val context = LocalContext.current
    val iconSize = 150 // specify the new width and height of the icon

    LaunchedEffect(key1 = location) {
        cameraState.centerOnLocation(location)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.toilet_marker)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, false)
        toiletIcon.value = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(mapType = mapType),
        cameraPositionState = cameraState
    )
    {
        Marker(
            state = MarkerState(position = location),
            title = "You are Here",
            snippet = "Your location"
        )

        toilets.filterNotNull().forEach { toilet ->
            val toiletLocation = LatLng(toilet.looLocation!!.latitude, toilet.looLocation.longitude)
            Marker(
                state = MarkerState(position = toiletLocation),
                title = toilet.looName,
                snippet = "Distance: ${toilet.distance} km",
                icon = toiletIcon.value
            )
        }
    }
}

private suspend fun CameraPositionState.centerOnLocation(location: LatLng) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        13f
    ),
    durationMs = 1500
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NearMeScreenPreview() {
    val sharedViewModel = SharedViewModel()
    val navController = rememberNavController()
    NearMeScreen(sharedViewModel, navController)
}

@Composable
fun LocationCoordinateDisplay(lat: String, lon: String) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(all = 8.dp)
    ) {
        val (goBtn, latField, lonField) = createRefs()
        Button(onClick = { /*TODO*/ }, modifier = Modifier.constrainAs(goBtn) {
            top.linkTo(parent.top, margin = 8.dp)
            end.linkTo(parent.end, margin = 0.dp)
            width = Dimension.wrapContent
        }) {
            Text(text = "GO")
        }
        OutlinedTextField(
            value = lat, label = { Text(text = "Latitude") },
            onValueChange = {}, modifier = Modifier.constrainAs(latField) {
                top.linkTo(parent.top, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(goBtn.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        OutlinedTextField(
            value = lon,
            label = { Text(text = "Longitude") },
            onValueChange = {},
            modifier = Modifier.constrainAs(lonField) {
                top.linkTo(latField.bottom, margin = 0.dp)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(goBtn.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentUserLocation(
    locationProvider: FusedLocationProviderClient,
    locationCb: LocationCallback
) {
    val locationReq = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build()
    locationProvider.requestLocationUpdates(locationReq, locationCb, null)
}