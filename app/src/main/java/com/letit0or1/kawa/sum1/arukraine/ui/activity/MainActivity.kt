package com.letit0or1.kawa.sum1.arukraine.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import com.google.ar.core.AugmentedImage
import com.google.ar.core.Frame
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.letit0or1.kawa.sum1.arukraine.ui.SnackbarHelper
import com.letit0or1.kawa.sum1.arukraine.ui.fragment.ARUkraineFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import com.letit0or1.kawa.sum1.arukraine.R
import com.letit0or1.kawa.sum1.arukraine.data.AugmentedImageNode
import java.util.HashMap
import java.util.concurrent.locks.Lock


class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModel()

    private lateinit var fitToScanView: ImageView
    private lateinit var arFragment: ARUkraineFragment
    private lateinit var redSphereRenderable: ModelRenderable
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private val augmentedImageMap = HashMap<AugmentedImage, AugmentedImageNode>()


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
                    ShapeFactory.makeSphere(0.5f, Vector3(0.0f, 0.0f, 0.0f), material)
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

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        val node = AugmentedImageNode(this, arFragment.transformationSystem)
                        node.onClickListener = { _, _, node, oblast ->
                            data.text = getString(oblast.stringRes)
                            data.text = data.text.toString() + "\n${node.localPosition}"
//                            xV.text = "x: $value"
                            attach(node)
                        }
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

    private fun attach(node: Node) {
        val divideOn = 400;
        fun valueFromSeekBar(seekBar: SeekBar): Float {
            return (seekBar.progress - 50).toFloat() / divideOn
        }

        fun setSeekBarFromValue(seekBar: SeekBar, value: Float) {
            seekBar.progress = (((value) + 50 / divideOn) * divideOn).toInt()+50
        }

        x.setOnSeekBarChangeListener(null)
        y.setOnSeekBarChangeListener(null)
        z.setOnSeekBarChangeListener(null)

        setSeekBarFromValue(x, node.localPosition.x)
        setSeekBarFromValue(y, node.localPosition.y)
        setSeekBarFromValue(z, node.localPosition.z)
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                when (seekBar) {
                    x -> {
                        xV.text = "x: ${valueFromSeekBar(x)}"
                        node.localPosition = Vector3(valueFromSeekBar(x), node.localPosition.y, node.localPosition.z)
                    }
                    y -> {
                        yV.text = "y: ${valueFromSeekBar(y)}"
                        node.localPosition = Vector3(node.localPosition.x, valueFromSeekBar(y), node.localPosition.z)
                    }
                    z -> {
                        zV.text = "z: ${valueFromSeekBar(z)}"
                        node.localPosition = Vector3(node.localPosition.x, node.localPosition.y, valueFromSeekBar(z))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        x.setOnSeekBarChangeListener(listener)
        y.setOnSeekBarChangeListener(listener)
        z.setOnSeekBarChangeListener(listener)
    }

    override fun onResume() {
        super.onResume()
        if (augmentedImageMap.isEmpty()) {
            fitToScanView.visibility = View.VISIBLE
        }
    }
}