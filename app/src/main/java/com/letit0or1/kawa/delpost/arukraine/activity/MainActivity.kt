package com.letit0or1.kawa.delpost.arukraine.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.TransformableNode
import com.letit0or1.kawa.delpost.arukraine.R
import com.letit0or1.kawa.delpost.arukraine.SnackbarHelper
import com.letit0or1.kawa.delpost.arukraine.fragment.ARUkraineFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.getViewModel

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel = getViewModel()

    private lateinit var fitToScanView: ImageView
    private lateinit var arFragment: ARUkraineFragment
    private lateinit var redSphereRenderable: ModelRenderable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fitToScanView = fit_to_scan_view
        arFragment = ux_fragment as ARUkraineFragment
        arFragment.arSceneView.scene.addOnUpdateListener { onUpdateFrame(it, arFragment.arSceneView.arFrame) }


        // Create a new anchor for newly found images.
        MaterialFactory.makeOpaqueWithColor(this, Color(getColor(android.R.color.background_dark)))
            .thenAccept { material ->
                redSphereRenderable =
                    ShapeFactory.makeCube(Vector3(1.0f, 0.01f, 2.0f), Vector3(0.0f, 0.0f, 0.0f), material)
            }
    }


    private fun onUpdateFrame(frameTime: FrameTime, frame: Frame?) {
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

                    //create red sphere
                    val anchor = augmentedImage.createAnchor(augmentedImage.centerPose)
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFragment.arSceneView.scene)

                    // Create the transformable andy and add it to the anchor.
                    val andy = TransformableNode(arFragment.transformationSystem)
                    andy.setParent(anchorNode)
                    andy.renderable = redSphereRenderable

//                    if (!augmentedImageMap.containsKey(augmentedImage)) {
//                        val node = AugmentedImageNode(this, arFragment.getTransformationSystem())
//                        node.setImage(augmentedImage)
//                        augmentedImageMap.put(augmentedImage, node)
//                        arFragment.arSceneView.scene.addChild(node)
//                    }
                }

                TrackingState.STOPPED -> {
                } //augmentedImageMap.remove(augmentedImage)
            }
        }
    }
}
