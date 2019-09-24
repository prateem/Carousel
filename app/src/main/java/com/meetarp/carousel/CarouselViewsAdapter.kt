package com.meetarp.carousel

import android.content.Context
import android.view.View

class CarouselViewsAdapter(context: Context)
    : CarouselAdapter<View>(context) {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int) {
        holder.progressBar.visibility = View.GONE
        holder.container.removeAllViews()
        holder.container.addView(carouselItems[position])
    }

}