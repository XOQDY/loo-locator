package org.classapp.loolocator

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val latValue = mutableDoubleStateOf(0.0)
    val lonValue = mutableDoubleStateOf(0.0)
    val maxRangeValue = mutableIntStateOf(5)
    val haveMale = mutableStateOf(false)
    val haveFemale = mutableStateOf(false)
    val haveDisabled = mutableStateOf(false)
    val haveBaby = mutableStateOf(false)
    val havePrayer = mutableStateOf(false)
}