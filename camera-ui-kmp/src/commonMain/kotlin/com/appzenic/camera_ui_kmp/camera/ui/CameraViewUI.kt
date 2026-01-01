package com.appzenic.camera_ui_kmp.camera.ui

import androidx.compose.runtime.Composable

@Composable
expect fun CameraViewUI(onResult: (ByteArray) -> Unit,onBackPressEvent: () -> Unit)
