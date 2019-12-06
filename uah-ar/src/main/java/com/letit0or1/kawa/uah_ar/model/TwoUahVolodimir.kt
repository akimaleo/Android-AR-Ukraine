package com.letit0or1.kawa.uah_ar.model

import android.content.Context
import android.util.Log
import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.letit0or1.kawa.uah_ar.R
import kotlinx.android.synthetic.main.volodimir.view.*
import java.util.concurrent.CompletableFuture
import android.text.method.ScrollingMovementMethod
import com.google.ar.sceneform.math.Vector3


class TwoUahVolodimir(context: Context) : AugmentedImageNode() {

    private val infoView =
        ViewRenderable.builder().setView(context, R.layout.volodimir).build()
    lateinit var infoViewRenderable: ViewRenderable

    override fun setImage(image: AugmentedImage) {
        if (!infoView.isDone) {
            CompletableFuture.allOf(infoView)
                .thenAccept {
                    infoViewRenderable = infoView.get()
                    setImage(image)
                }
                .exceptionally {
                    Log.e(TAG, "Exception loading", it)
                    null
                }
        } else {
            anchor = image.createAnchor(image.centerPose)
            val node = Node()
            node.renderable = infoViewRenderable
            infoViewRenderable.view.volodimir_desc.movementMethod = ScrollingMovementMethod()
            node.setParent(this)

            val scaleValue = 0.1f
            localScale = Vector3(scaleValue, scaleValue, scaleValue)
        }
    }

    companion object {
        const val TAG: String = "TwoUahVolodimir"
    }
}