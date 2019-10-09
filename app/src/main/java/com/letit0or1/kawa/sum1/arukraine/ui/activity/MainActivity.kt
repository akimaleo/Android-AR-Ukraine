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
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.letit0or1.kawa.sum1.arukraine.ui.SnackbarHelper
import com.letit0or1.kawa.sum1.arukraine.ui.fragment.ARUkraineFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import com.letit0or1.kawa.sum1.arukraine.R
import com.letit0or1.kawa.sum1.arukraine.data.AugmentedImageNode
import com.letit0or1.kawa.sum1.arukraine.data.Oblast
import kotlinx.android.synthetic.main.oblast_view_info.view.*
import java.util.HashMap
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {

    companion object {
        private const val SELECTED_NODE_Y_POS: Float = 0.02f
        private const val STANDART_NODE_Y_POS: Float = 0.00f
    }

    val viewModel: MainViewModel by viewModel()

    private lateinit var fitToScanView: ImageView
    private lateinit var arFragment: ARUkraineFragment
    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private val augmentedImageMap = HashMap<AugmentedImage, AugmentedImageNode>()
    private var solarControlsRenderable: ViewRenderable? = null
    lateinit var solarControlsStage: CompletableFuture<ViewRenderable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solarControlsStage =
            ViewRenderable.builder().setView(this, R.layout.oblast_view_info).build()

        fitToScanView = fit_to_scan_view
        arFragment = ux_fragment as ARUkraineFragment
        arFragment.arSceneView.scene.addOnUpdateListener {
            onUpdateFrame(it, arFragment.arSceneView.arFrame)
        }

        solarControlsStage.thenAccept { solarControlsRenderable = it }
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
                            attach(node, oblast)
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

    private var selectedNode: Node? = null
    private lateinit var uiNode: Node

    private fun attach(node: Node, oblast: Oblast) {
        selectedNode?.let {
            val pos = it.localPosition
            pos.y = STANDART_NODE_Y_POS
            it.localPosition = pos
        }
        selectedNode = node
        data.text = getString(oblast.stringRes)
        data.text = data.text.toString() + "\n${node.localPosition}"

        val curPos = node.localPosition
        curPos.y = SELECTED_NODE_Y_POS
        node.localPosition = curPos
        applyInfoToView(node, oblast)
    }

    private fun applyInfoToView(node: Node, oblast: Oblast) {
        if (!::uiNode.isInitialized) {
            uiNode = Node()
            uiNode.localScale = Vector3(0.1f, 0.1f, 0.1f)
            uiNode.localRotation = Quaternion(Vector3(1f, 0f, 0f), -75f)
        }
        uiNode.renderable = solarControlsRenderable
        uiNode.localPosition = node.localPosition
        uiNode.localScale = Vector3(0.1f, 0.1f, 0.1f)

        val solarControlsView = solarControlsRenderable!!.view
        solarControlsView.obl_info_title.text = viewModel.getDataForOblast(oblast, this)
        solarControlsView.closeView.setOnClickListener {
            uiNode.setParent(null)
        }

        uiNode.setParent(node)
    }

    private fun debugAttach(node: Node, oblast: Oblast) {

        val divideOn = 400
        fun valueFromSeekBar(seekBar: SeekBar): Float {
            return (seekBar.progress - 50).toFloat() / divideOn
        }

        fun setSeekBarFromValue(seekBar: SeekBar, value: Float) {
            seekBar.progress = (((value) + 50 / divideOn) * divideOn).toInt() + 50
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
                        node.localPosition =
                            Vector3(valueFromSeekBar(x), node.localPosition.y, node.localPosition.z)
                    }
                    y -> {
                        yV.text = "y: ${valueFromSeekBar(y)}"
                        node.localPosition =
                            Vector3(node.localPosition.x, valueFromSeekBar(y), node.localPosition.z)
                    }
                    z -> {
                        zV.text = "z: ${valueFromSeekBar(z)}"
                        node.localPosition =
                            Vector3(node.localPosition.x, node.localPosition.y, valueFromSeekBar(z))
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