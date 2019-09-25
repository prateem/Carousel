package com.meetarp.carousel.images

import android.view.View
import android.widget.ImageView
import com.meetarp.carousel.adapters.CarouselAdapter

class ResourceImage(private val resId: Int) : CarouselImage() {

    override fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    ) {
        viewHolder.progressBar.visibility = View.GONE
        viewHolder.error.visibility = View.GONE
        imageView.setImageResource(resId)
    }

}