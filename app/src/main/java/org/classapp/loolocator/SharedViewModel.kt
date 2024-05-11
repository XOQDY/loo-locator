package org.classapp.loolocator

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val latValue = mutableDoubleStateOf(0.0)
    val lonValue = mutableDoubleStateOf(0.0)
}