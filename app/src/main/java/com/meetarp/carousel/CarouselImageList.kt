package com.meetarp.carousel

import android.net.Uri
import androidx.annotation.DrawableRes

/**
 * Wrapper class for a List<Any> to make usage of [ImageCarousel.setImages] semantically clear.
 */
@Suppress("Unused")
class CarouselImageList {
    companion object Builder {
        fun fromDrawableResList(@DrawableRes listOfDrawableRes: List<Int>): CarouselImageList {
            return CarouselImageList().apply {
                listOfDrawableRes.forEach { add(it) }
            }
        }

        fun fromUriList(listOfUri: List<Uri>): CarouselImageList {
            return CarouselImageList().apply {
                listOfUri.forEach { add(it) }
            }
        }
    }

    private val items: MutableList<Any> = mutableListOf()

    fun clear() = items.clear()
    fun add(@DrawableRes drawableRes: Int) = items.add(drawableRes)
    fun add(imageUri: Uri) = items.add(imageUri)
    fun remove(src: Any) = items.remove(src)
    fun getList(): List<Any> = items
}