package com.letit0or1.kawa.uah_ar

import android.app.ActivityManager
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.letit0or1.kawa.common.RecordArFragment
import java.io.IOException

class ArUahFragment : RecordArFragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Check for Sceneform being supported on this device.  This check will be integrated into
        // Sceneform eventually.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later")
            SnackbarHelper.getInstance()
                .showError(activity, "Sceneform requires Android N or later")
        }

        val openGlVersionString =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later")
            SnackbarHelper.getInstance()
                .showError(activity, "Sceneform requires OpenGL ES 3.0 or later")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Turn off the plane discovery since we're only looking for images
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        return view
    }

    override fun getSessionConfiguration(session: Session): Config {
        val config = super.getSessionConfiguration(session)
        config.focusMode = Config.FocusMode.AUTO

        if (!setupAugmentedImageDatabase(config, session)) {
            SnackbarHelper.getInstance()
                .showError(activity, "Could not setup augmented image database")
        }
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        val assetManager = if (context != null) context!!.assets else null
        if (assetManager == null) {
            Log.e(TAG, "Context is null, cannot intitialize image database.")
            return false
        }
        val augmentedImageDatabase = AugmentedImageDatabase(session)
        listOfCurrency.forEach {
            val augmentedImageBitmap = loadImageBitmap(assetManager, it.fileName) ?: return false

            if (it.widthInMeters == 0f) {
                augmentedImageDatabase.addImage(it.fileName, augmentedImageBitmap)
            } else {
                augmentedImageDatabase.addImage(it.fileName, augmentedImageBitmap, it.widthInMeters)
            }
        }
        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadImageBitmap(assetManager: AssetManager, name: String): Bitmap? {
        try {
            assetManager.open(name).use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e(TAG, "IO exception loading augmented image bitmap.", e)
        }
        return null
    }

    companion object {
        private const val TAG = "ArUahFragment"
        // Do a runtime check for the OpenGL level available at runtime to avoid Sceneform crashing the
        // application.
        private const val MIN_OPENGL_VERSION = 3.0
    }
}