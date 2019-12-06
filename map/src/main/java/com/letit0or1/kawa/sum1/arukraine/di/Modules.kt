package com.letit0or1.kawa.sum1.arukraine.di

import com.letit0or1.kawa.sum1.arukraine.ui.activity.MainViewModel
import com.letit0or1.kawa.sum1.arukraine.data.MapImage
import com.letit0or1.kawa.sum1.arukraine.ui.fragment.ARFragmentViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val DEFAULT_IMAGE_FOLDER = "images"

val dataModule = module {
    single {
        listOf(
            MapImage("$DEFAULT_IMAGE_FOLDER/map0.png", 0.297f, arrayOf(0f, 0f, 0f, 0f)),
            MapImage("$DEFAULT_IMAGE_FOLDER/map1.png", 0f, arrayOf(0f, 0f, 0f, 0f)),
            MapImage("$DEFAULT_IMAGE_FOLDER/map2.png", 0f, arrayOf(0f, 0f, 0f, 0f)),
            MapImage("$DEFAULT_IMAGE_FOLDER/map3.jpg", 0.26f, arrayOf(0f, 0f, 0f, 0f))
        )
    }
}

val viewModelModule = module {
    viewModel { ARFragmentViewModel() }
    viewModel { MainViewModel() }
}