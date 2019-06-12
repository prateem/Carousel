package com.meetarp.imagecarousel

import android.net.Uri
import androidx.annotation.DrawableRes

/**
 * Wrapper class for a List<Any> to make usage of [ImageCarousel.setImages] semantically clear.
 */
class CarouselImageList {
    companion object Builder {
        fun fromDrawableResList(listOfDrawableRes: List<DrawableRes>): CarouselImageList {
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
    fun add(drawableRes: DrawableRes) = items.add(drawableRes)
    fun add(imageUri: Uri) = items.add(imageUri)
    fun remove(src: Any) = items.remove(src)
    fun getList(): List<Any> = items
}