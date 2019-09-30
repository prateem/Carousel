package com.example.meetarp.carousel.data

import android.widget.ImageView
import com.meetarp.carousel.CarouselAdapter

abstract class CarouselImage {

    abstract fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    )

}