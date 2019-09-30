package com.example.meetarp.carousel.data

import android.view.View
import android.widget.ImageView
import com.meetarp.carousel.CarouselAdapter

class ResourceImage(private val resId: Int) : CarouselImage() {

    override fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    ) {
        viewHolder.progressBar.visibility = View.GONE
        imageView.setImageResource(resId)
    }

}