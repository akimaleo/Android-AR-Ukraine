package com.letit0or1.kawa.delpost.arukraine.di

import com.letit0or1.kawa.delpost.arukraine.data.MapImage
import org.koin.dsl.module

private val DEFAULT_IMAGE_FOLDER = "images"

val moduleE = module {
    single { listOf(MapImage("$DEFAULT_IMAGE_FOLDER/map0.png", arrayOf(0f, 0f, 0f, 0f))) }
}