package com.example.android.navigationadvancedsample.listscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

import com.example.android.navigationadvancedsample.R
import java.io.File

/**
 * A simple [Fragment] subclass.
 */

class CameraFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registered, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(tag, "on view created")
    }
}

/*
class CameraFragment : Fragment(), LifecycleOwner {

    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    private lateinit var viewFinder: TextureView
    private lateinit var captureButton: ImageButton
    private lateinit var videoCapture: VideoCapture

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.i(tag, "onCreateView called")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        return view
    }

    @SuppressLint("RestrictedApi, ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(tag, "onViewCreated called")
        viewFinder = view.findViewById(R.id.view_finder)
        captureButton = view.findViewById(R.id.capture_button)


        captureButton.setOnTouchListener { _, event ->
            val file = File(activity?.externalMediaDirs?.first(),
                    "${System.currentTimeMillis()}.mp4")

            if (event.action == MotionEvent.ACTION_DOWN) {
                captureButton.setBackgroundColor(Color.GREEN)
                videoCapture.startRecording(file, object: VideoCapture.OnVideoSavedListener{
                    override fun onVideoSaved(file: File?) {
                        Log.i(tag, "Video File : $file")
                        Toast.makeText(requireActivity(),
                                "\"Video File : $file",
                                Toast.LENGTH_SHORT).show()
                    }
                    override fun onError(useCaseError: VideoCapture.UseCaseError?, message: String?, cause: Throwable?) {
                        Log.i(tag, "Video Error: $message")
                    }
                })

            } else if (event.action == MotionEvent.ACTION_UP) {
                captureButton.setBackgroundColor(Color.RED)
                videoCapture.stopRecording()
                Log.i(tag, "Video File stopped")
            }
            false
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            Log.i(tag, "all permission granted")
            viewFinder.post {
                Log.i(tag, "start camera")
                startCamera()
            }
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(requireActivity(),
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                requireContext(), it) == PackageManager.PERMISSION_GRANTED
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
        CameraX.bindToLifecycle(this, preview, videoCapture)
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
*/