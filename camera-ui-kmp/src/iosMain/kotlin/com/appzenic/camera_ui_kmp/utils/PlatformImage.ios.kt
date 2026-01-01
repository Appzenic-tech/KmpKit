package com.appzenic.camera_ui_kmp.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.getBytes
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation

actual typealias PlatformImage = UIImage

@OptIn(ExperimentalForeignApi::class)
actual fun PlatformImage.toByteArray(quality: Int): ByteArray {
    val data: NSData = UIImageJPEGRepresentation(this, quality / 100.0)
        ?: return byteArrayOf()

    val byteArray = ByteArray(data.length.toInt())
    byteArray.usePinned { pinned ->
        data.getBytes(pinned.addressOf(0), data.length)
    }
    return byteArray
}