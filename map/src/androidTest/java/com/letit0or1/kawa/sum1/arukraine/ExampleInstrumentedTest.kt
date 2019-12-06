package com.letit0or1.kawa.sum1.arukraine

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.letit0or1.kawa.sum1.arukraine.data.MapImage
import com.letit0or1.kawa.sum1.arukraine.ui.fragment.ARUkraineFragment

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest {

    val imagesList: List<MapImage> by inject<List<MapImage>>()

    lateinit var context: Context

    @Test
    fun useAppContext() {
        // Context of the app under test.
        context = InstrumentationRegistry.getTargetContext()
        assertEquals("com.letit0or1.kawa.delpost.arukraine", context.packageName)
    }

    @Test
    fun testDatabaseCreate() {
        setupAugmentedImageDatabase(getConfig(), getSession())
    }

    private fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
        val assetManager = if (context != null) context!!.assets else null
        if (assetManager == null) {
            Log.e("TAG", "Context is null, cannot intitialize image database.")
            return false
        }
        val augmentedImageDatabase = AugmentedImageDatabase(session)
        imagesList.forEach {
            val augmentedImageBitmap = loadImageBitmap(assetManager, it.fileName) ?: return false

            if (it.width == 0f)
                augmentedImageDatabase.addImage(it.fileName, augmentedImageBitmap)
            else
                augmentedImageDatabase.addImage(it.fileName, augmentedImageBitmap, it.width)
        }
        config.augmentedImageDatabase = augmentedImageDatabase
        return true
    }

    private fun loadImageBitmap(assetManager: AssetManager, name: String): Bitmap? {
        try {
            assetManager.open(name).use { `is` -> return BitmapFactory.decodeStream(`is`) }
        } catch (e: IOException) {
            Log.e("TAG", "IO exception loading augmented image bitmap.", e)
        }

        return null
    }
}
