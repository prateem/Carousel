package com.meetarp.carousel.adapters

import android.view.View

open class CarouselViewsAdapter : CarouselAdapter<View>() {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int) {
        holder.progressBar.visibility = View.GONE
        holder.container.removeAllViews()
        holder.container.addView(carouselItems[position])
    }

}