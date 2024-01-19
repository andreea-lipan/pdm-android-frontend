package com.example.myapp.util.maps

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MyLocation() {
    val myNewtworkStatusViewModel = viewModel<MyLocationViewModel>(
        factory = MyLocationViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
    val location = myNewtworkStatusViewModel.uiState
    if (location != null) {
        MyMap(location.latitude, location.longitude)
    }
}
