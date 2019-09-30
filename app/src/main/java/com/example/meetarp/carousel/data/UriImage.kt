package com.example.meetarp.carousel.data

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import coil.api.load
import coil.size.PixelSize
import com.example.meetarp.carousel.CarouselImagesAdapter
import com.meetarp.carousel.CarouselAdapter

open class UriImage(private val uri: Uri) : CarouselImage() {

    override fun loadInto(
        imageView: ImageView,
        adapter: CarouselAdapter<CarouselImage>,
        viewHolder: CarouselAdapter<CarouselImage>.CarouselViewHolder
    ) {
        viewHolder.container.post {
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageView.load(uri) {
                size(PixelSize(viewHolder.container.width, viewHolder.container.height))
                target(
                    onError = {
                        Log.w("Carousel", "Displaying error image due to a failure loading desired image")
                        viewHolder.progressBar.visibility = View.GONE
                        imageView.visibility = View.GONE

                        (adapter as CarouselImagesAdapter)
                            .getErrorView(viewHolder.container.context)?.let {
                                viewHolder.container.addView(it)
                            }
                    },
                    onSuccess = { image ->
                        viewHolder.progressBar.visibility = View.GONE
                        imageView.setImageDrawable(image)
                    }
                )
            }
        }
    }

}