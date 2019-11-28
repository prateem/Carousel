package com.example.meetarp.carousel

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.meetarp.carousel.CarouselAdapter
import com.example.meetarp.carousel.data.CarouselImage

class CarouselImagesAdapter : CarouselAdapter<CarouselImage>() {

    // Default gravity is START and TOP. This adapter wants the views CENTER in the container.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        return super.onCreateViewHolder(parent, viewType)
            .also { vh -> (vh as DefaultCarouselViewHolder).container.gravity = Gravity.CENTER }
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder as DefaultCarouselViewHolder
        val item = carouselItems[position]

        holder.container.visibility = View.VISIBLE
        holder.progressBar.visibility = View.VISIBLE

        val container = holder.container
        val imageView: ImageView =
            if (container.childCount > 0 && container.getChildAt(0) is ImageView) {
                container.getChildAt(0) as ImageView
            } else {
                container.removeAllViews()
                ImageView(container.context).also { container.addView(it) }
            }

        item.loadInto(imageView, this, holder)
    }

    fun getErrorView(context: Context): View? {
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.error_icon_size)

        return ImageView(context).also {
            it.layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
            it.setImageResource(R.drawable.error)
        }
    }

}