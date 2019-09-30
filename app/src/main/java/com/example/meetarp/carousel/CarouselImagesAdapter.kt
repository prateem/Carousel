package com.example.meetarp.carousel

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.meetarp.carousel.R
import com.meetarp.carousel.CarouselAdapter
import com.example.meetarp.carousel.data.CarouselImage

class CarouselImagesAdapter : CarouselAdapter<CarouselImage>() {

    fun getErrorView(context: Context): View? {
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.error_icon_size)

        return ImageView(context).also {
            it.layoutParams = RelativeLayout.LayoutParams(iconSize, iconSize)
            it.setImageResource(R.drawable.image_error)
        }
    }

    override fun bindItemForPosition(
        holder: CarouselViewHolder,
        position: Int,
        item: CarouselImage
    ) {
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

}