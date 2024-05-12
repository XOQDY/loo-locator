package org.classapp.loolocator

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import org.classapp.loolocator.ui.theme.LooLocatorTheme
import org.classapp.loolocator.utils.calculateDistance

data class Toilet(
    val looId: Int? = 0,
    val looName: String? = "",
    val looDetail: String? = "",
    val looLocation: GeoPoint? = GeoPoint(0.0, 0.0),
    val haveMale: Boolean? = false,
    val haveFemale: Boolean? = false,
    val haveDisabled: Boolean? = false,
    val haveBaby: Boolean? = false,
    val havePrayer: Boolean? = false,
    var distance: Double? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListedScreen(sharedViewModel: SharedViewModel, navController: NavController) {
    val screenContext = LocalContext.current
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
                    title = { Text(text = "Listed Toilet Locations") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
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
                ToiletList(toilets = toiletList)
            }
        }
    }
}

fun getToiletFromFirebase(onSuccess: (QuerySnapshot) -> Unit, onFailure: (Exception) -> Unit) {
    val db = Firebase.firestore
    db.collection("toilets")
        .get()
        .addOnSuccessListener { result ->
            onSuccess(result)
        }
        .addOnFailureListener { e ->
            onFailure(e)
        }
}

@Composable
fun ToiletItem(toilet: Toilet) {
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorSecondary)
        )
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(8.dp)
        ) {
            Text(text = toilet.looName!!,
                style = TextStyle(color = colorResource(id = R.color.white),
                    fontSize = 20.sp)
            )
            Text(text = toilet.looDetail!!,
                style = TextStyle(color = colorResource(id = R.color.white),
                    fontSize = 16.sp)
            )
            Text(text = "Distance: ${toilet.distance} km",
                style = TextStyle(color = colorResource(id = R.color.white),
                    fontSize = 16.sp)
            )
        }
    }
}

@Composable
fun ToiletList(toilets: List<Toilet?>) {
    // Sort the toilets by distance
    val sortedToilets = toilets.filterNotNull().sortedBy { it.distance }

    LazyColumn(
        contentPadding = PaddingValues(all = 4.dp)
    ) {
        items(items = sortedToilets) { toilet ->
            ToiletItem(toilet = toilet)
        }
    }
}