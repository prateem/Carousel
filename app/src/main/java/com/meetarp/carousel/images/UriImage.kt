package com.meetarp.carousel.images

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import coil.api.load
import coil.size.PixelSize
import com.meetarp.carousel.adapters.CarouselAdapter

class UriImage(private val uri: Uri) : CarouselImage() {

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
                    onError = { _ ->
                        Log.w("Carousel", "Displaying error image due to a failure loading desired image")
                        viewHolder.container.visibility = View.GONE
                        viewHolder.progressBar.visibility = View.GONE
                        viewHolder.error.let {
                            it.visibility = View.VISIBLE
                            it.setColorFilter(adapter.errorTint)
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