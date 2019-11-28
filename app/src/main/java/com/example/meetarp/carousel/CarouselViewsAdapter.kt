package com.example.meetarp.carousel

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.meetarp.carousel.CarouselAdapter

class CarouselViewsAdapter : CarouselAdapter<View>() {

    // Default gravity is START and TOP. This adapter wants the views CENTER in the container.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return super.onCreateViewHolder(parent, viewType)
            .also { vh -> (vh as DefaultCarouselViewHolder).container.gravity = Gravity.CENTER }
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder as DefaultCarouselViewHolder
        val item = carouselItems[position]

        holder.container.visibility = View.VISIBLE
        holder.progressBar.visibility = View.GONE
        holder.container.removeAllViews()
        holder.container.addView(item)
    }

}