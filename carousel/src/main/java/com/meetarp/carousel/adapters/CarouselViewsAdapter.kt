package com.meetarp.carousel.adapters

import android.view.View

open class CarouselViewsAdapter : CarouselAdapter<View>() {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int, item: View) {
        holder.progressBar.visibility = View.GONE
        holder.container.removeAllViews()
        holder.container.addView(item)
    }

}