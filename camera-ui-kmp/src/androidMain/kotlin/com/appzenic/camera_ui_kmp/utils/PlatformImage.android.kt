package com.appzenic.camera_ui_kmp.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

actual typealias PlatformImage = Bitmap

actual fun PlatformImage.toByteArray(quality: Int): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}