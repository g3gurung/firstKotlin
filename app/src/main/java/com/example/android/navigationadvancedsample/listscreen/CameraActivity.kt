package com.example.android.navigationadvancedsample.listscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.android.navigationadvancedsample.R
import kotlinx.android.synthetic.main.camera_activity.*
import java.io.File

class CameraActivity: AppCompatActivity(), LifecycleOwner {
    private val TAG = "CameraActivity:"

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var videoCapture: VideoCapture

    private  var videoStarted = 0

    val self = this

    @SuppressLint("RestrictedApi, ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity)
/*
        capture_button.setOnLongClickListener {
            println("long click listener detected")
            Toast.makeText(self, "Long click", Toast.LENGTH_SHORT).show()
            true
        }

        capture_button.setOnClickListener{
            println("only a click detected")
            Toast.makeText(self, "Normal click", Toast.LENGTH_SHORT).show()
        }
*/
        back_button.setOnClickListener {
            finish()
        }

        Log.i(TAG, "onViewCreated called")
        viewFinder = view_finder
        captureButton = capture_button

        captureButton.setOnTouchListener { _, event ->
            val file = File(self.externalMediaDirs?.first(),
                    "${System.currentTimeMillis()}.mp4")
            if(videoStarted == 0) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    captureButton.setBackgroundColor(Color.GREEN)
                    videoCapture.startRecording(file, object : VideoCapture.OnVideoSavedListener {
                        override fun onVideoSaved(file: File?) {
                            Log.i(TAG, "Video File : $file")
                            Toast.makeText(self,
                                    "\"Video File : $file",
                                    Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
                            Log.i(TAG, "Video Error: $message")
                        }
                    })

                } else if (event.action == MotionEvent.ACTION_UP) {
                    captureButton.setBackgroundColor(Color.RED)
                    videoCapture.stopRecording()
                    Log.i(TAG, "Video File stopped")
                }
            } else {

            }
            false
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            Log.i(TAG, "all permission granted")
            viewFinder.post {
                Log.i(TAG, "start camera")
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                self, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().build()
        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Create a configuration object for the video use case
        val videoCaptureConfig = VideoCaptureConfig.Builder().apply {
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        videoCapture = VideoCapture(videoCaptureConfig)

        preview.setOnPreviewOutputUpdateListener {
            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Bind use cases to lifecycle
        CameraX.bindToLifecycle(self, preview, videoCapture)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }
}