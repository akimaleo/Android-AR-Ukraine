package com.letit0or1.kawa.uah_ar

import android.content.ContentValues
import android.media.CamcorderProfile
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.letit0or1.kawa.common.VideoRecorder
import com.letit0or1.kawa.uah_ar.model.AugmentedImageNode
import com.letit0or1.kawa.uah_ar.model.TwoUahSofiaCathedral
import com.letit0or1.kawa.uah_ar.model.TwoUahVolodimir
import kotlinx.android.synthetic.main.activity_main.*
import java.util.HashMap

class MainActivityUahAr : AppCompatActivity() {

    private lateinit var fitToScanView: ImageView
    private lateinit var arFragment: ArUahFragment
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private val augmentedImageMap = HashMap<AugmentedImage, AnchorNode>()

    val videoRecorder = VideoRecorder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fitToScanView = fit_to_scan_view
        arFragment = ux_fragment as ArUahFragment
        arFragment.arSceneView.scene.addOnUpdateListener {
            onUpdateFrame(it, arFragment.arSceneView.arFrame)
        }
        videoRecorder.setSceneView(arFragment.arSceneView)
        videoRecorder.setVideoQuality(
            CamcorderProfile.QUALITY_2160P,
            resources.configuration.orientation
        )
    }

    public fun toggleRecording(unusedView: View?) {
        if (!arFragment.hasWritePermission()) {
            Log.e(TAG, "Video recording requires the WRITE_EXTERNAL_STORAGE permission")
            Toast.makeText(
                this,
                "Video recording requires the WRITE_EXTERNAL_STORAGE permission",
                Toast.LENGTH_LONG
            )
                .show()
            arFragment.launchPermissionSettings()
            return
        }
        val recording = videoRecorder.onToggleRecord()
        if (recording) {
            recordingSwitch.setImageResource(R.drawable.ic_stop_black_24dp)
        } else {
            recordingSwitch.setImageResource(R.drawable.ic_fiber_manual_record_black_24dp)
            val videoPath = videoRecorder.getVideoPath().getAbsolutePath()
            Toast.makeText(this, "Video saved: $videoPath", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Video saved: $videoPath")

            // Send  notification of updated content.
            val values = ContentValues()
            values.put(MediaStore.Video.Media.TITLE, "Sceneform Video")
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            values.put(MediaStore.Video.Media.DATA, videoPath)
            contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        }
    }

    private fun onUpdateFrame(frameTime: FrameTime, frame: Frame?) {
        print(frameTime)
        if (frame == null) return
        // If there is no frame, just return.
        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
        for (augmentedImage in updatedAugmentedImages) {
            when (augmentedImage.trackingState) {
                TrackingState.PAUSED -> {
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    val text = "Detected Image " + augmentedImage.getIndex()
                    SnackbarHelper.getInstance().showMessage(this, text)
                }
                TrackingState.TRACKING -> {
                    fitToScanView.visibility = View.GONE

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        val node = provideNodeForImage(augmentedImage)
                        node.setImage(augmentedImage)
                        augmentedImageMap[augmentedImage] = node
                        arFragment.arSceneView.scene.addChild(node)
                    }
                }

                TrackingState.STOPPED -> {
                    augmentedImageMap.remove(augmentedImage)
                }
            }
        }
    }

    private fun provideNodeForImage(augmentedImage: AugmentedImage): AugmentedImageNode {
        val im: AugmentedImageNode = when (UAHImage.typeByFileName(augmentedImage.name)) {
            Currency.ONE_FOREGROUND -> {
                TwoUahVolodimir(this)
            }
            Currency.ONE_BACKGROUND -> {
                TwoUahVolodimir(this)
            }
            Currency.TWO_FOREGROUND -> {
                TwoUahVolodimir(this)
            }
            Currency.TWO_BACKGROUND -> {
                TwoUahSofiaCathedral(this, arFragment.transformationSystem)
            }
            Currency.FIVE_FOREGROUND -> TODO()
            Currency.FIVE_BACKGROUND -> TODO()
            Currency.TEN_FOREGROUND -> TODO()
            Currency.TEN_BACKGROUND -> TODO()
        }
        Log.e("Type", im.toString())
        return im
    }

    override fun onResume() {
        super.onResume()
        if (augmentedImageMap.isEmpty()) {
            fitToScanView.visibility = View.VISIBLE
        }
    }

    companion object {
        val TAG = "AAA"
    }
}