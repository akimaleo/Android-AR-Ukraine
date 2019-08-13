package com.letit0or1.kawa.sum1.arukraine.data

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem
import com.letit0or1.kawa.sum1.arukraine.R
import java.util.concurrent.CompletableFuture

class AugmentedImageNode(val context: Context, val transformationSystem: TransformationSystem) : AnchorNode() {

    public var onClickListener: (HitTestResult, MotionEvent, Node, Oblast) -> Unit = { _, _, _, _ -> }
    private var image: AugmentedImage? = null

    private val renderableArray: Array<CompletableFuture<ModelRenderable>>
    private val infoView = ViewRenderable.builder().setView(context, R.layout.oblast_view_info).build()
    private var infoViewRenderable: ViewRenderable? = null
    private val oblastValues = Oblast.values()

    init {
        renderableArray = Array(oblastValues.size) {
            ModelRenderable.builder()
                .setSource(context, oblastValues[it].modelRes)
                .build()
        }
    }

    fun setImage(image: AugmentedImage) {
        this.image = image
        if (renderableArray.any { !it.isDone } && !infoView.isDone) {
            CompletableFuture.allOf(*renderableArray, infoView)
                .thenAccept {
                    infoViewRenderable = infoView.get()
                    setImage(image)
                }
                .exceptionally {
                    Log.e(TAG, "Exception loading", it)
                    null
                }
        }

        anchor = image.createAnchor(image.centerPose)
        renderableArray.forEachIndexed { index, it ->
            //            val anchorNode = AnchorNode(anchor)
            val oblastItem = oblastValues[index]
            val oblNode = Node()
            oblNode.localScale = Vector3(
                oblastItem.localScale.x * SCALE_VECTOR.x,
                oblastItem.localScale.y * SCALE_VECTOR.y,
                oblastItem.localScale.z * SCALE_VECTOR.z
            )
            oblNode.localPosition = Vector3.add(oblastItem.localPosition, SHIFT_VECTOR)
            oblNode.renderable = it.getNow(null)
            oblNode.setParent(this)
            oblNode.setOnTapListener { hitTestRes, motionEvent ->
                //                oblNode.select()
                onClickListener(hitTestRes, motionEvent, oblNode, oblastItem)
            }
        }
    }

    companion object {
        var SHIFT_VECTOR = Vector3(0.012f, 0f, 0f)
        var SCALE_VECTOR = Vector3(1f, 1f, 1f)
        const val TAG: String = "AugmentedImageNode"
        //resize to fit image
    }
}