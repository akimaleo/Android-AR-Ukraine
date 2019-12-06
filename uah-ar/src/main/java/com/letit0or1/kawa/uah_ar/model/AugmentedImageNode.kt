package com.letit0or1.kawa.uah_ar.model

import com.google.ar.core.AugmentedImage
import com.google.ar.sceneform.AnchorNode

abstract class AugmentedImageNode : AnchorNode() {
    abstract fun setImage(image: AugmentedImage)
}