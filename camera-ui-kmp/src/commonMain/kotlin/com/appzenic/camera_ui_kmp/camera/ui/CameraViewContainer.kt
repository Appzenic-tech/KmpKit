package com.appzenic.camera_ui_kmp.camera.ui

import androidx.compose.runtime.Composable

@Composable
fun CameraViewContainer(
    onPhotoCaptured: (ByteArray) -> Unit, // Let the caller decide how to save
    onBackPressed: () -> Unit
) {
    CameraViewUI(onResult = onPhotoCaptured, onBackPressEvent = onBackPressed)
}
