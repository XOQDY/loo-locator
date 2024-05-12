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
    private val _maxRangeValue = MutableLiveData<Int>().apply { value = 10 }
    val maxRangeValue: LiveData<Int> = _maxRangeValue
    private val _haveMale = MutableLiveData<Boolean>().apply { value = false }
    val haveMale: LiveData<Boolean> = _haveMale
    private val _haveFemale = MutableLiveData<Boolean>().apply { value = false }
    val haveFemale: LiveData<Boolean> = _haveFemale
    private val _haveBaby = MutableLiveData<Boolean>().apply { value = false }
    val haveBaby: LiveData<Boolean> = _haveBaby
    private val _havePrayer = MutableLiveData<Boolean>().apply { value = false }
    val havePrayer: LiveData<Boolean> = _havePrayer
    private val _haveDisabled = MutableLiveData<Boolean>().apply { value = false }
    val haveDisabled: LiveData<Boolean> = _haveDisabled

    fun setMaxRangeValue(value: Int) {
        _maxRangeValue.value = value
    }
    fun setHaveMale(have: Boolean) {
        _haveMale.value = have
    }
    fun setHaveFemale(have: Boolean) {
        _haveFemale.value = have
    }
    fun setHaveBaby(have: Boolean) {
        _haveBaby.value = have
    }
    fun setHavePrayer(have: Boolean) {
        _havePrayer.value = have
    }
    fun setHaveDisabled(have: Boolean) {
        _haveDisabled.value = have
    }
}