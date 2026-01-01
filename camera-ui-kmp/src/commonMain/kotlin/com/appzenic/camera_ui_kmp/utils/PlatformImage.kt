// commonMain/.../toByteArray.kt
package com.appzenic.camera_ui_kmp.utils

// A wrapper class that will hold the platform-specific image
expect class PlatformImage

// Now the signature is the same for both platforms
expect fun PlatformImage.toByteArray(quality: Int = 100): ByteArray

