package com.appzenic.camera_ui_kmp.camera.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.appzenic.camera_ui_kmp.R
import com.appzenic.camera_ui_kmp.utils.toByteArray

@Composable
actual fun CameraViewUI(
    onResult: (ByteArray) -> Unit,
    onBackPressEvent: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFlashOn by remember { mutableStateOf(false) }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    cameraController.bindToLifecycle(lifecycleOwner)

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (cameraPreview, cameraCapture, closeCamera, flashIcon, switchCamera) = createRefs()
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(cameraPreview) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, factory = { ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                }
            })

        Box(
            modifier = Modifier
                .constrainAs(cameraCapture) {
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .size(68.dp)
                .clip(shape = CircleShape)
                .clickable(onClick = {
                    takePhoto(context, cameraController, onPhotoTaken = onResult)
                })
                .border(width = 4.dp, color = Color.White, shape = CircleShape)
                .padding(8.dp)
                .background(color = Color.White, shape = CircleShape)
        )

        IconButton(
            onClick = {
                cameraController.cameraSelector =
                    if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else CameraSelector.DEFAULT_BACK_CAMERA

                isFlashOn = false
            }, modifier = Modifier
                .constrainAs(switchCamera) {
                    start.linkTo(cameraCapture.end)
                    top.linkTo(cameraCapture.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(cameraCapture.bottom)
                }
                .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_camera_switch),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                onBackPressEvent()
            }, modifier = Modifier
                .constrainAs(closeCamera) {
                    top.linkTo(parent.top, margin = 40.dp)
                    start.linkTo(parent.start, 16.dp)
                }
                .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_window_close),
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = {
                isFlashOn =
                    !isFlashOn && cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                cameraController.enableTorch(isFlashOn)
            }, modifier = Modifier
                .constrainAs(flashIcon) {
                    top.linkTo(parent.top, margin = 40.dp)
                    end.linkTo(parent.end, 16.dp)
                }
                .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)) {
            Icon(
                painter = painterResource(if (isFlashOn) R.drawable.ic_window_close else R.drawable.ic_window_close),
                contentDescription = null,
                tint = if (isFlashOn) Color.Yellow else Color.White
            )
        }
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (ByteArray) -> Unit
) {
    val isFrontCamera = controller.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureStarted() {
                super.onCaptureStarted()
                Log.d("CameraViewUI", "onCaptureStarted: ")
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val sourceBitmap = image.toBitmap()
                val rotationDegrees = image.imageInfo.rotationDegrees


//                val finalBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply {
                    // 1. Handle Rotation
                    postRotate(rotationDegrees.toFloat())

                    // 2. Handle Mirroring if Front Camera
                    if (isFrontCamera) {
                        postScale(-1f, 1f, sourceBitmap.width / 2f, sourceBitmap.height / 2f)
                    }
                }
                val finalBitmap = Bitmap.createBitmap(
                    sourceBitmap,
                    0,
                    0,
                    sourceBitmap.width,
                    sourceBitmap.height,
                    matrix,
                    true
                )
//                } else sourceBitmap
                onPhotoTaken(finalBitmap.toByteArray())
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.d("CameraViewUI", "onError: ")
            }
        })
}