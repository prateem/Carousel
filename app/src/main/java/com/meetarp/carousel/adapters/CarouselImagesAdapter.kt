package com.meetarp.carousel.adapters

import android.content.Context
import android.widget.ImageView
import com.meetarp.carousel.images.CarouselImage

class CarouselImagesAdapter(private val context: Context)
    : CarouselAdapter<CarouselImage>(context) {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int) {
        val carouselImage = carouselItems[position]
        val container = holder.container

        val imageView: ImageView =
            if (container.childCount > 0 && container.getChildAt(0) is ImageView) {
                container.getChildAt(0) as ImageView
            } else {
                container.removeAllViews()
                ImageView(context).also { container.addView(it) }
            }

        carouselImage.loadInto(imageView, this, holder)
    }

}