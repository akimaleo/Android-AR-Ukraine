package com.letit0or1.kawa.uah_ar.model

import android.content.Context
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.letit0or1.kawa.uah_ar.R
import java.util.concurrent.CompletableFuture
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.TransformableNode
import com.google.ar.sceneform.ux.TransformationSystem


class TwoUahSofiaCathedral(context: Context, val transformationSystem: TransformationSystem) :
    AugmentedImageNode() {

    private var renderableModel: CompletableFuture<ModelRenderable> = ModelRenderable.builder()
        .setSource(context, R.raw.sofiak)
        .build()

    override fun setImage(image: AugmentedImage) {
        if (!renderableModel.isDone) {
            CompletableFuture.allOf(renderableModel)
                .thenAccept {
                    setImage(image)
                }
                .exceptionally {
                    Log.e(TAG, "Exception loading", it)
                    null
                }
        }

        val rendable = renderableModel.getNow(null)

        anchor = image.createAnchor(image.centerPose)
        val node = TransformableNode(transformationSystem)
        node.translationController.isEnabled = false
        node.renderable = rendable
        node.setParent(this)
        val scaleValue = 0.08f
        localScale = Vector3(scaleValue, scaleValue, scaleValue)
    }

    companion object {
        const val TAG: String = "TwoUahVolodimir"
    }
}