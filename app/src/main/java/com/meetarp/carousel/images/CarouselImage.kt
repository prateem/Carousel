package com.meetarp.carousel.images

import android.widget.ImageView
import com.meetarp.carousel.adapters.CarouselAdapter

abstract class CarouselImage {
    abstract fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    )
}