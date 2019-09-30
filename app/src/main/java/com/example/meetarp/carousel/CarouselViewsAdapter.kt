package com.example.meetarp.carousel

import android.view.View
import com.meetarp.carousel.CarouselAdapter

class CarouselViewsAdapter : CarouselAdapter<View>() {

    override fun bindItemForPosition(holder: CarouselViewHolder, position: Int, item: View) {
        holder.progressBar.visibility = View.GONE
        holder.container.removeAllViews()
        holder.container.addView(item)
    }

}