package org.classapp.loolocator

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class App : Application(), ViewModelStoreOwner {

    override val viewModelStore = ViewModelStore()

    lateinit var viewModel: SharedViewModel
        private set

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this))
            .get(SharedViewModel::class.java)
    }


}